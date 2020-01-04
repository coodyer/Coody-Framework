package org.coody.framework.serializer.adapter;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class BooleanAdapter extends AbstractAdapter<Boolean> {

	@Override
	public String adapt(Boolean target) {
		return target.toString();
	}

}
