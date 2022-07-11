package org.dows.framewrok.retrofit.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.dows.framework.rest.interceptor.InterceptTyp;
import org.dows.framework.rest.log.LogLevel;
import org.dows.framework.rest.log.LogStrategy;
import org.dows.framework.rest.property.LogProperty;
import org.dows.framework.rest.property.RestProperties;
import org.dows.framewrok.retrofit.RetrofitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/1/2022
 */
@Slf4j
public class RetrofitLoggingInterceptor implements RetrofitInterceptor {

    private HttpLoggingInterceptor httpLoggingInterceptor;


    /**
     * 默认全局配置
     *
     * @param restProperties
     */
    @Autowired
    public RetrofitLoggingInterceptor(RestProperties restProperties) {
        LogProperty logProperty = restProperties.getLog();
        LogLevel logLevel = logProperty.getLogLevel();
        LogStrategy logStrategy = logProperty.getLogStrategy();
        settingLog(logLevel, logStrategy);
    }

    /**
     * 动态配置
     *
     * @param logLevel
     * @param logStrategy
     */
    public RetrofitLoggingInterceptor(LogLevel logLevel, LogStrategy logStrategy) {
        settingLog(logLevel, logStrategy);
    }


    private void settingLog(LogLevel logLevel, LogStrategy logStrategy) {
        HttpLoggingInterceptor.Logger logger = httpLoggingInterceptor(logLevel, log);
        httpLoggingInterceptor = new HttpLoggingInterceptor(logger);
        String name = logStrategy.name();
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.valueOf(name);
        httpLoggingInterceptor.setLevel(level);
    }

    @Override
    public InterceptTyp intetceptTyp() {
        return InterceptTyp.LOGGING;
    }


    @Override
    public Response doIntercept(Chain chain) throws IOException {
        return httpLoggingInterceptor.intercept(chain);
    }

}
