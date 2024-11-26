package com.dataour.bifrost.datasource.common.param;

import lombok.Data;

import static com.dataour.bifrost.datasource.common.constant.SqlDataTypeConstants.*;

@Data
public class SqlParam {
    Integer index = 1;
    String dataType;
    Object value;

    public SqlParam setString(int index, Object x) {
        return setValue(STRING, index, x);
    }

    public SqlParam setInt(int index, Object x) {
        return setValue(STRING, index, x);
    }

    public SqlParam setLong(int index, Object x) {
        return setValue(LONG, index, x);
    }

    public SqlParam setDouble(int index, Object x) {
        return setValue(DOUBLE, index, x);
    }

    public SqlParam setFloat(int index, Object x) {
        return setValue(FLOAT, index, x);
    }

    public SqlParam setBoolean(int index, Object x) {
        return setValue(BOOLEAN, index, x);
    }

    public SqlParam setDate(int index, Object x) {
        return setValue(DATE, index, x);
    }

    public SqlParam setTime(int index, Object x) {
        return setValue(TIME, index, x);
    }

    public SqlParam setTimestamp(int index, Object x) {
        return setValue(TIME_STAMP, index, x);
    }

    public SqlParam setInputStream(int index, Object x) {
        return setValue(INPUT_STREAM, index, x);
    }

    public SqlParam setObject(int index, Object x) {
        return setValue(OBJECT, index, x);
    }

    private SqlParam setValue(String dataType, int index, Object x) {
        this.dataType = dataType;
        this.index = index;
        this.value = x;
        return this;
    }
}
