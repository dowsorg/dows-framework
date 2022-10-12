package org.dows.crypto.api;


import java.io.InputStream;
import java.lang.annotation.Annotation;

/**
 * 自定义前端请求体格式化接口，实现该接口重写  requestBody 方法自定义解析 body
 */
@FunctionalInterface
public interface CryptoRequest {

    /**
     * 请求 body 自定义解析
     *
     * @param annotation:  执行注解
     * @param inputStream: 前端请求的 inputStream
     * @return
     **/
    CryptoBody requestBody(Annotation annotation, InputStream inputStream);
}
