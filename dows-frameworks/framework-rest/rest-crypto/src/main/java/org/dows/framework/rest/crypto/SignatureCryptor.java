//package org.dows.framework.rest.crypto;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.dows.crypto.api.CryptoBody;
//import CryptoStatusCode;
//import org.dows.framework.api.exceptions.CryptoException;
//import org.dows.framework.rest.RestRequest;
//import org.dows.framework.rest.RestResponse;
//import org.dows.framework.rest.cryptor.RestCryptor;
//import org.dows.framework.rest.property.RestProperties;
//import org.dows.framework.rest.property.SecurityProperty;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.util.DigestUtils;
//import org.springframework.util.StringUtils;
//
//import java.util.Objects;
//
///**
// * @author lait.zhang@gmail.com
// * @description: TODO
// * @weixin SH330786
// * @date 4/9/2022
// */
//@Slf4j
//@Component
//public class SignatureCryptor implements RestCryptor {
//
//    private final SecurityProperty securityProperty;
//    private final ObjectMapper objectMapper;
//
//    public SignatureCryptor(RestProperties restProperties, ObjectMapper objectMapper) {
//        this.securityProperty = restProperties.getSecurity();
//        this.objectMapper = objectMapper;
//    }
//
//
//    @Override
//    public void crypto(RestRequest request, RestResponse response) {
//
//    }
//
//
//    /**
//     * @param cryptoBody: 签名数据
//     * @param secretKey:  签名秘钥
//     * @return
//     **/
//    private String signature(CryptoBody cryptoBody, String secretKey, String charset) {
//        try {
//            // todo 对data进行排序、padding
//            String str = "data=" + cryptoBody.getData() +
//                    "&timestamp=" + cryptoBody.getTimestamp() +
//                    "&nonce=" + cryptoBody.getNonce() +
//                    "&key=" + secretKey;
//            return DigestUtils.md5DigestAsHex(str.getBytes(charset));
//        } catch (Exception e) {
//            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.SIGNATURE_FAILED;
//            log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
//            throw new CryptoException(cryptoStatusCode);
//        }
//    }
//}
