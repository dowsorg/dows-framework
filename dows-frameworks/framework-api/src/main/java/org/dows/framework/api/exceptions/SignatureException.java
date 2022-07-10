package org.dows.framework.api.exceptions;

import org.dows.framework.api.status.CryptoStatusCode;

/**
 * 签名异常类
 */
public class SignatureException extends BaseException {

    public SignatureException(CryptoStatusCode statusCode) {
        super(statusCode);
    }
}
