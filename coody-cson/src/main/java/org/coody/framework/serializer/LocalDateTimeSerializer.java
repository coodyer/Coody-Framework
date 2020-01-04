package org.coody.framework.serializer;

import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;

import org.coody.framework.serializer.iface.AbstractSerializer;

public class LocalDateTimeSerializer extends AbstractSerializer<ChronoLocalDateTime<?>> {

	@Override
	public String adapt(ChronoLocalDateTime<?> target) {
		return target.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli() + "";
	}

}