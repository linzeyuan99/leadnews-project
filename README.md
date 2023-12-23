### 一個小項目，前端使用到的是 react18 + antd



### 後端使用了 

### springcloud alibaba nacos、feign、gateway、redis、mysql、xxl-job、sentinel





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
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
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
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
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
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
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



## 注意 react 項目 

1. 是antd的5.11.5版本
2. 需要 npm - i axios 等一些常用的組件，路由之類的，還有一個富文本編輯器 reactQuill 





## 後端 springcloud 項目

1. 注意nacos的對應配置要修改
2. jdbc、redis的路徑
3. 有些功能尚未開發，後續更新
