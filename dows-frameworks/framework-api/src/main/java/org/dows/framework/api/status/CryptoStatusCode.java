package org.dows.framework.api.status;

import org.dows.framework.api.StatusCode;

/**
 * 签名异常状态枚举
 */
public enum CryptoStatusCode implements StatusCode {
    /**
     * 请求体转 SignatureBody 失败，请检查请求JSON格式是否正确.
     */
    REQUEST_TO_BEAN(210001, "请求内容转 SignatureBody 失败，请检查请求JSON格式是否正确."),

    /**
     * 字符串转换为输入流失败
     */
    STRING_TO_INPUT_STREAM(210002, "Signature字符串转换为输入流失败."),

    /**
     * 响应内容转 JSON 字符串出现异常
     */
    RESPONSE_TO_JSON(210003, "Signature响应内容转 JSON 字符串出现异常."),

    /**
     * 缺少必要的参数 vi
     */
    PARAM_VI_MISSING(210004, "Signature缺少必要的参数 \"vi\"，请检查JSON字段."),

    /**
     * 缺少必要的参数 data
     */
    PARAM_DATA_MISSING(210005, "Signature缺少必要的参数 \"data\"，请检查JSON字段."),

    /**
     * 缺少必要的参数 nonce
     */
    PARAM_NONCE_MISSING(210006, "Signature缺少必要的参数 \"nonce\"，请检查JSON字段."),

    /**
     * 缺少必要的参数 timestamp
     */
    PARAM_TIMESTAMP_MISSING(210007, "Signature缺少必要的参数 \"timestamp\"，请检查JSON字段."),

    /**
     * 缺少必要的参数 signStr
     */
    PARAM_SIGN_MISSING(210008, "Signature缺少必要的参数 \"signStr\"，请检查JSON字段."),

// 编码

    /**
     * 内容编码失败
     */
    ENCODING_FAILED(210009, "Signature内容编码失败."),
    /**
     * 内容解码失败
     */
    DECODING_FAILED(210010, "Signature内容编码失败."),

// 加密解密

    /**
     * 解密后数据为空
     */
    DATA_EMPTY(210011, "Signature解密后数据为空."),

    /**
     * 加密失败
     */

    ENCRYPTION_FAILED(210012, "Signature加密失败."),
    /**
     * 解密失败
     */

    DECRYPTION_FAILED(210013, "Signature解密失败."),
    /**
     * 请先配置加密或解密的必要参数
     */
    REQUIRED_CRYPTO_PARAM(210014, "Signature请先配置加密或解密的必要参数."),

    /**
     * 请先配置秘钥
     */
    NO_SECRET_KEY(210015, "Signature请先配置秘钥."),

    /**
     * 请先配置秘钥 privateKey.
     */
    NO_PRIVATE_KEY(210016, "Signature请先配置秘钥 privateKey."),

    /**
     * 请先配置秘钥 publicKey.
     */
    NO_PUBLIC_KEY(210017, "Signature请先配置秘钥 publicKey."),
// 签名

    /**
     * 签名已超时
     */
    SIGNATURE_TIMED_OUT(210018, "Signature签名已超时."),

    /**
     * 验证签名失败
     */
    VERIFY_SIGNATURE_FAILED(210019, "Signature验证签名失败."),

    /**
     * 签名失败
     */
    SIGNATURE_FAILED(210020, "Signature签名失败."),

    /**
     * 请先配置签名必要的参数
     */
    REQUIRED_SIGNATURE_PARAM(210021, "Signature请先配置签名的必要参数.");

    private Integer code;
    private String descr;

    CryptoStatusCode(Integer code, String descr) {
        this.code = code;
        this.descr = descr;

    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDescr() {
        return descr;
    }
}
