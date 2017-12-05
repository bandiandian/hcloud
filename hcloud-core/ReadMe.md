###分页插件使用
1. 在Serivice层进行插件的使用,不需要在mapper层进行分页
正确姿势:


    public PageInfo<MonitorAlarmInfo>  getAppListByPage(String appname){
        logger.info("appname: {}",appname);
        PageHelper.startPage(2, 12);
        List<MonitorAlarmInfo> list = monitorAlarmInfoDAO.getinfoByAppname(appname);
        //用PageInfo对结果进行包装
        PageInfo     page =new  PageInfo(list);
        return page;
    }
 
 
 在你需要进行分页的 MyBatis 查询方法前调用 PageHelper.startPage 静态方法即可，紧跟在这个方法后的第一个MyBatis 查询方法会被进行分页。
2. 什么时候会导致不安全的分页？

PageHelper 方法使用了静态的 ThreadLocal 参数，分页参数和线程是绑定的。
只要你可以保证在 PageHelper 方法调用后紧跟 MyBatis 查询方法，这就是安全的。
因为 PageHelper 在 finally 代码段中自动清除了 ThreadLocal 存储的对象
但是如果你写出下面这样的代码，就是不安全的用法:


        PageHelper.startPage(1, 10);
        List<Country> list;
        if(param1 != null){
            list = countryMapper.selectIf(param1);
        } else {
            list = new ArrayList<Country>();
        }
 
 这种情况下由于 param1 存在 null 的情况，就会导致 PageHelper 生产了一个分页参数，
 但是没有被消费，这个参数就会一直保留在这个线程上。
 当这个线程再次被使用时，就可能导致不该分页的方法去消费这个分页参数，这就产生了莫名其妙的分页。
 上面这个代码，应该写成下面这个样子：
 
 
         List<Country> list;
         if(param1 != null){
             PageHelper.startPage(1, 10);
             list = countryMapper.selectIf(param1);
         } else {
             list = new ArrayList<Country>();
         }
 
 
 



###trace持久到influxdb

默认spring boot会保留应用的最近100次访问请求记录，为了记录更多的访问次数，现在对trace做扩展,把数据持久到influxdb中

迁移influxdb需要在server机器上执行
CREATE DATABASE "mydb"
CREATE RETENTION POLICY "trace90" ON "mydb" DURATION 90d REPLICATION 1 DEFAULT
创建influxdb 的脚本

####使用说明

1，在对应的yml文件中,配置如下信息


    management:
      trace:
        include: request_headers,response_headers,errors,parameters,query_string,remote_address
        
配置trace要收集的信息,包括 request,response error,请求参数，以及远程主机ip

2,在yml中配置influxdb的url并把enable设置为true，表示可用，否则不会上传到influxdb

    
    influxdb:
      url: http://10.138.25.211:8086/
      enable: true

即可.


###fix应用注册到eurek时，避免appname重名也注册成功的问题

 实现一个clientFilter类AppNameCheckClientFilter,拦截eurka client to server的instanceinfo信息

在instanceinfo中获取appname和asgname 通过base64解码asgname 获取的值和appname一样 则通过可以注册，否则不允许注册

asgname 在微服务治理平台生成，每个微服务有唯一一个。【现在实现方式是base64】


### 服务治理页面API


1. 最近5分钟服务延迟,最小值，最大值，平均值 直接通过influxdb api获取

select  min(delay),max(delay),mean(delay) from "provider-service:9094" where time > now() - 5m order by time desc
2. 最近5分钟的调用总数

select  count(status) from "provider-service:9094" where time > now() - 5m order by time desc

3. 最近5分钟的QPS 最大值 最小值，平均值
select  count(*) from "provider-service:9094"  where time > now()  and status = 200 group by time(1m) order by time desc







    
    

 
 
 
 