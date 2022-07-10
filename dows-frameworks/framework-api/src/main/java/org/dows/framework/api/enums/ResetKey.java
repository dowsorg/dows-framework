package org.dows.framework.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 7/10/2022
 */
@Getter
@AllArgsConstructor
public enum ResetKey {
    /**
     * 验证码业务场景对应的 Redis 中的 key
     */
    /* 通过手机号码重置邮箱 */
    PHONE_RESET_EMAIL_CODE("phone_reset_email_code_", "通过手机号码重置邮箱"),

    /* 通过旧邮箱重置邮箱 */
    EMAIL_RESET_EMAIL_CODE("email_reset_email_code_", "通过旧邮箱重置邮箱"),

    /* 通过手机号码重置密码 */
    PHONE_RESET_PWD_CODE("phone_reset_pwd_code_", "通过手机号码重置密码"),

    /* 通过邮箱重置密码 */
    EMAIL_RESET_PWD_CODE("email_reset_pwd_code_", "通过邮箱重置密码");

    private final String code;
    private final String description;

    public static ResetKey find(Integer code) {
        for (ResetKey value : ResetKey.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
