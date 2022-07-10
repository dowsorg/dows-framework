package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.status.MqStatusCode;

/**
 *
 */
public class MqException extends BaseException {
    public MqException(MqStatusCode mqStatusCode) {
        super(mqStatusCode);
    }

    public MqException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public MqException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }
}
