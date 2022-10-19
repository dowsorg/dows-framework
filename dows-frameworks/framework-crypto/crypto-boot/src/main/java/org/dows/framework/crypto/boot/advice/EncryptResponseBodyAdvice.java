package org.dows.framework.crypto.boot.advice;

import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.annotation.NotEncrypt;
import org.dows.framework.crypto.boot.ApiCryptoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 加密响应类
 **/
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object>, Serializable {

    private final List<ApiCryptor> apiCryptors;

    @Override
    public boolean supports(MethodParameter parameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // 方法上有排除加密注解
        if (parameter.hasMethodAnnotation(NotEncrypt.class)) {
            return false;
        }
        if (Objects.nonNull(apiCryptors) && !apiCryptors.isEmpty()) {
            for (ApiCryptor a : apiCryptors) {
                if (a.isCanRealize(parameter.getMethod(), false)) {
                    ApiCryptoContext.setApiCryptor(a);
                    return true;
                }
            }
        } else {
            log.info("没有可用的 ApiCryptor 实现 )");
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter,
                                  MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        List<Annotation> methodAnnotations = Arrays.asList(methodParameter.getMethodAnnotations());
        Object o = ApiCryptoContext.getApiCryptor().responseBefore(body, methodAnnotations, mediaType, aClass);
        ApiCryptoContext.rmApiCryptor();
        return o;
    }

}
