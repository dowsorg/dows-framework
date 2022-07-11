package org.dows.framework.rest.degrade;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
public enum DegradeStrategy {

    /**
     * average RT
     */
    AVERAGE_RT,

    /**
     * exception ratio
     */
    EXCEPTION_RATIO,
}
