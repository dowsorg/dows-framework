package org.dows.crypto.api.annotation;

import org.dows.crypto.api.enums.CryptoType;
import org.dows.crypto.api.enums.DigestsType;
import org.dows.crypto.api.enums.EncodingType;

import java.lang.annotation.*;

/**
 * 摘要算法注解（MD、SHA）
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.DIGEST, decryptType = CryptoType.DIGEST)
public @interface DigestsCrypto {

    /**
     * 摘要加密类型
     *
     * @return DigestsType 摘要加密 类型枚举
     */
    DigestsType type() default DigestsType.MD5;

    /**
     * 编码类型
     * 默认为配置文件配置的编码类型
     *
     * @return EncodingType 编码 类型枚举
     */
    EncodingType encodingType() default EncodingType.DEFAULT;
}
