package org.coody.framework.tester;

import org.coody.framework.Cson;
import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.parser.iface.AbstractParser;

public class Test3 {

	public static void main(String[] args) {
		String json = "{\"localDateTime\":1578378353208,\"code\":0,\"id\":10086,\"email\":\"644556636@qq.com\",\"password\":\"123456\",\"resCode\":\"SUCCESS\",\"status\":1,\"createTime\":1578378353208,\"attrs\":[\"111\"],\"list\":[\"123\",\"234\"],\"setting\":{\"id\":10011,\"siteName\":\"测试标题\",\"keywords\":\"关键词\",\"description\":\"测试描述\"},\"map\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"isAllow\":true}";
		ObjectWrapper<UserInfo> result = AbstractParser.parser(json, new TypeAdapter<UserInfo>() {
		});
		UserInfo user = result.getObject();
		System.out.println(Cson.toJson(user));
	}
}
