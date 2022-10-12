package org.dows.crypto.api;

import org.dows.crypto.api.enums.EncodingType;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置类
 */
@Data
public class CryptoProperties {
    /**
     * 配置对称性密钥
     */
    private Map<String, String> symmetric = new HashMap<>();

    /**
     * 配置非对称性密钥
     */
    private Map<String, AsymmetryKey> asymmetry = new HashMap<>();

    /**
     * 配置数据签名
     */
    private Signature signature = new Signature();

    /**
     * 配置全局加密解密编码类型
     */
    private EncodingType encodingType = EncodingType.BASE64;

    /**
     * 配置全局加密解密处理字符集
     */
    private Charset charset = StandardCharsets.UTF_8;


    @Data
    public static class AsymmetryKey {
        /**
         * 非对称性 公钥
         */
        private String publicKey;

        /**
         * 非对称性 私钥
         */
        private String privateKey;

    }

    @Data
    public static class Signature {
        /**
         * 配置验证签名的超时时间(秒),小于1无效(默认).
         */
        private Long timeout = 0L;
        /**
         * 配置签名的密钥
         */
        private String secretKey;
    }
}
