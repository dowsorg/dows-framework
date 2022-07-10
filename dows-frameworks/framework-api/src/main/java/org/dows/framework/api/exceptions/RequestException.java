package org.dows.framework.api.exceptions;

import org.dows.framework.api.status.ReqeustStatusCode;

/**
 * 请求相关异常
 */
public class RequestException extends BaseException {
    public RequestException(ReqeustStatusCode statusCode) {
        super(statusCode);
    }
}
