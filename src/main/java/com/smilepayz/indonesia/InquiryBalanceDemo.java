package com.smilepayz.indonesia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.smilepayz.indonesia.bean.InquiryBalanceReq;
import com.smilepayz.indonesia.common.Constant;
import com.smilepayz.indonesia.common.SignatureUtils;

import lombok.SneakyThrows;

/**
 * @Author Moore
 * @Date 2024/7/2 10:40
 **/
public class InquiryBalanceDemo {
    @SneakyThrows
    public static void main(String[] args) {
        String env = "";
        String merchantId = "";
        String accountNo = "";
        String merchantSecret = "";
        String privateKeyString = "";
        inquiryBalance(env, accountNo, merchantId, merchantSecret, privateKeyString);
    }

    public static void inquiryBalance(String env, String accountNo, String merchantId, String merchantSecret, String privateKey) throws IOException {
        System.out.println("=====>InquiryBalanceDemo");
        //url
        String endPointUlr = "/v2.0/inquiry-balance";

        //sandbox
        String requestPath = Constant.BASE_URL_SANDBOX + endPointUlr;
        if (StringUtils.equals(env, "production")) {
            requestPath = Constant.BASE_URL + endPointUlr;
        }

        System.out.println("InquiryBalanceDemo request url = " + requestPath);

        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);

        InquiryBalanceReq inquiryBalanceReq = new InquiryBalanceReq();
        inquiryBalanceReq.setAccountNo(accountNo);
        inquiryBalanceReq.setBalanceTypes(Collections.singletonList("BALANCE"));

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(inquiryBalanceReq);
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
