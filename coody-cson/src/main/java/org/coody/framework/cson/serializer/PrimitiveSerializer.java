package org.coody.framework.cson.serializer;

import org.coody.framework.cson.serializer.iface.AbstractSerializer;

public class PrimitiveSerializer extends AbstractSerializer<Object> {

	@Override
	public String adapt(Object target) {
		return target.toString();
	}

}
