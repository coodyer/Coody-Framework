
### Coody Esource

### 注意事项：
   Coody-Esource是一款基于Coody Framework而实现的轻量化数据库连接池

### 功能特色：
1) 体积小、速度快

### 如何使用


```
	<dependency>
	    <groupId>org.coody.framework</groupId>
	    <artifactId>coody-esource</artifactId>
	    <version>${版本号}</version>
	</dependency>
```

### 在Coody Framework中的配置	

```
		coody.bean.dataSource.class=org.coody.framework.esource.ESource
		coody.bean.dataSource.field.driver=com.mysql.jdbc.Driver
		coody.bean.dataSource.field.url=jdbc\:mysql\://127.0.0.1/database?useUnicode\=true&characterEncoding\=UTF-8&serverTimezone=GMT%2B8&&useSSL=false&useCompression=true
		coody.bean.dataSource.field.user=root
		coody.bean.dataSource.field.password=123456
		coody.bean.dataSource.field.maxPoolSize=128 #最大连接数 默认64
		coody.bean.dataSource.field.minPoolSize=8 #最小连接数 默认2
		coody.bean.dataSource.field.initialPoolSize=16 #连接耗尽时初始化连接数 默认5
		coody.bean.dataSource.field.maxWaitTime=30000 #获取连接最大等待时长，单位毫秒，默认30000
		coody.bean.dataSource.field.maxIdleTime=60000 #空闲连接回收时长，单位毫秒，默认60000
		
```

### 在Spring中的配置	

```
	<bean id="dataSource" destroy-method="close"
		class="org.coody.framework.esource.ESource">
		<property name="driverClass" value="com.mysql.jdbc.Driver" />
		<property name="jdbcUrl" value="jdbc\:mysql\://127.0.0.1/database?useUnicode\=true&characterEncoding\=UTF-8&serverTimezone=GMT%2B8&&useSSL=false&useCompression=true" />
		<property name="user" value="root" />
		<property name="password" value="123456" />
		<!--最大连接数 默认64-->
		<property name="maxPoolSize" value="128" />
		<!--最小连接数 默认2-->
		<property name="minPoolSize" value="8" />
		<!--连接耗尽时初始化连接数 默认5-->
		<property name="initialPoolSize" value="16" />
		<!--获取连接最大等待时长，单位毫秒，默认30000-->
		<property name="maxWaitTime" value="30000" />
		<!--空闲连接回收时长，单位毫秒，默认60000-->
		<property name="maxIdleTime" value="60000" />
	</bean>
```

### 在SpringBoot中的配置	

```
	@Configuration
	public class ESourceConfig {

	@Bean
	public DataSource dataSource() {
		ESource source = new ESource();
		source.setDriver("com.mysql.jdbc.Driver");
		source.setUrl("jdbc\\:mysql\\://127.0.0.1/database?useUnicode\\=true&characterEncoding\\=UTF-8&serverTimezone=GMT%2B8&&useSSL=false&useCompression=true");
		source.setUser("root");
		source.setPassword("123456");
		//最大连接数  默认64
		source.setMaxPoolSize(64);
		//最小连接数  默认2-->
		source.setMinPoolSize(8);
		//连接耗尽时初始化连接数  默认5-->
		source.setInitialPoolSize(16);
		//获取连接最大等待时长 单位毫秒 默认30000-->
		source.setMaxWaitTime(30000);
		//空闲连接回收等待时长 单位毫秒 默认60000-->
		source.setMaxIdleTime(60000);
		
		return source;
	}
}
```
	
### 版权说明：

在本项目源代码中，已有测试demo

作者：Coody
    
版权：©2014-2020 Test404 All right reserved. 版权所有

反馈邮箱：644556636@qq.com

