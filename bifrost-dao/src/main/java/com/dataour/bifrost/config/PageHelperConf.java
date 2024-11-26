package com.dataour.bifrost.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 分页插件配置类
 *
 * @Author fuce
 * @ClassName: PageHelperConf
 * @Description: 这里用一句话描述这个类的作用
 * @Date 2018年6月3日
 */
@Configuration
public class PageHelperConf {

    /**
     * 分页插件处理
     *
     * @return
     */
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("dialect", "mysql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
