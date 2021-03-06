package org.coody.framework.serializer;

import org.coody.framework.serializer.iface.AbstractSerializer;

public class StringSerializer extends AbstractSerializer<String> {

	@Override
	public String adapt(String target) {
		return "\"" + target.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("\0", "\\0").replace("\"", "\\\"") + "\"";
	}

}
