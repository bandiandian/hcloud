package com.haier.interconn.hcloud.provider.entity;

import lombok.Data;

import java.util.Date;

/**
 * 告警信息实体
 * Author: bandd
 * Mailto:bandd@haier.com
 * On:  2017/2/10 16:34
 */
@Data
public class MonitorAlarmInfo {

    private  long id;
    private String userIds; //用户ids
    private String applicationId; //应用id
    private String applicationName;
    private String  groupIds; //告警组
    private String reason;
    private String alarmLvl;
    private Date createTime;
    private String appName; //应用名称 redis zookeeper or mysql
    private String exception;
    private int type =2; // 1表示app 2 表示middle

    private String resFrom;  //资源来源 即中间件的ip
    private String alarmType; //告警类型 net表示网络 sys表示系统



}
