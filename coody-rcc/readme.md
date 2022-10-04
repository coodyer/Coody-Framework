# Coody-RCC

#### 使用说明

##### Maven：


```
<dependency>
  <groupId>org.coody.framework</groupId>
  <artifactId>coody-rcc</artifactId>
  <version>${版本号}</version>
</dependency>
```

###### 1. 基本描述

	coody-rcc是Coody Framework旗下基于RPC协议的分布式框架。
	
	注册中心默认采用Redis，未来可能提供更多的注册中心，如需更换自行实现 org.coody.framework.rcc.registry.iface.RccRegistry
	
	通信介质默认采用TCP短连接，未来可能提供更多的通信方式，如需更换自行实现 org.coody.framework.rcc.signal.RccSignaler
	
	序列化工具默认采用JDKSerialer，如需更换自行实现 org.coody.framework.rcc.serialer.iface.RccSerialer
	

###### 2. 基础配置

```
		coody.rcc.port=8888  #分布式通信端口，与web端口无关
		coody.rcc.pr=1   #分配权重，当存在多个服务时，该指标越大，被调用的概率越高
		coody.rcc.expire=5000  #调用超时时间，毫秒
		coody.rcc.keepTime=5000  #同步注册中心时间，毫秒
```

######### 更多配置

```
		coody.rcc.registerKey=8888  #分布式通信端口，与web端口无关
		coody.rcc.serialer=org.coody.framework.rcc.serialer.iface.RccSerialer   #配置序列化工具
		coody.rcc.signaler=org.coody.framework.rcc.signal.RccSignaler  #配置通信实现类
```

###### 3. 配置注册中心

######### 配置redis

```
coody.bean.jedisPoolConfig.class=redis.clients.jedis.JedisPoolConfig
coody.bean.jedisPoolConfig.field.maxTotal=32
coody.bean.jedisPoolConfig.field.maxWait=32

coody.bean.jedisPool.class=redis.clients.jedis.JedisPool
coody.bean.jedisPool.parament.poolConfig=${jedisPoolConfig}
coody.bean.jedisPool.parament.host=127.0.0.1
coody.bean.jedisPool.parament.port=6379
coody.bean.jedisPool.parament.timeout=10000
coody.bean.jedisPool.parament.password=123456
```

######### 配置注册中心

```
coody.bean.redisRegistry.class=org.coody.framework.rcc.registry.RedisRegistry
coody.bean.redisRegistry.parament.jedisPool=${jedisPool}
```


###### 4. 基本使用

	注解 RccService,提供参数"path"，修饰在一个bean上，既代表该类为分布式服务者，所有的public方法均可被调用
	
	注解 RccClient,提供参数"path"，修饰在一个interface上，既代表该类为分布式消费者，所有的方法都会被Coody-RCC自动实现
	
######### 创建服务者项目，在coody.properties 按规则配置后，新建服务类UserServiceImpl
	```
	import org.coody.framework.core.util.encrypt.EncryptUtil;
	import org.coody.framework.rcc.annotation.RccService;
	
	@RccService(path = "user")
	public class UserServiceImpl {
	
		public String call(String call) {
			System.out.println("收到调用->" + call);
			return EncryptUtil.md5(call);
		}
	}
	
	```
	
######### 创建消费者项目，在coody.properties 按规则配置后，新建服务类UserClient
	```
	@RccClient(path = "user")
	public interface UserClient {
	
		public String call(String content);
	}
	```
######### 在消费者项目，在coody.properties 使用@AutoBuild注入UserClient，笔者使用的是controller
	```
	@PathBinding("/test")
	public class TestController {
	
		@AutoBuild
		UserClient client;
	
		@PathBinding("/call")
		@JsonOut
		public String call() {
			return client.call("123456");
		}
	}
	```
	
可以看到，消费者项目启动后，使用UserClient进行分布式调用。调用服务者项目，服务者项目会对传输过去的字符串进行MD5(测试案例)

注：需确保两台机器配置了相同的redis注册中心，也需要保证两台机器之间内网互通，并保证跨服务器调用的方法所使用的参数均已实现序列化
	
	
######### 配置注册中心

### 版权说明：


    在本项目源代码中，已有测试示例

    作者：Coody
    
    版权：©2014-2020 Test404 All right reserved. 版权所有

    反馈邮箱：644556636@qq.com
