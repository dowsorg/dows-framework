package org.dows.framework.api.exceptions;


import org.dows.framework.api.StatusCode;

/**
 * 校验异常
 * 调用接口时，参数格式不合法可以抛出该异常
 */
public class ValidationException extends BaseException {


    public ValidationException(StatusCode responseCode) {
        super(responseCode);
    }


}
