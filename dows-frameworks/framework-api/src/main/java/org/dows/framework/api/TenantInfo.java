package org.dows.framework.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantInfo {
    private Long accountId;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("租户名")
    private String tenantName;


}
