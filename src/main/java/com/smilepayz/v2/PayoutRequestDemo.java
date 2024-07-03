package com.smilepayz.v2;

import com.google.gson.Gson;
import com.smilepayz.v2.bean.MerchantReq;
import com.smilepayz.v2.bean.MoneyReq;
import com.smilepayz.v2.bean.TradeAdditionalReq;
import com.smilepayz.v2.bean.TradePayoutReq;
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
 * @Date 2024/6/27 14:21
 **/
public class PayoutRequestDemo {
    @SneakyThrows
    public static void main(String[] args) {
        //url
        String endPointUlr = "/v2.0/disbursement/pay-out";
        String sandboxPath = Constant.baseUrlSanbox + endPointUlr;
        String prodPath = Constant.baseUrl + endPointUlr;


        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);
        BigDecimal amount = new BigDecimal("200");

        AreaEnum areaEnum = AreaEnum.INDIA;

        //generate parameter
        String merchantOrderNo = Constant.merchantId + System.currentTimeMillis();
        String purpose = "Purpose For Disbursement from Java SDK";
        String paymentMethod = "YES"; //India

        //moneyReq
        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setCurrency(areaEnum.getCurrency().name());
        moneyReq.setAmount(amount);

        //merchantReq
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId(Constant.merchantId);


        //TradeAdditionalReq
        TradeAdditionalReq additionalReq = new TradeAdditionalReq();
        additionalReq.setIfscCode("YESB0000097"); //India

        //payoutReq
        TradePayoutReq payoutReq = new TradePayoutReq();
        payoutReq.setOrderNo(merchantOrderNo);
        payoutReq.setPurpose(purpose);
        payoutReq.setProductDetail("Product details");
        payoutReq.setAdditionalParam(additionalReq);
        payoutReq.setMoney(moneyReq);
        payoutReq.setMerchant(merchantReq);
        payoutReq.setCallbackUrl("https:your.dome.com/your.notify.path");
        payoutReq.setPaymentMethod(paymentMethod);
        payoutReq.setCashAccount("17385238451");
        payoutReq.setArea(areaEnum.getCode());

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(payoutReq);
        System.out.println("jsonStr = " + jsonStr);

        //minify
        String minify = SignatureUtils.minify(jsonStr);
        System.out.println("minify = " + minify);


        //signature
        String content = String.join("|", timestamp, Constant.merchantSecret, minify);
        String signature = SignatureUtils.sha256RsaSignature(content, Constant.privateKeyStr);


        // create httpClient
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(prodPath);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-TIMESTAMP", timestamp);
        httpPost.addHeader("X-SIGNATURE", signature);
        httpPost.addHeader("X-PARTNER-ID", Constant.merchantId);

        // set entity
        httpPost.setEntity(new StringEntity(jsonStr, StandardCharsets.UTF_8));
        System.out.println(prodPath);
        // send
        HttpResponse response = httpClient.execute(httpPost);

        // response
        HttpEntity httpEntity = response.getEntity();
        String responseContent = EntityUtils.toString(httpEntity, "UTF-8");
        System.out.println("responseContent = " + responseContent);

        // release
        EntityUtils.consume(httpEntity);
    }
}
