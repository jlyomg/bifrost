package com.dataour.bifrost.common.util;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Date;

import static com.dataour.bifrost.common.constant.SystemConstants.OPERATOR_SYSTEM;


/**
 * 参数工具类
 */
@Slf4j
@Data
public class ParamUtils {

    public static final String PARAM_ASC = "ASC";
    public static final String PARAM_DESC = "DESC";
    public static final String PARAM_CLIENT_ORDER_ID = "clientOrderId";
    public static final String PARAM_RETURN_ORDER_ID = "returnOrderId";
    public static final String PARAM_CONTAINER_ID = "containerId";

    public static String concatLikeParam(String param) {
        return "%" + param + "%";
    }

    public static String concatLikeParamWithQuotation(String param) {
        return "%" + concatQuotation(param) + "%";
    }

    public static String concatQuotation(String param) {
        return "\"" + param + "\"";
    }

    public static <T> T fillAddParams(T params) {
        Date now = new Date();
        String operator = getOperator();
        setValue(params, "gmtCreate", now);
        setValue(params, "gmtUpdate", now);
        setValue(params, "createdBy", operator);
        setValue(params, "updatedBy", operator);
        if (hasField(params, "isDeleted")) {
            setValue(params, "isDeleted", (byte) 0);
        }
        return params;
    }

    public static <T> T fillUpdateParams(T params) {
        Date now = new Date();
        setValue(params, "gmtUpdate", now);
        setValue(params, "updatedBy", getOperator());
        return params;
    }

    private static void setValue(Object params, String parmName, Object value) {
        try {
            Field field = params.getClass().getDeclaredField(parmName);
            field.setAccessible(true);
            field.set(params, value);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private static boolean hasField(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                return true;
            }
        } catch (NoSuchFieldException e) {
            return false;
        }
        return false;
    }

    public static String genAttributes(String key, String value) {
        return genAttributes(null, key, value);
    }

    public static String genAttributes(String attributes, String key, String value) {
        if (DataUtils.dataEmpty(key) || DataUtils.dataEmpty(value)) {
            return attributes;
        }
        JSONObject data = new JSONObject();
        if (DataUtils.dataNotEmpty(attributes)) {
            try {
                data = JSONObject.parseObject(attributes);
            } catch (Exception e) {
                log.error("genAttributes error", e);
                data = new JSONObject();
            }
        }
        data.put(key, value);
        return JSONObject.toJSONString(data);
    }

    public static JSONObject getAttributes(String attributes) {
        if (DataUtils.dataEmpty(attributes)) {
            return new JSONObject();
        }
        try {
            return JSONObject.parseObject(attributes);
        } catch (Exception e) {
            log.error("genAttributes error", e);
        }
        return new JSONObject();
    }

    /**
     * get the attribute value from json string
     *
     * @param attributes json string
     * @param key        the attribute key
     * @return the attribute value if exists, otherwise return empty string
     */
    public static String getAttributeFromJson(String attributes, String key) {
        if (DataUtils.dataEmpty(attributes) || DataUtils.dataEmpty(key)) {
            return null;
        }
        try {
            JSONObject data = JSONObject.parseObject(attributes);
            return data.getString(key);
        } catch (Exception e) {
            log.error("getAttributesFromJson error", e);
        }
        return "";
    }

    private static String getOperator() {
        return OPERATOR_SYSTEM;
    }
}
