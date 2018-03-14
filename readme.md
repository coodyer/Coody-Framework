
####Icop

更新记录：

2018-02-24：初步研发，实现IOC+AOP。

2018-02-25：拓展MVC，基于MVC结合ICO+AOP实现轻量化无依赖类spring开发模式

2018-02-26：整改为maven模式，并整合自写ORM框架配合aop实现事物管理。

2018-03-08：拓展接口形式依赖注入。

2018-03-10：拓展对注解形式Cron定时任务的支持。

已有基础功能支持： **IOC依赖注入、AOP切面编程、CRON定时任务、WEB-MVC** 

已有拓展功能支持： **事物管理、切面缓存** 

=======================================================



### 1. 项目背景：


由于笔者近期参与的一些项目体系未使用到任何框架，而笔者对spring体系特别向往，故此研发本项目。



### 2. 功能说明：


本项目实现注解形式的bean加载、依赖注入、切面等功能。简单实现mvc。

 

### 3. 项目特点：


本项目使用cglib。秉承轻量、易用、简单、高效等原则。依赖jar：cglib-nodep-3.1.jar fastjson-1.2.31.jar log4j-1.2.17.jar  依赖jar包其余版本自行测试。

 

### 4. 环境说明：


JDK1.8+

 

### 5.目录结构：
 

![输入图片说明](https://gitee.com/uploads/images/2018/0314/235011_b6f79022_1200611.png "0Z3WX6~`I946H3JIA1OI]_X.png")

 

### 6. 程序架构：


由于在撰写本文背景下无作图环境，故此略去架构图。以下提供一些结构说明

 **(1)、包说明** 

org.coody.framework.entity常用实体包。

org.coody.framework.util  常用工具包

org.coody.framework.box  核心实现包

org.coody.framework.box.adapt 适配器包

org.coody.framework.box.annotation 注解包

org.coody.framework.box.container 容器包

org.coody.framework.box.constant 常量包

org.coody.framework.box.iface 接口包

org.coody.framework.box.init 初始化入口包

org.coody.framework.box.mvc MVC实现包

org.coody.framework.box.proyx 动态代理包

org.coody.framework.box.wrapper 包装类

 **(2)、类说明-注解** 

org.coody.framework.box.annotation.Around环绕通知注解标识，用于切面实现

org.coody.framework.box.annotation.InitBean初始化Bean。类似于spring的Service等注解，标记一个bean类

org.coody.framework.box.annotation.JsonSerialize序列化JSON输出，用于controller方法标识。类似于spring的ResponseBody注解

org.coody.framework.box.annotation.OutBean 输出Bean。类似于Resource/AutoWired注解

org.coody.framework.box.annotation.PathBinding 输出Bean。类似于Resource/AutoWired注

 **(3)、类说明-适配器** 

org.coody.framework.box.adapt.ParamsAdapt  参数适配器，用于MVC下参数的装载(目前只实现request、response、session三个参数的自动装载)

 **(4)、类说明-容器** 

org.coody.framework.box.container.BeanContainer 容器，用于存储bean，类似于spring的application

org.coody.framework.box.container.MappingContainer  Mvc映射地址容器

 **(5)、类说明-接口** 

org.coody.framework.box.iface.InitFace 初始化接口，凡是实现该接口的bean需实现init方法。在容器启动完成后执行。

 **(6)、类说明-启动器** 

org.coody.framework.box.init.BoxRute 容器入口。通过该类的init(packet)方法指定扫描包路径

org.coody.framework.box.init.BoxServletListen 监听器，配置在webxml用于引导容器初始化

 **(7)、类说明-mvc分发器** 

org.coody.framework.box.mvc.DispatServlet 类似于spring的DispatServlet

 **(8)、类说明-代理工具** 

org.coody.framework.box.proyx.CglibProxy 基于cglib字节码创建子类的实现

 **(9)、类说明-包装类** 

org.coody.framework.box.wrapper.AspectWrapper 本处命名可能不尽规范。本类用于多切面的调度和适配  

 

### 7.使用说明：

 **(1)、web.xml说明** 


```
	<!-- 配置扫描的包 -->
	<context-param>
		<param-name>scanPacket</param-name>
		<param-value>org.coody.web</param-value>
	</context-param>
	<!-- 配置监听器 -->
	<listener>
		<listener-class>org.coody.framework.box.init.BoxServletListen</listener-class>
	</listener>
	<!-- 初始化分发器 -->
	<servlet>
		<servlet-name>DispatServlet</servlet-name>
		<servlet-class>org.coody.framework.box.mvc.DispatServlet</servlet-class>
	</servlet>
	<!-- MVC配置 -->
	<servlet-mapping>
		<servlet-name>DispatServlet</servlet-name>
		<url-pattern>*.do</url-pattern>
	</servlet-mapping>
```






 **(2)、MVC使用说明** 

no.1、在scanPacket指定的包目录下某个类使用@PathBinding("/test")注解。传入参数即该类映射路径

no.2、在方法上面指定@PathBinding("/index.do")注解，传入参数即方法映射路径

no.3、该方法的访问路径为 /test/index.do

no.4、映射方法拓展注解@JsonSerialize 被该注解标识的方法将输出json

no.5、示例：


![输入图片说明](https://gitee.com/uploads/images/2018/0228/085812_e2836fe6_1200611.jpeg "mvc.jpg")


 **(3)、IOC使用说明** 

no.1、在需要初始化为bean的类上指定@InitBean注解

no.2、在需要IOC赋值的字段上指定@OutBean

no.3、示例


![输入图片说明](https://gitee.com/uploads/images/2018/0228/085831_4605c756_1200611.jpeg "ioc1.jpg")


通过以上注解，完成整个IOC依赖注入的全过程。


 **(4)、切面使用说明** 

 _no.1_ 、自定义一个注解

 _no.2_ 、在需要拦截的方法上面标识该注解

 _no.3_ 、编写一个类，通过@InitBean修饰以实例化bean。在bean内部编写方法。方法用@Around注解修饰。@Around注解传入值为第一步的自定义注解。方法参数类型为AspectWrapper

 _no.4_ 、在如上操作完成后，凡是包含该自定义注解的方法均通过环绕通知

 _no.5_ 、示例：


![输入图片说明](https://gitee.com/uploads/images/2018/0228/085901_39327602_1200611.png "aspect.png")





### 8. 示例：

    
    依赖注入
![输入图片说明](https://gitee.com/uploads/images/2018/0314/235137_6318dce2_1200611.png "[}5%ZQRL_T8R62TP5BXDUGF.png")

    切面编程
![输入图片说明](https://gitee.com/uploads/images/2018/0314/235227_3d683631_1200611.png "EOC5SKADKLN}P`F`1@]EATS.png")

    定时任务
![输入图片说明](https://gitee.com/uploads/images/2018/0314/235300_1427cd5d_1200611.png "ALP63PY8`6MU2B]0AYL}Z16.png")

    MVC
![输入图片说明](https://gitee.com/uploads/images/2018/0314/235433_0318b322_1200611.png "1GAQ1OY1895$[W2}RF%XCVT.png")

    事物管理
![输入图片说明](https://gitee.com/uploads/images/2018/0314/235504_5666dcbb_1200611.png "$9`DAN2(F25R%_[P@)H$9SS.png")





### 9. 版权说明：



在本项目源代码中，已有测试demo，包括mvc、切面等示例

作者：Coody

反馈邮箱：644556636@qq.com

交流群号:218481849
