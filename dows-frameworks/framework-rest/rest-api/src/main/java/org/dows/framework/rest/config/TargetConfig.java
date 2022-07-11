package org.dows.framework.rest.config;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * 目标对象配置
 */
@Data
@Builder
@ToString
public class TargetConfig {
    /**
     * 请求地址
     */
    private String url;
    /**
     * 重试次数
     */
    @Builder.Default
    private int retryCount = RestSetting.DEFAULT_RETRY_COUNT;
    /**
     * 重试周期:5秒
     */
    @Builder.Default
    private long period = RestSetting.DEFAULT_PERIOD;
    /**
     * 数据传输处理时间
     */
    @Builder.Default
    private int socketTimeout = RestSetting.DEFAULT_SOCKET_TIMEOUT;
    /**
     * 建立连接的timeout时间
     */
    @Builder.Default
    private int connectionTimeout = RestSetting.DEFAULT_CONN_TIMEOUT;
}
