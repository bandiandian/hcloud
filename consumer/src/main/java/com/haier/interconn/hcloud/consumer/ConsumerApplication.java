package com.haier.interconn.hcloud.consumer;


import com.haier.interconn.hcloud.spring.EnableHcloud;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  14:55
 */
@SpringCloudApplication
@EnableFeignClients
@EnableHcloud
public class ConsumerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ConsumerApplication.class).web(true).run(args);
    }




    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }




}
