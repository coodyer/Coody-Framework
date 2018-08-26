
### Coody Web

### 注意事项：
   Coody-Web是一款基于Coody Framework而实现的轻量化MVC框架
### 注解：
1) @JsonOut
凡是被该注解标记的mvc方法，相关内容将以json响应，同springmvc的ResponseBody 
2) @ParamName:
用于参数适配时，指定参数别名
3) @ParamsAdapt:
用于一个Controller类或者mvc方法。指定其接受参数的适配器。
2) @PathBinding:
修饰在一个类上，用于初始化一个Controller。并指定其MVC路径
修饰在一个方法上，用于指定其方法的请求路径(继承于类mvc路劲)



### 功能特色：
1) 轻量化mvc体系
2) 支持自定义参数适配器
3) 统一MVC参数接受流程
4) 剔除如其他mvc相关双注解功能
5) 支持request、response等线程安全注入
### 如何使用
1、添加相关依赖

```
        <dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-core</artifactId>
			alpha-1.1.1
		</dependency>

		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-jdbc</artifactId>
			alpha-1.1.1
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-cache</artifactId>
			alpha-1.1.1
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-task</artifactId>
			alpha-1.1.1
		</dependency>
		<dependency>
			<groupId>org.coody.framework</groupId>
			<artifactId>coody-web</artifactId>
			alpha-1.1.1
		</dependency>
```


2、进行相关配置(web.xml)


```
<!-- 配置扫描的包 -->
	<context-param>
		<param-name>scanPacket</param-name>
		<!-- 逗号分割多个包名 -->
		<param-value>org.coody.czone</param-value>
	</context-param>
	<!-- 配置初始化适配器 -->
	<context-param>
		<param-name>initLoader</param-name>
		<!-- 逗号分割多个加载器 -->
		<param-value>org.coody.framework.web.loader.WebAppLoader,
		org.coody.framework.task.loader.TaskLoader,
		</param-value>
	</context-param>
	<!-- 配置监听器 -->
	<listener>
		<listener-class>
org.coody.framework.web.listen.IcopServletListen
</listener-class>
	</listener>
	<!-- 初始化分发器 -->
	<servlet>
		<servlet-name>DispatServlet</servlet-name>
		<servlet-class>
org.coody.framework.web.DispatServlet
</servlet-class>
		<init-param>
			<!-- 静态页面目录配置 -->
			<param-name>viewPath</param-name>
			<param-value>/</param-value>
		</init-param>
	</servlet>
	<!-- MVC配置 -->
	<servlet-mapping>
		<servlet-name>DispatServlet</servlet-name>
		<url-pattern>*.do</url-pattern>
	</servlet-mapping>
```


### 如何编写一个Controller
1、新建一个Controller

![输入图片说明](https://images.gitee.com/uploads/images/2018/0815/180637_d6e17fb5_1200611.png "1.png")

2、初始化并指定其映射路径

![输入图片说明](https://images.gitee.com/uploads/images/2018/0815/180646_0dae77fe_1200611.png "2.png")

3、新建一个方法

![输入图片说明](https://images.gitee.com/uploads/images/2018/0815/180653_215bdff0_1200611.png "3.png")

4、为该方法指定请求路径

![输入图片说明](https://images.gitee.com/uploads/images/2018/0815/180715_0a18183f_1200611.png "4.png")
    
5、添加返回页面

![输入图片说明](https://images.gitee.com/uploads/images/2018/0815/180722_fcf0857d_1200611.png "5.png")

6、访问：/test/hello.do即可
