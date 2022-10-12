package org.dows.crypto.api.annotation;

import org.dows.crypto.api.enums.CryptoType;
import org.dows.crypto.api.enums.EncodingType;
import org.dows.crypto.api.enums.SymmetricType;

import java.lang.annotation.*;

/**
 * 对称性算法注解 AES，DES，DESede(3DES) 等
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.SYMMETRIC, decryptType = CryptoType.SYMMETRIC)
public @interface SymmetricCrypto {

    /**
     * 加密/解密类型
     * 默认 AES_ECB_PKCS5_PADDING
     *
     * @return SymmetricType 对称性 加密/解密 类型枚举
     */

    SymmetricType type() default SymmetricType.AES_ECB_PKCS5_PADDING;

    /**
     * 自定义加密 秘钥（优先）
     *
     * @return
     */
    String SecretKey() default "";

    /**
     * 编码类型
     * 默认为配置文件配置的编码类型
     *
     * @return EncodingType 编码 类型枚举
     */
    EncodingType encodingType() default EncodingType.DEFAULT;
}
