package com.haier.interconn.hcloud.provider;

import com.haier.interconn.hcloud.endpoint.EndPointAutoConfig;
import com.haier.interconn.hcloud.ribbon.CoreAutoConfiguration;
import com.haier.interconn.hcloud.spring.EnableHcloud;
import com.haier.interconn.hcloud.swagger.SwaggerConfiguration;
import com.haier.interconn.hcloud.trace.CustomTraceRespositoryConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/8/4  11:50
 */
@SpringCloudApplication
//@EnableHcloud
@EnableFeignClients
//引用dubbo服务
@ImportResource("classpath:dubbo/dubbo-consumer-demo.xml")
@Import({ CustomTraceRespositoryConfiguration.class, EndPointAutoConfig.class,CoreAutoConfiguration.class,SwaggerConfiguration.class})
public class ProviderApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ProviderApplication.class).web(true).run(args);
    }




}
