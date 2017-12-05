package com.interconn.haier.provider.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/8/4  11:45
 */
@ApiModel(value = "User", description = "用户对象")
public class User {

    @ApiModelProperty(value = "用户名字",required = true)
    private String username;

    @ApiModelProperty(value = "用户密码",required = true)
    private String password;


    private long id;


    public long getId(){
        return id;

    }


    public  void setId(long id){
        this.id  = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
