
## Coody Framework

#### 动机：

一名野生的民间技术爱好者，早期活跃于各网络安全论坛，而后由白帽转战编程。因谋生于14年3月就职于CMGE。

浑浑噩噩已经24岁了，看着朋友圈各种晒宝宝照片的老同学，只得感叹光阴似箭。而单身的笔者，随着岁月的流逝，寂寞空虚冷愈加强烈。故此，笔者选择通过技术研究来饱和业余生活。

#### 前言：

    Coody Framework 是由笔者业余时间编写的一套Ioc框架。由最初的项目内置代码发展至今的Maven中央仓库。历时4个月有余。

    而在今后的时间里，Coody Framework将不断完善，争取今年内发布一套成熟的release版本。同时，笔者也会自带成功案例。

#### 用途与优势：
    
    Coody Framework是一套轻量化Ioc框架。除分布式(即Rcc模块)未完成外，其余均已经过笔者本地测试。全框架大小214kb。
    
    Coody Framework 依赖cglib-3.2、log4j-1.2、noson-1.0.7。

    基于Coody Framework框架的web项目启动耗时约370ms(在i5 2320上所耗费的时间)、加上tomcat所消耗的时间，约4s-5s
    
    基于Coody Framework框架的测试用例已在服务器长跑3个月有余：http://czone.icoody.cn/ 

    Coody Framework框架包含以下模块

        coody-core    ：框架核心包，包括Ioc+Aop的实现，包括相关工具和超类。

        coody-web     ：框架web-mvc的实现包，实现了mvc功能体系。

        coody-cache   ：框架缓存的实现，实现了基础缓存，切面缓存，并提供相关切面技术的支持。

        coody-jdbc    ：框架orm的实现，实现了基于mysql下基础操作的封装，实现了各种简易操作，拓展了切面事务。
        
        coody-task    ：框架定时任务的实现，基于cron实现了定时任务，提供了注解定时任务的支持。

        coody-rcc     ：框架分布式的实现，提供注册中心、序列化、通信协议等接口。字节码创建子类的实现(分布式功能未完成)

        coody-elock   ：框架分布式锁的实现，通过redis订阅实现的高性能分布式锁

        coody-mail    ：基于TCP的邮件发送工具
        
        coody-minicat ：基于TCP的Http服务器

        coody-cson    ：JSON序列化 & 反序列化插件

        coody-esource ：简易数据库连接池

#### 更新记录：

    2018-02-24： 初步研发，实现Ioc+Aop。

    2018-02-25： 拓展Mvc，基于MVC结合Ioc+Aop实现轻量化(类spring)开发模式

    2018-02-26： 由最初的常规java project整改为Maven Project，并整合自写ORM框架配合aop实现事务管理。

    2018-03-08： 拓展接口形式依赖注入，对interface的Ioc提供支持。

    2018-03-10： 拓展定时任务，基于切面通过注解(cron)实现任务调度。

    2018-03-21： 使用Coody Framework整合至一个外置项目中并成功运行。

    2018-03-21： 为切面的拦截条件新增类通配、方法通配，同时支持一个拦截方法通过多个注解引入不同的拦截规则。

    2018-04-22： 微调Mvc相关功能，支持request和response在controller中进行注入(线程安全)

    2018-04-22： 整改Mvc参数适配器，支持自定义参数适配。

    2018-06-02： 筹划拓展分布式体系，并引入字节码技术创建实现类，提供通讯、序列化、注册中心等接口

    2018-06-05： 项目拆分成Maven多模块化模式，为发布maven中央仓库做准备

    2018-06-28： 发布Alpha至Maven中央仓库。nexus搜索"Coody"即可

    2018-12-31： 新增分布式锁elock，并发行alpha-1.2.4版本

    2019-11-26： 新增邮件发送包mail，并发行alpha-1.2.5版本

    2019-12-27： 新增minicat支持，并发行alpha-1.2.6版本

    2019-12-31： 为minicat新增静态资源的支持，并发行alpha-1.3.0版本

    2020-01-04： 新增cson项目，为json序列化和反序列化提供支撑

    2020-01-14： 剔除cglib依赖，改为纯asm实现动态代理

    Coody Framework+Minicat实战项目：https://gitee.com/coodyer/reduce/

引用地址：

```
        <dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-core</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>

		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-jdbc</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-cache</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-task</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-web</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>
      <dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-elock</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-minicat</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-mail</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-cson</artifactId>
			<version>alpha-1.8.3</version>
		</dependency>
```


=======================================================



### 1. 项目背景：


    纵观整个国内开源圈，难以寻找一套比较成熟的Ioc框架。然实现一套Ioc框架并没有太高的技术含量和工作量。

    故此，笔者着手将业余时间的一些代码和案例汇总，研发Coody Framework。



### 2. 功能说明：


    Coody Framewrok实现了：IOC依赖注入、AOP切面、MVC、定时任务、切面缓存、ORM等功能，分布式Rcc模块也已进入研发阶段。
 

### 3. 环境说明：


    JDK1.8+  gpg4win2.3.1(windows上编译需要安装本环境)

### 4. 基本示例:

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

### 5. 具体文档：

coody-core   ：[Coody-Core](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-core)

coody-web    ：[Coody Web](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-web)

coody-cache  ：[Coody Cache](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-cache)

coody-jdbc   ：[Coody Jdbc](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-jdbc)

coody-task   ：[Coody Task](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-task)

coody-rcc    ：[Coody Rcc](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-rcc) 

coody-elock  ：[Coody Elock](http://gitee.com/coodyer/Coody-Framework/tree/original/coody-elock) 
 


### 6. 发展计划：

    1、完善rcc分布式模块

    2、为Coody Framework提供完整的配置中心(已初步实现)

    3、完成博文系统并作为成功案例

    4、提供完整的文档

    5、接入MiniCat实现类SpringBoot开发模式(可选)


### 7. 合作意向：

    首先说明一下，开发一套框架并不需要多么高深的技术。相反，开发一套框架非常简单，我们都是在追溯偷懒的艺术。
    
    通过一些特殊的机制和算法，提供一系列为开发者服务的解决方案。

    1、如果你逻辑思维活跃

    2、如果你具备java相关的技术

    3、如果你有互联网产品开发的实际经验

    4、如果你对Coody Framework 的研究有一定的兴趣

    那么，请联系我，笔者真诚的希望你能加入到Coody Framework 的开发中来，并肩作战，为国内开源技术做贡献。

### 8. 版权说明：


    在本项目源代码中，已有测试demo，包括mvc、切面等示例

    作者：Coody
    
    版权：©2014-2020 Test404 All right reserved. 版权所有

    反馈邮箱：644556636@qq.com  交流群号:218481849

    基于Coody Framework的博文系统：https://gitee.com/coodyer/czone  (研发中)

