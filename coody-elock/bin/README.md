# Coody-Elock

#### 动机：

在实际产品研发生涯中，有很多业务会涉及到分布式锁的使用。其中比较成熟的，当属redisson(笔者在用)。

然，对于笔者这种有无限洁癖又不喜臃肿的野生技术员来说。使用redisson徒增了近12M的体积让笔者非常难受。

加上redisson配置过于繁琐，相对笨重，仅仅使用redisson来作为分布式锁，实在是宰牛刀杀鸡。

故此，笔者准备研发自己的分布式锁插件。

注：本插件在未来一段时间将不断迭代，提供切面、redis集群、zookeeper支持等拓展，归属Coody Framework下，将发布Maven中央仓库。


#### 软件架构

使用jedis基于redis订阅功能实现的分布式锁插件


#### 使用说明

##### Maven：


```
<dependency>
  <groupId>org.coody.framework</groupId>
  <artifactId>coody-elock</artifactId>
  <version>alpha-1.2.4</version>
</dependency>
```


###### 1. 初始化JedisPool

```
        //直接传入连接池初始化
		ELockCache.initJedisPool(JediPool);
		//传入ip、端口、密码、超时时间初始化
		ELockCache.initJedisPool(host, port, secretKey, timeOut);
		//传入ip、端口、密码、超时时间、配置器初始化
		ELockCache.initJedisPool(host, port, secretKey, timeOut, jedisPoolConfig);
```


注：无密码请传null

###### 2. 加锁


```
ELocker.lock(key, expireSecond);
```

###### 3. 释放锁

```
ELocker.unLock(key);
```

###### 4. 注意

加锁代码(ELocker.lock(key, expireSecond);)。需try{}catch{}包围，并在finally释放锁(ELocker.unLock(key);)


```
try {
			ELocker.lock(key, 100);
			for (int i = 0; i < 10; i++) {
				System.out.println(Thread.currentThread().getId() + ">>" + i);
				Thread.sleep(100l);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			ELocker.unLock(key);
		}
```


###### 5. 测试代码


```
package org.coody.framework.elock.test;

import java.util.ArrayList;
import java.util.List;

import org.coody.framework.elock.ELocker;
import org.coody.framework.elock.redis.ELockCache;

/**
 * 分布式锁测试
 * @author Coody
 *
 * 2018年12月14日
 * 
 * @blog 54sb.org
 */
public class ELockTest {

	//要加锁的key
	static String key = "TESTLOCK_1";

	static {
		//初始化jedis连接
		ELockCache.initJedisPool("127.0.0.1", 16379, "123456", 10000);
	}

	public static void main(String[] args) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					test();
				}
			});
			threads.add(thread);
		}
		//启动十个线程
		for (Thread thread : threads) {
			thread.start();
		}
	}

	//要锁的方法
	private static void test() {
		try {
			ELocker.lock(key, 100);
			for (int i = 0; i < 10; i++) {
				System.out.println(Thread.currentThread().getId() + ">>" + i);
				Thread.sleep(100l);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			ELocker.unLock(key);
		}
	}
}

```

###### 6. 执行效果

![输入图片说明](https://images.gitee.com/uploads/images/2018/1214/184647_f99ea98c_1200611.png "c.png")

###### 7.Coody Framework配置

![输入图片说明](https://images.gitee.com/uploads/images/2019/0103/141224_a1711e32_1200611.png "屏幕截图.png")

     
###### 8.常规用法

    1、使用代码添加分布式锁：


```
String key="USER_MODIFY_LOCK"+userId;
		try {
			ELocker.lock(key, 20);
			userDao.delUser(userId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			ELocker.unLock(key);
		}
```

    
![输入图片说明](https://images.gitee.com/uploads/images/2019/0103/141650_d442cc01_1200611.png "屏幕截图.png")

    2、使用注解添加分布式锁：


```
	@ELock(name = "USER_MODIFY_LOCK", fields = "userId", waitTime = 20)
	public void delUser(String userId) {
		userDao.delUser(userId);
	}
```

![输入图片说明](https://images.gitee.com/uploads/images/2019/0103/141847_48f9532f_1200611.png "屏幕截图.png")

    3、使用执行器添加分布式锁：

```
public void delUser(String userId) throws InterruptedException {
		String key="USER_MODIFY_LOCK"+userId;
		Integer code=new AbstractLockAble(key,20) {
			
			@Override
			public Object doService() {
				return userDao.delUser(userId);
			}
		}.invoke();
	}
```


![输入图片说明](https://images.gitee.com/uploads/images/2019/0103/142026_3c84a07f_1200611.png "屏幕截图.png")

### 版权说明：


    在本项目源代码中，已有测试示例

    作者：Coody
    
    版权：©2014-2020 Test404 All right reserved. 版权所有

    反馈邮箱：644556636@qq.com
