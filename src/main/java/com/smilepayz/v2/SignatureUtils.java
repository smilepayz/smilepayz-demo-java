package com.smilepayz.v2;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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

    public static String createSignature(String stringToSign, String privateKeyStr) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean doCheckNotificationSignature(String content, String signed, String publicKeyStr, String encode) {
        try {
            byte[] publicKeys = Base64.decodeBase64(publicKeyStr);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeys);
            KeyFactory myKeyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = myKeyFactory.generatePublic(publicKeySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(content.getBytes(encode));
            return signature.verify(Base64.decodeBase64(signed));
        } catch (Exception e) {
            System.out.println("Sign doCheck exception:" + e);
        }
        return false;
    }

}
