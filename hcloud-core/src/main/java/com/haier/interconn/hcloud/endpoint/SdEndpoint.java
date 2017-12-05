package com.haier.interconn.hcloud.endpoint;

import com.haier.interconn.hcloud.ribbon.VersionServerListFilter;
import org.springframework.boot.actuate.endpoint.Endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  服务依赖关系endpoint
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-09-27  16:09
 */
public class SdEndpoint implements Endpoint<List<SdInfo>> {

    @Override
    public String getId() {
        return "sd";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return false;
    }

    @Override
    public List<SdInfo> invoke() {
        List<SdInfo>  sdInfoList = new ArrayList<>();
        Map<String,String> versionInfo = VersionServerListFilter.refercenServiceInfo ;
        versionInfo.forEach((k,v)->sdInfoList.add(new SdInfo(k,v)));
        return sdInfoList;
    }
}
