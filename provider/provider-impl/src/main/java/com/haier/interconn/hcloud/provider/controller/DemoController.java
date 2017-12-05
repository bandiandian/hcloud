package com.haier.interconn.hcloud.provider.controller;

import com.haier.interconn.hcloud.exception.ServiceUnavailableException;
import com.interconn.haier.provider.domain.User;
import com.interconn.haier.provider.domain.UserDto;
import com.interconn.haier.provider.domain.Users;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/8/4  15:51
 */
@Api(description = "一个栗子",tags = "/event")
@RestController
@RequestMapping("/event")
public class DemoController {

    @ApiOperation(value="创建用户", notes="根据User对象创建用户",response = UserDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "user", value = "用户实体", required = true, dataType = "User", paramType = "body")
    })
    @RequestMapping(value="/123/{id}", method= RequestMethod.GET)
    public UserDto postUser( @PathVariable  long  id) throws ServiceUnavailableException {

        throw  new ServiceUnavailableException("FUWUBUKEYONG");
//        users.put(user.getId(), user);
//        return null;
    }



    @ApiOperation(value="创建用户", notes="根据User对象创建用户",response = UserDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "user", value = "用户实体列表", required = true, dataType = "Users", paramType = "body")
    })
    @RequestMapping(value="/12", method= RequestMethod.POST)
    public UserDto postUser111(@RequestBody Users user, long  id) {
//        users.put(user.getId(), user);
        return null;
    }


}
