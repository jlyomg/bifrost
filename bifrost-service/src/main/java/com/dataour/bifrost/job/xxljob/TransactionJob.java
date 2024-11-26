package com.dataour.bifrost.job.xxljob;

import com.dataour.bifrost.service.TransactionService;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生成交易记录定时任务
 */
@Slf4j
@Component
//@EnableScheduling
public class TransactionJob {
    @Autowired
    private TransactionService transactionService;

    //    @XxlJob("transactionJob")
//    @Scheduled(fixedRate = 10000)
    public ReturnT<String> run() {
        log.info("TransactionJob start...");
        transactionService.generateTransaction();
        log.info("TransactionJob end...");
        return ReturnT.SUCCESS;
    }
}


