package com.smilepayz.thailand;

import com.google.gson.Gson;
import com.smilepayz.thailand.bean.MerchantReq;
import com.smilepayz.thailand.bean.MoneyReq;
import com.smilepayz.thailand.bean.PayerReq;
import com.smilepayz.thailand.bean.TradePayinReq;
import com.smilepayz.thailand.common.AreaEnum;
import com.smilepayz.thailand.common.Constant;
import com.smilepayz.thailand.common.SignatureUtils;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @Author Moore
 * @Date 2024/6/27 14:16
 **/
public class PayInRequestDemo {

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("=====>Payin transaction");
        String endPointUlr = "/v2.0/transaction/pay-in";


        //sandbox
        String requestPath = Constant.baseUrlSanbox + endPointUlr;
        String merchantId = Constant.merchantIdSandBox;
        String merchantSecret = Constant.merchantSecretSandBox;

        //production
//        String requestPath = Constant.baseUrl + endPointUlr;
//        String merchantId = Constant.merchantId;
//        String merchantSecret = Constant.merchantSecret;

        System.out.println("pay in request url = " + requestPath);

        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);
        BigDecimal amount = new BigDecimal("1000");

        AreaEnum areaEnum = AreaEnum.THAILAND;
        String paymentMethod = "QRPAY";


        //generate parameter
        String merchantOrderNo = (merchantId + UUID.randomUUID()).replaceAll("-", "")
                .substring(0, 32);
        String purpose = "Purpose For Transaction from Java SDK";

        // additional parameter
        PayerReq payerReq = new PayerReq();
        //required for THB transaction
        payerReq.setName("payer's name");
        payerReq.setAccountNo("232121122112");
        payerReq.setBankName("KBANK");

        //moneyReq
        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setCurrency(areaEnum.getCurrency().name());
        moneyReq.setAmount(amount);

        //merchantReq
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId(merchantId);

        TradePayinReq payinReq = new TradePayinReq();
        payinReq.setOrderNo(merchantOrderNo);
        payinReq.setPurpose(purpose);
        payinReq.setMoney(moneyReq);
        payinReq.setMerchant(merchantReq);
        payinReq.setCallbackUrl("your.notify.url");
        payinReq.setPaymentMethod(paymentMethod);
        payinReq.setArea(areaEnum.getCode());
        payinReq.setPayer(payerReq);

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(payinReq);
        System.out.println("jsonStr = " + jsonStr);

        //minify
        String minify = SignatureUtils.minify(jsonStr);
        System.out.println("minify = " + minify);

        //signature
        String content = String.join("|", timestamp, merchantSecret, minify);
        String signature = SignatureUtils.sha256RsaSignature(content, Constant.privateKeyStr);


        // create httpClient
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(requestPath);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-TIMESTAMP", timestamp);
        httpPost.addHeader("X-SIGNATURE", signature);
        httpPost.addHeader("X-PARTNER-ID", merchantId);

        // set entity
        httpPost.setEntity(new StringEntity(jsonStr, StandardCharsets.UTF_8));

        // send
        HttpResponse response = httpClient.execute(httpPost);

        // response
        HttpEntity httpEntity = response.getEntity();
        String responseContent = EntityUtils.toString(httpEntity, "UTF-8");
        System.out.println("responseContent = " + responseContent);

        // release
        EntityUtils.consume(httpEntity);

        System.out.println("======> request end ,request success");


    }
}
