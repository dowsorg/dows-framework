package org.dows.framework.crypto.sdk.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.encoders.UrlBase64;
import org.dows.crypto.api.enums.EncodingType;
import org.dows.framework.api.exceptions.CryptoException;
import org.dows.framework.api.status.CryptoStatusCode;

import java.nio.charset.Charset;

/**
 * 编码工具类
 */
@Slf4j
public class EncodingUtil {

    /**
     * 编码实现
     *
     * @param encodingType: 编码类型
     * @param encoding:     编码内容
     * @return java.lang.String 编码字符串
     **/
    public static String encode(EncodingType encodingType, byte[] encoding) {
        try {
            switch (encodingType) {
                case HEX:
                    return Hex.toHexString(encoding);
                case BASE64:
                    return Base64.toBase64String(encoding);
                case URL_BASE64:
                    return new String(UrlBase64.encode(encoding));
                case NONE:
                    return new String(encoding);
            }
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.ENCODING_FAILED;
            log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }
        return null;
    }

    /**
     * 解码实现
     *
     * @param encodingType: 编码类型
     * @param encoding:     编码内容
     * @return byte[] 解码字节数组
     **/
    public static byte[] decode(EncodingType encodingType, byte[] encoding) {
        try {
            switch (encodingType) {
                case HEX:
                    return Hex.decode(encoding);
                case BASE64:
                    return Base64.decode(encoding);
                case URL_BASE64:
                    return UrlBase64.decode(encoding);
                case NONE:
                    return encoding;
            }
        } catch (Exception e) {
            CryptoStatusCode cryptoStatusCode = CryptoStatusCode.DECODING_FAILED;
            log.error(cryptoStatusCode.getDescr() + " ERROR：" + e.getMessage());
            throw new CryptoException(cryptoStatusCode);
        }
        return null;
    }

    /**
     * 解码实现
     *
     * @param encodingType: 编码类型
     * @param encoding:     编码内容
     * @param charset:      字符集
     * @return byte[] 解码字节数组
     **/
    public static byte[] decode(EncodingType encodingType, String encoding, Charset charset) {
        return decode(encodingType, encoding.getBytes(charset));
    }

}
