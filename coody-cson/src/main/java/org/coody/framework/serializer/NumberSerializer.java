package org.coody.framework.serializer;

import org.coody.framework.serializer.iface.AbstractSerializer;

public class NumberSerializer extends AbstractSerializer<Number> {

	@Override
	public String adapt(Number target) {
		return target.toString();
	}

}
