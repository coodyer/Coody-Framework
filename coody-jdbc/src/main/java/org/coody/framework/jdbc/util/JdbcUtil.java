package org.coody.framework.jdbc.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.core.util.UnsafeUtil;
import org.coody.framework.jdbc.annotation.DBVague;
import org.coody.framework.jdbc.entity.DBDataBiller;
import org.coody.framework.jdbc.entity.DBModel;
import org.coody.framework.jdbc.entity.JDBCEntity;
import org.coody.framework.jdbc.entity.Pager;
import org.coody.framework.jdbc.entity.Where;
import org.coody.framework.jdbc.entity.Where.OrWhere;
import org.coody.framework.jdbc.entity.Where.ThisWhere;
import org.coody.framework.jdbc.exception.BuildResultException;
import org.coody.framework.jdbc.exception.base.EdbcException;
import org.coody.framework.jdbc.factory.DBDataBillerFactory;

/**
 * 
 * @author Coody
 * @date 2018年11月14日
 */
@SuppressWarnings("unchecked")
public class JdbcUtil {

	/**
	 * List<map>转models
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> List<T> buildModels(Class<? extends DBModel> clazz, List<Map<String, Object>> maps) {
		try {
			if (StringUtil.isNullOrEmpty(maps)) {
				return null;
			}
			DBDataBiller beanDataBiller = DBDataBillerFactory.getBiller(clazz);
			if (StringUtil.isNullOrEmpty(beanDataBiller.getFields())) {
				return null;
			}
			List<T> results = new ArrayList<T>();
			for (Map<String, Object> map : maps) {
				if (StringUtil.isNullOrEmpty(map)) {
					continue;
				}
				T model = UnsafeUtil.createInstance(clazz);
				for (FieldEntity field : beanDataBiller.getFields().keySet()) {
					String columnName = beanDataBiller.getFields().get(field);
					if (!map.containsKey(columnName)) {
						continue;
					}
					Object value = PropertUtil.parseValue(map.get(columnName), field.getFieldType());
					field.getSourceField().set(model, value);
				}
				results.add(model);
			}
			return results;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * List<map>转models
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T buildModel(Class<? extends DBModel> clazz, Map<String, Object> map) {
		try {
			if (StringUtil.isNullOrEmpty(map)) {
				return null;
			}
			DBDataBiller beanDataBiller = DBDataBillerFactory.getBiller(clazz);
			if (StringUtil.isNullOrEmpty(beanDataBiller.getFields())) {
				return null;
			}
			if (StringUtil.isNullOrEmpty(map)) {
				return null;
			}
			T model = UnsafeUtil.createInstance(clazz);
			for (FieldEntity field : beanDataBiller.getFields().keySet()) {
				String columnName = beanDataBiller.getFields().get(field);
				if (!map.containsKey(columnName)) {
					continue;
				}
				Object value = PropertUtil.parseValue(map.get(columnName), field.getFieldType());
				field.getSourceField().set(model, value);
			}
			return model;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String buildPagerSQL(Pager pager) {
		// 封装分页条件
		if (StringUtil.isNullOrEmpty(pager.getCurrent())) {
			pager.setCurrent(1);
		}
		if (StringUtil.isNullOrEmpty(pager.getSize())) {
			pager.setSize(10);
		}
		Integer startRows = (pager.getCurrent() - 1) * pager.getSize();
		return MessageFormat.format(" limit {0},{1} ", String.valueOf(startRows), String.valueOf(pager.getSize()));
	}

	public static String buildModelSetSql(DBDataBiller beanDataBiller, DBModel model, List<Object> parameters) {
		return buildModelSetSql(beanDataBiller, model, parameters, new String[] {});
	}

	public static String buildModelSetSql(DBDataBiller beanDataBiller, DBModel model, List<Object> parameters,
			String... addFields) {

		List<String> addFieldList = new ArrayList<String>();
		if (!StringUtil.isNullOrEmpty(addFields)) {
			addFieldList = Arrays.asList(addFields);
		}
		StringBuilder sqlBuilder = new StringBuilder();
		for (FieldEntity field : beanDataBiller.getFields().keySet()) {
			try {
				String columnName = beanDataBiller.getFields().get(field);
				if (StringUtil.isNullOrEmpty(columnName)) {
					continue;
				}
				Object fieldValue = field.getSourceField().get(model);
				if (fieldValue == null) {
					continue;
				}
				parameters.add(fieldValue);
				if (addFieldList.contains(columnName)) {
					sqlBuilder.append(columnName).append("=").append("`").append(columnName).append("`").append("+")
							.append("?").append(",");
					continue;
				}
				sqlBuilder.append("`").append(columnName).append("`").append("=?").append(",");
				continue;
			} catch (EdbcException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		sqlBuilder = new StringBuilder(sqlBuilder.toString().substring(0, sqlBuilder.toString().length() - 1));
		return sqlBuilder.toString();
	}

	/**
	 * 解析对象条件、where条件、分页条件
	 * 
	 * @param obj   对象条件
	 * @param where where条件
	 * @param pager 分页条件
	 * @return
	 */
	public static JDBCEntity buildSelectSql(Object modelOrClass, Where where, Pager pager, String orderField,
			Boolean isDesc) {

		DBDataBiller beanDataBiller = DBDataBillerFactory.getBiller(getModelClass(modelOrClass));
		// 获取表名
		StringBuilder sb = new StringBuilder(
				MessageFormat.format("select * from {0} where 1=1 ", beanDataBiller.getTable()));
		List<Object> parameters = new ArrayList<Object>();
		// 封装对象内置条件,默认以等于
		if (!(modelOrClass instanceof java.lang.Class)) {

			for (FieldEntity field : beanDataBiller.getFields().keySet()) {
				try {
					Object value = field.getSourceField().get(modelOrClass);
					if (StringUtil.isNullOrEmpty(value)) {
						continue;
					}
					if (!String.class.isAssignableFrom(field.getFieldType())) {
						sb.append(MessageFormat.format(" and {0}=? ", beanDataBiller.getFields().get(field)));
						parameters.add(value);
						continue;
					}
					DBVague vague = field.getSourceField().getAnnotation(DBVague.class);
					if (vague == null || StringUtil.isNullOrEmpty(vague.value())) {
						sb.append(MessageFormat.format(" and {0}=? ", beanDataBiller.getFields().get(field)));
						parameters.add(value);
						continue;
					}
					sb.append(MessageFormat.format(" and {0} like ? ", beanDataBiller.getFields().get(field)));
					String example = vague.value();
					parameters.add(example.replace("#{0}", value.toString()));
				} catch (EdbcException e) {
					throw e;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		}
		// 封装where条件
		if (!StringUtil.isNullOrEmpty(where) && !StringUtil.isNullOrEmpty(where.getWheres())) {
			List<Where.WhereFace> wheres = where.getWheres();
			for (Where.WhereFace childWhere : wheres) {
				if (childWhere == null) {
					continue;
				}
				if (Where.OrWhere.class.isAssignableFrom(childWhere.getClass())) {
					Where.OrWhere orWheres = (OrWhere) childWhere;
					if (StringUtil.isNullOrEmpty(orWheres.getWheres())) {
						continue;
					}
					sb.append(" and (1=2");
					for (Where.ThisWhere orWhere : orWheres.getWheres()) {
						sb.append(MessageFormat.format(" or {0} {1} ", orWhere.getFieldName(), orWhere.getSymbol()));
						if (StringUtil.isNullOrEmpty(orWhere.getFieldValues())) {
							continue;
						}
						String inParaSql = StringUtil.getInPara(orWhere.getFieldValues().size());
						sb.append(MessageFormat.format(" ({0})  ", inParaSql));
						for (Object value : orWhere.getFieldValues()) {
							parameters.add(value);
						}
					}
					sb.append(" )");
					continue;
				}
				if (Where.ThisWhere.class.isAssignableFrom(childWhere.getClass())) {
					Where.ThisWhere thisWhere = (ThisWhere) childWhere;
					sb.append(MessageFormat.format(" and {0} {1} ", thisWhere.getFieldName(), thisWhere.getSymbol()));
					if (StringUtil.isNullOrEmpty(thisWhere.getFieldValues())) {
						continue;
					}
					String inParaSql = StringUtil.getInPara(thisWhere.getFieldValues().size());
					sb.append(MessageFormat.format(" ({0})  ", inParaSql));
					for (Object value : thisWhere.getFieldValues()) {
						parameters.add(value);
					}
				}

			}
		}
		// 封装排序条件
		if (!StringUtil.isNullOrEmpty(orderField)) {
			sb.append(MessageFormat.format(" order by {0}", orderField));
			if (isDesc != null && isDesc) {
				sb.append(" desc ");
			}
		}
		// 封装分页条件
		if (!StringUtil.isNullOrEmpty(pager)) {
			sb.append(buildPagerSQL(pager));
		}
		return new JDBCEntity(sb.toString(), parameters.toArray());
	}

	public static List<Map<String, Object>> formatToContainer(ResultSet resultSet) {
		String columnName = null;
		Object value = null;
		List<Map<String, Object>> allRecord = new ArrayList<Map<String, Object>>();
		try {
			while (resultSet.next()) {
				ResultSetMetaData data = resultSet.getMetaData();
				Map<String, Object> record = new HashMap<String, Object>();
				for (int i = 1; i <= data.getColumnCount(); i++) {
					columnName = data.getColumnName(i);
					if (StringUtil.isNullOrEmpty(columnName)) {
						continue;
					}
					// 获得列值
					value = resultSet.getObject(columnName);
					// 数据字段存入集合
					if (DBDataBillerFactory.IGNORE_CASE) {
						record.put(columnName.toLowerCase(), value);
						continue;
					}
					record.put(columnName.toLowerCase(), value);
				}
				// 数据列存入集合
				allRecord.add(record);
			}
			return allRecord;
		} catch (SQLException e) {
			throw new BuildResultException("解析参数出现异常", e);
		}
	}

	public static Class<? extends DBModel> getModelClass(Object obj) {
		if (obj instanceof java.lang.Class) {
			return (Class<DBModel>) obj;
		}
		return (Class<DBModel>) obj.getClass();
	}

	public static Object getTableName(Class<? extends DBModel> clazz) {
		DBDataBiller beanDataBiller = DBDataBillerFactory.getBiller(clazz);
		return beanDataBiller.getTable();
	}

}
