package com.haier.interconn.hcloud.endpoint;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-09-27  16:10
 */
public class SdInfo {

    private String serviceId;

    private String version;

    SdInfo(String serviceId,String version){
        this.serviceId = serviceId;
        this.version = version;
    }
    SdInfo(){

    }



    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
