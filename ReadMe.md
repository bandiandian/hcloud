###Feign客户端使用注意
1,有FeignClient注解的接口，方法入参必须绑定spring mvc的注解 @RequestParam 或者@RequestBody
否则无法把参数值发送出去

2,针对一个FeignClient要定义FeignConfig时，最好重新生成Request.Options这个Bean

    @Bean
    public Request.Options options(){
        return new Request.Options(10*1000,60*1000);
    }
    
 Feign的默认request超时时间是10s,读超时时间是60s,如果不重写会的话 读超时会是10s，不是默认的60s

###使用Swagger自动生成微服务目录

1,针对spring cloud 工程 需要在pom文件中引入hcloud-core-1.0.1-SNAPSHOT.jar

        <dependency>
            <groupId>com.haier.interconn</groupId>
            <artifactId>hcloud-core</artifactId>
            <version>1.0.1-SNAPSHOT</version>
        </dependency>

2,在工程的启动类上接入@Import({WebApplication.class})

3,在工程对应的yml文件中配置以下信息，为了声明该应用暴露的服务信息

        swagger:
          api:
            scanPackage: com.haier.interconn.hcloud.provider.controller
            title: 服务提供方应用服务API
            createBy: 班点点
            version: 1.0.0-snapshot

参数说明
 scanPackage:需要扫描的包路径
 title：标题说明
 createBy:创建人
 version:版本

4,在需要生成api的controller类中，加入注解@Api,@ApiOperation,@ApiImplicitParam,即可
 每个注解的详细说明可以参考以下地址
 http://www.jianshu.com/p/12f4394462d5
 
 5,启动工程，访问ip;port/swagger-ui.html即可查看该工程的微服务API说明文档
 
### Archaius动态配置解决方案设计初稿

通过netflix提供的archaius组件进行微服务应用动态参数调整
![Image text](file:/D:workwork/projects/spring-cloud-demo/hcloud/docs/archaius-dynamic.png)


通过微服务中加入archaius读取远程的additionUrl远程的配置文件
https://github.com/Netflix/archaius/wiki/Getting-Started
然后微服务应用中轮训配置信息进行动态调整


         System.setProperty("archaius.configurationSource.additionalUrls","file:/D:/workwork/projects/spring-cloud-demo/hcloud/provider/provider-impl/target/classes/config1.properties");
     
         
支持http和file 协议 
把配置文件放在远程 通过http协议进行访问

#### 实现原理

微服务通过archaius组件进行轮询远程属性文件的变化,默认轮训时间60s,可以通过配置系统参数进行自定义
在yml中hcloud.archaius.delayMills参数自定义轮询时间

远程属性文件后续会提供http rest api进行修改操作。这样只需要调用http rest api就可以达到微服务的动态参数调整

#### 使用步骤

1,登录微服务开发框架控制台页面，进行属性文件的新建工作，新建工作时区分测试，生产，等不同环境，每个环境下新建的属性文件都有一个http url对应
like http://127.0.0.1/myapp/test/config.properties
[可以通过nginx做静态代理进行实现]
说明
serviceId:myapp
dev:test
filename:config.properties
配置微服务的yml文件对应的测试环境yml中

加入

        hcloud:
          archaius:
            initialFileName: http://127.0.0.1/myapp/test/config.properties
 
 通过微服务开发框架控制台修改config.properties文件内容达到动态配置
 
 本地调试时，可以不设置此属性 
 
 遗留问题
 
 deploymentContext 在archaius中是搞毛的？
 
 明天看下ribbon哪些参数可以通过archaius进行动态调整
 
### 负载均衡动态配置[ribbon]

动态调整ribbon rule策略

支持 轮询 随机 权重三个规则,分别对应以下class实现
最大权重:com.netflix.loadbalancer.WeightedResponseTimeRule
轮询:com.netflix.loadbalancer.RoundRobinRule
随机:com.netflix.loadbalancer.RandomRule

使用步骤
1,在配置中心找到客户端yml文件，加入

        provider-service:
          ribbon:
            NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule
以上例子说明针对provider-service这个微服务调整负载均衡策略为权重,如果改成其他策略,需要调整NFLoadBalancerRuleClassName属性的值

2,通过配置中心动态调整负载均衡

更改配置中心对应的yml信息，参考步骤1

