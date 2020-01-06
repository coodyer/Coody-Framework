package org.coody.framework.tester;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import org.coody.framework.Cson;

import com.alibaba.fastjson.JSON;

public class Test {

	public static void main(String[] args) {
		UserInfo user = new UserInfo();
		user.setCreateTime(new Date());
		user.setEmail("644556636@qq.com");
		user.setId(10086);
		user.setPassword("123456");
		user.setStatus(1);
		user.setResCode(ResCodeEnum.SUCCESS);
		user.setLocalDateTime(LocalDateTime.now());
		user.setAttrs(new String[] { null });
		user.setList(Arrays.asList(new String[] { "123", "234" }));
		user.setCreateTime(new Date());
		user.setIsAllow(true);
		SettingInfo setting = new SettingInfo();
		setting.setId(10011);
		setting.setDescription("测试");
		user.setSetting(setting);
		Long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			JSON.toJSONString(user);
		}
		System.out.println("fastjson:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			Cson.toJson(user);
		}
		System.out.println("cson:" + (System.currentTimeMillis() - start));
	}
}
