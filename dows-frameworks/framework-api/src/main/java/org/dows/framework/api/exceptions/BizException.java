package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;

/**
 * 业务异常
 * 业务处理时，出现异常，可以抛出该异常
 */
public class BizException extends BaseException {


    public BizException(String msg) {
        super(msg);
    }

    public BizException(StatusCode responseCode) {
        super(responseCode);
    }

    public BizException(int code, String message) {
        super(code, message);
    }

    public BizException(StatusCode code, Object[] args) {
        super(code);
    }
}
