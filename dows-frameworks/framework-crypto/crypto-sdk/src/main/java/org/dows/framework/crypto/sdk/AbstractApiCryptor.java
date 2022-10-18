package org.dows.framework.crypto.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.CryptoProperties;
import org.dows.crypto.api.CryptoRequest;
import org.dows.crypto.api.CryptoResponse;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractApiCryptor implements ApiCryptor {
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected CryptoProperties cryptoProperties;
    @Autowired(required = false)
    protected CryptoRequest cryptoRequest;
    @Autowired(required = false)
    protected CryptoResponse cryptoResponse;

    public void setCryptoRequest(CryptoRequest cryptoRequest) {
        this.cryptoRequest = cryptoRequest;
    }

    public void setCryptoResponse(CryptoResponse cryptoResponse) {
        this.cryptoResponse = cryptoResponse;
    }
}
