package org.dows.framework.rest.interceptor;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/4/2022
 */
public enum InterceptTyp {
    BALANCE,
    DEGRADE,
    LOGGING,
    RETRY,
    FILTER,
    CUMSTOM;

    public String prefix() {
        return this.name() + "#";
    }

}
