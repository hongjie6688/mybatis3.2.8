package org.apache.ibatis.encry;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * aes加密 解密
 * 采用的 算法/模式/补码方式 为 AES/ECB/PKCS5Padding
 *
 * @author zhanghj
 * @create 2017-11-15 11:46
 **/
public class AESEncryUtil {

    private final static String CHARSET_ENCODE = "UTF-8";
    private final static String AES = "AES";

    /**
     * encrypt   AES/ECB/PKCS5Padding
     *
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String sSrc, String sKey) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return encrypt(sSrc, sKey, CHARSET_ENCODE);
    }

    /**
     * encrypt   AES/ECB/PKCS5Padding
     *
     * @param sSrc
     * @param sKey
     * @param charsetName
     * @return
     * @throws Exception
     */
    public static String encrypt(String sSrc, String sKey, String charsetName) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (sKey == null) {
            throw new InvalidKeyException("key can not be null");
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            throw new InvalidKeyException("The length of key is less than 16 bits!");
        }
        byte[] raw = sKey.getBytes(charsetName);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        //"算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(CHARSET_ENCODE));
        //此处使用BASE64做转码功能，同时能起到2次加密的作用。
        return new Base64().encodeToString(encrypted);
    }


    /**
     * decrypt   AES/ECB/PKCS5Padding
     *
     * @param sSrc need decrypt
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String sSrc, String sKey) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return decrypt(sSrc, sKey, CHARSET_ENCODE);
    }


    /**
     * decrypt   AES/ECB/PKCS5Padding
     *
     * @param sSrc
     * @param sKey
     * @param charsetName
     * @return
     * @throws Exception
     */
    public static String decrypt(String sSrc, String sKey, String charsetName) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // 判断Key是否正确
        if (sKey == null) {
            throw new InvalidKeyException("key can not be null");
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            throw new InvalidKeyException("The length of key is less than 16 bits!");
        }
        byte[] raw = sKey.getBytes(charsetName);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        //先用base64解密
        byte[] encrypted1 = new Base64().decode(sSrc);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original, CHARSET_ENCODE);
        return originalString;
    }


    public static void main(String[] args) throws Exception {

        System.out.println(encrypt("20000.000000", "1234567812345678"));
        System.out.println(decrypt("2WqkK1kVGp6bWSX8nZWtrw==","1234567812345678"));
        System.out.println(decrypt(encrypt("2WqkK1kVGp6bWSX8nZWtrw==", "1234567812345678"), "1234567812345678"));
    }

}
