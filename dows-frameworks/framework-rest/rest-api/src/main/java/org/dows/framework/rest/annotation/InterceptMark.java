package org.dows.framework.rest.annotation;

import java.lang.annotation.*;

/**
 * 拦截标记注解
 *
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
public @interface InterceptMark {

}
