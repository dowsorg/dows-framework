package org.dows.framework.crypto;

import org.dows.framework.api.CryptoHandler;

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
public class RsaHandler implements CryptoHandler {

    @Override
    public String encrypt(String value) {
        return null;
    }

    @Override
    public String decrypt(String value) {
        return null;
    }
}
