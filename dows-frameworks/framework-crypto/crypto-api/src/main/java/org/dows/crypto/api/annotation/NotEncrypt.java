package org.dows.crypto.api.annotation;

import java.lang.annotation.*;

/**
 * 忽略 加密 注解
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEncrypt {
}
