package org.coody.framework.core.constant;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.coody.framework.core.util.AntUtil;
/**
 * 
 * @author Coody
 *
 * 2018年12月17日
 * 
 * @blog 54sb.org
 */
public class InsideTypeConstant {

	public static final List<Class<?>> INSIDE_TYPES=new ArrayList<Class<?>>(Arrays.asList(
			new Class<?>[]{
				String.class,Integer.class,Double.class,Float.class,Boolean.class,Date.class,Long.class
				}));
	public static final List<String> SYSTEM_MATEHERS = new ArrayList<String>(
			Arrays.asList(new String[] { "java.*", "javax.*" }));

	public static final boolean isSystem(Class<?> clazz) {
		for(String mateher:SYSTEM_MATEHERS){
			if(AntUtil.isAntMatch(clazz.getName(), mateher)){
				return true;
			}
		}
		return false;
	}
}
