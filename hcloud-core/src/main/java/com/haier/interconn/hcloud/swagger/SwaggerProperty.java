package com.haier.interconn.hcloud.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  15:09
 */
@ConfigurationProperties(prefix = "swagger.api")
public class SwaggerProperty {

    private String scanPackage;

    private String title;

    private  String createBy;

    private String version;

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
