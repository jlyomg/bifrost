package com.dataour.bifrost.job;

import com.dataour.bifrost.core.processor.bifrostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@EnableScheduling
public class LoadBillTemplateJob {
    @Autowired
    private bifrostProcessor bifrostProcessor;

    @PostConstruct
    public void init() {
        // 在应用启动时执行的逻辑
        loadClientbifrostCategory();
    }

    /**
     * 每隔60秒执行一次
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void loadClientbifrostCategory() {
        // 加载用户收费项
        bifrostProcessor.loadClientProfiles();
    }
}
