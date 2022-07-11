package org.dows.framewrok.retrofit.interceptor;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.dows.framework.rest.annotation.Retry;
import org.dows.framework.rest.interceptor.InterceptTyp;
import org.dows.framework.rest.property.RestProperties;
import org.dows.framework.rest.property.RetryProperty;
import org.dows.framework.rest.retry.RetryRule;
import org.dows.framework.rest.retry.RetryStrategy;
import org.dows.framewrok.retrofit.RetrofitInterceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/1/2022
 */
@Data
@Slf4j
@RequiredArgsConstructor
public class RetrofitRetryInterceptor implements RetrofitInterceptor {
    private final RestProperties restProperties;

    @Override
    public InterceptTyp intetceptTyp() {
        return InterceptTyp.RETRY;
    }

    @Override
    public boolean getEnableGlobalRetry() {
        return restProperties.getRetry().isEnable();
    }

    @Override
    public Response doIntercept(Chain chain) throws Exception {
        Request request = chain.request();
        Retry retry = getRetry(request);
        if (!needRetry(retry)) {
            return chain.proceed(request);
        }
        // 重试
        RetryProperty retryProperty = restProperties.getRetry();
        int maxRetries = retry == null ? retryProperty.getMaxRetries() : retry.maxRetries();
        int interval = retry == null ? retryProperty.getInterval() : retry.interval();
        RetryRule[] retryRules = retry == null ? retryProperty.getRetryRules() : retry.retryRules();
        // 最多重试100次
        maxRetries = Math.min(maxRetries, LIMIT_RETRIES);

        HashSet<RetryRule> retryRuleSet = (HashSet<RetryRule>) Arrays.stream(retryRules).collect(Collectors.toSet());
        RetryStrategy retryStrategy = new RetryStrategy(maxRetries, interval);
        while (true) {
            try {
                Response response = chain.proceed(request);
                // 如果响应状态码是2xx就不用重试，直接返回 response
                if (!retryRuleSet.contains(RetryRule.RESPONSE_STATUS_NOT_2XX) || response.isSuccessful()) {
                    return response;
                } else {
                    if (!retryStrategy.shouldRetry()) {
                        // 最后一次还没成功，返回最后一次response
                        return response;
                    }
                    // 执行重试
                    retryStrategy.retry();
                    log.debug("The response fails, retry is performed! The response code is " + response.code());
                    response.close();
                }
            } catch (Exception e) {
                if (shouldThrowEx(retryRuleSet, e)) {
                    throw new RuntimeException(e);
                } else {
                    if (!retryStrategy.shouldRetry()) {
                        // 最后一次还没成功，抛出异常
                        throw new RuntimeException("Retry Failed: Total " + maxRetries + " attempts made at interval " + interval + "ms");
                    }
                    retryStrategy.retry();
                }
            }
        }
    }

}
