package org.coody.framework.tester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coody.framework.Cson;
import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.parser.iface.AbstractParser;

public class Test5 {

	public static void main(String[] args) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("map1_k1", "map1_value1");
		map1.put("map1_k2", "map1_value2");
		map1.put("map1_k3", "map1_value3");
		list.add(map1);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("map2_k1", "map2_value1");
		map2.put("map2_k2", "map2_value2");
		map2.put("user", getUserInfo());
		list.add(map2);

		String json = Cson.toJson(list);

		ObjectWrapper<List<Map<String, Object>>> wrapper = AbstractParser.parser(json,
				new TypeAdapter<List<Map<String, Object>>>() {
				});
		List<Map<String, Object>> result = wrapper.getObject();
		System.out.println(Cson.toJson(result));

	}

	private static UserInfo getUserInfo() {
		UserInfo user = new UserInfo();
		user.setCreateTime(new Date());
		user.setDoubleTest(6546.457984654D);
		user.setFloatTest(1.116578545465f);
		user.setEmail("644556636@qq.com");
		user.setId(10086);
		user.setPassword("123456");
		user.setStatus(1);
		user.setResCode(ResCodeEnum.SUCCESS);
		user.setLocalDateTime(LocalDateTime.now());
		user.setAttrs(new String[] { "111" });
		user.setList(Arrays.asList(new String[] { "123", "234" }));
		user.setCreateTime(new Date());
		user.setIsAllow(true);

		SettingInfo setting = new SettingInfo();
		setting.setSiteName("测试标题");
		setting.setDescription("测试描述");
		setting.setId(10011);
		setting.setKeywords("关键词");
		user.setSetting(setting);

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		user.setMap(map);
		return user;
	}
}
