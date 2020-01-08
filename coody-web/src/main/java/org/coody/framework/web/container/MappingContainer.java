package org.coody.framework.web.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.ant.AntUtil;
import org.coody.framework.core.util.string.StringUtil;
import org.coody.framework.web.entity.MvcMapping;

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
				List<String> attrs = StringUtil.stringCutCenters(path, "{", "}");
				if (!CommonUtil.isNullOrEmpty(attrs)) {
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
