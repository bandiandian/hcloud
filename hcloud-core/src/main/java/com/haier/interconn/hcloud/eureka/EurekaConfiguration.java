package com.haier.interconn.hcloud.eureka;

import com.netflix.discovery.DiscoveryClient;
import com.sun.jersey.api.client.filter.ClientFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-30  15:14
 */
@Configuration
public class EurekaConfiguration {

    @Autowired
    private Environment environment;


    @Bean
    public DiscoveryClient.DiscoveryClientOptionalArgs discoveryClientOptionalArgs(){
        ArrayList<ClientFilter> clientFilters = new ArrayList<>();
        AppNameCheckClientFilter appNameCheckClientFilter = new AppNameCheckClientFilter(environment);
        clientFilters.add(appNameCheckClientFilter);
        AppUniqueDiscoveryClientOptionalArgs appUniqueDiscoveryClientOptionalArgs = new AppUniqueDiscoveryClientOptionalArgs();
        appUniqueDiscoveryClientOptionalArgs.setAdditionalFilters(clientFilters);
        return appUniqueDiscoveryClientOptionalArgs;
    }
}
