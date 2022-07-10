package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;


public class AuthenticationException extends BaseException {

    private static final long serialVersionUID = -3269140788552978763L;

    public AuthenticationException(StatusCode responseCode) {
        super(responseCode);
    }

    public AuthenticationException(int code, String message) {
        super(code, message);
    }

    public AuthenticationException(StatusCode code, Object[] args) {
        super(code);
    }
}
