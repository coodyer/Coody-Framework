package org.coody.framework.serializer;

import org.coody.framework.serializer.iface.AbstractSerializer;

public class EnumSerializer extends AbstractSerializer<Enum<?>> {

	@Override
	public String adapt(Enum<?> target) {
		return target.name();
	}

}
