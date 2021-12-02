package kr.co.dpm.agent.util;

import org.apache.commons.codec.binary.Base64;

import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;


public class Cryptogram {
    private String iv;
    private Key secretKeySpec;

    public Cryptogram(String key) {
        this.iv = key.substring(0, 16);

        byte[] keyBytes = new byte[16];
        byte[] utf8KeyBytes = null;

        try {
            utf8KeyBytes = key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int len = utf8KeyBytes.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }

        System.arraycopy(utf8KeyBytes, 0, keyBytes, 0, len);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        this.secretKeySpec = secretKeySpec;
    }

    public String decrypt(Object word) throws Exception {
        if (word == null) {
            return null;
        }

        return decrypt(String.valueOf(word));
    }

    private String decrypt(String word) throws Exception {
        if (StringUtils.isEmpty(word)) {
            return "";
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, this.secretKeySpec, new IvParameterSpec(this.iv.getBytes("UTF-8")));

        byte[] decrypted = Base64.decodeBase64(word.getBytes());

        String decryptWord = new String(cipher.doFinal(decrypted), "UTF-8");

        // logger.debug("############################################################");
        // logger.debug(" Decrypt : " + word + " ---> " + decryptWord);
        // logger.debug("############################################################");

        return decryptWord;
    }
}
