package com.haier.interconn.hcloud.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-09-27  16:16
 */
@Configuration
public class EndPointAutoConfig {

    @Bean
    public SdEndpoint sdEndpoint() {
        return new SdEndpoint();
    }
}
