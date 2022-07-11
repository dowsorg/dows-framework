package org.dows.framework.rest;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/2/2022
 */
@Data
public class RestConnectionPool {

    private int maxIdleConnections;
    private long keepAliveDuration;
    private TimeUnit timeUnit;

    public RestConnectionPool(int maxIdleConnections, long keepAliveSecond, TimeUnit timeUnit) {
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDuration = keepAliveSecond;
        this.timeUnit = timeUnit;
    }
}
