package com.haier.interconn.hcloud.trace;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置INFLUXDB 开关 url
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-17  17:37
 */
@ConfigurationProperties(prefix = "influxdb")
@Data
public class InfluxdbConfigProperties {

    private boolean enable;

    private String url;


}
