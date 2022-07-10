package org.dows.framework.api.exceptions;


import org.dows.framework.api.StatusCode;

public class RedisException extends BaseException {

    public RedisException(StatusCode statusCode) {
        super(statusCode);
    }

    public RedisException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RedisException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }
}
