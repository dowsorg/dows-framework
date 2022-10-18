package org.dows.framework.crypto.sdk;

import lombok.extern.slf4j.Slf4j;
import org.dows.crypto.api.ApiCryptor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/9/2022
 */
@Component
@Slf4j
public class NullCryptor extends AbstractApiCryptor implements ApiCryptor {


    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public boolean isCanRealize(Method method, boolean requestOrResponse) {
        return false;
    }

    @Override
    public boolean isCanRealize(Class<? extends Annotation> cryptoAnnoClass) {
        return false;
    }
}
