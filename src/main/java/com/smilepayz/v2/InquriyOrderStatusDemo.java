package com.smilepayz.v2;

import com.google.gson.Gson;
import com.smilepayz.v2.bean.InquiryBalanceReq;
import com.smilepayz.v2.bean.InquiryOrderStatsuReq;
import com.smilepayz.v2.data.Constant;
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
import java.util.UUID;

/**
 * @Author Moore
 * @Date 2024/7/2 10:41
 **/
public class InquriyOrderStatusDemo {

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("=====>Payin transaction");

        //url
        String endPointUlr = "/v2.0/inquiry-status";
        String sandboxPath = Constant.baseUrlSanbox + endPointUlr;
        String prodPath = Constant.baseUrl  + endPointUlr;



        System.out.println("pay in request url = " + prodPath);


        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);

        InquiryOrderStatsuReq inquiryOrderStatsuReq = new InquiryOrderStatsuReq();
        inquiryOrderStatsuReq.setOrderNo("112200182402261848252600");
        inquiryOrderStatsuReq.setTradeNo("D_1708948105016");
        inquiryOrderStatsuReq.setTradeType(2);

        //jsonStr by gson
        Gson gson = new Gson();
        String jsonStr = gson.toJson(inquiryOrderStatsuReq);
        System.out.println("jsonStr = " + jsonStr);

        //minify
        String minify = SignatureUtils.minify(jsonStr);
        System.out.println("minify = " + minify);

        //signature
        String content = String.join("|",  timestamp, Constant.merchantSecret,minify);
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
