package com.interconn.haier.provider.domain;

import java.util.List;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/8/4  16:24
 */
public class Users {

    private List<User> userList;

    public  void setUserList(List<User> userList){
        this.userList = userList;
    }

    public List<User> getUserList(){
        return userList;
    }
}
