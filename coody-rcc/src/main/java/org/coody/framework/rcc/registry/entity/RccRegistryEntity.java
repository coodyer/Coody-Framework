package org.coody.framework.rcc.registry.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.rcc.entity.RccInstance;

@SuppressWarnings("serial")
public class RccRegistryEntity extends BaseModel {

	private Map<String, Set<RccInstance>> data=new HashMap<String, Set<RccInstance>>();

	public Map<String, Set<RccInstance>> getData() {
		return data;
	}

	public void setData(Map<String, Set<RccInstance>> data) {
		this.data = data;
	}

}
