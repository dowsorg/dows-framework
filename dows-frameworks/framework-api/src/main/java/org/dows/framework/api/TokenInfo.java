package org.dows.framework.api;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * token信息
 */
@Data
@Accessors(chain = true)
public class TokenInfo {
    private String accountId;
    private String userId;
    private String tenantId;
    private String appId;
    private String secretKey;
    private String storeNo;

}
