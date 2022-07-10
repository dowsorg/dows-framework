# 永辉微服务脚手架

> 文档中心

    http://public-service.itwork-book.gw.dows.cn/

> Swagger-ui

    http://localhost:8080/swagger-ui.html
    
> SQL监控
    
    默认禁用，如需开启请设置 enabled: true
    http://localhost:8080/druid/index.html
    
> 健康检查
    
    http://localhost:8080/v1/health

## 工程结构(单个整体结构)
    
    yh-springboot-base      服务名称,格式：yh_项目名_服务名
    │
    └─org.dows.cop.framework      包命名：org.dows.服务名
        │
        ├─api               微服务接口和传输对象定义
        │  ├─controller     对外暴露的接口
        │  │ └─v1           接口版本（@RestController）
        │  ├─dto            数据传输对象
        │  └─validator      后台数据校验
        │
        ├─common            基础服务包
        │  ├─config         服务配置相关
        │  └─utils          自定义工具包
        │
        ├─mapper            数据持久层        
        │
        ├─domain            实体模型
        │  ├─DO         数据表对象模型
        │  └─VO             返回结果对象模型
        │
        ├─server            服务层   
        │  ├─message        消息队列
        │  ├─feign          feign客户端定义（@FeignClient）
        │  │ └─callback     服务降级处理（@FeignClient fallback）
        │  └─service        
        │    └─impl         内部业务逻辑处理（@Service）
        │
        └─SpringbootCopFrameworkApplication    服务启动类

<br>

    资源列表
    └──resources 
       ├─mapper                 SQL对应的XML文件
       ├─static                 静态资源
       ├─logback-spring.xml     日志收集配置（开发不用修改）
       ├─bootstrap.yml          启动的基础配置(包含服务名、端口号以及配置中心的连接信息等)
       └─application.yml        应用要使用的配置信息、与环境相关的信息

<br>

    charts                      应用容器化配置
    └──model-service            服务名
       ├─templates              为模板文件，将模板文件渲染成实际文件，然后发送给Kubernetes。
       │ ├─deployment.yaml：    创建 Kubernetes 部署的基本清单。
       │ └─_helpers.tpl：       放置模板助手的地方，您可以在整个 chart 中重复使用
       │
       ├─values.yaml            为模板的预定义变量。
       ├─Chart.yaml             包含 chart 的版本信息说明，您可以从模板中访问它。
       └─service.yaml：         为您的部署创建服务端点的基本清单。

## Apollo 配置中心

    http://public-service.itwork-book.gw.dows.cn/book/itwork/docs/qa/apollo.html

## 日志服务

    http://public-service.itwork-book.gw.dows.cn/book/itwork/docs/qa/elk.html
    
## 链路监控

    http://public-service.itwork-book.gw.dows.cn/book/itwork/docs/qa/skywalking.html

## CICD

    http://public-service.itwork-book.gw.dows.cn/book/itwork/docs/qa/cicd/cicd.html
    
## yh starter 描述

* yh-starter-core   [基础包]
* yh-starter-mybatis    [持久层处理]
* yh-starter-mybatis-dynamic-datasource [多数据源]
* yh-starter-swagger    [swagger]
* yh-starter-redis  [redis]
* yh-starter-excel  [excel操作]
* yh-starter-file   [文件操作]
* yh-starter-web    [Web应用基础]
* yh-starter-logback    [日志收集]

## 多数据源的配置

1. pom.xml引入 yh-starter-mybatis-dynamic-datasource
2. application.yml 配置添加如下配置：

<br>

    dynamic:
     datasource:
       slave1:
         driver-class-name: com.mysql.cj.jdbc.Driver
         url: jdbc:mysql://10.0.91.123:3306/mytest?autoReconnect=true&allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false
         username: root
         password: root
       slave2:
         driver-class-name: com.mysql.cj.jdbc.Driver
         url: jdbc:mysql://10.0.91.123:3306/mytest?autoReconnect=true&allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false
         username: root
         password: root

## 数据库建表示例

    CREATE TABLE `demo` (
      `id` varchar(32) NOT NULL,
      `user_name` varchar(50) NOT NULL COMMENT '用户名',
      `IS_DELETE` tinyint(4) DEFAULT NULL COMMENT '是否删除  1：已删除   0：正常',
      `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人，租户下的用户ID',
      `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
      `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '修改人，租户下的用户ID',
      `UPDATED_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `demo_user_name` (`user_name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测试模型';

## 开发规范
### 分层领域模型规约：

    * DO（Data Object）：与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
    * DTO（Data Transfer Object）：数据传输对象，Service 和 Manager 向外传输的对象。
    * BO（Business Object）：业务对象。可以由 Service 层输出的封装业务逻辑的对象。
    * Query：数据查询对象，各层接收上层的查询请求。注：超过 2 个参数的查询封装，禁止
      使用 Map 类来传输。
    * VO（View Object）：显示层对象，通常是 Web 向模板渲染引擎层传输的对象。 

### 安全规约 
    
【强制】用户请求传入的任何参数必须做有效性验证。

    说明：忽略参数校验可能导致：
    * page size 过大导致内存溢出
    * 恶意 order by 导致数据库慢查询
    * 任意重定向
    * SQL 注入
    * 反序列化注入
    * 正则输入源串拒绝服务 ReDoS
    说明：Java 代码用正则来验证客户端的输入，有些正则写法验证普通用户输入没有问题，
    但是如果攻击人员使用的是特殊构造的字符串来验证，有可能导致死循环的结果。

 防重放限制
        
    在使用平台资源，譬如短信、邮件、电话、下单、支付，必须实现正确的防重放限制，
    如数量限制、疲劳度控制、验证码校验，避免被滥刷、资损。
    说明：如注册时发送验证码到手机，如果没有限制次数和频率，那么可以利用此功能骚扰到其
    它用户，并造成短信平台资源浪费。

