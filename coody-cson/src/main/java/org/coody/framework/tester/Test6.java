package org.coody.framework.tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coody.framework.Cson;
import org.coody.framework.adapter.TypeAdapter;
import org.coody.framework.entity.ObjectWrapper;
import org.coody.framework.parser.iface.AbstractParser;

public class Test6 {

	public static void main(String[] args) {
		Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();

		map.put("1", getlist());
		map.put("2", getlist());

		String json = Cson.toJson(map);
		System.out.println(json);

		ObjectWrapper<Map<String, List<Map<String, Object>>>> wrapper = AbstractParser.parser(json,
				new TypeAdapter<Map<String, List<Map<String, Object>>>>() {
				});
		System.out.println(Cson.toJson(wrapper.getObject()));

	}

	public static List<Map<String, Object>> getlist() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 3; i++) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			for (int j = 0; j < 4; j++) {
				map1.put("map" + i + "_k" + j, "map" + i + "_value" + j);
			}
			list.add(map1);
		}
		return list;
	}
}
