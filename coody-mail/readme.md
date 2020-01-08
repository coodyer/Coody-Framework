
### Coody Mail

### 注意事项：
   Coody-Mail是一款基于Coody Framework而实现的轻量化邮件发送插件

### 功能特色：
1) 轻量化邮件发送插件
2) 体积小，无第三方依赖

### 如何使用


```
	<dependency>
	    <groupId>org.coody.framework</groupId>
	    <artifactId>coody-mail</artifactId>
	    <version>${版本号}</version>
	</dependency>
```

### 提供方法	

```
		public static void main(String[] args) {
			EmailSendConfig config = new EmailSendConfig();
			config.setEmail("644556636@qq.com");
			config.setPassword("密码");
			config.setPort(465);
			config.setSmtp("smtp.qq.com");
	
			EmailSender sender = new EmailSender(config);
			//单独发送
			sender.send("对方邮箱", "标题", "内容");
			//群发
			List<String> targeEmail = new ArrayList<String>(Arrays.asList(new String[] { "465464@qq.com", "459898@qq.com" }));
			sender.send(targeEmail, "标题", "内容");
		}
```
	
### 版权说明：

在本项目源代码中，已有测试demo，包括mvc、切面等示例

作者：Coody
    
版权：©2014-2020 Test404 All right reserved. 版权所有

反馈邮箱：644556636@qq.com

