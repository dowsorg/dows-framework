package org.dows.framework.rest.property;

import lombok.Data;
import org.dows.framework.rest.annotation.Retry;
import org.dows.framework.rest.interceptor.RestInterceptor;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/3/2022
 */
@Data
public abstract class RestProperty {

    /**
     * @LogProperty 是否启用全局重试，启用的话，所有HTTP请求都会自动重试。
     * 否则的话，只有被 {@link Retry}标注的接口才会执行重试。
     * 接口上Retry注解属性优先于全局配置。
     * @BalanceProperty 默认关闭负载
     * @RetryProperty 默认关闭重试
     */
    private boolean enable = false;
    /**
     * 拦截器
     */
    private Class<? extends RestInterceptor> interceptor;
}
