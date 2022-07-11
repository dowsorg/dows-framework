package org.dows.framework.rest.exception;

public class RetrofitBlockException extends RuntimeException {

    public RetrofitBlockException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrofitBlockException(Throwable cause) {
        super(cause);
    }
}
