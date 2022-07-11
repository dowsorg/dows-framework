package org.dows.framework.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/5/2022
 */
public interface RestRequest<R> {


    Map<String, String> getAllHeaders();


    String getContentType();


    String getHeader(String key);


    /**
     * 获取请求体
     *
     * @return
     * @throws IOException
     */
    InputStream getMessagePayload() throws IOException;

    /**
     * 获取查询参数
     *
     * @param request
     * @return
     */
    Map<String, String> getRequestParams(R request);

    String getMethod();


    String getRequestUrl();

    void setRequestUrl(String url);

    void setHeader(String key, String value);

    //Crypto getCrypto();

    String getCryptoContentPayload();

    void setCryptoContentPayload(String cryptoContentPayload);

    /**
     * 获取原请求对象
     *
     * @return
     */
    R unwrap();
}
