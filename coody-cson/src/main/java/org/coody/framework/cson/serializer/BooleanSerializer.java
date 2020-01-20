package org.coody.framework.cson.serializer;

import org.coody.framework.cson.serializer.iface.AbstractSerializer;

public class BooleanSerializer extends AbstractSerializer<Boolean> {

	@Override
	public String adapt(Boolean target) {
		return target.toString();
	}

}
