package com.smilepayz.thailand;

import com.google.gson.Gson;
import com.smilepayz.thailand.bean.InquiryBalanceReq;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @Author Moore
 * @Date 2024/7/2 10:40
 **/
public class InquiryBalanceDemo {


    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("=====>InquiryBalanceDemo");

        //url
        String endPointUlr = "/v2.0/inquiry-balance";

        //sandbox
//        String requestPath = Constant.baseUrlSanbox + endPointUlr;
//        String merchantId = Constant.merchantIdSandBox;
//        String merchantSecret = Constant.merchantSecretSandBox;

        //production
        String requestPath = Constant.baseUrl + endPointUlr;
        String merchantId = Constant.merchantId;
        String merchantSecret = Constant.merchantSecret;

        System.out.println("InquiryBalanceDemo request url = " + requestPath);


        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);

        InquiryBalanceReq inquiryBalanceReq = new InquiryBalanceReq();
        inquiryBalanceReq.setAccountNo("your account no");
        inquiryBalanceReq.setBalanceTypes(Arrays.asList("BALANCE"));

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(inquiryBalanceReq);
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
