package org.dows.framework.crypto.boot.advice;

import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.InputMessage;
import org.dows.crypto.api.annotation.NotDecrypt;
import org.dows.framework.crypto.boot.ApiCryptoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 请求解密类
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    private final List<ApiCryptor> apiCryptors;

    @Override
    public boolean supports(MethodParameter parameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 方法上有排除解密注解
        if (parameter.hasMethodAnnotation(NotDecrypt.class)) {
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
            log.info("没有可用的 ApiCryptor 实现 ");
        }
        return false;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        InputMessage inputMessage = new InputMessage();
        inputMessage.setBody(httpInputMessage.getBody());
        inputMessage.setHeaders(httpInputMessage.getHeaders());
        List<Annotation> methodAnnotations = Arrays.asList(parameter.getMethodAnnotations());
//        Optional.ofNullable(parameter.getMethodAnnotation(annotationType))
//                .orElse(methodParameter.getDeclaringClass().getAnnotation(annotationType))
        //apiCryptor.getAnnotations(parameter.getMethod());
        return ApiCryptoContext.getApiCryptor().requestBefore(inputMessage, methodAnnotations, targetType, converterType);
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
//        List<Annotation> methodAnnotations = Arrays.asList(parameter.getMethodAnnotations());
//        return apiCryptor.requestAfter(body, methodAnnotations,targetType);
        ApiCryptoContext.rmApiCryptor();
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        ApiCryptoContext.rmApiCryptor();
        return body;
    }
}
