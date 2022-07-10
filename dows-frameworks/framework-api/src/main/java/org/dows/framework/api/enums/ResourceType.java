package org.dows.framework.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ResourceType {
    // 1目录, 2菜单, 3按钮, 4链接
    directory(1, "目录"), menu(2, "菜单"), button(3, "按钮"),
    link(4, "链接");
    private Integer value;
    private String desc;
}
