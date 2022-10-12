package org.dows.crypto.api.annotation;

import org.dows.crypto.api.enums.CryptoType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 加密、解密 标记注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface ApiCrypto {

    /**
     * 加密 类型（枚举）
     *
     * @return CryptoType 加密解密 类型枚举（用于描述）
     */
    CryptoType encryptType();


    /**
     * 解密 类型（枚举）
     *
     * @return CryptoType 加密解密 类型枚举（用于描述）
     */
    CryptoType decryptType();
}
