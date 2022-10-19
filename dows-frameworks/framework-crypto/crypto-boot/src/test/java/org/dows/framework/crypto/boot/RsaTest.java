package org.dows.framework.crypto.boot;

import org.dows.crypto.api.enums.EncodingType;
import org.dows.crypto.api.enums.RSASignatureType;
import org.dows.framework.crypto.sdk.util.CryptoUtil;
import org.dows.framework.crypto.sdk.util.EncodingUtil;
import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

/**
 * RSA 测试
 */
public class RsaTest {

    /**
     * 生成密钥
     */
    @Test
    public void generator() throws Exception {

        KeyPair keyPair = CryptoUtil.generatorRsaKeyPair(1024);

        String privateKey = new String(Base64Utils.encode(keyPair.getPrivate().getEncoded()));

        String publicKey = new String(Base64Utils.encode(keyPair.getPublic().getEncoded()));

        System.out.println("---------- privateKey ----------");
        System.out.println(privateKey);

        System.out.println("---------- publicKey ----------");
        System.out.println(publicKey);
    }

    /**
     * 加密、解密
     */
    @Test
    public void rsa() throws Exception {

        String data = "http://wisdomer.tech";

        KeyPair keyPair = CryptoUtil.generatorRsaKeyPair(1024);
        String privateKey = new String(Base64Utils.encode(keyPair.getPrivate().getEncoded()));
        String publicKey = new String(Base64Utils.encode(keyPair.getPublic().getEncoded()));

        System.out.println("---------- privateKey ----------");
        System.out.println(privateKey);
        System.out.println("---------- publicKey ----------");
        System.out.println(publicKey);

        // 加密
        String encryptData = CryptoUtil.asymmetry(
                "RSA",
                "RSA/ECB/PKCS1Padding",
                Cipher.ENCRYPT_MODE,
                publicKey,
                EncodingType.BASE64,
                data,
                EncodingType.BASE64,
                StandardCharsets.UTF_8
        );

        // 签名
        byte[] bytes = CryptoUtil.resSignature(
                RSASignatureType.MD5withRSA,
                EncodingUtil.decode(EncodingType.BASE64, encryptData, StandardCharsets.UTF_8),
                EncodingUtil.decode(EncodingType.BASE64, privateKey, StandardCharsets.UTF_8)
        );

        String signature = Base64Utils.encodeToString(bytes);

        // 验证签名
        boolean verify = CryptoUtil.resSignatureVerify(
                RSASignatureType.MD5withRSA,
                EncodingUtil.decode(EncodingType.BASE64, encryptData, StandardCharsets.UTF_8),
                EncodingUtil.decode(EncodingType.BASE64, signature, StandardCharsets.UTF_8),
                EncodingUtil.decode(EncodingType.BASE64, publicKey, StandardCharsets.UTF_8)
        );


        // 解密
        String decryptData = CryptoUtil.asymmetry(
                "RSA",
                "RSA/ECB/PKCS1Padding",
                Cipher.DECRYPT_MODE,
                privateKey,
                EncodingType.BASE64,
                encryptData,
                EncodingType.BASE64,
                StandardCharsets.UTF_8
        );

        System.out.println("验签结果：" + verify);
        System.out.println("加密前数据：" + data);
        System.out.println("加密后数据：" + encryptData);
        System.out.println("解密后数据：" + decryptData);
        System.out.println("对比结果：" + data.equals(decryptData));
    }
}
