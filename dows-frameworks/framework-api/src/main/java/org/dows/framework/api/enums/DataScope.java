package org.dows.framework.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限枚举
 */
@Getter
@AllArgsConstructor
public enum DataScope {

    ALL("全部", "全部的数据权限"),

    THIS_LEVEL("本级", "自己机构的数据权限"),

    CUSTOMIZE("自定义", "自定义的数据权限");

    private final String value;
    private final String description;

    public static DataScope find(String val) {
        for (DataScope dataScopeEnum : DataScope.values()) {
            if (val.equals(dataScopeEnum.getValue())) {
                return dataScopeEnum;
            }
        }
        return null;
    }

}
