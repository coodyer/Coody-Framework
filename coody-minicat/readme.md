# Coody-Minicat

#### 项目介绍
一款轻量化Http服务器。支持bio、nio两种模式。归属Coody Framework下。传送门：https://gitee.com/coodyer/coody-icop

#### 更新说明

2018-07-02：MiniCat正式立项。

2018-07-03：实现Http基本请求与响应。

2018-07-04：实现Session机制、Servlet、Gzip等

2018-07-05：实现formdata参数装载、Multipart参数与文件上传

2018-07-06：拓展Nio模式

2018-07-07：优化Http协议加载，修复某些情况下read阻塞。

2018-07-09：实现Filter机制。


#### 截图说明
一个Servlet：

![Servlet](https://images.gitee.com/uploads/images/2018/0710/141520_a311875f_1200611.png "015123_deeae509_1200611.png")

Multipart文件上传：

![文件上传](https://images.gitee.com/uploads/images/2018/0710/141309_dba8ecaf_1200611.png "文件上传.png")

参数接受：

![接受参数](https://images.gitee.com/uploads/images/2018/0710/141354_3b1034be_1200611.png "接受参数.png")

1、本项目未使用任意框架
2、本项目基于Socket实现Http服务器
3、本项目将在近期内持续更新

#### 性能说明

基于MiniCat下测试Servlet打印hello world

 **NIO** ：i5 2320  8G DDR3  QPS峰值：2000 均值：1850

 **BIO** ：i5 2320  8G DDR3  QPS峰值：1800 均值：1720

 **NIO** ：E3 1230V2  8G DDR3  QPS峰值：2900 均值：2700

 **BIO** ：E3 1230V2  8G DDR3  QPS峰值：2800 均值：2650

TOMCAT：I5 2320  QPS峰值：1760 均值：1700

其中Tomcat未开启Nio和Apr（笔者懒得配了），喜欢折腾的小伙伴可以去试试

其余未测试======

#### 本项目现实现功能：
1、Http服务端访问
2、Http(GET POST 多图文上传)参数装载
3、Gzip压缩
4、Cookie机制

#### 本项目将实现功能：
1、服务器配置中心
2、程序启动入口
3、外置项目加载
4、内置整合加载
5、兼容javax规范
6、jstl模板引擎


#### 运行说明：

1、新建Servlet。继承org.coody.framework.minicat.servlet.HttpServlet。实现doService方法

![输入图片说明](https://gitee.com/uploads/images/2018/0704/114513_2aba897b_1200611.png "1.png")

2、在新建的Servlet上写上@Servlet("/index.do")地址。其中/index.do即Mapping地址

![输入图片说明](https://gitee.com/uploads/images/2018/0704/114538_d094d7b7_1200611.png "2.png")

3、在org.coody.web.init.Rute方法里面调用CoreApp.init(Class<>...clazz)方法上。初始化这个Servlet。

![输入图片说明](https://gitee.com/uploads/images/2018/0704/114608_164d1c53_1200611.png "3.png")

4、运行org.coody.web.init.Rute的main方法

5、访问http://127.0.0.1/index.do即可

![输入图片说明](https://gitee.com/uploads/images/2018/0704/015123_deeae509_1200611.png "Servlet.png")

#### 相关配置：

配置类位于org.coody.framework.minicat.config.MiniCatConfig

![输入图片说明](https://gitee.com/uploads/images/2018/0704/114731_0bb54635_1200611.png "6.png")
#### 版权说明：

作者：Coody

版权：©2014-2020 Test404 All right reserved. 版权所有

反馈邮箱：644556636@qq.com
