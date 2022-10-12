//package org.dows.framework.crypto.handler;
//
//import org.dows.crypto.api.CryptoHandler;
//import org.dows.framework.api.enums.EncryptMode;
//import org.springframework.util.Base64Utils;
//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.SecureRandom;
//
///**
// * 1.BASE64加密/解密
// * 2.MD5(Message Digest Algorithm)加密
// * 3.DES(Data Encryption Standard)对称加密/解密
// * 4.AES（Advanced Encryption Standard） 加密/解密
// * 5.HMAC(Hash Message Authentication Code，散列消息鉴别码)
// * 6.恺撒加密
// * 7.SHA(Secure Hash Algorithm，安全散列算法)
// * 8.RSA 加密/解密
// * 9.PBE 加密/解密
// */
//public class AesHandler implements CryptoHandler {
//
//    @Override
//    public String encode(String value) {
//        try {
//            KeyGenerator keygen = KeyGenerator.getInstance("AES");
//            keygen.init(128, new SecureRandom(secret.getBytes()));
//            SecretKey original_key = keygen.generateKey();
//            byte[] raw = original_key.getEncoded();
//            SecretKey key = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            byte[] byte_AES = cipher.doFinal(b);
//            return Base64Utils.encode(byte_AES);
//        } catch (Exception e) {
//        }
//        return new byte[0];
//    }
//
//    @Override
//    public String decode(String value) {
//        try {
//            KeyGenerator keygen = KeyGenerator.getInstance("AES");
//            keygen.init(128, new SecureRandom(secret.getBytes()));
//            SecretKey original_key = keygen.generateKey();
//            byte[] raw = original_key.getEncoded();
//            SecretKey key = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.DECRYPT_MODE, key);
//            byte[] byte_content = Base64Utils.decode(b);
//            byte[] byte_decode = cipher.doFinal(byte_content);
//            return byte_decode;
//        } catch (Exception e) {
//        }
//        return new byte[0];
//    }
//    @Override
//    public EncryptMode getMode() {
//        return EncryptMode.AES;
//    }
//}
