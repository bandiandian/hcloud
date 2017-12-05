package com.haier.interconn.hcloud.provider.service.geteway;
import com.interconn.haier.provider.DemoService;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  16:13
 */

@FeignClient(name = "PROVIDER-SERVICE",path = "/user")
public interface UserServiceClient extends DemoService {

}
