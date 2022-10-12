package org.dows.crypto.api.annotation;

import org.dows.crypto.api.enums.CryptoType;

import java.lang.annotation.*;

/**
 * 签名注解
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCrypto(encryptType = CryptoType.SIGNATURE, decryptType = CryptoType.SIGNATURE)
public @interface SignatureCrypto {


    /**
     * 自定义超时时间 （优先）
     * 小于等于 "0" 不限制
     *
     * @return
     **/
    long timeout() default 0L;

    /**
     * 自定义签名 秘钥（优先）
     *
     * @return
     **/
    String secretKey() default "";
}
