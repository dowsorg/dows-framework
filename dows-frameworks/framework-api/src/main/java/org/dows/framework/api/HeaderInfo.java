package org.dows.framework.api;

import lombok.Data;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 1/4/2022
 */
@Data
public class HeaderInfo {
    private String appId;
    private String tenantCode;
    private String callSource;
    private String appVersion;
    private String xToken;
}
