package org.dows.framework.crypto.sdk;

import lombok.extern.slf4j.Slf4j;
import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.CryptoBody;
import org.dows.crypto.api.InputMessage;
import org.dows.crypto.api.annotation.SymmetricCrypto;
import org.dows.crypto.api.enums.EncodingType;
import org.dows.framework.api.exceptions.CryptoException;
import org.dows.framework.api.status.CryptoStatusCode;
import org.dows.framework.crypto.sdk.util.CryptoUtil;
import org.dows.framework.crypto.sdk.util.RandomStrUtil;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * API接口对称性加密/解密实现
 */
@Component
@Slf4j
public class SymmetricCryptor extends AbstractApiCryptor implements ApiCryptor {

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public boolean isCanRealize(Method method, boolean requestOrResponse) {
        SymmetricCrypto annotation = this.getAnnotation(method, SymmetricCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public boolean isCanRealize(Class<? extends Annotation> cryptoAnnoClass) {
        return cryptoAnnoClass.equals(SymmetricCrypto.class);
    }

    @Override
    public InputMessage requestBefore(InputMessage inputMessage,
                                      List<Annotation> annotations,
                                      Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        SymmetricCrypto annotation = this.selectAnnotation(annotations, SymmetricCrypto.class);

        CryptoBody cryptoBody = this.requestBody(annotation, inputMessage, cryptoRequest, objectMapper);

        if (!StringUtils.hasText(cryptoBody.getData())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_DATA_MISSING;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        if (annotation.type().isProduceIv()) {
            if (!StringUtils.hasText(cryptoBody.getIv())) {
                CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_VI_MISSING;
                log.error(cryptoStatusCode.getDescr());
                throw new CryptoException(cryptoStatusCode);
            }
        } else {
            cryptoBody.setIv(null);
        }

        String secretKey = secretKey(annotation);


        EncodingType encodingType = cryptoProperties.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }
        String encryptData;
        try {
            encryptData = CryptoUtil.symmetric(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    cryptoBody.getData(),
                    encodingType,
                    cryptoBody.getIv() != null ? cryptoBody.getIv() : null,
                    cryptoProperties.getCharset()
            );
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.DECRYPTION_FAILED;
            log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }

        if (!StringUtils.hasText(encryptData)) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.DATA_EMPTY;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        return this.stringToInputStream(encryptData.getBytes(cryptoProperties.getCharset()),
                inputMessage.getHeaders());
    }

    @Override
    public Object responseBefore(Object body, List<Annotation> annotations,
                                 MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass) {
        SymmetricCrypto annotation = this.selectAnnotation(annotations, SymmetricCrypto.class);
        String json = responseBody(body, objectMapper);
        String secretKey = secretKey(annotation);
        String iv = null;
        if (annotation.type().isProduceIv()) {
            iv = RandomStrUtil.getRandomNumber(annotation.type().getIvLength());
        }
        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }
        EncodingType encodingType = cryptoProperties.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }
        String encryptData;
        try {
            encryptData = CryptoUtil.symmetric(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    json,
                    encodingType,
                    iv,
                    cryptoProperties.getCharset()
            );
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.ENCRYPTION_FAILED;
            log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }
        // 使用默认响应体
        CryptoBody cryptoBody = new CryptoBody().setData(encryptData);
        if (annotation.type().isProduceIv()) {
            cryptoBody.setIv(iv);
        }
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

    /**
     * 获取 秘钥
     *
     * @param annotation: 执行注解
     * @return
     */
    private String secretKey(SymmetricCrypto annotation) {
        String secretKey = cryptoProperties.getSymmetric().get(annotation.type().getType());

        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        if (!StringUtils.hasText(secretKey)) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.NO_SECRET_KEY;
            log.error(cryptoStatusCode.getDescr() + " ERROR：(无效的秘钥,请配置秘钥 secretKey )");
            throw new CryptoException(cryptoStatusCode);
        }
        return secretKey;
    }

}
