package com.smilepayz.brazil;

import com.google.gson.Gson;
import com.smilepayz.brazil.bean.ReceiverReq;
import com.smilepayz.brazil.common.SignatureUtils;
import com.smilepayz.brazil.bean.MerchantReq;
import com.smilepayz.brazil.bean.MoneyReq;
import com.smilepayz.brazil.bean.TradePayoutReq;
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
 * @Date 2024/6/27 14:21
 **/
public class PayoutRequestDemo {
    @SneakyThrows
    public static void main(String[] args) {
        //url
        String endPointUlr = "/v2.0/disbursement/pay-out";

        //sandbox
        String requestPath = Constant.baseUrlSanbox + endPointUlr;
        String merchantId = Constant.merchantIdSandBox;
        String merchantSecret = Constant.merchantSecretSandBox;

        //production
//        String requestPath = Constant.baseUrl + endPointUlr;
//        String merchantId = Constant.merchantId;
//        String merchantSecret = Constant.merchantSecret;


        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);
        BigDecimal amount = new BigDecimal("200");

        AreaEnum areaEnum = AreaEnum.BRAZIL;
        String paymentMethod = "CPF"; //INDONESIA

        //generate parameter
        String merchantOrderNo = (merchantId + UUID.randomUUID()).replaceAll("-","").substring(0,32);
        String purpose = "Purpose For Disbursement from Java SDK";

        //moneyReq
        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setCurrency(areaEnum.getCurrency().name());
        moneyReq.setAmount(amount);

        //merchantReq
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId(merchantId);

        ReceiverReq receiverReq = new ReceiverReq();
        receiverReq.setTaxNumber("12345678909");

        //payoutReq
        TradePayoutReq payoutReq = new TradePayoutReq();
        payoutReq.setOrderNo(merchantOrderNo);
        payoutReq.setPurpose(purpose);
        payoutReq.setMoney(moneyReq);
        payoutReq.setMerchant(merchantReq);
        payoutReq.setCallbackUrl("your.notify.url");
        payoutReq.setPaymentMethod(paymentMethod);
        payoutReq.setCashAccount("123456788900");
        payoutReq.setArea(areaEnum.getCode());
        payoutReq.setReceiver(receiverReq);

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(payoutReq);
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
        System.out.println(requestPath);
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
