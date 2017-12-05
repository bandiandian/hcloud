package com.haier.interconn.hcloud.trace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-25  11:49
 */

@EnableConfigurationProperties(InfluxdbConfigProperties.class)
@Configuration
public class CustomTraceRespositoryConfiguration {

    @Autowired
    private InfluxdbConfigProperties influxdbConfigProperties;

    @Bean
    public  InfluxdbTraceRepository traceRepository(InfluxdbConfigProperties influxdbConfigProperties){

       return  new InfluxdbTraceRepository(influxdbConfigProperties.isEnable(),influxdbConfigProperties.getUrl());
    }



    @Bean
    public CustomWebRequestTraceFilter customWebRequestTraceFilter(TraceRepository traceRepository,TraceProperties traceProperties, ErrorAttributes errorAttributes) {
        CustomWebRequestTraceFilter filter = new CustomWebRequestTraceFilter(traceRepository,
                traceProperties);
        if (errorAttributes != null) {
            filter.setErrorAttributes(errorAttributes);
        }
        return filter;
    }



}
