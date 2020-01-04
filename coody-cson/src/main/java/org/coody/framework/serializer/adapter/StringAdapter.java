package org.coody.framework.serializer.adapter;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class StringAdapter extends AbstractAdapter<String> {

	@Override
	public String adapt(String target) {
		return "\"" + target.replace("\"", "\\\"") + "\"";
	}

}
