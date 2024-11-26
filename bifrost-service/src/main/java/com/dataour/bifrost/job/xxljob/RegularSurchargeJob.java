package com.dataour.bifrost.job.xxljob;

import com.dataour.bifrost.service.RegularSurchargeService;
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
public class RegularSurchargeJob {
    @Autowired
    private RegularSurchargeService regularSurchargeService;

    /**
     * 每周2凌晨执行一次
     *
     * @return
     */
    @XxlJob("Bill_RegularSurchargeJob")
//    @Scheduled(fixedRate = 10000)
    public ReturnT<String> execute() {
        log.info("RegularSurchargeJob start...");
        regularSurchargeService.execute();
        log.info("RegularSurchargeJob end.");
        return ReturnT.SUCCESS;
    }
}


