package org.dows.crypto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应体、请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class CryptoBody implements Serializable {
    /**
     * 数据体
     */
    private String data;

    /**
     * 偏移量
     */
    private String iv;

    /**
     * 随机字符串
     */
    private String nonce;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 签名
     */
    private String signature;

    /**
     * 应用Key
     */
    private String appKey;

}
