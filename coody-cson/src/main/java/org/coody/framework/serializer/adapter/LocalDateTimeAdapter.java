package org.coody.framework.serializer.adapter;

import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class LocalDateTimeAdapter extends AbstractAdapter<ChronoLocalDateTime<?>> {

	@Override
	public String adapt(ChronoLocalDateTime<?> target) {
		return target.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli() + "";
	}

}