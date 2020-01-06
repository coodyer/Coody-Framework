package org.coody.framework.parser;

import java.util.Collection;
import java.util.LinkedList;

import org.coody.framework.entity.JsonFieldEntity;
import org.coody.framework.parser.iface.AbstractParser;
import org.coody.framework.tester.UserInfo;
import org.coody.framework.util.FieldUtil;

public class ValueParser extends AbstractParser {

	@Override
	public void parseValue(String json, Object object) {
		Boolean isInput = false;
		StringBuilder temp = new StringBuilder();
		JsonFieldEntity field = null;
		for (int i = 0; i < json.length(); i++) {
			char chr = json.charAt(i);
			if (chr == '"') {
				isInput = isInput ? false : true;
				String content = temp.toString();
				temp = new StringBuilder();
				if (field == null) {
					field = FieldUtil.getDeclaredField(object.getClass(), content);
					continue;
				} else {
					setAndAppendFieldValue(field, object, content);
				}
				System.out.println(field.getField().getName() + ">>" + content);
				continue;
			}
			if (!isInput && chr == ',') {
				field = null;
				continue;
			}
			temp.append(chr);
		}
	}

	private static void setAndAppendFieldValue(JsonFieldEntity field, Object object, Object value) {
		try {
			if (Collection.class.isAssignableFrom(field.getField().getType())) {
				Collection<Object> exists = (Collection<Object>) field.getField().get(object);
				if (exists == null) {
					exists = new LinkedList<Object>();
				}
				exists.add(value);
				field.getField().set(object, exists);
				return;
			}
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {

		String str = "\"localDateTime\":1578284107970,\"code\":0,\"id\":10086,\"email\":\"644556636@qq.com\",\"password\":\"123456\",\"resCode\":SUCCESS,\"status\":1,\"createTime\":1578284107970,\"attrs\":\"111\",\"222\"";
		UserInfo user = new UserInfo();
		new ValueParser().parseValue(str, user);
		System.out.println(user.getList());
	}
}
