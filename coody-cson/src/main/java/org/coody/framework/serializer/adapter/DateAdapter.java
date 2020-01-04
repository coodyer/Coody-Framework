package org.coody.framework.serializer.adapter;

import java.util.Date;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class DateAdapter extends AbstractAdapter<Date> {

	@Override
	public String adapt(Date target) {
		return ((Long) target.getTime()).toString();
	}

}