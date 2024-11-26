//package com.dataour.bifrost.config;
//
//import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
//import groovy.util.logging.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//public class XxlJobConfig {
//
//    @Value("${xxl.job.admin.addresses}")
//    private String adminAddresses;
//
//    @Value("${xxl.job.accessToken:#{null}}")
//    private String accessToken;
//
//    @Value("${xxl.job.executor.appname:#{null}}")
//    private String appname;
//
//    @Value("${xxl.job.executor.address:#{null}}")
//    private String address;
//
//    @Value("${xxl.job.executor.ip:#{null}}")
//    private String ip;
//
//    @Value("${xxl.job.executor.port:#{0}}")
//    private int port;
//
//    @Value("${xxl.job.executor.logpath:#{null}}")
//    private String logPath;
//
//    @Value("${xxl.job.executor.logretentiondays:#{30}}")
//    private int logRetentionDays;
//
//
//    @Bean
//    public XxlJobSpringExecutor xxlJobExecutor() {
//        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
//        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
//        xxlJobSpringExecutor.setAppname(appname);
//        xxlJobSpringExecutor.setAddress(address);
//        xxlJobSpringExecutor.setIp(ip);
//        xxlJobSpringExecutor.setPort(port);
//        xxlJobSpringExecutor.setAccessToken(accessToken);
//        xxlJobSpringExecutor.setLogPath(logPath);
//        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
//        return xxlJobSpringExecutor;
//    }
//}
//
//
