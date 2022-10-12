package org.dows.crypto.api.annotation;

import org.dows.crypto.api.enums.CryptoType;
import org.dows.crypto.api.enums.EncodingType;

import java.lang.annotation.*;

/**
 * 编码注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.ENCODING, decryptType = CryptoType.ENCODING)
public @interface EncodingCrypto {

    /**
     * 编码类型
     * 默认为配置文件配置的编码类型
     *
     * @return EncodingType 编码 类型枚举
     **/
    EncodingType encodingType() default EncodingType.DEFAULT;
}
