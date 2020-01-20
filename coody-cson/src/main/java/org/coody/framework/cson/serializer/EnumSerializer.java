package org.coody.framework.cson.serializer;

import org.coody.framework.cson.serializer.iface.AbstractSerializer;

public class EnumSerializer extends AbstractSerializer<Enum<?>> {

	@Override
	public String adapt(Enum<?> target) {
		return "\"" + target.name() + "\"";
	}

}
