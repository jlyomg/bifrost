package com.dataour.bifrost.common.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

import static com.dataour.bifrost.common.util.ConvertUtils.toStr;

public class BigDecimalUtils {
    /**
     * 转换为BigDecimal<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Long) {
            return new BigDecimal((Long) value);
        }
        if (value instanceof Double) {
            return new BigDecimal((Double) value);
        }
        if (value instanceof Float) {
            return new BigDecimal((Double) value);
        }
        if (value instanceof Integer) {
            return new BigDecimal((Integer) value);
        }
        final String valueStr = toStr(value, null);
        if (org.apache.commons.lang3.StringUtils.isEmpty(valueStr)) {
            return defaultValue;
        }
        try {
            return new BigDecimal(valueStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static BigDecimal floatToBigDecimal(Float value) {
        if (value == null) {
            return toBigDecimal("0.00");
        }
        return toBigDecimal(value.toString());
    }

    public static BigDecimal toBigDecimal(String priceValue) {
        if (StringUtils.isEmpty(priceValue)) {
            priceValue = "0.00";
        }
        return new BigDecimal(priceValue).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal toBigDecimal(String priceValue, int scale) {
        if (StringUtils.isEmpty(priceValue)) {
            priceValue = "0";
        }
        return new BigDecimal(priceValue).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal formatPrice(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            bigDecimal = new BigDecimal(0);
        }
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal formatNumber(BigDecimal bigDecimal, int scale) {
        if (bigDecimal == null) {
            bigDecimal = new BigDecimal(0);
        }
        return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }
}
