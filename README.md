leadnews-user

```java
spring:
  redis:
    host: 192.168.1.6
    port: 6379
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/leadnews_user?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: root
# 设置Mapper接口所对应的XML文件位置，如果你在Mapper接口中有自定义方法，需要进行该配置
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.lzy.model.user.pojos
```



 leadnews-article

```java
spring:
  redis:
    host: 192.168.1.6
    port: 6379
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/leadnews_article?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: root
# 设置Mapper接口所对应的XML文件位置，如果你在Mapper接口中有自定义方法，需要进行该配置
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.lzy.model.user.pojos
```



 leadnews-schedule

```java
spring:
  redis:
    host: 192.168.231.1
    port: 6379
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/leadnews_schedule?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: root
# 设置Mapper接口所对应的XML文件位置，如果你在Mapper接口中有自定义方法，需要进行该配置
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.lzy.model.schedule.pojos
```



leadnews-app-gateway

```java
spring:
  cloud:
    gateway:
      globalcors:
        add-to-simple-url-handler-mapping: true
        corsConfigurations:
          '[/**]':
            allowedHeaders: "*"
            allowedOrigins: "*"
            allowedMethods:
              - GET
              - POST
              - DELETE
              - PUT
              - OPTION
      routes:
        # 平台管理
        - id: user
          uri: lb://leadnews-user
          predicates:
            - Path=/apiUser/lin/user/**
          filters:
            - StripPrefix= 1
        - id: article
          uri: lb://leadnews-article
          predicates:
            - Path=/apiArticle/lin/article/**
          filters:
            - StripPrefix= 1
```



## 注意 react 项目 

1. 用的是antd的 5.11.5 版本
2. 需要 npm - i axios 等等一些常用组件，路由之类的，还有一个富文本编辑器 reactQuill





## 后端springcloud项目

1. 注意nacos的对应配置修改
2. jdbc和redis的路径
3. 有些功能尚未开发齐全，后续更新