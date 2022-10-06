# Coody-Logged

#### 使用说明

#### Maven：


```
<dependency>
  <groupId>org.coody.framework</groupId>
  <artifactId>coody-logged</artifactId>
  <version>${版本号}</version>
</dependency>
```

##### 1. 基本描述

	coody-logged是Coody Framework旗下日志插件。
	

##### 2. 基础配置

###### Coody Framework下配置
```
	#日志级别，默认DEBUG
	coody.logged.level=INFO 
	#日志编码，默认UTF-8
	coody.logged.encode=UTF-8
	#日志格式，默认[${LEVEL} ${TIME(yyyy-MM-dd HH:mm:ss:SSS)} ${THREAD} ](${SIMPLESTACK}) ：${MSG} 
	coody.logged.pattern=[${LEVEL} ${TIME(yyyy-MM-dd HH:mm:ss:SSS)} ${THREAD} ](${SIMPLESTACK}) ：${MSG} 
	#是否输出到控制台 默认true
	coody.logged.sysout=true
	#自定义输出函数，默认加载内置函数无需配置，逗号分割，传入class需要继承 org.coody.framework.logged.function.iface.LoggedFunction
	coody.logged.functions=org.coody.framework.logged.function.AppendFunction,org.coody.framework.logged.function.LevelFunction 
	#DEBUG输出文件
	coody.logged.outOfDebug=debug.log
	#INFO输出文件
	coody.logged.outOfInfo=info.log
	#ERROR输出文件
	coody.logged.outOfError=error.log
```

###### 其他场景下配置(springboot、spring均可)
```
	LoggedConfig.encode = "UTF-8";
	LoggedConfig.functions = "org.coody.framework.logged.function.AppendFunction,org.coody.framework.logged.function.LevelFunction";
	LoggedConfig.level = "INFO";
	LoggedConfig.outOfDebug = "output.log";
	LoggedConfig.outOfInfo = "output.log";
	LoggedConfig.outOfError = "output.log";
	LoggedConfig.pattern = "[${LEVEL} ${TIME(yyyy-MM-dd HH:mm:ss:SSS)} ${THREAD} ](${SIMPLESTACK}) ：${MSG}";
	LoggedConfig.sysout = true;
```



###### 自定义函数

###### 新建class，继承LoggedFunction，实现invoke和getName方法
```
	public class CustomFunction extends LoggedFunction {
	
		@Override
		public String invoke(String parameter, LoggedEntity logged) {
			return "自定义函数内容,参数:" + parameter;
		}
	
		@Override
		public String getName() {
			return "CUSTOM";
		}
	}

```

###### 加载函数


```
	LoggedConfig.functions = "org.coody.custom.CustomFunction";
```

###### Coody Framework下加载函数
```
	coody.logged.functions=org.coody.custom.CustomFunction
```
	
###### 使用函数，无参不需要传，有参直接使用()包围参数内容


```
	[${CUSTOM(参数内容)} ${TIME(yyyy-MM-dd HH:mm:ss:SSS)} ${THREAD} ](${SIMPLESTACK}) ：${MSG}
```

###### 打印日志

```
	public class LogUtil {
	
		public static LoggedEngine log = new LoggedEngine();
	
	}
```
```
	LogUtil.log.info("日志内容,参数1:%s,参数2:%s" ,"参数1内容","参数2内容");
```

### 版权说明：


    在本项目源代码中，已有测试示例

    作者：Coody
    
    版权：©2014-2020 Test404 All right reserved. 版权所有

    反馈邮箱：644556636@qq.com
