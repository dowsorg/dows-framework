//package org.dows.framework.crypto.handler;
//
//import org.dows.crypto.api.CryptoHandler;
//import org.dows.crypto.api.RsaKeyEntity;
//import org.dows.framework.api.enums.EncryptMode;
//import org.springframework.util.Base64Utils;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import java.io.ByteArrayOutputStream;
//import java.security.*;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.HashMap;
//import java.util.Map;
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
//public class RsaHandler implements CryptoHandler {
//
//    private static final String KEY_ALGORITHM = "RSA";
//    private static final int KEY_SIZE = 512;
//    private static final String PUBLIC_KEY = "RSAPublicKey";
//    private static final String PRIVATE_KEY = "RSAPrivateKey";
//    private static final int MAX_ENCODE_BLOCK = (KEY_SIZE / 8) - 11;
//    private static final int MAX_DECODE_BLOCK = KEY_SIZE / 8;
//    private String publicKey;
//    private String privateKey;
//
//    public void setPublicKey(String publicKey) {
//        this.publicKey = publicKey;
//    }
//
//    public String getPublicKey() {
//        return publicKey;
//    }
//
//    public void setPrivateKey(String privateKey) {
//        this.privateKey = privateKey;
//    }
//
//    @Override
//    public String encode(String value) {
//        try {
//            byte[] bytes = encryptByPrivateKey(content, Base64Utils.decodeFromString(privateKey));
//            return bytes;
//        } catch (Exception e) {
//            e.printStackTrace();
//            //throw new CryptoException("rsa加密错误", e);
//            throw new RuntimeException("rsa加密错误", e);
//        }
//    }
//
//    @Override
//    public String decode(String value) {
//        try {
//            byte[] bytes = decryptByPrivateKey(content, Base64Utils.decodeFromString(privateKey));
//            return bytes;
//        } catch (Exception e) {
//            e.printStackTrace();
//            //throw new CryptoException("rsa解密错误", e);
//            throw new RuntimeException("rsa解密错误", e);
//        }
//    }
//
//
//    @Override
//    public EncryptMode getMode() {
//        return EncryptMode.RSA;
//    }
//
//
//
//
//    public static RsaKeyEntity getRsaKeys() throws Exception {
//        Map<String, Object> keyMap = initKey();
//        byte[] publicKey = getPublicKey(keyMap);
//        byte[] privateKey = getPrivateKey(keyMap);
//        RsaKeyEntity rsaKeyEntity = new RsaKeyEntity();
//        rsaKeyEntity.setPublicKey(Base64Utils.encodeToString(publicKey));
//        rsaKeyEntity.setPrivateKey(Base64Utils.encodeToString(privateKey));
//        return rsaKeyEntity;
//    }
//
//    private static Map<String, Object> initKey() throws Exception {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
//        keyPairGenerator.initialize(KEY_SIZE);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        Map<String, Object> keyMap = new HashMap<String, Object>();
//        keyMap.put(PUBLIC_KEY, publicKey);
//        keyMap.put(PRIVATE_KEY, privateKey);
//        return keyMap;
//    }
//
//    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
//        byte[] encryptedData = new byte[0];
//        if (data.length == 0) {
//            return encryptedData;
//        }
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
//            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
//            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//
//            encryptedData = doFinal(data, cipher, out, MAX_ENCODE_BLOCK);
//        }
//        return encryptedData;
//    }
//
//    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
//        byte[] encryptedData = new byte[0];
//        if (data.length == 0) {
//            return encryptedData;
//        }
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
//            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
//            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//            encryptedData = doFinal(data, cipher, out, MAX_ENCODE_BLOCK);
//        }
//        return encryptedData;
//    }
//
//    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
//        byte[] encryptedData = new byte[0];
//        if (data.length == 0) {
//            return encryptedData;
//        }
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
//            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
//            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//            cipher.init(Cipher.DECRYPT_MODE, privateKey);
//
//            encryptedData = doFinal(data, cipher, out, MAX_DECODE_BLOCK);
//        }
//        return encryptedData;
//    }
//
//    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
//        byte[] encryptedData = new byte[0];
//        if (data.length == 0) {
//            return encryptedData;
//        }
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
//            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
//            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//            cipher.init(Cipher.DECRYPT_MODE, pubKey);
//
//            encryptedData = doFinal(data, cipher, out, MAX_DECODE_BLOCK);
//        }
//        return encryptedData;
//    }
//
//    private static byte[] getPrivateKey(Map<String, Object> keyMap) {
//        Key key = (Key) keyMap.get(PRIVATE_KEY);
//        return key.getEncoded();
//    }
//
//    private static byte[] getPublicKey(Map<String, Object> keyMap) throws Exception {
//        Key key = (Key) keyMap.get(PUBLIC_KEY);
//        return key.getEncoded();
//    }
//
//    private static byte[] doFinal(byte[] data, Cipher cipher, ByteArrayOutputStream out, int MAX_BLOCK) throws BadPaddingException, IllegalBlockSizeException {
//        int inputLen = data.length;
//        int offSet = 0;
//        byte[] cache;
//        int i = 0;
//        while (inputLen - offSet > 0) {
//            if (inputLen - offSet > MAX_BLOCK) {
//                cache = cipher.doFinal(data, offSet, MAX_BLOCK);
//            } else {
//                cache = cipher.doFinal(data, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * MAX_BLOCK;
//        }
//        return out.toByteArray();
//    }
//}
