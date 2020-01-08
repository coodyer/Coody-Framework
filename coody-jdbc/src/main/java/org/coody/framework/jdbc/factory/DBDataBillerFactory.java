package org.coody.framework.jdbc.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.magic.iface.DynamicContainer;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.jdbc.annotation.DBColumn;
import org.coody.framework.jdbc.annotation.DBTable;
import org.coody.framework.jdbc.entity.DBDataBiller;
import org.coody.framework.jdbc.entity.DBModel;
import org.coody.framework.jdbc.exception.JdbcBuilderException;

/**
 * 
 * @author Coody
 *
 */
public class DBDataBillerFactory {

	public static final ConcurrentHashMap<Class<?>, DBDataBiller> BEANDATABILLER_CONTAINER = new ConcurrentHashMap<Class<?>, DBDataBiller>();

	public static DynamicContainer dataBillerContainer;
	/**
	 * 是否反驼峰表名
	 */
	private static final boolean IS_UNPARSE_TABLE = true;
	/**
	 * 是否反驼峰字段名
	 */
	private static final boolean IS_UNPARSE_FIELD = false;

	/**
	 * 忽略大小写
	 */
	public static final boolean IGNORE_CASE = true;

	public static DBDataBiller getBiller(Class<?> clazz) {

		if (CommonUtil.isNullOrEmpty(clazz)) {
			throw new JdbcBuilderException("传入class为空");
		}
		DBDataBiller biller = BEANDATABILLER_CONTAINER.get(clazz);
		if (!CommonUtil.isNullOrEmpty(biller)) {
			return biller;
		}
		biller = createBiller(clazz);
		BEANDATABILLER_CONTAINER.put(clazz, biller);
		return biller;
	}

	private static DBDataBiller createBiller(Class<?> clazz) {
		if (CommonUtil.isNullOrEmpty(clazz)) {
			throw new JdbcBuilderException("传入class为空");
		}
		DBDataBiller biller = new DBDataBiller();

		DBTable table = clazz.getAnnotation(DBTable.class);
		String tableName = "";
		if (!CommonUtil.isNullOrEmpty(table)) {
			tableName = table.value();
		}
		if (CommonUtil.isNullOrEmpty(tableName)) {
			tableName = getTableName(clazz.getSimpleName());
		}
		biller.setTable(tableName);
		List<FieldEntity> fields = PropertUtil.getBeanFields(clazz);
		biller.setFields(new HashMap<FieldEntity, String>());
		for (FieldEntity field : fields) {
			biller.getFields().put(field, getColumnName(field));
		}
		return biller;
	}

	public static void accelerateEngine(Set<Class<?>> initialingClazzs)
			throws InstantiationException, IllegalAccessException {
		Set<Class<?>> dbClazzs = new HashSet<Class<?>>();
		for (Class<?> clazz : initialingClazzs) {
			if (!DBModel.class.isAssignableFrom(clazz)) {
				continue;
			}
			dbClazzs.add(clazz);
		}
		// 创建字段
		Set<String> fields = new HashSet<String>();
		for (Class<?> clazz : dbClazzs) {
			fields.add(clazz.getName());
		}
		// 对象赋值
		for (Class<?> clazz : dbClazzs) {
			DBDataBiller biller = createBiller(clazz);
			BEANDATABILLER_CONTAINER.put(clazz, biller);
		}
	}

	private static String getTableName(String modelName) {
		try {
			if (IS_UNPARSE_TABLE) {
				return unParsParaName(modelName);
			}
			return modelName;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据字段名获取数据库列名
	 * 
	 * @param fieldName
	 * @return
	 */
	private static String getColumnName(String fieldName) {
		if (IGNORE_CASE) {
			fieldName = fieldName.toLowerCase();
		}
		if (!IS_UNPARSE_FIELD) {
			return fieldName;
		}
		return unParsParaName(fieldName);
	}

	/**
	 * 驼峰式命名转下划线
	 * 
	 * @param paraName
	 * @return
	 */
	private static String unParsParaName(String paraName) {
		char[] chrs = paraName.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chrs.length; i++) {
			char chr = chrs[i];
			if (i != 0 && Character.isUpperCase(chr)) {
				sb.append("_");
			}
			sb.append(String.valueOf(chr).toLowerCase());
		}
		return sb.toString();
	}

	/**
	 * 获取模型对于数据库字段名
	 * 
	 * @param field
	 * @return
	 */
	private static String getColumnName(FieldEntity field) {
		DBColumn column = field.getSourceField().getAnnotation(DBColumn.class);
		if (CommonUtil.isNullOrEmpty(column)) {
			return getColumnName(field.getFieldName());
		}
		if (!IGNORE_CASE) {
			return column.value();
		}
		return column.value().toLowerCase();
	}

	/**
	 * 首个字符串大写
	 * 
	 * @param s
	 * @return
	 */
	public static String firstUpcase(String s) {
		if (Character.isUpperCase(s.charAt(0))) {
			return s;
		}
		return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
	}
}
