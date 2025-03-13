package com.smilepayz.mexico.common;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.smilepayz.mexico.bean.MerchantReq;
import com.smilepayz.mexico.bean.MoneyReq;
import com.smilepayz.mexico.bean.TradePayinReq;

/**
 * @Author Moore
 * @Date 2024/6/27 14:15
 **/
public class SignatureUtils {

    /**
     * minify
     */
    public static String minify(String jsonString) {
        boolean in_string = false;
        boolean in_multi_line_comment = false;
        boolean in_single_line_comment = false;
        char string_opener = 'x'; // unused value, just something that makes compiler happy

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < jsonString.length(); i++) {
            // get next (c) and next-next character (cc)

            char c = jsonString.charAt(i);
            String cc = jsonString.substring(i, Math.min(i + 2, jsonString.length()));

            // big switch is by what mode we're in (in_string etc.)
            if (in_string) {
                if (c == string_opener) {
                    in_string = false;
                    out.append(c);
                } else if (c == '\\') { // no special treatment needed for \\u, it just works like this too
                    out.append(cc);
                    ++i;
                } else
                    out.append(c);
            } else if (in_single_line_comment) {
                if (c == '\r' || c == '\n')
                    in_single_line_comment = false;
            } else if (in_multi_line_comment) {
                if (cc.equals("*/")) {
                    in_multi_line_comment = false;
                    ++i;
                }
            } else {
                // we're outside of the special modes, so look for mode openers (comment start, string start)
                if (cc.equals("/*")) {
                    in_multi_line_comment = true;
                    ++i;
                } else if (cc.equals("//")) {
                    in_single_line_comment = true;
                    ++i;
                } else if (c == '"' || c == '\'') {
                    in_string = true;
                    string_opener = c;
                    out.append(c);
                } else if (!Character.isWhitespace(c))
                    out.append(c);
            }
        }
        return out.toString();
    }

    public static String sha256RsaSignature(String stringToSign, String privateKeyStr) {
        try {
            byte[] privateKeys = java.util.Base64.getDecoder().decode(privateKeyStr.getBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeys);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(stringToSign.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return java.util.Base64.getEncoder().encodeToString(signed);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            System.err.println("generate signature failed: " + e.getMessage());
        }
        return null;
    }

    public static boolean checkSha256RsaSignature(String content, String signed, String publicKeyStr, String encode) {
        try {
            byte[] publicKeys = Base64.decodeBase64(publicKeyStr);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeys);
            KeyFactory myKeyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = myKeyFactory.generatePublic(publicKeySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(content.getBytes(encode));
            return signature.verify(Base64.decodeBase64(signed));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException | IllegalArgumentException | UnsupportedEncodingException e) {
            System.err.println("generate signature failed: " + e.getMessage());
        }
        return false;
    }


    public static void main(String[] args) {
        String sandboxMerchantCode = "6a58a603e5043290f4097ee4a7745661b3656932d4eebc3106b5dddc3af6e053";

        String merchantOrderNo = "T_" + System.currentTimeMillis();
        String purpose = "Purpose For Transaction from Java SDK";
        String paymentMethod = "W_DANA";
        BigDecimal amount = new BigDecimal("10000");
        String timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        //moneyReq
        MoneyReq moneyReq = new MoneyReq();
        moneyReq.setCurrency(CurrencyEnum.IDR.name());
        moneyReq.setAmount(amount);
        //merchantReq
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setMerchantId("20019");

        TradePayinReq payinReq = new TradePayinReq();
        payinReq.setOrderNo(merchantOrderNo);
        payinReq.setPurpose(purpose);
        payinReq.setMoney(moneyReq);
        payinReq.setMerchant(merchantReq);
        //replace this value
        payinReq.setCallbackUrl("https:your.dome.com/your.notify.path");
        payinReq.setPaymentMethod(paymentMethod);
        payinReq.setArea(AreaEnum.INDONESIA.getCode());

        Gson gson = new Gson();
        String jsonStr = gson.toJson(payinReq);
        String minify = minify(jsonStr);
        System.out.println("minify=" + minify);

        //signature
        String content = String.join("|", timestamp, sandboxMerchantCode, minify);
        System.out.println("sign string =" + content);

        String signature = SignatureUtils.sha256RsaSignature(content, Constant.PRIVATE_KEY_STR);

        System.out.println("signature=" + signature);


        boolean b = SignatureUtils.checkSha256RsaSignature(content, signature, Constant.PUBLIC_KEY_STR, "UTF-8");
        System.out.println("check signature result=" + b);


    }

}
