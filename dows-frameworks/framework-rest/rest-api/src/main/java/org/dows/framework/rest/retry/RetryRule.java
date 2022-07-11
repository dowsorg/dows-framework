package org.dows.framework.rest.retry;

/**
 * 触发重试的规则
 *
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
public enum RetryRule {

    /**
     * 响应状态码不是2xx
     * The response status code is not 2xx
     */
    RESPONSE_STATUS_NOT_2XX,

    /**
     * 发生任意异常
     * Any exception occurred
     */
    OCCUR_EXCEPTION,

    /**
     * 发生IO异常
     * IO exception occurred
     */
    OCCUR_IO_EXCEPTION,

}
