package com.haier.interconn.hcloud.feign.config;

import feign.Logger;
import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  17:38
 */
@Configuration
public class FeignConfig {

    @Autowired
    private Environment environment;

    private final  String DEBUG = "debug";

    @Bean
    Logger.Level feignLoggerLevel() {
        String loglevel =environment.getProperty("feign.logger.level");
        if(!StringUtils.isEmpty(loglevel)&&DEBUG.equals(loglevel)){
            return Logger.Level.FULL;
        }else{
            return Logger.Level.NONE;
        }


    }


    @Bean
    public Request.Options options(){
        return new Request.Options(10*1000,60*1000);
    }

}
