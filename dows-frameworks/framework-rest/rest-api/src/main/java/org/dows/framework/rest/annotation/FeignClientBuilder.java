package org.dows.framework.rest.annotation;

import java.lang.annotation.*;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/29/2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface FeignClientBuilder {
}
