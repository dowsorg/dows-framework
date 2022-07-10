package org.dows.framework.api;

import lombok.Data;

import java.util.List;

@Data
public class AccountInfo {
    private Long id;
    private String accountName;
    private String avatar;
    private Integer status;
    private String loginName;
    private String phone;
    private String email;
    private String employeeNo;
    private String password;
    private Long tenantId;
    private String tenantName;
    private Integer isPlatform;
    private TenantInfo currentTenant;
    private List<TenantInfo> tenantList;
}
