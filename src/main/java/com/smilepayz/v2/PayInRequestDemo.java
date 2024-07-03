package com.smilepayz.v2;

import com.google.gson.Gson;
import com.smilepayz.v2.bean.MerchantReq;
import com.smilepayz.v2.bean.MoneyReq;
import com.smilepayz.v2.bean.TradePayinReq;
import com.smilepayz.v2.data.AreaEnum;
import com.smilepayz.v2.data.Constant;
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

/**
 * @Author Moore
 * @Date 2024/6/27 14:16
 **/
public class PayInRequestDemo {

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("=====>Payin transaction");

        //url
        String endPointUlr = "/v2.0/transaction/pay-in";
        String sandboxPath = Constant.baseUrlSanbox + endPointUlr;
        String prodPath = Constant.baseUrl + endPointUlr;


        System.out.println("pay in request url = " + sandboxPath);


        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);
        BigDecimal amount = new BigDecimal("10000");

        AreaEnum areaEnum = AreaEnum.INDONESIA;

        //generate parameter
        String merchantOrderNo = Constant.merchantId + System.currentTimeMillis();
        String purpose = "Purpose For Transaction from Java SDK";
        String paymentMethod = "W_DANA";

        //moneyReq
        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setCurrency(areaEnum.getCurrency().name());
        moneyReq.setAmount(amount);
        //merchantReq
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId(Constant.merchantIdSandBox);

        TradePayinReq payinReq = new TradePayinReq();
        payinReq.setOrderNo(merchantOrderNo);
        payinReq.setPurpose(purpose);
        payinReq.setMoney(moneyReq);
        payinReq.setMerchant(merchantReq);
        //replace this value
        payinReq.setCallbackUrl("https:your.dome.com/your.notify.path");
        payinReq.setPaymentMethod(paymentMethod);
        payinReq.setArea(areaEnum.getCode());

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(payinReq);
        System.out.println("jsonStr = " + jsonStr);

        //minify
        String minify = SignatureUtils.minify(jsonStr);
        System.out.println("minify = " + minify);

        //signature
        String content = String.join("|", timestamp, Constant.merchantSecretSandBox, minify);
        String signature = SignatureUtils.sha256RsaSignature(content, Constant.privateKeyStr);


        // create httpClient
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(sandboxPath);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-TIMESTAMP", timestamp);
        httpPost.addHeader("X-SIGNATURE", signature);
        httpPost.addHeader("X-PARTNER-ID", Constant.merchantIdSandBox);

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

        System.out.println("The pay interface is completed, and you can get your payment link");


    }
}
