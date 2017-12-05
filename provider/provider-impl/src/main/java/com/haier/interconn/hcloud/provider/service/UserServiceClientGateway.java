package com.haier.interconn.hcloud.provider.service;

import com.haier.interconn.hcloud.provider.service.geteway.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: bandd
 * @mailto:bandd@haier.com On: 2017-10-26  14:56
 */
@Service
public class UserServiceClientGateway {

    @Autowired
    private UserServiceClient userServiceClient;
}
