package org.dows.framework.crypto.handler;

import org.dows.framework.api.CryptoHandler;
import org.dows.framework.api.enums.EncryptMode;

public class Base64Handler implements CryptoHandler {
    @Override
    public String encrypt(String value) {
        return null;
    }

    @Override
    public String decrypt(String value) {
        return null;
    }

    @Override
    public EncryptMode getMode() {
        return EncryptMode.BASE64;
    }
}
