package org.dows.framework.api.enums;

/**
 * @desc 调用来源枚举类
 */
public enum RefSource {
    /**
     * WEB网站
     */
    WEB,
    /**
     * PC客户端
     */
    PC,
    /**
     * 微信公众号
     */
    WECHAT,
    /**
     * IOS平台
     **/
    IOS,
    /**
     * 安卓平台
     */
    ANDROID;

    public static boolean isValid(String name) {
        for (RefSource callSource : RefSource.values()) {
            if (callSource.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
