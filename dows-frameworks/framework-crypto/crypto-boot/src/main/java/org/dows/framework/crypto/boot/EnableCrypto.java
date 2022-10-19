package org.dows.framework.crypto.boot;

import org.dows.framework.crypto.boot.advice.DecryptRequestBodyAdvice;
import org.dows.framework.crypto.boot.advice.EncryptResponseBodyAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * ApiCrypto 自动装配注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EncryptResponseBodyAdvice.class, DecryptRequestBodyAdvice.class, CryptoConfig.class})
public @interface EnableCrypto {
}
