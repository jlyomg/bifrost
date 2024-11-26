package com.dataour.bifrost.common.util;

import com.dataour.bifrost.common.enums.DataTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * 数据工具类
 *
 * @Author JASON
 * @Date 2024-01-11 21:23
 */
@Slf4j
@Data
public class DataUtils {

    public static Boolean dataEmpty(Object... data) {
        if (data == null) {
            return true;
        }
        for (Object obj : data) {
            if (dataNotEmpty(obj)) {
                return false;
            }
        }
        return true;
    }

    public static Boolean dataEmpty(Object data) {
        if (data == null) {
            return true;
        } else if (data instanceof String) {
            return data.toString().trim().length() == 0;
        } else if (data instanceof Number) {
            return ((Number) data).doubleValue() == 0;
        } else if (data instanceof Collection) {
            return ((Collection) data).size() == 0;
        }
        return false;
    }

    public static Boolean dataNotEmpty(Object data) {
        return !dataEmpty(data);
    }

    public static int compareData(String data, String dataType, String value) {
        if (value == null && data == null) {
            return 0;
        }
        if (data != null && value == null) {
            return 1;
        }
        if (data == null && value != null) {
            return -1;
        }
        if (value != null && data != null) {
            if (DataTypeEnum.Integer.getCode().equals(dataType)) {
                return Integer.valueOf(data).compareTo(Integer.valueOf(value));
            } else if (DataTypeEnum.Long.getCode().equals(dataType)) {
                return Long.valueOf(data).compareTo(Long.valueOf(value));
            } else if (DataTypeEnum.Float.getCode().equals(dataType)) {
                return Float.valueOf(data).compareTo(Float.valueOf(value));
            } else if (DataTypeEnum.Double.getCode().equals(dataType)) {
                return Double.valueOf(data).compareTo(Double.valueOf(value));
            } else if (DataTypeEnum.BigDecimal.getCode().equals(dataType)) {
                return new BigDecimal(data).compareTo(new BigDecimal(value));
            }
        }
        return 0;
    }

    /**
     * 是否在区间范围
     *
     * @param data
     * @param fromValue
     * @param toValue
     * @return
     */
    public static Boolean compareRangeData(String data, String dataType, String fromValue, String toValue) {
        if (StringUtils.isEmpty(data)) {
            return false;
        }
        if (fromValue == null && toValue != null) {
            if (compareData(data, dataType, toValue) == 1) {
                return false;
            } else {
                return true;
            }
        }
        if (fromValue != null && fromValue == null) {
            if (compareData(data, dataType, fromValue) == -1) {
                return false;
            } else {
                return true;
            }
        }
        if (fromValue != null && toValue != null) {
            if (compareData(data, dataType, fromValue) > -1 && compareData(data, dataType, toValue) < 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
