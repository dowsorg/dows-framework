package org.dows.framework.api.status;

import lombok.Getter;
import lombok.Setter;
import org.dows.framework.api.StatusCode;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/29/2022
 */
public enum RestStatusCode implements StatusCode {
    ReadResponseBodyException(11, "读取响应异常"),

    RetrofitBlockException(12, "RetrofitBlockException"),

    ServiceInstanceChooseException(13, "ServiceInstanceChooseException , No valid service instance selector, Please configure it!");


    @Getter
    final Integer code;
    @Setter
    @Getter
    String descr;

    RestStatusCode(Integer code, String descr) {
        this.code = code;
        this.descr = descr;
    }
}
