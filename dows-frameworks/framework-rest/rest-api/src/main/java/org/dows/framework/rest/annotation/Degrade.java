package org.dows.framework.rest.annotation;

import org.dows.framework.rest.degrade.DegradeStrategy;

import java.lang.annotation.*;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Degrade {

    /**
     * RT threshold or exception ratio threshold count.
     */
    double count();

    /**
     * Degrade recover timeout (in seconds) when degradation occurs.
     */
    int timeWindow() default 5;

    /**
     * Degrade strategy (0: average RT, 1: exception ratio).
     */
    DegradeStrategy degradeStrategy() default DegradeStrategy.AVERAGE_RT;
}
