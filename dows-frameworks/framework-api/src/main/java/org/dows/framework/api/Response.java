package org.dows.framework.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.dows.framework.api.status.ArgumentStatuesCode;
import org.dows.framework.api.status.AuthStatusCode;
import org.dows.framework.api.status.CommonStatusCode;
import org.dows.framework.api.status.CrudStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 通用返回对象
 */
@Data
@ApiModel(value = "响应", description = "响应数据")
public class Response<T> implements Serializable {
    @ApiModelProperty(value = "状态码")
    private Integer code;
    @ApiModelProperty(value = "描述")
    private String descr;
    @ApiModelProperty("状态(成功:true,失败:false)")
    private Boolean status = true;

    @ApiModelProperty(value = "响应时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime timestamp = LocalDateTime.now();
    ;
    /**
     * 响应数据，为空时json序列化时会忽略
     */
    @ApiModelProperty(value = "响应数据")
    private T data;

    public Response() {
        this.code = HttpStatus.OK.value();
    }


    public Response(Integer code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    public Response(Integer code, String descr, T data) {
        this.code = code;
        this.descr = descr;
        this.data = data;
    }


    public Response(Boolean status, Integer code, String descr, T data) {
        this.code = code;
        this.descr = descr;
        this.data = data;
        this.status = status;
    }

    public Response(Boolean status, StatusCode statusCode) {
        this.status = status;
        this.code = statusCode.getCode();
        this.descr = statusCode.getDescr();
    }

    public Response(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.descr = statusCode.getDescr();
    }

    public Response(StatusCode statusCode, T data) {
        this.code = statusCode.getCode();
        this.descr = statusCode.getDescr();
        this.data = data;

    }


    public static <T> Response<T> ok() {
        Response<T> api = new Response(CommonStatusCode.SUCCESS);
        api.setStatus(true);
        return api;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Response<T> ok(T data) {
        Response<T> api = new Response(CommonStatusCode.SUCCESS);
        api.setStatus(true);
        api.setData(data);
        return api;
    }


    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public static <T> Response<T> failed(Integer code, String message) {
        return new Response<T>(false, code, message, null);
    }

    public static <T> Response<T> failed(StatusCode statusCode) {

        return new Response<T>(false, statusCode);
    }

    public static <T> Response<T> failed(String message) {
        return new Response<T>(CommonStatusCode.FAILED.getCode(), message);
    }


    /**
     * 创建失败
     * 删除失败
     * 更新失败
     * 查询失败
     */
    public static <T> Response<T> crudFailed(CrudStatusCode crudStatusCode) {
        Response<T> api = new Response(crudStatusCode);
        api.setStatus(false);
        return api;
    }


    /**
     * 参数验证失败返回结果
     *
     * @param argumentStatuesCode
     * @param <T>
     * @return
     */
    public static <T> Response<T> validateFailed(ArgumentStatuesCode argumentStatuesCode) {
        Response<T> api = new Response(argumentStatuesCode);
        api.setStatus(false);
        return api;
    }

    /**
     * 未登录返回结果
     * 未授权返回结果
     * 其他 参考 AuthStatusCode
     *
     * @param authStatusCode
     * @param <T>
     * @return
     */
    public static <T> Response<T> authFailed(AuthStatusCode authStatusCode) {
        Response<T> api = new Response(authStatusCode);
        api.setStatus(false);
        return api;
    }

    public static <T> Response<T> ok(StatusCode statusCode) {
        return new Response<T>(statusCode.getCode(), statusCode.getDescr(), null);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> Response<T> ok(T data, String message) {
        return new Response<T>(CommonStatusCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     *
     * @param statusCode 错误码
     */
    public static <T> Response<T> fail(StatusCode statusCode) {
        return new Response<T>(false, statusCode.getCode(), statusCode.getDescr(), null);
    }

    /**
     * 失败返回结果
     *
     * @param statusCode 错误码
     * @param message    错误信息
     */
    public static <T> Response<T> fail(StatusCode statusCode, String message) {
        return new Response<T>(false, statusCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Response<T> fail(String message) {
        return new Response<T>(false, CommonStatusCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> Response<T> fail() {
        return fail(CommonStatusCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> Response<T> validateFailed() {
        return fail(ArgumentStatuesCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Response<T> validateFailed(String message) {
        return new Response<T>(ArgumentStatuesCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> Response<T> unauthorized(T data) {
        return new Response<T>(AuthStatusCode.UNAUTHORIZED.getCode(), AuthStatusCode.UNAUTHORIZED.getDescr(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> Response<T> forbidden(T data) {
        return new Response<T>(AuthStatusCode.FORBIDDEN.getCode(), AuthStatusCode.FORBIDDEN.getDescr(), data);
    }

    /**
     * 统一返回
     */
    public ResponseEntity<Response<T>> responseEntity() {
        return new ResponseEntity<Response<T>>(this, HttpStatus.valueOf(this.getCode()));
    }

}
