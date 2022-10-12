package org.dows.crypto.api.annotation;

import java.lang.annotation.*;

/**
 * 忽略 解密/解密
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@NotDecrypt
@NotEncrypt
public @interface NotCrypto {
}
