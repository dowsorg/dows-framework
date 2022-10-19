//package org.dows.framewrok.retrofit.interceptor;
//
//import org.dows.crypto.api.ApiCryptor;
//import org.dows.crypto.api.InputMessage;
//import org.dows.crypto.api.annotation.NotDecrypt;
//import org.dows.crypto.api.annotation.NotEncrypt;
//import org.dows.framework.rest.property.RestProperties;
//import org.dows.framework.rest.property.SecurityProperty;
//import org.dows.framewrok.retrofit.RetrofitInterceptor;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.Headers;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.springframework.http.HttpHeaders;
//import retrofit2.Invocation;
//
//import java.lang.reflect.Method;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @author lait.zhang@gmail.com
// * @description: TODO
// * @weixin SH330786
// * @date 4/5/2022
// */
//@Slf4j
//public class RetrofitSecurityInterceptor implements RetrofitInterceptor {
//
//    private final SecurityProperty securityProperty;
//
//    private final List<ApiCryptor> apiCryptors;
//
//    public RetrofitSecurityInterceptor(RestProperties restProperties, List<ApiCryptor> apiCryptors) {
//        this.securityProperty = restProperties.getSecurity();
//        this.apiCryptors = apiCryptors;
//    }
//
//    @Override
//    public Response doIntercept(Chain chain) throws Exception {
//        Request request = chain.request();
//        Invocation invocation = request.tag(Invocation.class);
//        assert invocation != null;
//        Method method = invocation.method();
//        if (null != method.getAnnotation(NotDecrypt.class)) {
//            return chain.proceed(request);
//        }
//        ApiCryptor apiCryptor = null;
//        if (Objects.nonNull(apiCryptors) && !apiCryptors.isEmpty()) {
//            for (ApiCryptor a : apiCryptors) {
//                if (a.isCanRealize(method, false)) {
//                    apiCryptor = a;
//                    break;
//                }
//            }
//        } else {
//            log.info("没有可用的 ApiCryptor 实现 ");
//        }
//        assert apiCryptor != null;
//
//        HttpHeaders empty = HttpHeaders.EMPTY;
//        Headers headers = request.headers();
//        for (String name : headers.names()) {
//            empty.add(name, headers.get(name));
//        }
//        InputMessage inputMessage = new InputMessage(request.body().writeTo(), empty);
//        inputMessage = apiCryptor.beforeBodyRead(inputMessage, method,null,null);
//
//        chain.proceed(request).body().byteStream();
//        if (null != method.getAnnotation(NotEncrypt.class)) {
//            return chain.proceed(request);
//        }
//
//
//
///*        Crypto crypto = getCrypto(request);
//        if (!needCrypto(crypto)) {
//            return chain.proceed(request);
//        }
//        // todo 抛异常
//        RestCryptor restCryptor = SpringUtil.getBean(crypto.cryptor().name());
//        RetrofitRequestAdapter retrofitRequestAdapter = new RetrofitRequestAdapter(request, crypto);
//        // 加密&签名等
//        restCryptor.crypto(retrofitRequestAdapter, null);
//        // todo 获取加签后的request,重新设置
////        retrofitRequestAdapter.unwrap();
//        request = RequestParamUtil.handlerRequest(retrofitRequestAdapter);*/
//        return chain.proceed(request);
//    }
//
//    @Override
//    public boolean getEnableGlobalCrypto() {
//        return securityProperty.isEnable();
//    }
//
//
//}
