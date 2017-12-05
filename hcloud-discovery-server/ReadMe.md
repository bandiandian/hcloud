### eureka注册中心部署说明

#### 测试环境

1. 部署机器10.138.25.211
    目录是/usr/local/hcloud/eureka-app
    访问地址:http://10.138.25.211:1111/

2. 部署步骤
    1，在server-2模块下 执行 mvn clean package -Dmaven.test.skip=true进行打包。
    2，copy target下面的jar包 传到211服务器上。
    3，通过java -jar 命令启动即可。
    4，如果遇到端口问题，需要通过firewall 进行端口开放
    
#### 生产环境

1. 部署机器10.138.25.214
    目录是/usr/local/hcloud/eureka-app
    访问地址:http://10.138.25.214:1111/

2. 部署步骤
    1，在server-3模块下 执行 mvn clean package -Dmaven.test.skip=true进行打包。
    2，copy target下面的jar包 传到214服务器上。
    3，通过java -jar 命令启动即可。
    4，如果遇到端口问题，需要通过firewall 进行端口开放