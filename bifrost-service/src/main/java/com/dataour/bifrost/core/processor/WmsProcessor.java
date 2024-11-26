package com.dataour.bifrost.core.processor;

import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.common.module.response.BillCategoryResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * WMS 对接服务
 *
 * @Author: JASON
 * @Date: 2024-01-11 10:38
 * @Description:
 */
@Slf4j
@Component
public class WmsProcessor {
    public Resp<List<BillCategoryResp>> getPurchaseOrderIds() {
        return null;
    }

    public Resp<List<BillCategoryResp>> getOrderIds() {
        return null;
    }

    public Resp<List<BillCategoryResp>> getReturnOrderIds() {
        return null;
    }
}
