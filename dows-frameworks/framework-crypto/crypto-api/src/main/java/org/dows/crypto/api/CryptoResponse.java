package org.dows.crypto.api;

import java.lang.annotation.Annotation;

/**
 * 自定义加密数据响应格式接口，实现该接口重写 responseBody 方法自定义返回体
 */
@FunctionalInterface
public interface CryptoResponse {

    /**
     * 自定义加密数据响应体格式
     *
     * @param annotation: 执行注解
     * @param cryptoBody: 响应数据
     * @return
     */
    Object responseBody(Annotation annotation, CryptoBody cryptoBody);
}
