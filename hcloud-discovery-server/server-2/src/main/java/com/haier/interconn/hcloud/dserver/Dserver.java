package com.haier.interconn.hcloud.dserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * service discovery server
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/7/29  10:53
 */
@SpringBootApplication
@EnableEurekaServer
public class Dserver {

    public static void main(String[] args) {
        SpringApplication.run(Dserver.class, args);
    }
}
