
## Coody Framework

#### 基本介绍：

一名野生的民间技术爱好者，早期活跃于各网络安全论坛，而后由白帽转战编程。因谋生于14年3月就职于CMGE。

在从业生涯中，本项目自2018年发布后，后续更新也是改改停停，这些年JDK已经更新了N多个版本，而本框架至今未发布正式版。

自古闲人出金货，也许有一天笔者自由了，会回过头来完善这套框架。

目前本项目存在如下挑战：

1、Java9之后屏蔽了unsafe，会使得底层框架对创建对象场景性能下降

2、Java9之后反射技术部分操作空间被阉割，需要针对阉割部分进行调整

3、需兼容javax，无缝兼容各种模板引擎、各种应用服务器

4、Cson json序列化&反序列化插件，需修复特定场景下兼容性问题

#### 前言：

    Coody Framework 是由笔者业余时间编写的一套Ioc框架。由最初立项到发布jar到Maven中央仓库。历时4个月有余。

#### 用途与优势：

    Coody Framework框架包含以下模块

        coody-core    ：框架核心包，包括Ioc+Aop的实现，包括相关工具和超类

        coody-web     ：框架web-mvc的实现包，实现了mvc功能体系

        coody-cache   ：框架缓存的实现，实现了基础缓存，切面缓存，并提供相关切面技术的支持

        coody-jdbc    ：框架orm的实现，实现了基于mysql下基础操作的封装，实现了各种简易操作，拓展了切面事务
        
        coody-task    ：框架定时任务的实现，基于cron实现了定时任务，提供了注解定时任务的支持

        coody-rcc     ：框架分布式的实现，提供注册中心、序列化、通信协议等接口。字节码创建子类的实现(分布式功能未完成)

        coody-elock   ：框架分布式锁的实现，通过redis订阅实现的高性能分布式锁

        coody-mail    ：基于TCP的邮件发送工具
        
        coody-minicat ：基于TCP的Http服务器

        coody-cson    ：JSON序列化 & 反序列化插件

        coody-esource ：简易数据库连接池
        
        coody-logged  ：轻量化日志插件

#### 更新记录：

    2018-02-24： 立项，提供 IOC & AOP框架

    2018-02-25： 提供Web-MVC框架

    2018-02-26： 提供ORM框架

    2018-03-08： 拓展IOC对Interface的支持

    2018-03-10： 提供CRON定时器插件，并提供CronTask注解

    2018-03-21： 拓展AOP，提供类通配、方法通配、提供多注解规则

    2018-04-22： 提供request和response在controller中进行注入

    2018-04-23： 拓展MVC参数适配器，并提供自定义参数适配入口

    2018-06-02： 拓展分布式框架，并制定分布式解决方案，提供ASM创建实现类，通讯、序列化、注册中心等接口

    2018-06-05： 对项目进行拆分，为发布maven中央仓库做准备

    2018-06-28： 发布Alpha至Maven中央仓库。nexus搜索"Coody"即可

    2018-12-31： 提供Elock，用于分布式加锁，发行alpha-1.2.4版本

    2019-11-26： 提供Mail，用于邮件发送，发行alpha-1.2.5版本

    2019-12-27： 提供Minicat，实现HTTP应用服务器，并发行alpha-1.2.6版本
	
    2020-01-04： 提供Cson，实现Json 序列化&反序列化插件

    2020-01-14： 剔除cglib依赖，改为纯asm实现动态代理
    
    2022-10-03： 编写并调通RCC分布式模块
    
    2022-10-09： 编写logged日志插件，剔除log4j

    Coody Framework+Minicat实战项目：https://gitee.com/coodyer/reduce/

引用地址：

```
        <dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-core</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>

		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-jdbc</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-cache</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-task</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-web</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>
      <dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-elock</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-minicat</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-mail</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-cson</artifactId>
			<version>alpha-1.8.4</version>
		</dependency>
```


=======================================================


### 1. 功能说明：


    Coody Framewrok实现了：IOC依赖注入、AOP切面、MVC、定时任务、切面缓存、ORM等功能，分布式Rcc模块也已进入研发阶段。
 

### 2. 环境说明：


    JDK1.8+  gpg4win2.3.1(windows上编译需要安装本环境)

### 3. 基本示例:

##### (1)、实例化一个Bean:

