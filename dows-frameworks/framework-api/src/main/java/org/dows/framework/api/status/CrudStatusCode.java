package org.dows.framework.api.status;

import lombok.Setter;
import lombok.ToString;
import org.dows.framework.api.StatusCode;

/**
 * 枚举了一些常用API操作码
 */
@ToString
public enum CrudStatusCode implements StatusCode {

    CREATE_FAILSED(2002, "创建失败"),

    QUERY_MANY_RESULT(2010, "查询到多条记录"),
    QUERY_NOT_EXIT(2011, "未查询到"),

    UPDATE_BY_ID_NOT_EXIT(2031, "更新主键不存在"),
    UPDATE_FAILED(2034, "更新失败"),

    DELETE_FAILED(2006, "删除失败"),
    DELETE_SUCCESS(2005, "删除成功"),
    CREATE_SUCCESS(2001, "创建成功"),
    UPDATE_SUCCESS(2003, "更新成功"),
    ;
    private final Integer code;
    @Setter
    private String descr;

    CrudStatusCode(Integer code, String message) {
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
