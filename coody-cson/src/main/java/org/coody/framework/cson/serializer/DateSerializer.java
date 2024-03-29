package org.coody.framework.cson.serializer;

import java.util.Date;

import org.coody.framework.cson.serializer.iface.AbstractSerializer;

public class DateSerializer extends AbstractSerializer<Date> {

	@Override
	public String adapt(Date target) {
		return ((Long) target.getTime()).toString();
	}

}