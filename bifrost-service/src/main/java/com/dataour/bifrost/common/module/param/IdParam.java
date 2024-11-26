package com.dataour.bifrost.common.module.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * id参数
 */
@Data
public class IdParam {
    /**
     * 业务id
     */
    @NotNull
    private Long id;
}
