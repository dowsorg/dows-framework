package org.dows.framework.crypto.sdk;

import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.CryptoBody;
import org.dows.crypto.api.InputMessage;
import org.dows.crypto.api.annotation.AsymmetryCrypto;
import org.dows.crypto.api.enums.EncodingType;
import org.dows.framework.api.exceptions.CryptoException;
import org.dows.framework.api.status.CryptoStatusCode;
import org.dows.framework.crypto.sdk.util.CryptoUtil;
import org.dows.framework.crypto.sdk.util.EncodingUtil;
import lombok.extern.slf4j.Slf4j;
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
 * API接口非对称加密/解密实现
 */
@Component
@Slf4j
public class AsymmetryCryptor extends AbstractApiCryptor implements ApiCryptor {
    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public boolean isCanRealize(Method methodParameter, boolean requestOrResponse) {
        AsymmetryCrypto annotation = this.getAnnotation(methodParameter, AsymmetryCrypto.class);
        return !Objects.isNull(annotation);
    }
    @Override
    public boolean isCanRealize(Class<? extends Annotation> cryptoAnnoClass) {
        return cryptoAnnoClass.equals(AsymmetryCrypto.class);
    }

    @Override
    public InputMessage requestBefore(InputMessage inputMessage, List<Annotation> annotations,
                                      Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        AsymmetryCrypto annotation = selectAnnotation(annotations, AsymmetryCrypto.class);

        CryptoBody cryptoBody = this.requestBody(annotation, inputMessage, cryptoRequest, objectMapper);

        if (annotation.verifySignature() && !StringUtils.hasText(cryptoBody.getSignature())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_SIGN_MISSING;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        if (!StringUtils.hasText(cryptoBody.getData())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_DATA_MISSING;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        // 密钥
        String privateKey = getPrivateKey(annotation);
        String publicKey = getPublicKey(annotation);

        // 内容编码
        EncodingType contentEncodingType = getContentEncodingType(annotation);
        // 秘钥编码
        EncodingType keyEncodingType = getKeyEncodingType(annotation);

        // 验证签名
        if (annotation.verifySignature()) {
            boolean bo = false;
            try {
                bo = CryptoUtil.resSignatureVerify(
                        annotation.signatureType(),
                        EncodingUtil.decode(contentEncodingType, cryptoBody.getData(), cryptoProperties.getCharset()),
                        EncodingUtil.decode(contentEncodingType, cryptoBody.getSignature(), cryptoProperties.getCharset()),
                        EncodingUtil.decode(keyEncodingType, publicKey, cryptoProperties.getCharset())
                );
            } catch (Exception e) {
                CryptoStatusCode cryptoStatusCode = CryptoStatusCode.VERIFY_SIGNATURE_FAILED;
                log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
                throw new CryptoException(cryptoStatusCode);
            }

            if (!bo) {
                CryptoStatusCode cryptoStatusCode = CryptoStatusCode.VERIFY_SIGNATURE_FAILED;
                log.error(cryptoStatusCode.getDescr());
                throw new CryptoException(cryptoStatusCode);
            }
        }

        // 解密
        String encryptData;
        try {
            encryptData = CryptoUtil.asymmetry(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.DECRYPT_MODE,
                    privateKey,
                    keyEncodingType,
                    cryptoBody.getData(),
                    contentEncodingType,
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
        return this.stringToInputStream(encryptData.getBytes(cryptoProperties.getCharset()), inputMessage.getHeaders());
    }

    @Override
    public Object responseBefore(Object body, List<Annotation> annotations, MediaType mediaType,
                                 Class<? extends HttpMessageConverter<?>> aClass) {
        AsymmetryCrypto annotation = selectAnnotation(annotations, AsymmetryCrypto.class);

        String privateKey = getPrivateKey(annotation);
        String publicKey = getPublicKey(annotation);
        String json = responseBody(body, objectMapper);

        // 内容编码
        EncodingType contentEncodingType = getContentEncodingType(annotation);
        // 秘钥编码
        EncodingType keyEncodingType = getKeyEncodingType(annotation);
        String encryptData;

        try {
            encryptData = CryptoUtil.asymmetry(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.ENCRYPT_MODE,
                    publicKey,
                    keyEncodingType,
                    json,
                    contentEncodingType,
                    cryptoProperties.getCharset()
            );
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.ENCRYPTION_FAILED;
            log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }

        // 使用默认响应体
        CryptoBody cryptoBody = new CryptoBody().setData(encryptData);

        if (annotation.signature()) {
            byte[] signature = null;
            try {
                signature = CryptoUtil.resSignature(
                        annotation.signatureType(),
                        EncodingUtil.decode(contentEncodingType, cryptoBody.getData(), cryptoProperties.getCharset()),
                        EncodingUtil.decode(keyEncodingType, privateKey, cryptoProperties.getCharset())
                );
            } catch (Exception e) {
                CryptoStatusCode cryptoStatusCode = CryptoStatusCode.SIGNATURE_FAILED;
                log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
                throw new CryptoException(cryptoStatusCode);
            }

            if (Objects.isNull(signature) || signature.length < 1) {
                CryptoStatusCode cryptoStatusCode = CryptoStatusCode.SIGNATURE_FAILED;
                log.error(cryptoStatusCode.getDescr());
                throw new CryptoException(cryptoStatusCode);
            }
            cryptoBody.setSignature(EncodingUtil.encode(contentEncodingType, signature));
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

    private String getPrivateKey(AsymmetryCrypto annotation) {
        String privateKey = StringUtils.hasText(annotation.privateKey()) ?
                annotation.privateKey() : cryptoProperties.getAsymmetry().get(annotation.type().getType()).getPrivateKey();
        if (!StringUtils.hasText(privateKey)) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.NO_PRIVATE_KEY;
            log.error(cryptoStatusCode.getDescr() + " ERROR：(无效的秘钥,请配置秘钥 privateKey)");
            throw new CryptoException(cryptoStatusCode);
        }
        return privateKey;
    }

    private String getPublicKey(AsymmetryCrypto annotation) {
        String publicKey = StringUtils.hasText(annotation.publicKey()) ?
                annotation.publicKey() : cryptoProperties.getAsymmetry().get(annotation.type().getType()).getPublicKey();
        if (!StringUtils.hasText(publicKey)) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.NO_PUBLIC_KEY;
            log.error(cryptoStatusCode.getDescr() + " ERROR：(无效的秘钥,请配置秘钥 publicKey)");
            throw new CryptoException(cryptoStatusCode);
        }
        return publicKey;
    }


    private EncodingType getContentEncodingType(AsymmetryCrypto annotation) {
        EncodingType encodingType = cryptoProperties.getEncodingType();
        if (!annotation.contentEncodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.contentEncodingType();
        }
        return encodingType;
    }


    private EncodingType getKeyEncodingType(AsymmetryCrypto annotation) {
        EncodingType encodingType = cryptoProperties.getEncodingType();
        if (!annotation.keyEncodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.keyEncodingType();
        }
        return encodingType;
    }
}
