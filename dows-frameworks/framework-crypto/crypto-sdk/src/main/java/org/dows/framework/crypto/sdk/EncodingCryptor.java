package org.dows.framework.crypto.sdk;

import lombok.extern.slf4j.Slf4j;
import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.CryptoBody;
import org.dows.crypto.api.InputMessage;
import org.dows.crypto.api.annotation.EncodingCrypto;
import org.dows.crypto.api.enums.EncodingType;
import org.dows.framework.api.exceptions.CryptoException;
import org.dows.framework.api.status.CryptoStatusCode;
import org.dows.framework.crypto.sdk.util.EncodingUtil;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * API接口编码实现
 */
@Component
@Slf4j
public class EncodingCryptor extends AbstractApiCryptor implements ApiCryptor {

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public boolean isCanRealize(Method methodParameter, boolean requestOrResponse) {
        EncodingCrypto annotation = this.getAnnotation(methodParameter, EncodingCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public boolean isCanRealize(Class<? extends Annotation> cryptoAnnoClass) {
        return cryptoAnnoClass.equals(EncodingCrypto.class);
    }

    @Override
    public InputMessage requestBefore(InputMessage inputMessage, List<Annotation> annotations,
                                      Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        EncodingCrypto annotation = this.selectAnnotation(annotations, EncodingCrypto.class);

        CryptoBody cryptoBody = this.requestBody(annotation, inputMessage, cryptoRequest, objectMapper);

        if (!StringUtils.hasText(cryptoBody.getData())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_DATA_MISSING;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        EncodingType encodingType = cryptoProperties.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        byte[] decode = EncodingUtil.decode(encodingType, cryptoBody.getData().getBytes(cryptoProperties.getCharset()));

        return this.stringToInputStream(decode, inputMessage.getHeaders());
    }


    @Override
    public Object responseBefore(Object body, List<Annotation> annotations, MediaType mediaType,
                                 Class<? extends HttpMessageConverter<?>> aClass) {
        EncodingCrypto annotation = this.selectAnnotation(annotations, EncodingCrypto.class);

        String json = responseBody(body, objectMapper);

        EncodingType encodingType = cryptoProperties.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        String encode = EncodingUtil.encode(encodingType, json.getBytes(cryptoProperties.getCharset()));

        CryptoBody cryptoBody = new CryptoBody().setData(encode);

        // 使用自定义响应体
        if (cryptoResponse != null) {
            return cryptoResponse.responseBody(annotation, cryptoBody);
        }

        if (body instanceof String) {
            return responseBody(cryptoBody, objectMapper);
        } else {
            return cryptoBody;
        }
    }
}
