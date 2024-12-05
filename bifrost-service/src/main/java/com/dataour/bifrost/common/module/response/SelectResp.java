package com.dataour.bifrost.common.module.response;

import lombok.Data;

@Data
public class SelectResp {
    /**
     * 下拉框显示名称
     */
    private String label;
    /**
     * 下拉框的值
     */
    private Object value;

    public SelectResp(String label, Object value) {
        this.label = label;
        this.value = value;
    }
}
