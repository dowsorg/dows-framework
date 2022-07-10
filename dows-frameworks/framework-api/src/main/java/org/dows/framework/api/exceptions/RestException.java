package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
public class RestException extends BaseException {
    public RestException(String msg) {
        super(msg);
    }

    public RestException(Integer code, String msg) {
        super(code, msg);
    }

    public RestException(Throwable throwable) {
        super(throwable);
    }

    public RestException(StatusCode statusCode) {
        super(statusCode);
    }

    public RestException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public RestException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RestException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public RestException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
