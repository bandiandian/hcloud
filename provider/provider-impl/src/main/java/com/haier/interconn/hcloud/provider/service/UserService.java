package com.haier.interconn.hcloud.provider.service;

import com.github.pagehelper.PageHelper;
import com.haier.interconn.hcloud.mybatis.PageInfo;
import com.haier.interconn.hcloud.provider.dao.MonitorAlarmInfoDAO;
import com.haier.interconn.hcloud.provider.entity.MonitorAlarmInfo;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-09  10:53
 */
//@Service
public class UserService {

    @Autowired
    private Tracer tracer;

    @Autowired
    private MonitorAlarmInfoDAO monitorAlarmInfoDAO;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${file.name}")
    private  String username;


    public  String syaHelloa(){
        logger.info("username: {}",username);
        return null;
    }

//    @Transactional(rollbackFor=Exception.class)
    public PageInfo<MonitorAlarmInfo>  getAppListByPage(String appname){
//        Span span = tracer.createSpan("SQL TIME");
//        logger.info("appname: {}",appname);
//        span.logEvent(System.currentTimeMillis(),"SQL开始执行");
//        PageHelper.startPage(2, 12);
//        List<MonitorAlarmInfo> list = monitorAlarmInfoDAO.getinfoByAppname(appname);
        //用PageInfo对结果进行包装
        List<MonitorAlarmInfo> list = null;
        PageInfo     page =new  PageInfo(list);
//        span.logEvent(System.currentTimeMillis(),"SQL开始结束");
//        this.tracer.close(span);
        return page;
    }
}
