package com.haier.interconn.hcloud.monitor;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * 监控应用
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-10  15:43
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard
public class MonitorApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MonitorApplication.class).web(true).run(args);
    }

}
