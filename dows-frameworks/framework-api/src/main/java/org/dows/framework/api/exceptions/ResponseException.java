package org.dows.framework.api.exceptions;

import org.dows.framework.api.status.ResponseStatusCode;

/**
 * 响应相关异常
 */
public class ResponseException extends BaseException {

    public ResponseException(ResponseStatusCode statusCode) {
        super(statusCode);
    }
}
