package com.hst.fsp.tools;

import android.util.Base64;

import javax.crypto.Cipher;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FspToken {
    private String m_appid;
    private String m_secretkey;
    private String m_userid;
    private long m_expiretime;

    private String m_version = "001";


    public void setAppId(String appid) {
        m_appid = appid;
    }

    public void setSecretKey(String secretKey) {
        m_secretkey = secretKey;
    }

    public void setUserId(String userid) {
        m_userid = userid;
    }

    public void setExpireTime(long expireTime) {
        m_expiretime = expireTime;
    }

    public String build() {
        if (m_secretkey.length() != 16) {
            return "";
        }

        String rawJson = generateJsonRaw();
        String encodedContent = encode(rawJson);

        return m_version + encodedContent;
    }

    public static String build(String appId, String appSecrectKey, String userId) {
        //生成token的代码应该在服务器， demo中直接生成token不是 正确的做法
        FspToken token = new FspToken();
        token.setAppId(appId);
        token.setSecretKey(appSecrectKey);
        token.setUserId(userId);
        return token.build();
    }

    private String generateJsonRaw() {
        //simple string build, you can use your self json library
        StringBuilder jsonString = new StringBuilder("{");

        jsonString.append("\"aid\":\"").append(m_appid).append("\",");
        jsonString.append("\"uid\":\"").append(m_userid).append("\",");

        if (m_expiretime != 0) {
            jsonString.append("\"et\":").append(m_expiretime).append(",");
        }

        jsonString.append("\"ts\":").append(System.currentTimeMillis()).append(",");

        java.util.Random r = new java.util.Random();
        jsonString.append("\"r\":").append(Math.abs(r.nextInt()));

        jsonString.append("}");

        return jsonString.toString();
    }

    private String encode(String jsonContent) {
        try {
            byte byteIv[] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                    0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
            IvParameterSpec iv = new IvParameterSpec(byteIv);
            SecretKeySpec skeySpec = new SecretKeySpec(m_secretkey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            while (jsonContent.getBytes().length % 16 != 0) {
                jsonContent = jsonContent + " ";
            }

            byte[] encrypted = cipher.doFinal(jsonContent.getBytes());

            //if in server side, use java.util.Base64
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }


}