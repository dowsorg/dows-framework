package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;

/**
 * 基础异常类，所有自定义异常类都需要继承本类
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 返回码
     */
    protected StatusCode statusCode;
    /**
     * 异常消息参数
     */
    protected Object[] args;

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(Integer code, String msg) {
        super(msg);
        this.statusCode = new StatusCode() {
            @Override
            public Integer getCode() {
                return code;
            }

            @Override
            public String getDescr() {
                return msg;
            }
        };
    }

    public BaseException(Throwable throwable) {
        super(throwable);
    }

    public BaseException(StatusCode statusCode) {
        super(statusCode.getDescr());
        this.statusCode = statusCode;
    }

    public BaseException(StatusCode statusCode, Exception exception) {
        super(String.format(statusCode.getDescr(), exception.getMessage()));
        this.statusCode = statusCode;
    }

    public BaseException(StatusCode statusCode, String msg) {
        super(String.format(statusCode.getDescr(), msg));
        this.statusCode = statusCode;
    }

    public BaseException(StatusCode statusCode, Object[] args, String message) {
        super(message);
        this.statusCode = statusCode;
        this.args = args;
    }

    public BaseException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.args = args;
    }


    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Object[] getArgs() {
        return args;
    }

}
