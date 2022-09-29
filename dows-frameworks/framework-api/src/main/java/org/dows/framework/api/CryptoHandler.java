package org.dows.framework.api;


import org.dows.framework.api.enums.EncryptMode;

/**
 * 1.BASE64加密/解密
 * 2.MD5(Message Digest Algorithm)加密
 * 3.DES(Data Encryption Standard)对称加密/解密
 * 4.AES（Advanced Encryption Standard） 加密/解密
 * 5.HMAC(Hash Message Authentication Code，散列消息鉴别码)
 * 6.恺撒加密
 * 7.SHA(Secure Hash Algorithm，安全散列算法)
 * 8.RSA 加密/解密
 * 9.PBE 加密/解密
 */
public interface CryptoHandler {

    /**
     * 加密
     *
     * @return
     */
    String encrypt(String value);

    /**
     * 解密
     *
     * @return
     */
    String decrypt(String value);

    EncryptMode getMode();

}
