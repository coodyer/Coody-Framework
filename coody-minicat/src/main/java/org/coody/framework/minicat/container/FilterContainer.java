package org.coody.framework.minicat.container;

import java.util.ArrayList;
import java.util.List;

import org.coody.framework.minicat.servlet.HttpFilter;

public class FilterContainer {

	public static final List<HttpFilter> FILTER_CONTAINER=new ArrayList<HttpFilter>();
	
	public static void pushFilter(HttpFilter filter){
		FILTER_CONTAINER.add(filter);
	}
}