通过配置中心的url查看是否已经修改成功
like this：
http://localhost:8888/consumer-service.yml

使其客户端生效

需要对客户端发起一个post请求 like this

http://localhost:8091/user/refresh
成功返回200说明刷新成功，
调用客户端应用的endpoint查看配置参数已经生效
http://localhost:8091/user/env

重启客户端，新的负载均衡策略生效。



## 服务版本动态配置实现思路

### 使用步骤
provider向euraka注册服务时,在metaData中填入服务的version信息

        eureka:
          instance:
            prefer-ip-address: true
            instance-id: ${spring.cloud.client.ipAddress}:${server.port}
            metadata-map:
              version: 1.0.0  #version信息

consumer端在yml中指定服务的版本 格式如下


        microservice: 
          reference:
            service-id: 
              version: 1.0.0

### 实现原理
重新ribbon客户端的ServerListFilter.class
provider向eureka注册服务时，向metatdata中填入version信息
在Filter中获取yml中consumer定义的引用服务版本信息

在consumer端update server时 根据yml中定义的version信息过滤服务端的实例对应的version
过滤规则
1，如果consumer端没有定义version信息 那匹配provider所有的version
2，如果consumer端定义version信息，匹配对应provider端version 实例

通过动态挑战eureka上服务的metadata中的version信息，达到服务版本的动态配置

## 服务区域保护策略实现原理【默认已经实现】
使用场景:Server A 部署在北京和青岛机房都有实例，如果青岛机房的consumer默认应该访问到青岛机房的instance，
北京机房访问到北京的instance，这样避免网络上的延迟,使其效率更好。

### 实现原理
在服务版本动态配置中的VersionServerListFilter,我们重新实现了ZoneAffinityServerListFilter，
把服务版本和区域保护已经在一个Filter中实现

consumer端update server时，会根据上下文中的DeploymentContext中的ContextKey.zone属性获取当前的zone
比对server的zone 如果不在一个zone则剔除掉.

关于deployment可以参考:
https://github.com/Netflix/archaius/wiki/Deployment-context
关于我们重新实现了ZoneAffinityServerListFilter可以参考
https://github.com/Netflix/ribbon/wiki/Working-with-load-balancers








## 实例权重动态调整实现思路

使用场景:压力测试,服务动态路由
如果有一台部署微服务的机器 性能更好，可以把该机器对应的微服务实例权重提高，让更多的流量走该机器提供的服务.

### 实现原理

首先服务实例的权重保存在eureka上，通过调用eureka API 更新具体一个实例的权重信息

    PUT /eureka/v2/apps/appID/instanceID/metadata?key=value


重写ribbon客户端默认的Rule实现，在实现类中chooose server时 通过获取每个instance的metadata得到该实例的权重信息，通过random算法，权重大的实例被命中的概率就大。

比如：

                    totalWeight=300
                    instance1.weight=100
                    instance2.weight=200
                    randomWight = this.random.nextInt(totalWeight)
                    





## 标记实例不可用

使用场景;灰度发布
### 实现原理
调用eureka api标记服务的状态为out_of_service,
consumer端会30s左右pullupdate 服务中心的信息，下次客户端发起的调用不会再走
待重新发布后 调用api 标记服务状态为UP 即可再次使用


## zuul上传文件名中文乱码解决办法

使用微服务实现文件上传服务，单独访问是没有问题的
http://localhost:8090/user/upload/test
但是通过zuul做网关访问过来时候
后台报错 文件名乱码
http://localhost:12345/provider-service/user/upload/test

### 解决办法

对于文件上传有可选的路径"/zuul/*"绕过Spring DispatcherServlet (避免处理multipart).
正确姿势:
http://localhost:12345/zuul/provider-service/user/upload/test

即可

### spring cloud sleuth 链路追踪扩展

sleuth帮助我们解决了微服务开发中，多个微服务相互调用时，出现问题进行诊断的依据，比如调用一个微服务超时，从而通过sleuth可以看到
该微服务的整个调用链路以及每个链路的延迟，帮我们精确定义问题。

同时也支持扩展，比如在一个微服务内部调用数据库进行查询数据，如果你想把查询数据库这块代码的调用延迟记录下来当做链路的一部分有以下2中
方式

