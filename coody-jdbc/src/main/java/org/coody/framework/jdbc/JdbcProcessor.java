package org.coody.framework.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.DateUtils;
import org.coody.framework.core.util.LogUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.jdbc.annotation.DBColumn;
import org.coody.framework.jdbc.container.TransactedThreadContainer;
import org.coody.framework.jdbc.entity.DBDataBiller;
import org.coody.framework.jdbc.entity.DBModel;
import org.coody.framework.jdbc.entity.JDBCEntity;
import org.coody.framework.jdbc.entity.Pager;
import org.coody.framework.jdbc.entity.Where;
import org.coody.framework.jdbc.exception.ExecSqlException;
import org.coody.framework.jdbc.exception.FormatParamsException;
import org.coody.framework.jdbc.exception.PrimaryKeyException;
import org.coody.framework.jdbc.factory.DBDataBillerFactory;
import org.coody.framework.jdbc.util.JdbcUtil;

import com.alibaba.fastjson.JSON;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class JdbcProcessor {

	public Boolean formatSql = true;

	public Boolean getFormatSql() {
		return formatSql;
	}

	public void setFormatSql(Boolean formatSql) {
		this.formatSql = formatSql;
	}

	protected DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 获取主键列表
	 * 
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public List<String> getPrimaryKeys(String tableName) throws SQLException {
		if (StringUtil.isNullOrEmpty(tableName)) {
			return null;
		}
		Connection conn = null;
		try {
			conn = getConn();
			ResultSet colRet = conn.getMetaData().getPrimaryKeys(null, null, tableName);
			List<String> primaryKeys = new ArrayList<String>();
			while (colRet.next()) {
				String primaryKey = colRet.getString("COLUMN_NAME");
				primaryKeys.add(primaryKey);
			}
			return primaryKeys;
		} catch (Exception e) {
			throw new PrimaryKeyException("获取主键列表出现异常", e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	public List<Map<String, Object>> baseQuery(String sql, Object... parameters) {
		ResultSet resultSet = null;
		Connection conn = null;
		PreparedStatement statement = null;
		try {
			// 打开连接对象
			conn = getConn();
			if (conn != null) {
				// statement用来执行SQL语句
				statement = conn.prepareStatement(sql);
				Long threadId = Thread.currentThread().getId();
				String outSql = sql;
				if (formatSql) {
					outSql = formatParameters(sql, parameters);
				}
				LogUtil.log.debug("[线程ID:" + threadId + "][执行语句:" + outSql + "]");
				if (!StringUtil.isNullOrEmpty(parameters)) {
					for (int i = 0; i < parameters.length; i++) {
						statement.setObject((i + 1), parameters[i]);
					}
				}
				// 执行语句，返回结果
				resultSet = statement.executeQuery();
				return JdbcUtil.formatToContainer(resultSet);
			}
		} catch (Exception e) {
			throw new ExecSqlException("语句执行异常>>sql:" + sql + ",parameters:" + JSON.toJSONString(parameters), e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 执行SQL更新语句
	 * 
	 * @param sql        语句
	 * @param parameters 参数
	 * @return
	 */
	public Long baseUpdate(String sql, Object... parameters) {
		Connection conn = null;
		PreparedStatement statement = null;
		try {
			// 打开连接对象
			conn = getConn();
			if (conn != null) {
				// statement用来执行SQL语句
				statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				if (!StringUtil.isNullOrEmpty(parameters)) {
					for (int i = 0; i < parameters.length; i++) {
						statement.setObject((i + 1), parameters[i]);
					}
				}
				Integer code = statement.executeUpdate();
				if (sql.toLowerCase().contains("insert")) {
					try {
						ResultSet rs = statement.getGeneratedKeys();
						if (rs.next()) {
							return rs.getLong(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
						return code.longValue();
					}
				}
				return code.longValue();
			}
		} catch (Exception e) {
			throw new ExecSqlException("语句执行异常>>sql:" + sql + ",parameters:" + JSON.toJSONString(parameters), e);
		} finally {
			if (!TransactedThreadContainer.hasTransacted()) {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return 0L;
	}

	/**
	 * 查询功能区 -start
	 */

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> query(String sql) {
		return baseQuery(sql, new Object[] {});
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @param parameters 参数map容器
	 * @return 结果集
	 */
	public List<Map<String, Object>> query(String sql, Object... parameters) {
		return baseQuery(sql, parameters);
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public Map<String, Object> queryFirst(String sql) {
		return queryFirst(sql, new Object[] {});
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public Map<String, Object> queryFirst(String sql, Object... parameters) {
		if (!sql.toLowerCase().contains("limit")) {
			sql = sql + " limit 1";
		}
		List<Map<String, Object>> list = query(sql, parameters);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 执行SQL语句获得任意类型结果
	 * 
	 * @param clazz      返回类型
	 * @param sql        sql语句
	 * @param parameters 参数列表
	 * @return
	 */
	public <T> T queryFirst(Class<?> clazz, String sql, Object... parameters) {
		List<T> list = query(clazz, sql, parameters);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 执行SQL语句获得任意类型结果
	 * 
	 * @param clazz      返回类型
	 * @param sql        sql语句
	 * @param parameters 参数列表
	 * @return
	 */
	public <T> List<T> query(Class<?> clazz, String sql, Object... parameters) {
		List<Map<String, Object>> records = query(sql, parameters);
		if (StringUtil.isNullOrEmpty(records)) {
			return null;
		}
		if (BaseModel.class.isAssignableFrom(clazz)) {
			List<T> list = new ArrayList<T>();
			for (Map<String, Object> line : records) {
				T t = PropertUtil.mapToModel(line, clazz);
				if (!StringUtil.isNullOrEmpty(t)) {
					list.add(t);
				}
			}
			return list;
		}
		sql = formatSql(sql);
		List list = new ArrayList();
		for (Map<String, Object> line : records) {
			if (StringUtil.isNullOrEmpty(line)) {
				continue;
			}
			Object value = PropertUtil.parseValue(new ArrayList<Object>(line.values()).get(0), clazz);
			if (StringUtil.isNullOrEmpty(value)) {
				if (sql.contains("select count(") || sql.contains("select sum(") || sql.contains("select avg(")) {
					list.add(PropertUtil.parseValue(0, clazz));
				}
				continue;
			}
			list.add(value);
		}
		return list;
	}

	public List<?> queryField(Class<?> fieldType, String sql, Object... parameters) {
		List<Map<String, Object>> records = query(sql, parameters);
		if (StringUtil.isNullOrEmpty(records)) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		for (Map<String, Object> rec : records) {
			if (StringUtil.isNullOrEmpty(rec)) {
				continue;
			}
			for (String key : rec.keySet()) {
				Object value = rec.get(key);
				if (!StringUtil.isNullOrEmpty(value)) {
					value = PropertUtil.parseValue(value, fieldType);
					list.add(value);
				}
				break;
			}
		}
		return list;
	}

	/**
	 * 根据多个字段查询对象
	 * 
	 * @param cla        类
	 * @param parameters 条件集合
	 * @return
	 */
	public <T> List<T> findBean(Class<? extends DBModel> modelClazz, Map<String, Object> parameterMap) {
		List<Map<String, Object>> records = findRecord(modelClazz, parameterMap, null, null);
		return JdbcUtil.buildModels(modelClazz, records);
	}

	/**
	 * 根据多个字段查询对象
	 * 
	 * @param cla        类
	 * @param parameters 条件集合
	 * @return
	 */
	public <T> List<T> findBean(Class<? extends DBModel> modelClazz, Map<String, Object> parameters, String orderField,
			Boolean isDesc) {
		List<Map<String, Object>> records = findRecord(modelClazz, parameters, orderField, isDesc);
		return JdbcUtil.buildModels(modelClazz, records);
	}

	/**
	 * 根据字段查询对象
	 * 
	 * @param cla        类
	 * @param fieldName  字段名
	 * @param fieldValue 字段值,可支持集合与数组IN查询
	 * @return
	 */
	public <T> List<T> findBean(Class<? extends DBModel> modelClazz, String fieldName, Object fieldValue,
			String orderField, Boolean isDesc) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(fieldName, fieldValue);
		List<Map<String, Object>> records = findRecord(modelClazz, parameters, orderField, isDesc);
		return JdbcUtil.buildModels(modelClazz, records);
	}

	public <T> List<T> findBean(Class<? extends DBModel> modelClazz, String orderField, Boolean isDesc) {
		List<Map<String, Object>> records = findRecord(modelClazz, null, orderField, isDesc);
		return JdbcUtil.buildModels(modelClazz, records);
	}

	/**
	 * 根据字段查询对象
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public <T> List<T> findBean(Class<? extends DBModel> modelClazz, String fieldName, Object fieldValue) {
		return findBean(modelClazz, fieldName, fieldValue, null, null);
	}

	/**
	 * 根据对象查询对象集合
	 * 
	 * @param modelOrClass 对象
	 * @param where        where条件
	 * @param pager        分页对象
	 * @return
	 */
	public <T> List<T> findBean(Object modelOrClass, Where where, Pager pager) {
		List<Map<String, Object>> list = findRecord(modelOrClass, where, pager, null, null);
		return JdbcUtil.buildModels(JdbcUtil.getModelClass(modelOrClass), list);
	}

	/**
	 * 根据对象查询对象集合
	 * 
	 * @param modelOrClass 对象
	 * @param where        where条件
	 * @param pager        分页对象
	 * @return
	 */
	public <T> List<T> findBean(Object modelOrClass, Where where, Pager pager, String orderField, Boolean isDesc) {
		List<Map<String, Object>> list = findRecord(modelOrClass, where, pager, orderField, isDesc);
		return JdbcUtil.buildModels(JdbcUtil.getModelClass(modelOrClass), list);
	}

	/**
	 * 根据对象查询对象集合
	 * 
	 * @param modelOrClass 对象
	 * @param where        where条件
	 * @param pager        分页对象
	 * @return
	 */
	public <T> List<T> findBean(Object modelOrClass, Where where, String orderField, Boolean isDesc) {
		List<Map<String, Object>> list = findRecord(modelOrClass, where, null, orderField, isDesc);
		return JdbcUtil.buildModels(JdbcUtil.getModelClass(modelOrClass), list);
	}

	/**
	 * 根据对象查询对象集合
	 * 
	 * @param modelOrClass 对象
	 * @param where        where条件
	 * @param pager        分页对象
	 * @return
	 */
	public <T> List<T> findBean(Object modelOrClass, Pager pager) {
		List<Map<String, Object>> list = findRecord(modelOrClass, null, pager, null, null);
		return JdbcUtil.buildModels(JdbcUtil.getModelClass(modelOrClass), list);
	}

	/**
	 * 根据对象查询对象
	 * 
	 * @param modelOrClass 对象条件
	 * @param where        where条件
	 * @return
	 */
	public <T> List<T> findBean(Object modelOrClass, Where where) {
		List<Map<String, Object>> list = findRecord(modelOrClass, where, null, null, null);
		return JdbcUtil.buildModels(JdbcUtil.getModelClass(modelOrClass), list);
	}

	/**
	 * 根据obj内部字段名和值进行查询，默认条件为等于
	 * 
	 * @param modelOrClass
	 * @return
	 */
	public <T> List<T> findBean(Object modelOrClass) {
		List<Map<String, Object>> list = findRecord(modelOrClass, null, null, null, null);
		return JdbcUtil.buildModels(JdbcUtil.getModelClass(modelOrClass), list);
	}

	/**
	 * 根据字段查询对象
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	public <T> T findBeanFirst(Class<? extends DBModel> modelClazz, String fieldName, Object fieldValue,
			String orderField, Boolean isDesc) {
		List<Object> list = (List<Object>) findBean(modelClazz, fieldName, fieldValue, orderField, isDesc);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return (T) list.get(0);
	}

	/**
	 * 根据字段查询对象
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public <T> T findBeanFirst(Class<? extends DBModel> modelClazz, String fieldName, Object fieldValue) {
		return findBeanFirst(modelClazz, fieldName, fieldValue, null, null);
	}

	/**
	 * 根据对象条件进行查询
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	public <T> T findBeanFirst(Object modelOrClass, Where where, String orderField, Boolean isDesc) {
		List<Map<String, Object>> list = findRecord(modelOrClass, where, null, orderField, isDesc);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return JdbcUtil.buildModel(JdbcUtil.getModelClass(modelOrClass), list.get(0));
	}

	/**
	 * 根据对象条件进行查询
	 * 
	 * @param modelOrClass
	 * @param where
	 * @return
	 */
	public <T> T findBeanFirst(Object modelOrClass, Where where) {
		return findBeanFirst(modelOrClass, where, null, null);
	}

	/**
	 * 根据对象条件进行查询
	 * 
	 * @param modelOrClass
	 * @return
	 */
	public <T> T findBeanFirst(DBModel model) {
		return findBeanFirst(model, null, null, null);
	}

	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 * @param parameters
	 * @return
	 */
	public <T> T findBeanFirst(Class<? extends DBModel> modelClazz, Map<String, Object> parameterMap) {
		Map<String, Object> record = findRecordFirst(modelClazz, parameterMap, null, null);
		return JdbcUtil.buildModel(modelClazz, record);
	}

	/**
	 * 根据对象查询结果集
	 * 
	 * @param modelOrClass 对象条件
	 * @param where        where条件
	 * @param pager        分页信息
	 * @return
	 */
	public List<Map<String, Object>> findRecord(Object modelOrClass, Where where, Pager pager, String orderField,
			Boolean isDesc) {

		JDBCEntity jDBCEntity = JdbcUtil.buildSelectSql(modelOrClass, where, pager, orderField, isDesc);
		return baseQuery(jDBCEntity.getSql(), jDBCEntity.getParameters());
	}

	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla          类
	 * @param parameterMap 多个字段
	 * @return
	 */
	public List<Map<String, Object>> findRecord(Class<? extends DBModel> modelClazz, Map<String, Object> parameterMap,
			String orderField, Boolean isDesc) {
		Where where = new Where();
		List<FieldEntity> entitys = PropertUtil.getBeanFields(modelClazz);
		if (StringUtil.isNullOrEmpty(parameterMap)) {
			JDBCEntity jDBCEntity = JdbcUtil.buildSelectSql(modelClazz, where, null, orderField, isDesc);
			return baseQuery(jDBCEntity.getSql(), jDBCEntity.getParameters());
		}
		for (String key : parameterMap.keySet()) {
			Object value = parameterMap.get(key);
			FieldEntity entity = PropertUtil.getByList(entitys, "fieldName", key);
			DBColumn column = (DBColumn) entity.getAnnotation(DBColumn.class);
			if (column != null) {
				key = column.value();
			}
			if (StringUtil.isNullOrEmpty(value)) {
				where.set(key, "is null", new Object[] {});
				continue;
			}
			if (value instanceof Collection<?>) {
				if (value instanceof Collection<?>) {
					where.set(key, "in", ((Collection<?>) value).toArray());
				}
				continue;
			}
			if (value.getClass().isArray()) {
				if (value instanceof Object[]) {
					where.set(key, "in", (Object[]) value);
				}
				continue;
			}
			where.set(key, value);
		}
		JDBCEntity jDBCEntity = JdbcUtil.buildSelectSql(modelClazz, where, null, orderField, isDesc);
		return baseQuery(jDBCEntity.getSql(), jDBCEntity.getParameters());
	}

	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 * @param parameters
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	public Map<String, Object> findRecordFirst(Class<? extends DBModel> modelClazz, Map<String, Object> parameterMap,
			String orderField, Boolean isDesc) {
		List<Map<String, Object>> list = findRecord(modelClazz, parameterMap, orderField, isDesc);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 分页查询
	 * 
	 * @param modelOrClass 对象条件
	 * @param pager        分页信息
	 * @return
	 */
	public Pager findPager(Object modelOrClass, Pager pager) {
		return findPager(modelOrClass, null, pager, null, null);
	}

	/**
	 * 分页查询
	 * 
	 * @param modelOrClass 对象条件
	 * @param pager        分页信息
	 * @return
	 */
	public Pager findPager(Object modelOrClass, Pager pager, String orderField, Boolean isDesc) {
		return findPager(modelOrClass, null, pager, orderField, isDesc);
	}

	/**
	 * 分页查询
	 * 
	 * @param modelOrClass 对象条件
	 * @param where        where条件
	 * @param pager        分页条件
	 * @return
	 */
	public Pager findPager(Object modelOrClass, Where where, Pager pager, String orderField, Boolean isDesc) {
		JDBCEntity jDBCEntity = JdbcUtil.buildSelectSql(modelOrClass, where, pager, orderField, isDesc);
		Integer totalRows = getCount(jDBCEntity.getSql(), jDBCEntity.getParameters());
		pager.setCount(totalRows);
		List<Map<String, Object>> list = baseQuery(jDBCEntity.getSql(), jDBCEntity.getParameters());
		List<?> objList = JdbcUtil.buildModels(JdbcUtil.getModelClass(modelOrClass), list);
		pager.setData(objList);
		return pager;
	}

	/**
	 * 分页查询
	 * 
	 * @param modelOrClass 对象条件
	 * @param where        where条件
	 * @param pager        分页条件
	 * @return
	 */
	public Pager findFieldPager(Object modelOrClass, String queryField, Where where, Pager pager) {
		JDBCEntity jDBCEntity = JdbcUtil.buildSelectSql(modelOrClass, where, pager, null, null);
		jDBCEntity.setSql(jDBCEntity.getSql().replace("select *", "select " + queryField));
		Integer totalRows = getCount(jDBCEntity.getSql(), jDBCEntity.getParameters());
		pager.setCount(totalRows);
		List<Map<String, Object>> list = baseQuery(jDBCEntity.getSql(), jDBCEntity.getParameters());
		List<?> objList = JdbcUtil.buildModels(JdbcUtil.getModelClass(modelOrClass), list);
		pager.setData(objList);
		return pager;
	}

	/**
	 * 根据语句和条件查询总记录数
	 * 
	 * @param sql 语句
	 * @param map 条件容器
	 * @return
	 */
	public Integer getCount(String sql, Object... parameters) {
		sql = parsCountSql(sql);
		Integer count = queryFirst(Integer.class, sql, parameters);
		return count;
	}

	/**
	 * 根据sql语句查询总记录数
	 * 
	 * @param sql
	 * @return
	 */
	public Integer getCount(String sql) {
		return getCount(sql, new Object[] {});
	}

	/**
	 * 查询功能区 -end
	 */

	/**
	 * 更新功能区 -start
	 */

	/**
	 * 更新操作
	 * 
	 * @param sql
	 * @param modelOrClasss
	 * @return
	 */
	public Long update(String sql, Object... objs) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		for (Object obj : objs) {
			map.put(map.size() + 1, obj);
		}
		return baseUpdate(sql, map.values().toArray());
	}

	/**
	 * 更新操作
	 * 
	 * @param sql
	 * @return
	 */
	public Long update(String sql) {
		return baseUpdate(sql, new Object[] {});
	}

	/**
	 * 根据对象进行更新
	 * 
	 * @param modelOrClass
	 * @param priKeyNames
	 * @return
	 */
	public Long updateByPriKey(DBModel model, String... priKeys) {
		if (model == null) {
			return -1L;
		}
		// 获取表名
		DBDataBiller biller = DBDataBillerFactory.getBiller(JdbcUtil.getModelClass(model));

		StringBuilder sql = new StringBuilder(MessageFormat.format("update {0} set ", biller.getTable()));
		try {
			List<String> keys = Arrays.<String>asList(priKeys);
			Map<Integer, Object> parameters = new HashMap<Integer, Object>();
			for (FieldEntity field : biller.getFields().keySet()) {
				if (field == null) {
					continue;
				}
				String columnName = biller.getFields().get(field);
				if (keys.contains(columnName)) {
					continue;
				}
				Object value = field.getSourceField().get(model);
				if (StringUtil.isNullOrEmpty(value)) {
					continue;
				}
				sql.append(columnName).append("=?").append(",");
				// 封装参数
				parameters.put(parameters.size() + 1, value);
			}
			if (sql.toString().endsWith(",")) {
				sql = new StringBuilder(sql.toString().substring(0, sql.toString().length() - 1));
			}
			sql.append(" where ");
			for (int i = 0; i < priKeys.length; i++) {
				Object fieldValue = PropertUtil.getFieldValue(model, priKeys[i]);
				if (StringUtil.isNullOrEmpty(fieldValue)) {
					sql.append(MessageFormat.format(" {0} is null  ", priKeys[i]));
				} else {
					sql.append(MessageFormat.format(" {0}=? ", priKeys[i]));
					parameters.put(parameters.size() + 1, fieldValue);
				}
				if (i != priKeys.length - 1) {
					sql.append(" and ");
				}
			}
			return baseUpdate(sql.toString(), parameters.values().toArray());
		} catch (Exception e) {
			throw new ExecSqlException("语句执行异常>>sql:" + sql + ",priKeyNames:" + JSON.toJSONString(priKeys), e);
		}
	}

	/**
	 * 更新功能区 -end
	 */

	/**
	 * 更新功能区 -end
	 */

	/**
	 * 插入功能区 -start
	 */
	/**
	 * 根据对象条件进行插入
	 * 
	 * @param modelOrClass
	 * @return
	 */
	public Long insert(DBModel model) {
		if (model == null) {
			return -1L;
		}
		DBDataBiller biller = DBDataBillerFactory.getBiller(JdbcUtil.getModelClass(model));

		// 拼接SQL语句
		StringBuilder sql = new StringBuilder(MessageFormat.format("insert into {0} set ", biller.getTable()));
		try {
			Map<Integer, Object> parameters = new TreeMap<Integer, Object>();

			for (FieldEntity field : biller.getFields().keySet()) {
				String columnName = biller.getFields().get(field);
				Object value = field.getSourceField().get(model);
				if (value == null) {
					continue;
				}
				sql.append(columnName).append("=?").append(",");
				// 封装参数
				parameters.put(parameters.size() + 1, value);
			}
			if (sql.toString().endsWith(",")) {
				sql = new StringBuilder(sql.toString().substring(0, sql.toString().length() - 1));
			}
			return baseUpdate(sql.toString(), parameters.values().toArray());
		} catch (Exception e) {
			throw new ExecSqlException("语句执行异常>>sql:" + sql, e);
		}
	}

	/**
	 * 根据对象进行插入或更新
	 * 
	 * @param modelOrClass
	 * @param priKeyName
	 * @return
	 */
	public Long saveOrUpdate(DBModel model) {
		return saveOrUpdate(model, new String[] {});
	}

	/**
	 * 保存或更新
	 * 
	 * @param modelOrClass 欲保存的对象
	 * @param addFields    当数据存在时累加的字段
	 * @return
	 */
	public Long saveOrUpdate(DBModel model, String... addFields) {
		if (model == null) {
			return -1L;
		}
		DBDataBiller beanDataBiller = DBDataBillerFactory.getBiller(model.getClass());
		// 拼接SQL语句
		StringBuilder sqlBuilder = new StringBuilder(
				MessageFormat.format("insert into {0} set ", beanDataBiller.getTable()));
		List<Object> paras = new ArrayList<Object>();
		String diySql = JdbcUtil.buildModelSetSql(beanDataBiller, model, paras);
		if (StringUtil.isNullOrEmpty(diySql)) {
			return -1L;
		}
		sqlBuilder.append(diySql);
		sqlBuilder.append(" on duplicate key update ");
		diySql = JdbcUtil.buildModelSetSql(beanDataBiller, model, paras, addFields);
		sqlBuilder.append(diySql);
		return baseUpdate(sqlBuilder.toString(), paras.toArray());
	}

	/**
	 * 插入功能区 -end
	 */

	public Set<String> getTables() throws SQLException {
		Connection connection = dataSource.getConnection();
		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet resultSet = metaData.getTables(null, null, null, new String[] { "TABLE" });
		Set<String> tables = new HashSet<String>();
		while (resultSet.next()) {
			tables.add(resultSet.getString(3));
		}
		return tables;
	}

	/**
	 * 内部方法 -start
	 */

	private String parsCountSql(String sql) {
		while (sql.indexOf("  ") > -1) {
			sql = sql.replace("  ", " ");
		}
		Integer formIndex = sql.toLowerCase().indexOf("from");
		if (formIndex > -1) {
			sql = sql.substring(formIndex, sql.length());
		}
		Integer orderIndex = sql.toLowerCase().indexOf("order by");
		if (orderIndex > -1) {
			sql = sql.substring(0, orderIndex);
		}
		Integer limitIndex = sql.toLowerCase().indexOf("limit");
		while (limitIndex > -1) {
			String firstSql = sql.substring(0, limitIndex);
			String lastSql = sql.substring(limitIndex);
			if (lastSql.indexOf(")") > -1) {
				lastSql = lastSql.substring(lastSql.indexOf(")"));
				firstSql = firstSql + lastSql;
			}
			sql = firstSql;
			limitIndex = sql.toLowerCase().indexOf("limit");
		}
		if (orderIndex > -1) {
			sql = sql.substring(0, orderIndex);
		}
		sql = "select count(*) " + sql;
		return sql;
	}

	private String formatSql(String sql) {
		while (sql.contains("  ")) {
			sql = sql.replace("  ", " ");
		}
		return sql.toLowerCase();
	}

	private String formatParameters(String sql, Object... parameters) {
		sql += " ";
		String[] sqlRanks = sql.split("\\?");
		if (sqlRanks.length == 1) {
			return sql;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sqlRanks.length; i++) {
			sb.append(sqlRanks[i]);
			if (i != sqlRanks.length - 1) {
				try {
					Object value = parameters[i];
					if (!StringUtil.isNullOrEmpty(value)) {
						if (Date.class.isAssignableFrom(value.getClass())) {
							value = DateUtils.toString((Date) value, "yyyy-MM-dd HH:mm:ss");
						}
						if (String.class.isAssignableFrom(value.getClass())) {
							value = "'" + value + "'";
						}
					}
					sb.append(value);
				} catch (Exception e) {
					throw new FormatParamsException("格式化参数出现异常>>sql:" + sql + ",parameters:" + parameters.toString());
				}
			}
		}
		return sb.toString();
	}

	// 创建connection连接对象
	private Connection getConn() throws Exception {
		if (!TransactedThreadContainer.hasTransacted()) {
			Connection conn = dataSource.getConnection();
			conn.setAutoCommit(true);
			return conn;
		}
		// 创建并写入连接
		Connection conn = TransactedThreadContainer.getConnection(dataSource);
		if (conn == null) {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			TransactedThreadContainer.writeDataSource(dataSource, conn);
		}
		return conn;
	}

}
