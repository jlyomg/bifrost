package com.dataour.bifrost.common.util;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.dataour.bifrost.common.util.BigDecimalUtils.toBigDecimal;

/**
 * Bean对象转换工具类
 *
 * @Author: JASON
 * @Date: 2023-3-20 11:30
 */
@Slf4j
public class BeanUtils {
    /**
     * 拷贝两个对象的同名同类型变量值
     *
     * @param src
     * @param target
     */
    public static <T> T copyValue(Object src, T target) {
        return copy(src, target, false);
    }

    /**
     * 拷贝两个对象的同名同类型变量值
     *
     * @param src
     * @param target
     */
    public static <T> T copyValue(Object src, Class<T> target) {
        try {
            return copy(src, target.newInstance(), false);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 拷贝两个对象的同名同类型变量值
     *
     * @param src
     * @param target
     */
    public static <T> T copyValueWithSuper(Object src, T target) {
        return copy(src, target, true);
    }


    /**
     * 拷贝两个对象的同名同类型变量
     *
     * @param src
     * @param target
     * @param withSuper
     */
    private static <T> T copy(Object src, T target, boolean withSuper) {
        if (src == null || target == null) {
            return target;
        }
        try {
            Map<String, Field> srcFieldMap = getAssignableFieldsMap(src, withSuper);
            Map<String, Field> targetFieldMap = getAssignableFieldsMap(target, withSuper);
            for (String srcFieldName : srcFieldMap.keySet()) {
                Field srcField = srcFieldMap.get(srcFieldName);
                // 变量名要相同
                if (srcField == null || !targetFieldMap.keySet().contains(srcFieldName)) {
                    continue;
                }
                Field targetField = targetFieldMap.get(srcFieldName);
                // 类型要相同
                Class<?> srcFieldType = srcField.getType();
                Class<?> targetFieldType = targetField.getType();
                if (srcFieldType == null || targetField == null) {
                    continue;
                }
                // 处理String和BigDecimal之间转换
                if (srcFieldType.getSimpleName().equals("BigDecimal") || targetFieldType.getSimpleName().equals("BigDecimal")) {
                    if (srcFieldType.getSimpleName().equals("BigDecimal") && targetFieldType.getSimpleName().equals("BigDecimal")) {
                        targetField.set(target, srcField.get(src));
                    } else if (srcFieldType.getSimpleName().equals("BigDecimal") && targetFieldType.getSimpleName().equals("String")) {
                        targetField.set(target, ConvertUtils.toStringValue((BigDecimal) srcField.get(src)));
                    } else if (srcFieldType.getSimpleName().equals("String") && targetFieldType.getSimpleName().equals("BigDecimal")) {
                        targetField.set(target, toBigDecimal((String) srcField.get(src)));
                    }
                    // 处理String和String[]之间转换
                } else if (srcFieldType.getSimpleName().equals("String[]") || targetFieldType.getSimpleName().equals("String[]")) {
                    if (srcFieldType.getSimpleName().equals("String[]") && targetFieldType.getSimpleName().equals("String[]")) {
                        targetField.set(target, srcField.get(src));
                    } else if (srcFieldType.getSimpleName().equals("String[]") && targetFieldType.getSimpleName().equals("String")) {
                        targetField.set(target, ConvertUtils.toStringValue((String[]) srcField.get(src)));
                    } else if (srcFieldType.getSimpleName().equals("String") && targetFieldType.getSimpleName().equals("String[]")) {
                        targetField.set(target, ConvertUtils.toStringArray((String) srcField.get(src)));
                    }
                } else {
                    if (!srcFieldType.equals(targetFieldType)) {
                        continue;
                    }
                    targetField.set(target, srcField.get(src));
                }
            }
        } catch (Exception e) {
            log.error("Copy field value error, src:{}, target:{}", e, JSONObject.toJSONString(src), JSONObject.toJSONString(target));
        }
        return target;
    }

    private static Map<String, Field> getAssignableFieldsMap(Object obj, boolean withSuper) {
        if (obj == null) {
            return new HashMap<>(0);
        }
        Map<String, Field> fieldMap = getStringFieldMap(obj.getClass());
        if (withSuper) {
            // 父类字段
            Class<?> superclass = obj.getClass().getSuperclass();
            if (superclass != null && !(superclass.getSimpleName().equals("Object"))) {
                fieldMap.putAll(getStringFieldMap(superclass));
            }
        }
        return fieldMap;
    }

    private static Map<String, Field> getStringFieldMap(Class<?> aClass) {
        Field[] fields = aClass.getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            // 过滤不需要拷贝的属性
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }
}
