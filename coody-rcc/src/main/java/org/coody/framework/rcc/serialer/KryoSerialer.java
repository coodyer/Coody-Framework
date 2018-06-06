package org.coody.framework.rcc.serialer;

import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.rcc.serialer.iface.RccSerialer;
import org.coody.framework.rcc.util.KryoUtil;

@InitBean
public class KryoSerialer implements RccSerialer{

	@Override
	public byte[] serialize(Object object) {
		return KryoUtil.convertDataToBytes(object);
	}

	@Override
	public <T> T unSerialize(byte[] data) {
		return KryoUtil.convertToObject(data);
	}

}
