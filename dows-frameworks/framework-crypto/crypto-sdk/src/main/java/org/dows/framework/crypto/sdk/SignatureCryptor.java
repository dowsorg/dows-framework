package org.dows.framework.crypto.sdk;

import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.CryptoBody;
import org.dows.crypto.api.InputMessage;
import org.dows.crypto.api.annotation.SignatureCrypto;
import org.dows.framework.api.Response;
import org.dows.framework.api.exceptions.CryptoException;
import org.dows.framework.api.status.CryptoStatusCode;
import org.dows.framework.crypto.sdk.util.RandomStrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * API接口签名实现
 */
@Component
@Slf4j
public class SignatureCryptor extends AbstractApiCryptor implements ApiCryptor {

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public boolean isCanRealize(Method method, boolean requestOrResponse) {
        SignatureCrypto annotation = this.getAnnotation(method, SignatureCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public boolean isCanRealize(Class<? extends Annotation> cryptoAnnoClass) {
        return cryptoAnnoClass.equals(SignatureCrypto.class);
    }

    @Override
    public InputMessage requestBefore(InputMessage inputMessage, List<Annotation> annotations,
                                      Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        SignatureCrypto annotation = this.selectAnnotation(annotations, SignatureCrypto.class);

        CryptoBody cryptoBody = this.requestBody(annotation, inputMessage, cryptoRequest, objectMapper);

        if (!StringUtils.hasText(cryptoBody.getData())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_DATA_MISSING;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        if (!StringUtils.hasText(cryptoBody.getNonce())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_NONCE_MISSING;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        if (Objects.isNull(cryptoBody.getTimestamp())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_TIMESTAMP_MISSING;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        if (!StringUtils.hasText(cryptoBody.getSignature())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.PARAM_SIGN_MISSING;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }


        long timeout = cryptoProperties.getSignature().getTimeout();
        if (annotation.timeout() > 0) {
            timeout = annotation.timeout();
        }

        if (timeout > 0) {
            long time = (System.currentTimeMillis() / 1000) - cryptoBody.getTimestamp();
            if (time > timeout) {
                CryptoStatusCode cryptoStatusCode = CryptoStatusCode.SIGNATURE_TIMED_OUT;
                log.error(cryptoStatusCode.getDescr());
                throw new CryptoException(cryptoStatusCode);
            }
        }

        String secretKey = cryptoProperties.getSignature().getSecretKey();

        if (StringUtils.hasText(annotation.secretKey())) {
            secretKey = annotation.secretKey();
        }

        if (!StringUtils.hasText(secretKey)) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.REQUIRED_SIGNATURE_PARAM;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        if (!signature(cryptoBody, secretKey).equals(cryptoBody.getSignature())) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.VERIFY_SIGNATURE_FAILED;
            log.error(cryptoStatusCode.getDescr());
            throw new CryptoException(cryptoStatusCode);
        }

        return this.stringToInputStream(cryptoBody.getData()
                .getBytes(cryptoProperties.getCharset()), inputMessage.getHeaders());
    }

    /**
     * 请求之后
     *
     * @param body
     * @param methodAnnotations
     * @param targetType
     * @return
     */
    @Override
    public Object requestAfter(Object body, List<Annotation> methodAnnotations, Type targetType) {
        SignatureCrypto annotation = this.selectAnnotation(methodAnnotations, SignatureCrypto.class);
        CryptoBody cryptoBody = new CryptoBody();
        if (body instanceof Response) {
            Response r = (Response) body;
            Object data = r.getData();
            String json = responseBody(data, objectMapper);
            cryptoBody.setData(json);
        } else {
            String json = responseBody(body, objectMapper);
            cryptoBody.setData(json);
        }

        String secretKey = cryptoProperties.getSignature().getSecretKey();
        if (StringUtils.hasText(annotation.secretKey())) {
            secretKey = annotation.secretKey();
        }
        cryptoBody.setNonce(RandomStrUtil.getRandomNumber(32));
        cryptoBody.setTimestamp(System.currentTimeMillis() / 1000);
        cryptoBody.setSignature(signature(cryptoBody, secretKey));

        InputMessage inputMessage = new InputMessage();
        // 使用自定义响应体
        if (cryptoRequest != null) {
            try {
                return cryptoRequest.requestBody(annotation, inputMessage.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (body instanceof String) {
            return responseBody(annotation, objectMapper);
        } else {
            return cryptoBody;
        }
    }


    @Override
    public Object responseBefore(Object body, List<Annotation> annotations, MediaType mediaType,
                                 Class<? extends HttpMessageConverter<?>> aClass) {
        SignatureCrypto annotation = this.selectAnnotation(annotations, SignatureCrypto.class);
        CryptoBody cryptoBody = new CryptoBody();
        cryptoBody.setData(responseBody(body, objectMapper));
        String secretKey = cryptoProperties.getSignature().getSecretKey();
        if (StringUtils.hasText(annotation.secretKey())) {
            secretKey = annotation.secretKey();
        }
        cryptoBody.setNonce(RandomStrUtil.getRandomNumber(32));
        cryptoBody.setTimestamp(System.currentTimeMillis() / 1000);
        cryptoBody.setSignature(signature(cryptoBody, secretKey));

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
     * @param cryptoBody: 签名数据
     * @param secretKey:  签名秘钥
     * @return
     **/
    private String signature(CryptoBody cryptoBody, String secretKey) {
        try {
            String str = "data=" + cryptoBody.getData() +
                    "&timestamp=" + cryptoBody.getTimestamp() +
                    "&nonce=" + cryptoBody.getNonce() +
                    "&key=" + secretKey;
            return DigestUtils.md5DigestAsHex(str.getBytes(cryptoProperties.getCharset()));
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.SIGNATURE_FAILED;
            log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }
    }
}
