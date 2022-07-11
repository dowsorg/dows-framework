package org.dows.framework.rest.annotation;

import org.dows.framework.rest.RestClientTyp;
import org.dows.framework.rest.config.RestSetting;
import org.dows.framework.rest.factory.CallAdapterFactory;
import org.dows.framework.rest.factory.ConverterFactory;
import org.dows.framework.rest.log.LogLevel;
import org.dows.framework.rest.log.LogStrategy;

import java.lang.annotation.*;

/**
 * 远程服务客户端注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestClient {

    RestClientTyp value() default RestClientTyp.RETROFIT;

    /**
     * 绝对URL（协议是必需的）。
     * 可以指定为属性键，例如：$ {propertyKey}。
     * 如果baseUrl没有配置，则必须配置serviceId以及，path可选配置。
     *
     * @return baseUrl
     */
    String baseUrl() default "";

    /**
     * service id
     */
    String serviceId() default "";

    /**
     * 子路径
     */
    String path() default "";

    /**
     * 连接失败重试
     */
    boolean retryOnConnectionFailure() default true;

    /**
     * 数据传输处理时间
     */
    int socketTimeout() default RestSetting.DEFAULT_SOCKET_TIMEOUT;

    /**
     * 建立连接的timeout时间
     */
    int connectionTimeout() default RestSetting.DEFAULT_CONN_TIMEOUT;

    /**
     * connection pool name
     */
    String poolName() default "default";


    /**
     *
     */
    int readTimeout() default -1;

    /**
     *
     */
    int writeTimeout() default -1;


    /**
     *
     */
    int callTimeout() default -1;

    /**
     * Sets the interval between HTTP/2 and web socket pings initiated by this client.
     * Use this to automatically send ping frames until either the connection fails or it is closed.
     * This keeps the connection alive and may detect connectivity failures.
     *
     * @return pingInterval
     */
    int pingInterval() default 0;


    /**
     * Fallback class for the specified retrofit client interface. The fallback class must
     * implement the interface annotated by this annotation and be a valid spring bean.
     */
    Class<?> fallback() default void.class;


    /**
     *
     */
    Class<?> fallbackFactory() default void.class;


    /**
     * 针对当前接口是否启用日志打印
     */
    boolean enableLog() default true;

    /**
     * 日志打印级别，支持的日志级别参见{@link LogLevel}
     * 如果为NULL，则取全局日志打印级别
     *
     * @return logLevel
     */
    LogLevel logLevel() default LogLevel.NULL;

    /**
     * 日志打印策略，支持的日志打印策略参见{@link LogStrategy}
     * 如果为NULL，则取全局日志打印策略
     *
     * @return logStrategy
     */
    LogStrategy logStrategy() default LogStrategy.NULL;

    /**
     *
     */
    boolean validateEagerly() default false;

    /**
     * Configure this client to allow protocol redirects from HTTPS to HTTP and from HTTP to HTTPS.
     * Redirects are still first restricted by followRedirects. Defaults to true.
     *
     * @return followSslRedirects
     */
    boolean followSslRedirects() default true;

    /**
     * Configure this client to follow redirects. If unset, redirects will be followed.
     *
     * @return followRedirects
     */
    boolean followRedirects() default true;


    /**
     * 适用于当前接口的转换器工厂，优先级比全局转换器工厂更高。转换器实例优先从Spring容器获取，如果没有获取到，则反射创建。
     */
    Class<? extends ConverterFactory>[] converterFactories() default {};

    /**
     * 适用于当前接口的调用适配器工厂，优先级比全局调用适配器工厂更高。转换器实例优先从Spring容器获取，如果没有获取到，则反射创建。
     */
    Class<? extends CallAdapterFactory>[] callAdapterFactories() default {};

    /**
     * 当前接口采用的错误解码器，当请求发生异常或者收到无效响应结果的时候，将HTTP相关信息解码到异常中，无效响应由业务自己判断。
     * 一般情况下，每个服务对应的无效响应各不相同，可以自定义对应的{@link ErrorDecoder}，然后配置在这里。
     *
     * @return ErrorDecoder
     */
    /*    Class<? extends ErrorDecoder> errorDecoder() default DefaultErrorDecoder.class;*/


}
