package org.dows.framework.rest.interceptor;

import org.dows.framework.rest.annotation.Retry;
import org.dows.framework.rest.balance.NoValidServiceInstanceBalance;
import org.dows.framework.rest.balance.ServiceBalance;
import org.dows.framework.rest.degrade.DegradeType;
import org.dows.framework.rest.property.DegradeProperty;
import org.dows.framework.rest.retry.RetryRule;
import org.slf4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
public interface RestInterceptor<R, T, I> {
    /**
     * 最大重试次数
     */
    int LIMIT_RETRIES = 100;
    /**
     * 缓存重拾方法
     */
    Map<String, Retry> RETRYS = new ConcurrentHashMap<>();

    /**
     * 缓存加密,验证签注解
     */
    //Map<String, Crypto> CRYPTOS = new ConcurrentHashMap<>();


    R doIntercept(T t) throws Exception;

    /**
     * 获取拦截器的类型
     *
     * @return
     */
    default InterceptTyp intetceptTyp() {
        return InterceptTyp.CUMSTOM;
    }


    /**
     * 获取当前对象
     *
     * @return
     */
    default I getInterceptor() {
        return (I) this;
    }


    /**
     * 获取pathMatcher,子类可以自定义
     *
     * @return
     */
    default PathMatcher getPathMatcher() {
        return new AntPathMatcher();
    }


    /**
     * 当前http的url路径是否与指定的patterns匹配
     *
     * @param patterns the specified patterns
     * @param path     http URL path
     * @return 匹配结果
     */
    default boolean isMatch(List<String> patterns, String path) {
        if (patterns == null || patterns.size() == 0) {
            return false;
        }
        for (String pattern : patterns) {
            boolean match = getPathMatcher().match(pattern, path);
            if (match) {
                return true;
            }
        }
        return false;
    }


    /**
     * 默认全局开启重试
     *
     * @return
     */
    default boolean getEnableGlobalRetry() {
        return Boolean.FALSE;
    }

    /**
     * 全集加密
     *
     * @return
     */
    default boolean getEnableGlobalCrypto() {
        return Boolean.FALSE;
    }


    /**
     * @param retry
     * @return
     */
    default boolean needRetry(Retry retry) {
        if (getEnableGlobalRetry()) {
            // 开启全局重试的情况下
            // 没配置@Retry，需要重试
            if (retry == null) {
                return true;
            }
            // 配置了@Retry，enable==true，需要重试
            if (retry.enable()) {
                return true;
            }
        } else {
            // 未开启全局重试
            // 配置了@Retry，enable==true，需要重试
            if (retry != null && retry.enable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取retry注解（可以缓存优化一下）
     *
     * @param method
     * @return
     */
    default Retry getRetry(Method method) {
        Retry retry;
        if (method.isAnnotationPresent(Retry.class)) {
            retry = method.getAnnotation(Retry.class);
        } else {
            Class<?> declaringClass = method.getDeclaringClass();
            retry = declaringClass.getAnnotation(Retry.class);
        }
        return retry;
    }


    /**
     * 是否加密
     *
     * @param crypto
     * @return
     */
    /*default boolean needCrypto(Crypto crypto) {
        if (getEnableGlobalCrypto()) {
            // 开启全局加密的情况下，没配置@Crypto，需要加密
            if (crypto != null && crypto.sign()) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * 获取crypto 注解(可以缓存优化一下)
     *
     * @param method
     * @return
     */
    /*default Crypto getCrypto(Method method) {
        Crypto crypto;
        if (method.isAnnotationPresent(Crypto.class)) {
            crypto = method.getAnnotation(Crypto.class);
        } else {
            Class<?> declaringClass = method.getDeclaringClass();
            crypto = declaringClass.getAnnotation(Crypto.class);
        }
        return crypto;
    }*/
    default boolean shouldThrowEx(HashSet<RetryRule> retryRuleSet, Exception e) {
        if (retryRuleSet.contains(RetryRule.OCCUR_EXCEPTION)) {
            return false;
        }
        if (retryRuleSet.contains(RetryRule.OCCUR_IO_EXCEPTION)) {
            return !(e instanceof IOException);
        }
        return true;
    }

    /**
     * @return
     */
    default ServiceBalance getServiceBalance() {
        return new NoValidServiceInstanceBalance();
    }


    /**
     * 初始化balance
     *
     * @param degradeProperty
     */
    default void initBalance(DegradeProperty degradeProperty, Logger log) {
        //DegradeProperty degradeProperty = restProperties.getDegrade();
        if (degradeProperty.isEnable()) {
            DegradeType degradeType = degradeProperty.getDegradeType();
            switch (degradeType) {
                case SENTINEL: {
                    try {
                        Class.forName("com.alibaba.csp.sentinel.SphU");
                    } catch (ClassNotFoundException e) {
                        log.warn("com.alibaba.csp.sentinel not found! No SentinelDegradeInterceptor is set.");
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Not currently supported! degradeType=" + degradeType);
                }
            }
        }
    }

}
