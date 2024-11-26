package com.dataour.bifrost.common.module.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * id数组参数
 */
@Data
public class IdListParam {
    /**
     * 业务id列表
     */
    @NotNull
    private List<Long> ids;
}
