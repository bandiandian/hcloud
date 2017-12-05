package com.haier.interconn.hcloud.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-09-25  11:04
 */
@Configuration
public class DefaultRibbonConfiguration {
    @Value("${ribbon.client.name:#{null}}")
    private String name;

    @Autowired(required = false)
    private IClientConfig config;

    @Autowired
    private PropertiesFactory propertiesFactory;

    @Autowired
    private Environment environment;


    @Bean
    public ServerListFilter serverListFilter(){

        if (StringUtils.isEmpty(name)) {
            return null;
        }

        if (this.propertiesFactory.isSet(ServerListFilter.class, name)) {
            return this.propertiesFactory.get(ServerListFilter.class, config, name);
        }

        // 默认配置
        VersionServerListFilter serverListFilter = new VersionServerListFilter<DiscoveryEnabledServer>(environment);
        serverListFilter.initWithNiwsConfig(config);
        return serverListFilter;

    }

    @Bean
    public IRule ribbonRule() {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        if (this.propertiesFactory.isSet(IRule.class, name)) {
            return this.propertiesFactory.get(IRule.class, config, name);
        }

        // 默认配置
        LabelAndWeightMetadataRule rule = new LabelAndWeightMetadataRule();
        rule.initWithNiwsConfig(config);
        return rule;
    }
}
