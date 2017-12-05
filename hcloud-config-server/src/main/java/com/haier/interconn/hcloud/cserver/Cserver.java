package com.haier.interconn.hcloud.cserver;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Import;

/**
 *
 * 配置中心server
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/7/29  11:11
 */
@SpringBootApplication
//@EnableDiscoveryClient
@EnableConfigServer
//@Import(EurekaConfiguration.class)
public class Cserver {

    /**
     * Run the application using Spring Boot and an embedded servlet engine.
     *
     * @param args
     *            Program arguments - ignored.
     */
    public static void main(String[] args) {
        SpringApplication.run(Cserver.class, args);
    }
}