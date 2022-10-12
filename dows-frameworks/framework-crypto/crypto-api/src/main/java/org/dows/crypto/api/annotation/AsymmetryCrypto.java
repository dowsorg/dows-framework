package org.dows.crypto.api.annotation;


import org.dows.crypto.api.enums.AsymmetryType;
import org.dows.crypto.api.enums.CryptoType;
import org.dows.crypto.api.enums.EncodingType;
import org.dows.crypto.api.enums.RSASignatureType;

import java.lang.annotation.*;

/**
 * 非对称性算法注解（RSA）
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.ASYMMETRY, decryptType = CryptoType.ASYMMETRY)
public @interface AsymmetryCrypto {


    /**
     * 加密/解密类型
     *
     * @return AsymmetryType 非对称性 加密/解密 类型枚举
     * @see AsymmetryType
     **/
    AsymmetryType type() default AsymmetryType.RSA_ECB_PKCS1_PADDING;

    /**
     * 公钥加密，配置该项时将优先使用
     *
     * @return java.lang.String 字符串
     **/
    String publicKey() default "";

    /**
     * 私钥解密，配置该项时将优先使用
     *
     * @return java.lang.String 字符串
     **/
    String privateKey() default "";

    /**
     * @return RSASignatureType
     * @see RSASignatureType
     **/
    RSASignatureType signatureType() default RSASignatureType.MD5withRSA;

    /**
     * 对加密数据签名
     *
     * @return boolean
     **/
    boolean signature() default false;

    /**
     * 验证加密数据签名
     *
     * @return boolean
     **/
    boolean verifySignature() default false;

    /**
     * 秘钥编码类型
     * <p>
     * 默认为配置文件配置的编码类型
     *
     * @return EncodingType 编码 类型枚举
     * @see EncodingType
     **/
    EncodingType keyEncodingType() default EncodingType.DEFAULT;

    /**
     * 内容编码类型
     * <p>
     * 默认为配置文件配置的编码类型
     *
     * @return EncodingType 编码 类型枚举
     * @see EncodingType
     **/
    EncodingType contentEncodingType() default EncodingType.DEFAULT;
}
