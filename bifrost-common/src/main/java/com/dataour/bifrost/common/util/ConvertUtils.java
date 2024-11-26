package com.dataour.bifrost.common.util;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类型转换器
 *
 * @Author fc
 */
@Slf4j
public class ConvertUtils {
    /**
     * List类型转换
     *
     * @param sourceList
     * @param targetClazz
     * @param <T>
     * @return
     */
    public static <T> List trans2TargetList(List<T> sourceList, Class<?> targetClazz) {
        if (sourceList == null || sourceList.size() == 0 || targetClazz == null) {
        }
        List dataList = sourceList.stream().map(data -> {
            try {
                return BeanUtils.copyValueWithSuper(data, targetClazz.getConstructor().newInstance());
            } catch (Exception e) {
                log.error("", e);
            }
            return null;
        }).collect(Collectors.toList());
        return dataList;
    }

    /**
     * 转换为字符串<br>
     * 如果给定的值为null，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static String toStr(Object value) {
        if (value == null) {
            if (value instanceof Number) {
                return "0";
            } else {
                return "";
            }
        } else {
            return value.toString();
        }
    }

    public static String toStr(Object value, String defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    public static void main(String[] args) {
        System.out.println(toStr(""));
    }

    /**
     * 转换为Integer数组<br>
     *
     * @param str 被转换的值
     * @return 结果
     */
    public static Integer[] toIntArray(String str) {
        return toIntArray(",", str);
    }

    /**
     * 转换为Integer数组<br>
     *
     * @param split 分隔符
     * @param split 被转换的值
     * @return 结果
     */
    public static Integer[] toIntArray(String split, String str) {
        if (org.apache.commons.lang.StringUtils.isEmpty(str)) {
            return new Integer[]{};
        }
        String[] strings = str.split(split);
        final Integer[] ints = new Integer[strings.length];
        for (int i = 0; i < strings.length; i++) {
            final Integer v = toInt(strings[i], 0);
            ints[i] = v;
        }
        return ints;
    }

    /**
     * 转换为int<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Integer toInt(Object value) {
        return toInt(value, 0);
    }

    public static Integer toInt(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(valueStr.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Float toFloat(Object value) {
        return toFloat(value, 0F);
    }

    public static Float toFloat(Object value, Float defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(valueStr.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换为List<String>数组<br>
     *
     * @param str 被转换的值
     * @return 结果
     */
    public static List<String> toListStrArray(String str) {
        String[] stringArray = toStrArray(str);
        List<String> stringB = Arrays.asList(stringArray);
        return stringB;
    }


    /**
     * 转换为List<Long>数组<br>
     *
     * @param str 被转换的值
     * @return 结果
     */
    public static List<Long> toListLongArray(String str) {
        Long[] stringArray = toLongArray(str);
        List<Long> stringB = Arrays.asList(stringArray);
        return stringB;
    }


    /**
     * 转换为String数组<br>
     *
     * @param str 被转换的值
     * @return 结果
     */
    public static String[] toStrArray(String str) {
        return toStrArray(",", str);
    }

    /**
     * 转换为String数组<br>
     *
     * @param split 分隔符
     * @param split 被转换的值
     * @return 结果
     */
    public static String[] toStrArray(String split, String str) {
        return str.split(split);
    }

    /**
     * 转换为Long数组<br>
     *
     * @param str 被转换的值
     * @return 结果
     */
    public static Long[] toLongArray(String str) {
        return toLongArray(",", str);
    }

    /**
     * 转换为Long数组<br>
     *
     * @param split 是否忽略转换错误，忽略则给值null
     * @param str   被转换的值
     * @return 结果
     */
    public static Long[] toLongArray(String split, String str) {
        if (StringUtils.isEmpty(str)) {
            return new Long[]{};
        }
        String[] arr = str.split(split);
        final Long[] longs = new Long[arr.length];
        for (int i = 0; i < arr.length; i++) {
            final Long v = toLong(arr[i], null);
            longs[i] = v;
        }
        return longs;
    }

    /**
     * 转换为long<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Long toLong(Object value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr)) {
            return defaultValue;
        }
        try {
            // 支持科学计数法
            return new BigDecimal(valueStr.trim()).longValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String toStringValue(BigDecimal priceValue) {
        if (priceValue == null) {
            priceValue = new BigDecimal("0.00").setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return priceValue.toString();
    }

    public static String[] toStringArray(String value) {
        try {
            return JSONObject.parseObject(value, String[].class);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static String toStringValue(String[] value) {
        if (value == null) {
            return null;
        }
        return JSONObject.toJSONString(value);
    }

    public static Float toFloatValue(String value) {
        if (value == null) {
            return 0f;
        }
        return Float.valueOf(value);
    }
}
