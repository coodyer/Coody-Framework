package org.coody.framework.serializer;

import org.coody.framework.serializer.iface.AbstractSerializer;

public class PrimitiveSerializer extends AbstractSerializer<Object> {

	@Override
	public String adapt(Object target) {
		return target.toString();
	}

}
