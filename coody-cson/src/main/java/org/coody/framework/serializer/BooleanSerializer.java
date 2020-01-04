package org.coody.framework.serializer;

import org.coody.framework.serializer.iface.AbstractSerializer;

public class BooleanSerializer extends AbstractSerializer<Boolean> {

	@Override
	public String adapt(Boolean target) {
		return target.toString();
	}

}
