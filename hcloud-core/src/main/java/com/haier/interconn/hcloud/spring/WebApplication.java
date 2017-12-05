package com.haier.interconn.hcloud.spring;

import com.haier.interconn.hcloud.endpoint.EndPointAutoConfig;
import com.haier.interconn.hcloud.mvc.AppExceptionHandlerController;
import com.haier.interconn.hcloud.mybatis.MyBatisConfig;
import com.haier.interconn.hcloud.swagger.SwaggerConfiguration;
import com.haier.interconn.hcloud.trace.CustomTraceRespositoryConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 *
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-08  16:10
 */
@Import({SwaggerConfiguration.class,MyBatisConfig.class, CustomTraceRespositoryConfiguration.class, EndPointAutoConfig.class})
public class WebApplication {

    //exception handler
    @Bean
    public AppExceptionHandlerController appExceptionHandlerController() {
        return new AppExceptionHandlerController();
    }
}
