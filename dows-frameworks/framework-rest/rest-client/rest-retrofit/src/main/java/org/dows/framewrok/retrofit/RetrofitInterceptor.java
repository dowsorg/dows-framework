package org.dows.framewrok.retrofit;

import lombok.SneakyThrows;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.dows.framework.rest.annotation.Retry;
import org.dows.framework.rest.interceptor.RestInterceptor;
import org.dows.framework.rest.log.LogLevel;
import org.dows.framework.rest.parser.ResourceNameParser;
import org.dows.framework.rest.property.FilterProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import retrofit2.Invocation;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/3/2022
 */
public interface RetrofitInterceptor extends Interceptor,
        RestInterceptor<Response, Interceptor.Chain, Interceptor> {

    @SneakyThrows
    @NotNull
    @Override
    default Response intercept(@NotNull Chain chain) throws IOException {
        return doIntercept(chain);
    }

    /**
     * 获取资源名称
     *
     * @param request
     * @param resourceNameParser
     * @param environment
     * @return
     */
    default String getResourceName(Request request, ResourceNameParser resourceNameParser, Environment environment) {
        Invocation invocation = request.tag(Invocation.class);
        assert invocation != null;
        Method method = invocation.method();
        return resourceNameParser.parseResourceName(method, environment);
    }

    /**
     * 获取retry
     *
     * @param request
     * @return
     */
    default Retry getRetry(@NotNull Request request) {
        Invocation invocation = request.tag(Invocation.class);
        assert invocation != null;
        // 获取重试配置注解
        return getRetry(invocation.method());
    }

    /**
     * 获取crypt
     *
     * @param request
     * @return
     */
/*    default Crypto getCrypto(@NotNull Request request) {
        Invocation invocation = request.tag(Invocation.class);
        assert invocation != null;
        return getCrypto(invocation.method());
    }*/

    /**
     * 过滤
     *
     * @param request
     * @param filterProperty
     * @return
     */
    default Boolean filter(@NotNull Request request, FilterProperty filterProperty) {
        String path = request.url().encodedPath();
        if (isMatch(filterProperty.getExcludes(), path)) {
            return true;
        }
        if (!isMatch(filterProperty.getIncludes(), path)) {
            return true;
        }
        return false;
    }


    default HttpLoggingInterceptor.Logger httpLoggingInterceptor(LogLevel level, Logger log) {
        if (level == LogLevel.DEBUG) {
            return log::debug;
        } else if (level == LogLevel.ERROR) {
            return log::error;
        } else if (level == LogLevel.INFO) {
            return log::info;
        } else if (level == LogLevel.WARN) {
            return log::warn;
        }
        throw new UnsupportedOperationException("We don't support this log level currently.");
    }


}