![输入图片说明](https://images.gitee.com/uploads/images/2018/0905/100026_1a540d5b_1200611.png "a.png")

实例化一个bean只需要在一个Class标明@AutoBuild注解即可

##### (2)、注入Bean到一个类字段:

![输入图片说明](https://images.gitee.com/uploads/images/2018/0905/100233_dda96161_1200611.png "b.png")

注入Bean到字段，只需要在该字段上面标明@AutoBuild注解

##### (3)、web.xml配置:


```
  <!-- 初始化加载配置文件目录 -->
  <context-param>
    <param-name>configPath</param-name>
    <param-value>config</param-value>
  </context-param>
  <!-- 监听器 -->
  <listener>
    <listener-class>org.coody.framework.web.listen.CoodyServletListen</listener-class>
  </listener>
  <!-- mvc路由器 -->
  <servlet>
    <servlet-name>DispatServlet</servlet-name>
    <servlet-class>org.coody.framework.web.DispatServlet</servlet-class>
    <!-- 静态资源目录 -->
    <init-param>
      <param-name>viewPath</param-name>
      <param-value>/</param-value>
    </init-param>
  </servlet>
  <!-- mvc拦截规则 -->
  <servlet-mapping>
    <servlet-name>DispatServlet</servlet-name>
    <url-pattern>*.do</url-pattern>
  </servlet-mapping>
```

##### (4)、配置一个bean:

######## 配置dataConfig  （coody.bean.{bean名称}.field.${字段名}）

    coody.bean.hikariDataConfig.class=com.zaxxer.hikari.HikariConfig

    coody.bean.hikariDataConfig.field.driverClassName=com.mysql.jdbc.Driver

    coody.bean.hikariDataConfig.field.jdbcUrl=jdbc\:mysql\://127.0.0.1/czone?useUnicode\=true&characterEncoding\=utf-8

    coody.bean.hikariDataConfig.field.username=root

    coody.bean.hikariDataConfig.field.password=root

    coody.bean.hikariDataConfig.field.maxPoolSize=64

    coody.bean.hikariDataConfig.field.minIdle=8


####### 配置DataSource  （coody.bean.{bean名称}.parament.${参数名}），当值为${表达式}，即${bean名称}

    coody.bean.hikariDataSource.class=com.zaxxer.hikari.HikariDataSource

    coody.bean.hikariDataSource.parament.configuration=${hikariDataConfig}

注意：表达式coody.bean.{bean名称}.方式.${参数名}中。当方式为field的时候，代表通过设置参数值初始化Bean，field的值与字段名一致；当方式为parament的时候，代表通过构造函数初始化Bean。parament的值与参数名一致。

如图：

![输入图片说明](https://images.gitee.com/uploads/images/2018/1221/150706_02849aef_1200611.png "cc.png")


##### (5)、Mvc的使用:

###### 简易使用


![输入图片说明](https://images.gitee.com/uploads/images/2018/1204/180006_691d49b5_1200611.png "a.png")


###### 参数适配器

![输入图片说明](https://images.gitee.com/uploads/images/2018/1204/180122_c0c8d827_1200611.png "b.png")



###### 系统提供的参数装载器位于org.coody.framework.web.adapt包下

    FormMealAdapt：混合装载适配器，form表单装载到多个bean，平级装载，字段名谁有谁得。

    FormNomalAdapt：混合装载适配器，form表单装载到多个bean，以方法参数名为请求参数前缀。

    GeneralAdapt：简易装载适配器，装载request、response、session等参数。

    JsonMealAdapt：混合装载适配器，json数据混合装载到bean，平级装载，字段名谁有谁得。

    JsonNomalAdapt：混合装载适配器，json数据装载到多个bean，以方法参数名为请求参数前缀。


##### (6)、定时任务的使用:

![输入图片说明](https://images.gitee.com/uploads/images/2018/0905/101505_aad09568_1200611.png "f.png")

##### (7)、切面的使用:

![输入图片说明](https://images.gitee.com/uploads/images/2018/0905/101713_066e33cb_1200611.png "g.png")

##### (8)、事务的使用:

![输入图片说明](https://images.gitee.com/uploads/images/2018/0905/101840_c99a11c6_1200611.png "h.png")

##### (9)、缓存的使用:

![输入图片说明](https://images.gitee.com/uploads/images/2018/0905/102029_b0ba0804_1200611.png "i.png")

缓存使用请参考具体文档

### 4. 各插件具体文档：

[Coody Web （MVC说明文档）](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-web)

[Coody Cache （缓存插件说明文档）](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-cache)

[Coody Jdbc （ORM说明文档）](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-jdbc)

[Coody Task （CRON定时任务说明文档）](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-task)

[Coody Elock （分布式锁说明文档）](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-elock) 

[Coody Minicat （HTTP服务器说明文档）](https://gitee.com/coodyer/Coody-Framework/tree/original/coody-minicat)

[Coody Mail （TCP邮件插件说明文档）](https://gitee.com/coodyer/Coody-Framework/tree/original/coody-mail)

[Coody ESource （数据库连接池说明文档）](https://gitee.com/coodyer/Coody-Framework/tree/original/coody-esource)

[Coody Cson （JSON序列化&反序列化插件说明文档）](https://gitee.com/coodyer/Coody-Framework/tree/original/coody-cson)

[Coody RCC （分布式框架）](https://gitee.com/coodyer/Coody-Framework/tree/original/coody-rcc)

[Coody Logged （日志插件）](https://gitee.com/coodyer/Coody-Framework/tree/original/coody-logged)
 

### 5. 版权说明：


    在本项目源代码中，已有测试demo，包括mvc、切面等示例

    作者：Coody
    
    版权：©2014-2025 Test404 All right reserved. 版权所有

    反馈邮箱：644556636@qq.com  交流群号:218481849

    基于Coody Framework的博文系统：https://gitee.com/coodyer/czone  (研发中)

