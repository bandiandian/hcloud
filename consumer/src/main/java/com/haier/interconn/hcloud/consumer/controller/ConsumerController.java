package com.haier.interconn.hcloud.consumer.controller;

import com.haier.interconn.hcloud.consumer.service.gateway.UserServiceClient;
import com.haier.interconn.hcloud.consumer.service.gateway.UserServiceGateway;
import com.interconn.haier.provider.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  16:11
 */
@RestController
public class ConsumerController {

    @Autowired
    private UserServiceGateway userServiceGateway;

    @Autowired
    private Tracer tracer;

    @Autowired
    UserServiceClient userServiceClient;



    @RequestMapping(value = "sayHello",method = RequestMethod.GET)
    public  String sayHello(String name){

        Span span = this.tracer.createSpan("local:trans",new AlwaysSampler());
        String result = trans("nihao");
        span.logEvent("执行了SQL"+result);
        this.tracer.close(span);
        return userServiceGateway.sayHello(name);
    }


    @RequestMapping(value = "deleteUser",method = RequestMethod.GET)
    public  String deleteUser(String name){
        User user = userServiceGateway.deleteUser(name);
        System.out.println("user.id:"+user.getId());
        return user.getUsername();
    }

    //createUser

    @RequestMapping(value = "createUser",method = RequestMethod.GET)
    public  String createUser(String name){
        return  userServiceGateway.createUser(name);

    }


    private String trans(String s){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("--- GateWayService.trans");

        return "Result :" + s;

    }



}
