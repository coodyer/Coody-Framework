package org.coody.framework.cson.serializer;

import org.coody.framework.cson.serializer.iface.AbstractSerializer;

public class NumberSerializer extends AbstractSerializer<Number> {

	@Override
	public String adapt(Number target) {
		return target.toString();
	}

}
