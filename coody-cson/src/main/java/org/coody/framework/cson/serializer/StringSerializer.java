package org.coody.framework.cson.serializer;

import org.coody.framework.cson.serializer.iface.AbstractSerializer;

public class StringSerializer extends AbstractSerializer<String> {

	@Override
	public String adapt(String target) {
		return "\"" + target.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("\0", "\\0").replace("\"", "\\\"") + "\"";
	}

}
