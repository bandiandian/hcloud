package com.haier.interconn.hcloud.consumer.service.gateway;

import com.haier.interconn.hcloud.feign.config.FeignConfig;
import com.interconn.haier.provider.DemoService;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  16:13
 */

@FeignClient(name= "backend-zuul",path = "/user",configuration = FeignConfig.class)
public interface  UserServiceClient extends DemoService {

}
