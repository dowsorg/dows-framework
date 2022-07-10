package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 5/15/2022
 */
public class SequenceException extends BaseException {
    public SequenceException(String msg) {
        super(msg);
    }

    public SequenceException(Integer code, String msg) {
        super(code, msg);
    }

    public SequenceException(Throwable throwable) {
        super(throwable);
    }

    public SequenceException(StatusCode statusCode) {
        super(statusCode);
    }

    public SequenceException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }

    public SequenceException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public SequenceException(StatusCode statusCode, Object[] args, String message) {
        super(statusCode, args, message);
    }

    public SequenceException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(statusCode, args, message, cause);
    }
}
