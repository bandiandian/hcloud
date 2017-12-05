package com.haier.interconn.hcloud.provider.service;

import com.interconn.haier.provider.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  10:57
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Value("${file.name}")
    private  String username;

    @Test
    public void testSyaHelloa() throws Exception {
        System.out.println("f:"+username);
        userService.syaHelloa();

    }
}