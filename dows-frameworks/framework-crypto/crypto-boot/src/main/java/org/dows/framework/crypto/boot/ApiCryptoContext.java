package org.dows.framework.crypto.boot;

import org.dows.crypto.api.ApiCryptor;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/17/2022
 */
public class ApiCryptoContext {
    private final static ThreadLocal<ApiCryptor> apiCryptorThreadLocal = new ThreadLocal<>();

    public static void setApiCryptor(ApiCryptor apiCryptor) {
        apiCryptorThreadLocal.set(apiCryptor);
    }

    public static ApiCryptor getApiCryptor() {
        return apiCryptorThreadLocal.get();
    }

    public static void rmApiCryptor() {
        apiCryptorThreadLocal.remove();
    }
}
