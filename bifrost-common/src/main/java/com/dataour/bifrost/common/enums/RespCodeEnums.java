package com.dataour.bifrost.common.enums;

/**
 * 返回参数状态码
 */
public enum RespCodeEnums {

    /**
     * 100-199 业务异常
     */
    BIZ_ERROR(100, "Service busy please try again later"), //
    UNKNOW_ERROR(101, "未知异常"),   //
    SYSTEM_ERROR(102, "系统异常"),   //
    PARAM_ERROR(103, "缺少参数"),    //
    DUPLICATE_PARAM(104, "参数重复"), //
    INVALID_PARAM(105, "无效参数"), //
    /**
     * 200 成功
     */
    SUCCESS(200, "操作成功"), //
    /**
     * 300-399 请求相关异常
     */
    DB_ERROR(300, "数据库繁忙"), //
    DB_DUPLICATE_DATA(301, "数据已存在"), //
    DB_DATA_OPT_ERROR(302, "数据操作错误"), //
    DB_HAS_NO_DATA(304, "数据不存在"), //
    /**
     * 400-499 请求相关异常
     */
    REQ_UNAUTHORIZED(401, "未登录或Token失效"), //
    REQ_FORBIDDEN(403, "未授权"), //
    REQ_PATH_NOT_FOUND_ERROR(404, "请求不存在"), //
    REQ_METHOD_NOT_ALLOWED(405, "请求方式错误"), //
    JWT_ERRCODE_EXPIRE(406, "token已过期"), //
    JWT_ERRCODE_FAIL(407, "token验证失败"), //
    JWT_ERRCODE_NULL(408, "token不存在"), //
    REQ_ACCOUNT_NOT_EXIST(409, "账号密码错误"), //
    /**
     * 500-599 服务器错误
     */
    SERVICE_ERROR(500, "服务器繁忙");

    RespCodeEnums(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
