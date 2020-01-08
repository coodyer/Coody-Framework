package org.coody.framework.core.util.uuid;

import java.util.UUID;

public class JUUIDUtil {

	public synchronized static String createUuid() {
			String str = UUID.randomUUID().toString().replace("-", "");
			return str;
	}

}
