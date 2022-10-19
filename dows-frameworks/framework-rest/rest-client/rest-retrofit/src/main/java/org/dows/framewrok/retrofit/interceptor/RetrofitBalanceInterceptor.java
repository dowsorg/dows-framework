package org.dows.framewrok.retrofit.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.dows.framework.rest.annotation.RestClient;
import org.dows.framework.rest.balance.ServiceBalance;
import org.dows.framework.rest.interceptor.InterceptTyp;
import org.dows.framewrok.retrofit.RetrofitInterceptor;
import org.springframework.util.StringUtils;
import retrofit2.Invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
@Slf4j
@RequiredArgsConstructor
public class RetrofitBalanceInterceptor implements RetrofitInterceptor {

    private final ServiceBalance serviceBalance;

    @Override
    public InterceptTyp intetceptTyp() {
        return InterceptTyp.BALANCE;
    }

    @Override
    public Response doIntercept(Chain chain) throws IOException {
        Request request = chain.request();
        Invocation invocation = request.tag(Invocation.class);
        assert invocation != null;
        Method method = invocation.method();
        Class<?> declaringClass = method.getDeclaringClass();
        RestClient restClient = declaringClass.getAnnotation(RestClient.class);
        String baseUrl = restClient.baseUrl();
        if (StringUtils.hasText(baseUrl)) {
            return chain.proceed(request);
        }
        // serviceId服务发现
        String serviceId = restClient.serviceId();
        URI uri = serviceBalance.choose(serviceId);
        HttpUrl url = request.url();
        HttpUrl newUrl = url.newBuilder()
                .scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort())
                .build();
        Request newReq = request.newBuilder()
                .url(newUrl)
                .build();
        return chain.proceed(newReq);
    }


}
