package org.coody.framework.tester;

import org.coody.framework.Cson;

public class Test2 {

	public static void main(String[] args) {
		String json = "{\"localDateTime\":1578303683248,\"code\":0,\"id\":10086,\"email\":\"644556636@qq.com\",\"password\":\"123456\",\"resCode\":SUCCESS,\"status\":1,\"createTime\":1578303683248,\"list\":[\"123\",\"234\"],\"setting\":{\"id\":10011,\"keywords\":\"关键词\",\"description\":\"测试描述\"},\"isAllow\":true}";
		UserInfo user = Cson.toObject(json, UserInfo.class);
		System.out.println(user.toString());
	}
}
