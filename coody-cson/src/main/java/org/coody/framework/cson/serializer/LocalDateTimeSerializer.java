package org.coody.framework.cson.serializer;

import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;

import org.coody.framework.cson.serializer.iface.AbstractSerializer;

public class LocalDateTimeSerializer extends AbstractSerializer<ChronoLocalDateTime<?>> {

	@Override
	public String adapt(ChronoLocalDateTime<?> target) {
		return target.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli() + "";
	}

}