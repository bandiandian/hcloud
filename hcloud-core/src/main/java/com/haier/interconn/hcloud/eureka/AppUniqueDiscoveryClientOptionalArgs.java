package com.haier.interconn.hcloud.eureka;

import com.netflix.discovery.DiscoveryClient;
import com.sun.jersey.api.client.filter.ClientFilter;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-30  14:55
 */
public class AppUniqueDiscoveryClientOptionalArgs extends DiscoveryClient.DiscoveryClientOptionalArgs {
    private Collection<ClientFilter> additionalFilters;

    @Override
    public void setAdditionalFilters(Collection<ClientFilter> additionalFilters) {
        additionalFilters = new LinkedHashSet<ClientFilter>(additionalFilters);
        this.additionalFilters = additionalFilters;
        super.setAdditionalFilters(additionalFilters);
    }

    public Collection<ClientFilter> getAdditionalFilters() {
        return this.additionalFilters;
    }

}
