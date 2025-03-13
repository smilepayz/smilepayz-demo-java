package com.smilepayz.indonesia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.smilepayz.indonesia.bean.InquiryOrderStatsuReq;
import com.smilepayz.indonesia.common.Constant;
import com.smilepayz.indonesia.common.SignatureUtils;

import lombok.SneakyThrows;

/**
 * @Author Moore
 * @Date 2024/7/2 10:41
 **/
public class InquriyOrderStatusDemo {

    @SneakyThrows
    public static void main(String[] args) {
        String env = "";
        String orderNo = "";
        String tradeNo = "";

        // 1 for pay-in order, 2 for pay-out order
        Integer tradeType = 1;
        String merchantId = "";
        String merchantSecret = "";
        String privateKeyString = "";
        inquiryOrderStatus(env, merchantId, tradeType, orderNo, tradeNo, merchantSecret, privateKeyString);
    }

    public static void inquiryOrderStatus(String env, String merchantId, Integer tradeType, String orderNo, String tradeNo, String merchantSecret, String privateKey) throws IOException {
        System.out.println("=====>Payin transaction");

        //url
        String endPointUlr = "/v2.0/inquiry-status";
        //sandbox
        String requestPath = com.smilepayz.brazil.common.Constant.BASE_URL_SANDBOX + endPointUlr;
        if (StringUtils.equals(env, "production")) {
            requestPath = Constant.BASE_URL + endPointUlr;
        }
        System.out.println("request url = " + requestPath);
        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);

        InquiryOrderStatsuReq inquiryOrderStatsuReq = new InquiryOrderStatsuReq();
        inquiryOrderStatsuReq.setOrderNo(orderNo);
        inquiryOrderStatsuReq.setTradeNo(tradeNo);
        inquiryOrderStatsuReq.setTradeType(tradeType);

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(inquiryOrderStatsuReq);
        System.out.println("jsonStr = " + jsonStr);

        //minify
        String minify = com.smilepayz.brazil.common.SignatureUtils.minify(jsonStr);
        System.out.println("minify = " + minify);

        //signature
        String content = String.join("|", timestamp, merchantSecret, minify);
        String signature = SignatureUtils.sha256RsaSignature(content, privateKey);

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
