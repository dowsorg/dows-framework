package org.dows.framework.crypto.sdk;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.*;
import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.CryptoBody;
import org.dows.crypto.api.annotation.DigestsCrypto;
import org.dows.crypto.api.enums.EncodingType;
import org.dows.framework.crypto.sdk.util.CryptoUtil;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * API接口摘要实现
 */
@Component
@Slf4j
public class DigestCryptor extends AbstractApiCryptor implements ApiCryptor {

    @Override
    public Logger getLog() {
        return log;
    }
    @Override
    public boolean isCanRealize(Method methodParameter, boolean requestOrResponse) {
        DigestsCrypto annotation = this.getAnnotation(methodParameter, DigestsCrypto.class);
        if (Objects.isNull(annotation)) {
            return false;
        }
        return !requestOrResponse;
    }

    @Override
    public boolean isCanRealize(Class<? extends Annotation> cryptoAnnoClass) {
        return cryptoAnnoClass.equals(DigestsCrypto.class);
    }

    @Override
    public Object responseBefore(Object body, List<Annotation> annotations, MediaType mediaType,
                                 Class<? extends HttpMessageConverter<?>> aClass) {
        DigestsCrypto annotation = this.selectAnnotation(annotations, DigestsCrypto.class);
        String json = responseBody(body, objectMapper);
        Digest digest;
        if (Objects.isNull(annotation)) {
            digest = new MD5Digest();
        } else {
            switch (annotation.type()) {
                case MD2:
                    digest = new MD2Digest();
                    break;
                case MD4:
                    digest = new MD4Digest();
                    break;
                case SHA1:
                    digest = new SHA1Digest();
                    break;
                case SHA3:
                    digest = new SHA3Digest();
                    break;
                case SHA224:
                    digest = new SHA224Digest();
                    break;
                case SHA256:
                    digest = new SHA256Digest();
                    break;
                case SHA384:
                    digest = new SHA384Digest();
                    break;
                case SHA512:
                    digest = new SHA512Digest();
                    break;
                case SHAKE:
                    digest = new SHAKEDigest();
                    break;
                default:
                    digest = new MD5Digest();
            }
        }

        EncodingType encodingType = cryptoProperties.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }
        String data = CryptoUtil.digest(digest, encodingType, json, cryptoProperties.getCharset());
        CryptoBody cryptoBody = new CryptoBody().setData(data);
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
