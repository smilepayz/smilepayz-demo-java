package com.smilepayz.v2;

import com.google.gson.Gson;
import com.smilepayz.v2.bean.MerchantReq;
import com.smilepayz.v2.bean.MoneyReq;
import com.smilepayz.v2.bean.TradeAdditionalReq;
import com.smilepayz.v2.bean.TradePayoutReq;
import com.smilepayz.v2.data.AreaEnum;
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
    private static final String privateKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC3LbmfPhjGXJ+a6NVKyRWEfCpsKfl9UFnMRltNosJv+7qd6OUK3t7Q8fKX8rxuJBeXLLWrOZvjoGlP3ybvvhUSGKH+BLcN1k2eJbcKuThvXvKQvd7pXolY91gkF8V78FX+TKegZupbeji0XUGXCBNyShocYM6Cvailf0Iyv49dcktJmi4drBKfgj5l4HUY9TaDuCZOrhvFZfZZxBK1zkm72k0ZLfqTGmG8O2tsByndCTH2aPLT9odR8/O4qfoTG+vV9HivIKKOI2h0kZfQBOqWD4/ofwu9PNoRRzGgzfRpF3GhnKa3bRVSpCUuBtHfotQfFxbjaDk0s3K5BhQE7HxzAgMBAAECggEARHhaBxUeC595pVzcxVyOp3wGG3JBKL9NIZc277kj9tngcsAoRTzziqS1qmh4WK8zBjYXHg6ln5tJYiqmkjy6AY6llp7KkeiGENRGLEL5vl9+Se4/EXpd2pxyHOOp1N8MNccPbVyqw1DXO0wUhVDme/UI94yUBLjB/kKoSvHhs+qwJ8cz9C8sF3Zs/zF6Te/f+Z+HrIbVj4vlx6DYBs3pWe7J+XYg3XXpbvIBJVTN0lreQAjtopic0F7o1EULnllJmOqy+kiRMuSi5ESrWFOnuCrY0iz/C8LJKlqWa3d+1jVLPvKYucXvddYrwjyu8kYHZAenKWKkLPlQkQxGUFcX8QKBgQDYSCsgg7tDlXTw1P9Z2BhkVo1ugYWA1FcxqTY2sTOMOwCTt3QZAFEqofbNvdjZbk7vu8gh5LyxbWAr/sNqOXH5915a6DZKBGl+qBTH2TZZKmhCUWlmn8T7ySYclfsgIZsUxHaDfY+otiXGtNegSX9US9/AFVSMdiQBCmr5/i48mQKBgQDY0U5+MDl6GnbDH/hCT+YMq/m5W58m7ehJfG6jPtLQkPPvMq2Oj5HUeXx4vDdUxzaRlC9fICwe2KgKiGYpJsRPte6JfO6te7wsO2lDk0oBiw+jewm4CwZ5KeDYyReha8Hc2H1j5hllVx5DIZiage2ZswS5+kCNgzf4QbdAOd886wKBgAo5TyCYWY/WTtLbnr6GgpCrrr/ci40NfJmyYAex1Lf6Sgqxj2FnLG8RfPM42DlfB4g5njpL78eLXhJ2VpJ86LBiSymM9JQHJV2BYIoZ8IHCiW8pHgxl3Q/x8EVFqbtZG1Wd++Q3WUUmZx6/ibnf/47ij08rMvX417bc4TW0GEdxAoGAPX3nUBynQH0e76oyg8QbT766nZphoe3ZcnYK/tuDeMmTlWR/Gq6XQnaOGcPvwWiajmFDqiv6t2jlB8+1gbhP9vd3RqEbJDKypKzY5uRwGc3xyoDLudnOpTB+Z51oyUxBeDwiG+IXk8lIeOufVzrAQ1YlYgWap0fu6MbijSGcsa8CgYEA0OolBxRKdBSxmwMbXDocg5HtHd15UgJdlgL9GFjDZPisUvM7LWK97E5QPhY2IEIcv0EW9jM62bc1uPMhwDuN1dzmLafQIivGsIj5qUy18CoE4KJHsTeKLeHkrd6/nBd4da4A37+ksJ1t/sgZpjl40ShrxTV+OtzzY/kuQ4Uqc9g=";
    private static final String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAty25nz4YxlyfmujVSskVhHwqbCn5fVBZzEZbTaLCb/u6nejlCt7e0PHyl/K8biQXlyy1qzmb46BpT98m774VEhih/gS3DdZNniW3Crk4b17ykL3e6V6JWPdYJBfFe/BV/kynoGbqW3o4tF1BlwgTckoaHGDOgr2opX9CMr+PXXJLSZouHawSn4I+ZeB1GPU2g7gmTq4bxWX2WcQStc5Ju9pNGS36kxphvDtrbAcp3Qkx9mjy0/aHUfPzuKn6Exvr1fR4ryCijiNodJGX0ATqlg+P6H8LvTzaEUcxoM30aRdxoZymt20VUqQlLgbR36LUHxcW42g5NLNyuQYUBOx8cwIDAQAB";

    @SneakyThrows
    public static void main(String[] args) {
        //url
        String endPointUlr = "/v2.0/disbursement/pay-out";
        String sandboxPath = "https://sandbox-gateway-test.smilepayz.com" + endPointUlr;
        String prodPath = "https://gateway-test.smilepayz.com" + endPointUlr;
        String sandboxMerchantId = "sandbox-20019";
        String prodMerchantId = "20019";
        String merchantSecretSandbox = "6a58a603e5043290f4097ee4a7745661b3656932d4eebc3106b5dddc3af6e053";
        String merchantSecret = "95b57c46b8c2e068982be23fb669a80612cad68e6ce6ba4f5af9ec20d23bb274";



        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        System.out.println("timestamp = " + timestamp);
        BigDecimal amount = new BigDecimal("200");

        AreaEnum areaEnum = AreaEnum.INDIA;

        //generate parameter
        String merchantOrderNo = "D_" + System.currentTimeMillis();
        String purpose = "Purpose For Disbursement from Java SDK";
        String paymentMethod = "YES"; //India

        //moneyReq
        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setCurrency(areaEnum.getCurrency().name());
        moneyReq.setAmount(amount);

        //merchantReq
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId(prodMerchantId);


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
        String content = String.join("|", timestamp, merchantSecret,minify);
        String signature = SignatureUtils.sha256RsaSignature(content, privateKeyStr);


        // create httpClient
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(prodPath);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-TIMESTAMP", timestamp);
        httpPost.addHeader("X-SIGNATURE", signature);
        httpPost.addHeader("X-PARTNER-ID", prodMerchantId);

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
