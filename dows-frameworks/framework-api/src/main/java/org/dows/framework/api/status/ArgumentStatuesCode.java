package org.dows.framework.api.status;

import org.dows.framework.api.StatusCode;

/**
 * 参数校验异常返回结果
 */
public enum ArgumentStatuesCode implements StatusCode {
    /**
     * 绑定参数校验异常
     */
    VALID_ERROR(460000, "参数校验异常"),
    VALIDATE_FAILED(460004, "参数检验失败"),
    API_VERSION_ERROR(460005, "API版本校验异常"),
    APP_VERSION_ERROR(460006, "APP版本号异常"),
    METHOD_PARAM_SIGN(460007, "方法参数签名错误:%s"),


    ;

    /**
     * 返回码
     */
    private final int code;
    /**
     * 返回消息
     */
    private final String descr;

    ArgumentStatuesCode(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescr() {
        return descr;
    }


}
