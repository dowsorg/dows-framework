package org.dows.crypto.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dows.framework.api.exceptions.CryptoException;
import org.dows.framework.api.status.CryptoStatusCode;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * API接口加密/解密/编码/签名
 */
public interface ApiCryptor {

    default String percentEncode(String key) {
        return null;
    }

    Logger getLog();

    /**
     * 注解判断
     *
     * @param method:           执行方法参数
     * @param requestOrResponse : request (true) / Response(false)
     * @return boolean
     */
    boolean isCanRealize(Method method, boolean requestOrResponse);

    /**
     * @param cryptoAnnoClass
     * @return
     */
    boolean isCanRealize(Class<? extends Annotation> cryptoAnnoClass);

    /**
     * 请求前（ 可自定义解密方式）
     *
     * @param inputMessage :    请求数据体
     * @param annotations  : 执行的方法参数
     * @param type         :            执行目标类型
     * @param aClass       :          消息转换器
     * @return
     */
    default InputMessage requestBefore(InputMessage inputMessage, List<Annotation> annotations,
                                       Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return inputMessage;
    }


    default Object requestAfter(Object body, List<Annotation> methodAnnotations, Type targetType) {
        return body;
    }

    /**
     * 响应前（ 可自定义加密方式 ）
     *
     * @param body        :      响应体（加密前）
     * @param annotations :    执行的方法参数
     * @param mediaType   : 交互数据类型
     * @param aClass      :    消息转换器
     * @return
     */
    default Object responseBefore(Object body, List<Annotation> annotations, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass) {
        return body;
    }


    /**
     * 获取方法或类上指定注解
     *
     * @param method:         方法参数
     * @param annotationType: 注解类型
     * @return
     */
    default <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        A a = method.getAnnotation(annotationType);
        if (a == null) {
            Class<?> declaringClass = method.getDeclaringClass();
            a = declaringClass.getAnnotation(annotationType);
        }
        return a;
       /* return Optional.ofNullable(methodParameter.getMethodAnnotation(annotationType))
                .orElse(methodParameter.getDeclaringClass().getAnnotation(annotationType));*/
    }

    default <A extends Annotation> A selectAnnotation(List<Annotation> annotations, Class<A> annotationType) {
        return (A) annotations.stream().filter(a -> a.annotationType().equals(annotationType)).findFirst().orElse(null);
    }

    default List<Annotation> getAnnotations(Method method) {
        //method.getAnnotation
        return null;
    }

    /**
     * 字符数组转换为输入流
     *
     * @param bytes:       字符串内容
     * @param httpHeaders: 请求头
     * @return
     */
    default InputMessage stringToInputStream(byte[] bytes, HttpHeaders httpHeaders) {
        try {
            assert bytes != null;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return new InputMessage(inputStream, httpHeaders);
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.STRING_TO_INPUT_STREAM;
            getLog().error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }
    }

    /**
     * 请求体解析
     *
     * @param annotation:       执行注解
     * @param httpInputMessage: 输入
     * @param apiRequestBody:   自定义解析
     * @param objectMapper:     json
     * @return
     */
    default CryptoBody requestBody(Annotation annotation, InputMessage httpInputMessage,
                                   CryptoRequest apiRequestBody, ObjectMapper objectMapper) {
        CryptoBody apiCryptoBody;
        try {
            if (apiRequestBody != null) {
                // 自定义请求体解析格式
                apiCryptoBody = apiRequestBody.requestBody(annotation, httpInputMessage.getBody());
            } else {
                // 默认解析格式
                apiCryptoBody = objectMapper.readValue(httpInputMessage.getBody(), CryptoBody.class);
            }
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.REQUEST_TO_BEAN;
            getLog().error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }
        return apiCryptoBody;
    }

    /**
     * 响应体解析
     *
     * @param body:         响应对象
     * @param objectMapper: json
     * @return
     */
    default String responseBody(Object body, ObjectMapper objectMapper) {
        if (body instanceof String) {
            return (String) body;
        }
        // 转成json字符串
        String json;
        try {
            json = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.RESPONSE_TO_JSON;
            getLog().error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }
        return json;
    }

}

