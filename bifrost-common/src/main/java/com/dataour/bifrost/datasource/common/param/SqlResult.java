package com.dataour.bifrost.datasource.common.param;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class SqlResult {
    List<JSONObject> resultList = new ArrayList<>();
    private JSONObject currentValue;
    private int currentIndex = 0;

    public boolean next() {
        if (currentIndex < resultList.size()) {
            currentValue = resultList.get(currentIndex++);
            return true;
        } else {
            return false;
        }
    }

    public void addResult(JSONObject object) {
        resultList.add(object);
    }

    public String getString(String key) {
        return currentValue.getString(key);
    }

    public Integer getInt(String key) {
        return currentValue.getInteger(key);
    }

    public Long getLong(String key) {
        return currentValue.getLong(key);
    }

    public Double getDouble(String key) {
        return currentValue.getDouble(key);
    }

    public Float getFloat(String key) {
        return currentValue.getFloat(key);
    }

    public Boolean getBoolean(String key) {
        return currentValue.getBoolean(key);
    }

    public Date getDate(String key) {
        return currentValue.getDate(key);
    }

    public Time getTime(String key) {
        return currentValue.getObject(key, Time.class);
    }

    public Timestamp getTimestamp(String key) {
        return currentValue.getObject(key, Timestamp.class);
    }

    public InputStream getInputStream(String key) {
        return currentValue.getObject(key, InputStream.class);
    }

    public Object getObject(String key) {
        return currentValue.get(key);
    }

}
