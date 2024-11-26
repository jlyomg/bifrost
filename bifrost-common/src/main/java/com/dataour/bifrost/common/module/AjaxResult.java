package com.dataour.bifrost.common.module;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.dataour.bifrost.common.enums.RespCodeEnums;
import com.dataour.bifrost.common.module.response.Resp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Ajax操作消息提醒
 *
 * @Author JASON
 * @Date 2023-03-18
 */
@Data
@Slf4j
public class AjaxResult<T> implements Serializable {
    private static final String OPT_SUCCESS_MSG = "操作成功";
    private static final String OPT_FAIL_MSG = "操作失败";
    /**
     * 状态码 200-成功
     */
    private int code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 返回内容
     */
    private T Data;

    /**
     * 初始化一个新创建的 Message 对象
     */
    public AjaxResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 返回错误消息
     *
     * @return 错误消息
     */
    public static AjaxResult error() {
        return error(500, OPT_FAIL_MSG);
    }

    /**
     * 返回错误消息
     *
     * @param msg 内容
     * @return 错误消息
     */
    public static AjaxResult error(String msg) {
        return error(500, msg);
    }

    /**
     * 返回错误消息
     *
     * @param code 错误码
     * @param msg  内容
     * @return 错误消息
     */
    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg);
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static AjaxResult success() {
        return AjaxResult.success(OPT_SUCCESS_MSG);
    }

    /**
     * 返回成功消息
     *
     * @param value 内容
     * @return 成功消息
     */
    public static AjaxResult success(Object value) {
        return successData(value);
    }

    /**
     * 根据服务接口结果返回
     */
    public static AjaxResult resp(Resp<?> resp) {
        if (resp.isSuccess()) {
            return successData(resp.getData());
        } else {
            return error(resp.getCode(), resp.getMessage());
        }
    }

    /**
     * 根据服务接口结果返回
     */
    public static AjaxResult resp2Json(Resp<?> resp, String[] ignoreFields) {
        if (resp.isSuccess()) {
            return success2Json(resp.getData(), ignoreFields);
        } else {
            return error(resp.getCode(), resp.getMessage());
        }
    }

    /**
     * 将字段全部转成json对象
     */
    public static AjaxResult success2Json(Object value) {
        return success2Json(trans2JSONObject(JSONObject.parseObject(JSONObject.toJSONString(value)), null));
    }

    /**
     * 将字段全部转成json对象
     */
    public static AjaxResult success2Json(Object value, String[] ignoreFields) {
        return successData(trans2JSONObject(JSONObject.parseObject(JSONObject.toJSONString(value)), ignoreFields));
    }

    public static AjaxResult successData(Object value) {
        AjaxResult json = new AjaxResult(RespCodeEnums.SUCCESS.getCode(), OPT_SUCCESS_MSG);
        json.setData(value);
        return json;
    }

    /**
     * 将String字段转成JsonObject
     */
    public static JSONObject trans2JSONObject(JSONObject originalJSONObject, String[] ignoreFields) {
        String ignoreFieldsStr = "";
        if (ignoreFields != null) {
            ignoreFieldsStr = JSONObject.toJSONString(ignoreFields);
        }
        try {
            for (String key : originalJSONObject.keySet()) {
                if (ignoreFieldsStr.contains("\"" + key + "\"")) {
                    continue;
                }
                Object value = originalJSONObject.get(key);
                if (value instanceof String) {
                    if (StringUtils.isEmpty(value.toString())) {
                        continue;
                    }
                    String v = value.toString().trim();
                    if ((v.startsWith("[\"") && v.endsWith("\"]")) || (v.startsWith("[{") && v.endsWith("}]"))) {
                        JSONArray objects = JSONArray.parseArray(v);
                        originalJSONObject.put(key, objects);
                    } else if (v.startsWith("{") && v.endsWith("}")) {
                        JSONObject object = JSONObject.parseObject(v);
                        originalJSONObject.put(key, object);
                    }
                } else if (value instanceof JSONObject) {
                    JSONObject object = (JSONObject) value;
                    trans2JSONObject(object, ignoreFields);
                } else if (value instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) value;
                    if (!jsonArray.isEmpty()) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject o1 = jsonArray.getJSONObject(i);
                            trans2JSONObject(o1, ignoreFields);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("JSONString is {}", e, originalJSONObject);
        }
        return originalJSONObject;
    }
}