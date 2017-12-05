package com.haier.interconn.hcloud.provider.controller;

import com.haier.interconn.hcloud.provider.service.UserService;
import com.haier.interconn.hcloud.provider.service.UserServiceClientGateway;
import com.haier.profiler.project.DTO.ProjectInfoData;
import com.haier.profiler.project.service.ProfilerProjectService;
import com.interconn.haier.provider.DemoService;
import com.interconn.haier.provider.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/8/4  13:15
 */
@RestController
@Api("用户模块")
public class UserController implements DemoService{

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

//    @Autowired
    private UserService userService;


//    @Autowired
    private UserServiceClientGateway userServiceClientGateway;

    @Autowired
    private ProfilerProjectService profilerProjectService;



    @ApiOperation(value="根据用户名问声好", notes="")
    @ApiImplicitParam(name = "name", value = "用户名称", required = true, dataType = "String")
    @Override
    public String sayHello(String name) {

        List<ProjectInfoData> projectInfoDataList =   profilerProjectService.getProjectInfoFromPaaS();

        logger.info("projectInfoDataList.sizee: {}",projectInfoDataList.size());

        logger.info("sayHello method:        1   {}",name);
//        PageInfo<MonitorAlarmInfo> page = userService.getAppListByPage("os");
//
//        logger.info("getPageNum"+page.getPageNum());
//        logger.info("getPageSize"+ page.getPageSize());
//        logger.info("getStartRow"+page.getStartRow());
//        logger.info("getEndRow"+ page.getEndRow());
//        logger.info("getTotal"+ page.getTotal());
//        logger.info("getPages"+ page.getPages());
//        logger.info("getFirstPage"+ page.getFirstPage());
//        logger.info("getLastPage"+page.getLastPage());
//        logger.info("isIsFirstPage"+ page.isIsFirstPage());
//        logger.info("isIsFirstPage"+ page.isIsFirstPage());
//        logger.info("isHasPreviousPage"+ page.isHasPreviousPage());
//        logger.info("isHasNextPage"+ page.isHasNextPage());
        return "Hello "+name;
    }


    @ApiOperation(value="创建新用户", notes="更新用户信息")
    @ApiImplicitParam(name = "user", value = "用户实体信息", required = true,dataType = "User")
    @Override
    public String createUser(@RequestBody  User user) {

        logger.info("user.username {}",user.getUsername());
        logger.info("user.password {}",user.getPassword());
        logger.info("user.id {}",user.getId());
        return user.getUsername();
    }

    @ApiOperation(value="更新用户信息", notes="更新用户信息",response = User.class)
    @ApiImplicitParam(name = "user", value = "用户实体信息", required = true,dataType = "User")
    @Override
    public User updateUser(@RequestBody User user) {
        return null;
    }


    @ApiOperation(value="根据用户名称获取用户实体信息", notes="根据用户名称获取用户实体信息",response = User.class)
    @ApiImplicitParam(name = "name", value = "用户名称 不超过15个汉字", required = true,dataType = "String")
    @Override
    public User findByName(String name) {
        return null;
    }




    @Override
    @RequestMapping(value = "/deleteByName", method = RequestMethod.DELETE)
    public User  deleteByName(@RequestParam("id") String id,@RequestBody User user){
        logger.info("id:{}",id);
        logger.info("user.username:{}",user.getUsername());
        logger.info("user.password:{}",user.getPassword());
        user.setId(123);
        return user;
    }

    @Override
    public User findById(@PathVariable("id") Long id) {
        System.out.println("id:"+id);
        User user = new User();
        user.setId(id);
        user.setUsername("computer-x");
        user.setPassword("s");
        return user;
    }

    @Override
    public String uploadMultiFile(@RequestParam(value = "file", required = true)MultipartFile file) {

        logger.info("filename:"+file.getName());
        try {
            byte[] bytes =  file.getBytes();
            File fileToSave = new File(file.getOriginalFilename());
            FileCopyUtils.copy(bytes, fileToSave);
            return fileToSave.getAbsolutePath();
        }catch (IOException e){
            e.printStackTrace();
        }


        logger.info("filesize:{}",file.getSize());



        return "200";
    }
}
