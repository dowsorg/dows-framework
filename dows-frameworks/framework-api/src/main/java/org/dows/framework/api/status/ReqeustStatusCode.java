package org.dows.framework.api.status;

import lombok.Setter;
import lombok.ToString;
import org.dows.framework.api.StatusCode;

/**
 * 请求相关状态码
 */
@ToString
public enum ReqeustStatusCode implements StatusCode {

    REQUEST_LIMIT(210001, "请求次数受限");


    private final Integer code;
    @Setter
    private String descr;

    ReqeustStatusCode(Integer code, String message) {
        this.code = code;
        this.descr = message;
    }

    @Override
    public String getDescr() {
        return descr;
    }

    @Override
    public Integer getCode() {
        return code;
    }

}
