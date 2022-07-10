package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;

/**
 * 远程调用异常
 */
public class RemotingException extends BaseException {

    public RemotingException(String msg) {
        super(msg);
    }

    public RemotingException(StatusCode statusCode) {
        super(statusCode);
    }

    public RemotingException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public RemotingException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }
}
