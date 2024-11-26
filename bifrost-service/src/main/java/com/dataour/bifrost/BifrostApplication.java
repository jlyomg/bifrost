package com.dataour.bifrost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

/**
 * @Author JASON
 * @Date 2023-03-18
 */
//@EnableFeignClients(basePackages = {"com.dataour.bifrost"})
//@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
public class BifrostApplication {
    public static void main(String[] args) {
        SpringApplication.run(BifrostApplication.class, args);
    }
}
