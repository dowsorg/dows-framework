package org.dows.framework.rest.property;

import lombok.Data;
import org.dows.framework.rest.RestConnectionPool;
import org.dows.framework.rest.factory.CallAdapterFactory;
import org.dows.framework.rest.factory.ConverterFactory;
import org.dows.framework.rest.interceptor.RestInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/3/2022
 */
@Data
@ConfigurationProperties(prefix = "rest")
public class RestProperties {

    private static final String DEFAULT_POOL = "default";

    /**
     *
     */
    private boolean disableVoidReturnType = false;

    /**
     * 全局连接超时时间
     */
    private int globalConnectTimeout = 10_000;

    /**
     * 全局读取超时时间
     */
    private int globalReadTimeout = 10_000;

    /**
     * 全局写入超时时间
     */
    private int globalWriteTimeout = 10_000;

    /**
     * 全局完整调用超时时间
     */
    private int globalCallTimeout = 0;
    /**
     * 连接池配置
     */
    @NestedConfigurationProperty
    private Map<String, RestConnectionPool> pool = new LinkedHashMap<>();

    /**
     * 重试配置
     */
    @NestedConfigurationProperty
    private RetryProperty retry = new RetryProperty();

    /**
     * 熔断降级配置
     */
    @NestedConfigurationProperty
    private DegradeProperty degrade = new DegradeProperty();


    /**
     * 日志配置
     */
    @NestedConfigurationProperty
    private LogProperty log = new LogProperty();

    /**
     * 负载均衡配置
     */
    @NestedConfigurationProperty
    private BalanceProperty balance = new BalanceProperty();

    /**
     * 过滤拦截
     */
    @NestedConfigurationProperty
    private FilterProperty filter = new FilterProperty();

    /**
     * 过滤拦截
     */
    @NestedConfigurationProperty
    private SecurityProperty security = new SecurityProperty();

    /**
     * 全局拦截器
     */
    private List<Class<? extends RestInterceptor>> globalInterceptors;


    /**
     * 全局转换器工厂，转换器实例优先从Spring容器获取，如果没有获取到，则反射创建。
     */
    @SuppressWarnings("unchecked")
    private List<Class<? extends ConverterFactory>> globalConverterFactories;

    /**
     * 全局调用适配器工厂，转换器实例优先从Spring容器获取，如果没有获取到，则反射创建。
     */
    @SuppressWarnings("unchecked")
    private List<Class<? extends CallAdapterFactory>> globalCallAdapterFactories;


    public Map<String, RestConnectionPool> getPool() {
        if (!pool.isEmpty()) {
            return pool;
        }
        pool.put(DEFAULT_POOL, new RestConnectionPool(5, 300, TimeUnit.SECONDS));
        return pool;
    }

}