#### pom中引入依赖包
            
                    <dependency>
                        <groupId>org.springframework.cloud</groupId>
                        <artifactId>spring-cloud-starter-zipkin</artifactId>
                    </dependency>

#### 追加SPAN

1,在service或者dao的bean中注入tracer
然后调用tracer的方法进行创建span和close span
注意 必须要close span
    注入bean  
      
            @Autowired
            private Tracer tracer;
            
  在 getAppListByPage方法中创建span和关闭span
  
                public PageInfo<MonitorAlarmInfo>  getAppListByPage(String appname){
                    Span span = tracer.createSpan("SQL TIME");
                    logger.info("appname: {}",appname);
                    span.logEvent(System.currentTimeMillis(),"SQL开始执行");
                    PageHelper.startPage(2, 12);
                    List<MonitorAlarmInfo> list = monitorAlarmInfoDAO.getinfoByAppname(appname);
                    //用PageInfo对结果进行包装
                    PageInfo     page =new  PageInfo(list);
                    span.logEvent(System.currentTimeMillis(),"SQL开始结束");
                    this.tracer.close(span);
                    return page;
                }

#### 追加TAG

调用tracer的方法 在当前线程的span中追加tag

    
    this.tracer.addTag("ms-b-callable-sleep-millis", String.valueOf(millis));
    
    
### zuul压力测试

http://blog.csdn.net/u013815546/article/details/69669165


## dubbo集成

微服务中引用dubbo服务,以provider-impl模块作为例子，引用dubbo服务profilerProjectService获取paas工程列表

根据项目引用的服务，需要随时调整api jar包 以及xml中的内容
以下是具体步骤

1. 在pom.xml引入dubbo的依赖包和服务的api对应的jar包


                    <!--dubbo 支持 start-->
            
                    <dependency>
                        <groupId>com.alibaba</groupId>
                        <artifactId>dubbo</artifactId>
                        <version>2.5.4.2-interconn-1.0.0-SNAPSHOT</version>
                        <exclusions>
                            <exclusion>
                                <groupId>org.springframework</groupId>
                                <artifactId>spring</artifactId>
                            </exclusion>
                        </exclusions>
                    </dependency>
            
                    <!-- zookeeper -->
                    <dependency>
                        <groupId>org.apache.zookeeper</groupId>
                        <artifactId>zookeeper</artifactId>
                        <version>3.4.6</version>
                        <exclusions>
                            <exclusion>
                                <groupId>org.slf4j</groupId>
                                <artifactId>slf4j-log4j12</artifactId>
                            </exclusion>
                        </exclusions>
                    </dependency>
            
                    <dependency>
                        <groupId>com.alibaba</groupId>
                        <artifactId>fastjson</artifactId>
                        <version>1.2.32</version>
                    </dependency>
            
                    <dependency>
                        <groupId>com.github.sgroschupf</groupId>
                        <artifactId>zkclient</artifactId>
                        <version>0.1</version>
                    </dependency>
            
            
                 <!--远程dubbo服务的jar包-->
                    <dependency>
                        <groupId>com.haier.hlht</groupId>
                        <artifactId>profiler-project-api</artifactId>
                        <version>1.1-SNAPSHOT</version>
                    </dependency>
            
            
                    <!--dubbo 支持 end-->

2. 在resources的dubbo目录下新建一个xml文件，比如dubbo-consumer-demo.xml
    在xml文件中声明应用的dubbo bean
    
    
        <!--每次生成要修改应用名称，保证唯一性-->
        <dubbo:application name="demo-consumer"/>
        <!--zookeeper注册中心地址-->
        <dubbo:registry address="zookeeper://10.138.8.223:2181"/>
        <!--引用服务-->
        <dubbo:reference  id="profilerProjectService" interface="com.haier.profiler.project.service.ProfilerProjectService"  check="false"   timeout="10000" version = "1.0.0" />


3. 在模块的启动，使用@ImportResource注解，引入dubbo-consumer-demo.xml到上下文

        @ImportResource("classpath:dubbo/dubbo-consumer-demo.xml")
        

4. 在service或者controller层使用dubbo bean 即可

         @Autowired
         private ProfilerProjectService profilerProjectService;
     
    
### docker部署微服务注册到euraka多网卡问题

http://blog.csdn.net/neosmith/article/details/53126924



 


