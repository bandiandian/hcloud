package com.haier.interconn.hcloud.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-17  17:07
 */
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class MyDataSourceConfigProperties {

    private String driverClassName;

    private String jdbcUrl;

    private String username;

    private String password;

    private int  idleTimeout;

    private int  connectionTimeout;

    private int  maxLifetime;

    private int maximumPoolSize;



}
