
### Coody Cson

### 注意事项：
   Coody-Cson是一款基于Coody Framework而实现的轻量化JSON框架
### 注解：
1) @CsonDateFormat
用于标记Date字段格式
2) @CsonIgnore:
用于屏蔽某个字段的序列化 & 反序列化




### 功能特色：
1) 轻量化JSON插件
2) 静态方法，使用简单
3) 体积小，无第三方依赖

### 如何使用


```
	<dependency>
	    <groupId>org.coody.framework</groupId>
	    <artifactId>coody-cson</artifactId>
	    <version>${版本号}</version>
	</dependency>
```

### 提供方法
	
	Cson.toJson(Object object);
	
	Cson.toObject(String json, Class<T> clazz);
	
	Cson.toObject(String json, TypeAdapter<T> type);
	

### 性能测试

	关于性能问题，笔者暂时不做深入优化，因为不同的实现方案在对于不同的数据类型各有优劣。以下提供几个简单的测试。

###### 序列化测试：
![序列化对象](https://images.gitee.com/uploads/images/2020/0108/105822_730f1507_1200611.png "序列化")
###### 反序列化测试：
![反序列化对象](https://images.gitee.com/uploads/images/2020/0108/105830_33bcaca7_1200611.png "反序列化")
	
	
### 序列化复杂容器

![复杂容器](https://images.gitee.com/uploads/images/2020/0108/110224_ef236ad2_1200611.jpeg "1578452537(1).jpg")
	
	
### 版权说明：

在本项目源代码中，已有测试demo，包括mvc、切面等示例

作者：Coody
    
版权：©2014-2020 Test404 All right reserved. 版权所有

反馈邮箱：644556636@qq.com

