package org.dows.framework.crypto.handler;

import org.dows.crypto.api.CryptoHandler;
import org.dows.framework.api.enums.EncryptMode;

public class Base64Handler implements CryptoHandler {
    @Override
    public String encode(String value) {
        return null;
    }

    @Override
    public String decode(String value) {
        return null;
    }

    @Override
    public EncryptMode getMode() {
        return EncryptMode.BASE64;
    }
}
