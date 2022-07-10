package org.dows.framework.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LoginType {
    login(0, "登录名"), phone(1, "手机号"), email(2, "邮箱"),
    employeeNo(3, "员工号");
    private Integer value;
    private String desc;

    public static LoginType of(Integer value) {
        for (LoginType item : LoginType.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return LoginType.login;
    }

}
