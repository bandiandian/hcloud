package com.haier.interconn.hcloud.consumer.service.gateway;

import com.haier.interconn.hcloud.exception.RemoteCallException;
import com.interconn.haier.provider.domain.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  16:15
 */
@Service
public class UserServiceGateway {

    protected Logger logger = LoggerFactory.getLogger(UserServiceGateway.class);

    @Autowired
    UserServiceClient userServiceClient;



//    @HystrixCommand(ignoreExceptions = RemoteCallException.class)
    public String sayHello(String name) {
        logger.info("sayHello in UserServiceGateway {}",name);
       return   userServiceClient.sayHello("bandd");
//        return null;
    }


//    @HystrixCommand(ignoreExceptions = RemoteCallException.class)
    public User deleteUser(String name) {
        logger.info("deleteUser in UserServiceGateway {}",name);
        User user   = new User();
        user.setUsername("computer");
        user.setPassword("1230");
        //createUser
        return   userServiceClient.deleteByName("12345",user);
//        return null;
    }


    @HystrixCommand(ignoreExceptions = RemoteCallException.class)
    public String createUser(String name) {
        logger.info("createUser in UserServiceGateway {}",name);
        User user   = new User();
        user.setUsername("computer11111");
        user.setPassword("1230");
        //createUser

        User user1 = userServiceClient.findById(123L);
        logger.info("user1.username {}",user1.getUsername());
        logger.info("user1.password {}",user1.getPassword());
        logger.info("user1.id {}",user1.getId());
        return   userServiceClient.createUser(user);
//        return null;
    }

}
