package com.dataour.bifrost.common.module.response;

import com.dataour.bifrost.domain.TransactionDO;
import lombok.Data;

/**
 * 收费模版接口
 *
 * @Author JASON
 * @Date 2023-12-16 10:10
 */
@Data
public class TransactionResp extends TransactionDO {
    /**
     * 收费项
     */
    private BillCategoryResp billCategory;
}