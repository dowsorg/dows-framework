package org.dows.framewrok.retrofit.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.dows.framework.api.GlobalKeys;
import org.dows.framework.api.TokenContext;
import org.dows.framework.api.exceptions.RestException;
import org.dows.framework.rest.interceptor.InterceptTyp;
import org.dows.framework.rest.interceptor.PrototypeInterceptor;
import org.dows.framework.rest.property.FilterProperty;
import org.dows.framework.rest.property.RestProperties;
import org.dows.framewrok.retrofit.RetrofitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/29/2022
 */
@Slf4j
public class RetrofitFilterInterceptor implements RetrofitInterceptor, PrototypeInterceptor {

    private FilterProperty filterProperty;

    @Autowired
    public RetrofitFilterInterceptor(RestProperties restProperties) {
        filterProperty = restProperties.getFilter();
    }

    @Override
    public InterceptTyp intetceptTyp() {
        return InterceptTyp.FILTER;
    }

    @Override
    public Response doIntercept(Chain chain) throws IOException {
        //Request request = chain.request();
        String token = TokenContext.getToken();
        Request original = chain.request();
        Request request = original.newBuilder()
                .addHeader("User-Agent", "Your-App-Name")
                .addHeader(GlobalKeys.TOKEN_NAME, token)
                .method(original.method(), original.body())
                .build();

        if (filter(request, filterProperty)) {
            return chain.proceed(request);
        }
        throw new RestException("不支持的路径");
    }


}
