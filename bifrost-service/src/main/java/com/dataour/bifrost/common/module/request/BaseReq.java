package com.dataour.bifrost.common.module.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 基本参数
 */
@Data
public class BaseReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
}
