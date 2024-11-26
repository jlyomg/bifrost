package com.dataour.bifrost.common.module.response;

import com.dataour.bifrost.common.enums.RespCodeEnums;
import lombok.Data;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isEmpty;


/**
 * 服务层返回对象
 *
 * @param <T>
 */
@Data
public class Resp<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private int code;
    private String message;
    private boolean success;
    private T data;

    public Resp(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 成功
     *
     * @param
     * @return
     */
    public static <T> Resp<T> success() {
        return success(null);
    }

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static <T> Resp<T> success(T data) {
        Resp<T> response = new Resp<T>(RespCodeEnums.SUCCESS.getCode(), RespCodeEnums.SUCCESS.getName());
        response.data = data;
        response.success = true;
        return response;
    }

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static <T> Resp<T> success(T data, String message) {
        Resp<T> response = new Resp<T>(RespCodeEnums.SUCCESS.getCode(), isEmpty(message) ? RespCodeEnums.SUCCESS.getName() : message);
        response.data = data;
        response.success = true;
        return response;
    }

    /**
     * 通用业务失败
     */
    public static <T> Resp<T> error() {
        return new Resp<T>(RespCodeEnums.BIZ_ERROR.getCode(), RespCodeEnums.BIZ_ERROR.getName());
    }

    /**
     * 通用失败
     */
    public static <T> Resp<T> error(RespCodeEnums respCode) {
        return new Resp<T>(respCode.getCode(), respCode.getName());
    }

    /**
     * 失败
     */
    public static <T> Resp<T> error(RespCodeEnums respCode, String message) {
        return new Resp<T>(respCode.getCode(), isEmpty(message) ? respCode.getName() : message);
    }

    /**
     * 失败
     */
    public static <T> Resp<T> error(int respCode, String message) {
        return new Resp<T>(respCode, message);
    }
}
