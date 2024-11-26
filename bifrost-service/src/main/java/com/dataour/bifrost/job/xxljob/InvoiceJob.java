package com.dataour.bifrost.job.xxljob;

import com.dataour.bifrost.common.enums.OptTypeEnum;
import com.dataour.bifrost.service.InvoiceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生成交易记录定时任务
 */
@Slf4j
@Component
//@EnableScheduling
public class InvoiceJob {
    @Autowired
    private InvoiceService invoiceService;

    /**
     * 每周执行一次
     *
     * @return
     */
    @XxlJob("invoiceJob")
//    @Scheduled(fixedRate = 5000)
    public ReturnT<String> generateInvoice() {
        log.info("TransactionJob start...");
        invoiceService.generateInvoiceAll(OptTypeEnum.System.getCode());
        log.info("TransactionJob end...");
        return ReturnT.SUCCESS;
    }
}


