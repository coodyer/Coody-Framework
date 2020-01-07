package org.coody.framework.tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coody.framework.Cson;
import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.parser.iface.AbstractParser;

public class Test7 {

	public static void main(String[] args) {
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("map1_k1", "map1_value1");
		map1.put("map1_k2", "map1_value2");
		map1.put("map1_k3", "map1_value3");

		Map<Object, Object> map2 = new HashMap<Object, Object>();
		map2.put("map2_k1", "map2_value1");
		map2.put(map1, "map2_value2");
		list.add(map2);

		String json = Cson.toJson(list);

		System.out.println(json);

		ObjectWrapper<List<Map<String, Object>>> wrapper = AbstractParser.parser(json,
				new TypeAdapter<List<Map<String, Object>>>() {
				});
		List<Map<String, Object>> result = wrapper.getObject();
		System.out.println(Cson.toJson(result));

	}
}
