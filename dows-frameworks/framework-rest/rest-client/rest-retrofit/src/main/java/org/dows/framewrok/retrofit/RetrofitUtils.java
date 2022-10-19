package org.dows.framewrok.retrofit;

import cn.hutool.extra.spring.SpringUtil;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import org.dows.framework.api.exceptions.RestException;
import org.dows.framework.api.status.RestStatusCode;
import org.dows.framework.rest.RestConnectionPool;
import org.dows.framework.rest.annotation.*;
import org.dows.framework.rest.degrade.DegradeRule;
import org.dows.framework.rest.degrade.DegradeRuleInitializer;
import org.dows.framework.rest.degrade.DegradeStrategy;
import org.dows.framework.rest.degrade.FallbackFactory;
import org.dows.framework.rest.parser.ResourceNameParser;
import org.dows.framework.rest.property.RestProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class RetrofitUtils {

    public static final String GZIP = "gzip";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String IDENTITY = "identity";
    private static final Charset UTF8 = Charset.forName("UTF-8");


    private RetrofitUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    public static String readResponseBody(Response response) {
        try {
            Headers headers = response.headers();
            if (bodyHasUnknownEncoding(headers)) {
                return null;
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            long contentLength = responseBody.contentLength();

            BufferedSource source = responseBody.source();
            // Buffer the entire body.
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();

            if (GZIP.equalsIgnoreCase(headers.get(CONTENT_ENCODING))) {
                try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                }
            }
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (contentLength != 0) {
                return buffer.clone().readString(charset);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RestException(RestStatusCode.ReadResponseBodyException, e);
        }
    }

    public static Method findOkHttpClientBuilderMethod(Class<?> retrofitClientInterfaceClass) {
        Method[] methods = retrofitClientInterfaceClass.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())
                    && method.isAnnotationPresent(OkHttpClientBuilder.class)
                    && method.getReturnType().equals(OkHttpClient.Builder.class)) {
                return method;
            }
        }
        return null;
    }


    /**
     * @param restClientInterfaceClass
     * @return
     */
    public static List<Annotation> getInterceptAnnotations(Class<?> restClientInterfaceClass) {
        Annotation[] classAnnotations = restClientInterfaceClass.getAnnotations();
        // 找出被@InterceptMark标记的注解。Find the annotation marked by @InterceptMark
        List<Annotation> interceptAnnotations = new ArrayList<>();
        for (Annotation classAnnotation : classAnnotations) {
            Class<? extends Annotation> annotationType = classAnnotation.annotationType();
            if (annotationType.isAnnotationPresent(InterceptMark.class)) {
                interceptAnnotations.add(classAnnotation);
            }
            if (classAnnotation instanceof Intercepts) {
                Intercept[] value = ((Intercepts) classAnnotation).value();
                for (Intercept intercept : value) {
                    interceptAnnotations.add(intercept);
                }
            }
        }
        return interceptAnnotations;
    }


    /**
     * 加载降级规则
     *
     * @param retrofitInterface
     * @param resourceNameParser
     * @param environment
     * @param degradeRuleInitializer
     */
    public static void loadDegradeRules(Class<?> retrofitInterface, ResourceNameParser resourceNameParser,
                                        Environment environment, DegradeRuleInitializer degradeRuleInitializer) {
        // 读取熔断配置
        Method[] methods = retrofitInterface.getMethods();
        for (Method method : methods) {
            if (method.isDefault()) {
                continue;
            }
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                continue;
            }
            // 获取熔断配置
            Degrade degrade;
            if (method.isAnnotationPresent(Degrade.class)) {
                degrade = method.getAnnotation(Degrade.class);
            } else {
                degrade = retrofitInterface.getAnnotation(Degrade.class);
            }

            if (degrade == null) {
                continue;
            }

            DegradeStrategy degradeStrategy = degrade.degradeStrategy();
            String resourceName = resourceNameParser.parseResourceName(method, environment);

            DegradeRule degradeRule = new DegradeRule();
            degradeRule.setCount(degrade.count());
            degradeRule.setDegradeStrategy(degradeStrategy);
            degradeRule.setTimeWindow(degrade.timeWindow());
            degradeRule.setResourceName(resourceName);
            try {
                degradeRuleInitializer.addDegradeRule(degradeRule);
            } catch (Exception e) {
                throw new RestException(e.getMessage());
            }
        }
    }


    /**
     * RetrofitInterface 接口检查
     *
     * @param retrofitInterface
     * @param restClient
     * @param restProperties
     */
    public static void checkRetrofitInterface(Class<?> retrofitInterface, RestClient restClient, RestProperties restProperties) {
        // check class type
        Assert.isTrue(retrofitInterface.isInterface(), "@RestClient can only be marked on the interface type!");
        Method[] methods = retrofitInterface.getMethods();

        Assert.isTrue(StringUtils.hasText(restClient.baseUrl()) || StringUtils.hasText(restClient.serviceId()),
                "@RestClient's baseUrl and serviceId must be configured with one！");

        for (Method method : methods) {
            Class<?> returnType = method.getReturnType();
            if (method.isAnnotationPresent(OkHttpClientBuilder.class)) {
                Assert.isTrue(returnType.equals(OkHttpClient.Builder.class), "For methods annotated by @OkHttpClientBuilder, the return value must be OkHttpClient.Builder！");
                Assert.isTrue(Modifier.isStatic(method.getModifiers()), "only static method can annotated by @OkHttpClientBuilder!");
                continue;
            }

            Assert.isTrue(!void.class.isAssignableFrom(returnType),
                    "The void keyword is not supported as the return type, please use java.lang.Void！ method=" + method);
            if (restProperties.isDisableVoidReturnType()) {
                Assert.isTrue(!Void.class.isAssignableFrom(returnType),
                        "Configured to disable Void as the return value, please specify another return type!method=" + method);
            }
        }

        Class<?> fallbackClass = restClient.fallback();
        if (!void.class.isAssignableFrom(fallbackClass)) {
            Assert.isTrue(retrofitInterface.isAssignableFrom(fallbackClass), "The fallback type must implement the current interface！the fallback type is " + fallbackClass);
            Object fallback = SpringUtil.getBean(fallbackClass);
            Assert.notNull(fallback, "fallback  must be a valid spring bean! the fallback class is " + fallbackClass);
        }

        Class<?> fallbackFactoryClass = restClient.fallbackFactory();
        if (!void.class.isAssignableFrom(fallbackFactoryClass)) {
            Assert.isTrue(FallbackFactory.class.isAssignableFrom(fallbackFactoryClass), "The fallback factory type must implement FallbackFactory！the fallback factory is " + fallbackFactoryClass);
            Object fallbackFactory = SpringUtil.getBean(fallbackFactoryClass);
            Assert.notNull(fallbackFactory, "fallback factory  must be a valid spring bean! the fallback factory class is " + fallbackFactoryClass);
        }
    }

    /**
     * @param restClient
     * @param restProperties
     * @param restConnectionPool
     * @return
     */
    public static OkHttpClient.Builder createPoorOkHttpClientBuilder(RestClient restClient, RestProperties restProperties,
                                                                     RestConnectionPool restConnectionPool) {
        final int connectTimeoutMs = restClient.connectionTimeout() == -1 ?
                restProperties.getGlobalConnectTimeout() : restClient.connectionTimeout();
        final int readTimeoutMs = restClient.readTimeout() == -1 ?
                restProperties.getGlobalReadTimeout() : restClient.readTimeout();
        final int writeTimeoutMs = restClient.writeTimeout() == -1 ?
                restProperties.getGlobalWriteTimeout() : restClient.writeTimeout();
        final int callTimeoutMs = restClient.callTimeout() == -1 ?
                restProperties.getGlobalCallTimeout() : restClient.callTimeout();
        ConnectionPool connectionPool = new ConnectionPool(restConnectionPool.getMaxIdleConnections(),
                restConnectionPool.getKeepAliveDuration(), restConnectionPool.getTimeUnit());
        // 创建okhttp client
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
                .callTimeout(callTimeoutMs, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(restClient.retryOnConnectionFailure())
                .followRedirects(restClient.followRedirects())
                .followSslRedirects(restClient.followSslRedirects())
                .pingInterval(restClient.pingInterval(), TimeUnit.MILLISECONDS)
                .connectionPool(connectionPool);
    }


    private static boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get(CONTENT_ENCODING);
        return contentEncoding != null
                && !IDENTITY.equalsIgnoreCase(contentEncoding)
                && !GZIP.equalsIgnoreCase(contentEncoding);
    }


}

