package org.dows.framework.api.status;

import lombok.Getter;
import org.dows.framework.api.StatusCode;

public enum MqStatusCode implements StatusCode {

    COMUSER_EXCEPTION(461000, "MQ消费异常:%s"),
    PRODUCER_EXCEPTION(461001, "MQ生产异常:%s");

    @Getter
    private Integer code;
    @Getter
    private String descr;

    MqStatusCode(Integer code, String descr) {
        this.code = code;
        this.descr = descr;
    }
}
