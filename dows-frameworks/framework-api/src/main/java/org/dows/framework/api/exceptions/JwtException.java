package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/13/2022
 */
public class JwtException extends BaseException {
    public JwtException(String msg) {
        super(msg);
    }

    public JwtException(Throwable throwable) {
        super(throwable);
    }

    public JwtException(StatusCode statusCode) {
        super(statusCode);
    }

    public JwtException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public JwtException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }
}
