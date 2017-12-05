package com.haier.interconn.hcloud.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-17  17:37
 */
@ConfigurationProperties(prefix = "mybatis")
@Data
public class MyBatisConfigProperties {

    private String mapperLocations;

    private String dialect;

    private String  reasonable; //针对分页 如果是true表示page>最大页数时，返回最后一页内容

    private  String  pageSizeZero; //针对分页 true：表示如果pagesize=0,会查询所有的记录
}
