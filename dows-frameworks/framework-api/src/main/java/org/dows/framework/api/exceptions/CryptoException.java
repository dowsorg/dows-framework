package org.dows.framework.api.exceptions;

import org.dows.framework.api.status.CryptoStatusCode;

/**
 * 加解密异常类
 */
public class CryptoException extends BaseException {

    public CryptoException(CryptoStatusCode statusCode) {
        super(statusCode);
    }
}
