package org.dows.framework.rest;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/5/2022
 */
public interface RestResponse<R> {

    /**
     * 获取状态码
     *
     * @return
     * @throws Exception
     */
    int getStatusCode() throws Exception;

    /**
     * @return
     * @throws Exception
     */
    String getReasonPhrase() throws Exception;

    /**
     * @return
     * @throws IOException
     */
    InputStream getContent() throws IOException;

    /**
     * 获取原响应对象
     *
     * @return
     */
    R unwrap();
}
