package org.coody.framework.minicat.web.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.util.AntUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.minicat.web.entity.MvcMapping;

public class MappingContainer {

	private static Map<String, MvcMapping> MVC_MAP = new ConcurrentHashMap<String, MvcMapping>();

	private static Map<String, MvcMapping> MVC_ANT_MAP = new ConcurrentHashMap<String, MvcMapping>();

	public static MvcMapping getMapping(String path) {
		MvcMapping mapping = MVC_MAP.get(path);
		if (mapping != null) {
			return mapping;
		}
		for (String mappingPath : MVC_ANT_MAP.keySet()) {
			if (AntUtil.isAntMatch(path, mappingPath)) {
				return MVC_ANT_MAP.get(mappingPath);
			}
		}
		return null;
	}

	public static void writeMapping(MvcMapping mappinger) {
		if (AntUtil.isAntPatten(mappinger.getPath())) {
			mappinger.setIsAntPath(true);
			String path = mappinger.getPath();
			if (path.contains("{")) {
				List<String> attrs = StringUtil.textCutCenters(path, "{", "}");
				if (!StringUtil.isNullOrEmpty(attrs)) {
					for (String attr : attrs) {
						path = path.replace("{" + attr + "}", "*");
					}
				}
			}
			MVC_ANT_MAP.put(path, mappinger);
			return;
		}
		MVC_MAP.put(mappinger.getPath(), mappinger);
	}

	public static boolean containsPath(String path) {
		if (MVC_MAP.containsKey(path)) {
			return true;
		}
		return MVC_ANT_MAP.containsKey(path);
	}
	

}
