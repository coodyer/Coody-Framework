package org.coody.framework.serializer.adapter;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class NumberAdapter extends AbstractAdapter<Number> {

	@Override
	public String adapt(Number target) {
		return target.toString();
	}

}
