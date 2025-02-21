package com.smilepayz.brazil;

import com.google.gson.Gson;
import com.smilepayz.brazil.common.SignatureUtils;
import com.smilepayz.brazil.bean.MerchantReq;
import com.smilepayz.brazil.bean.MoneyReq;
import com.smilepayz.brazil.bean.TradePayinReq;
import com.smilepayz.brazil.bean.PayerReq;
import com.smilepayz.brazil.common.AreaEnum;
import com.smilepayz.brazil.common.Constant;
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
        BigDecimal amount = new BigDecimal("100");
        AreaEnum areaEnum = AreaEnum.BRAZIL;
        String paymentMethod = "PIX";


        //generate orderNo
        String merchantOrderNo = merchantId.replace("sandbox-", "S") +
                UUID.randomUUID().toString().replaceAll("-","");
        String purpose = "Purpose For Transaction from Java SDK";

        //payer info
        PayerReq payerInfo = new PayerReq();
        payerInfo.setPixAccount("12345678901");

        //moneyReq
        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setCurrency(areaEnum.getCurrency().name());
        moneyReq.setAmount(amount);

        //merchantReq
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId(merchantId);

        //pay in request
        TradePayinReq payinReq = new TradePayinReq();
        payinReq.setOrderNo(merchantOrderNo.substring(0, 32));
        payinReq.setPurpose(purpose);
        payinReq.setMoney(moneyReq);
        payinReq.setMerchant(merchantReq);
        payinReq.setCallbackUrl("https://your call back url that receive the order status notification");//replace this value
        payinReq.setPaymentMethod(paymentMethod);
        payinReq.setArea(areaEnum.getCode());
        payinReq.setPayer(payerInfo);

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
