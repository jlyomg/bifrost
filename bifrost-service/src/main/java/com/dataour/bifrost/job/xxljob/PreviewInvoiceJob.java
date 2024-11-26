package com.dataour.bifrost.job.xxljob;

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
public class PreviewInvoiceJob {
    @Autowired
    private InvoiceService invoiceService;

    /**
     * 每周2凌晨执行一次
     *
     * @return
     */
    @XxlJob("previewInvoiceJob")
//    @Scheduled(fixedRate = 10000)
    public ReturnT<String> generatePreviewInvoice() {
        log.info("TransactionJob start...");
        invoiceService.generatePreviewInvoice();
        log.info("TransactionJob end...");
        return ReturnT.SUCCESS;
    }
}


