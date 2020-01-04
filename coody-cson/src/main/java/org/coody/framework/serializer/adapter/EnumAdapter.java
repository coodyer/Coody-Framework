package org.coody.framework.serializer.adapter;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class EnumAdapter extends AbstractAdapter<Enum<?>> {

	@Override
	public String adapt(Enum<?> target) {
		return target.name();
	}

}
