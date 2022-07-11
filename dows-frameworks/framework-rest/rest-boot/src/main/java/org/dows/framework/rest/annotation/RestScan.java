package org.dows.framework.rest.annotation;

import org.dows.framework.rest.RestClientScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 1/11/2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RestClientScannerRegistrar.class)
public @interface RestScan {

    /**
     * 扫包路径
     *
     * @return
     */
    String[] value() default {};


    /**
     * 扫包路径
     *
     * @return
     */
    String[] basePackages() default {};


    /**
     * 扫class
     *
     * @return
     */

    Class<?>[] basePackageClasses() default {};
}
