#### Coody Jdbc

### 注意事项：
在系统开发中对于数据库模型尽量不要使用基础数据类型，所有的实体类需继承BaseModel。文档中灰色方法为不常用方法，红色为常用方法。

###面向问题：
剔除mapping.xml文件

避免sql语句误操作

简化繁琐的语句

易于拓展控制数据源

可剔除dao层

### 配置：


```
#配置dataConfig

coody.bean.hikariDataSource.class=com.zaxxer.hikari.HikariDataSource
coody.bean.hikariDataSource.driverClassName=com.mysql.jdbc.Driver
coody.bean.hikariDataSource.jdbcUrl=jdbc\:mysql\://127.0.0.1/czone?useUnicode\=true&characterEncoding\=utf-8
coody.bean.hikariDataSource.username=root
coody.bean.hikariDataSource.password=123456
coody.bean.hikariDataSource.maxPoolSize=64
coody.bean.hikariDataSource.minIdle=8

#配置jdbcHandle
coody.bean.jdbcHandle.class=org.coody.framework.jdbc.JdbcHandle
coody.bean.jdbcHandle.dataSource=${hikariDataSource}
```


### 核心方法：
List<Map<String, Object>> baseQuery(String sql, Object... paras)



Long baseUpdate(final String sql, final Object... paras)



baseQuery用于查询操作。是数据库查询操作最终流入点

baseUpdate用于更新操作。是数据库更新操作最终流入点



### 公开方法：

### query(String sql)
List<Map<String, Object>> query(String sql)

使用案例：

String sql="select * from user"
List<Map<String, Object>> records= query(sql);

### query(String sql, Object... paras)
List<Map<String, Object>> query(String sql, Object... paras)

使用案例：

String sql="select * from user where id>?"
Integer userId=10001; List> records= query(sql,userId);

### queryFirst(String sql)
Map<String, Object> queryFirst(String sql)

使用案例：

String sql="select * from user where id=1001"
Map<String,Object> rec= queryFirst(sql);



### queryFirst(String sql, Object... paras)
Map<String, Object> queryFirst(String sql, Object... paras)

使用案例：

String sql="select * from user where id=?"
Integer userId=10001; Record rec= queryFirst(sql,userId);

### queryAuto(Class<?> clazz, String sql, Object... paras)
<T> T queryAuto(Class<?> clazz, String sql, Object... paras)

使用案例：

String sql="select account from user where status=?"
List<Integer> userAccounts= queryAuto(Integer.class,sql,1);

### queryFirstAuto(Class<?> clazz, String sql, Object... paras)
<T> T queryFirstAuto(Class<?> clazz, String sql, Object... paras)

使用案例：

String sql="select account from user where status=?"
Integer account = queryFirstAuto(Integer.class,sql,1);



### queryField(Class<?> fieldType, String sql, Object... paras)
List<?> queryField(Class<?> fieldType, String sql, Object... paras)

使用案例：

String sql="select id from user where status=?"
List<Integer> ids= queryField(Integer.class,sql,1);

String sql="select user_name from user where status=?"

List<String> ids= queryField(String.class,sql,1);

### findBean(Object obj)
<T> List<T> findBean(Object obj)

使用案例：

List users= findBean (User.class);//加载所有用户列表

User user=new User();

user.setType(1);

user.setStatus(1);

user.setChannel("CHANNEL001");

List users= findBean (user);

//加载满足条件的用户列表，条件为：type=1 and status=1 and channel=’ CHANNEL001’

### findBean(Object obj, Where where)
<T> List<T> findBean(Object obj, Where where)

使用案例：

Where where=new Where();

where.set("type",1);

where.set("status",2);

where.set("channel","in",{"channel001","channel002"});

Where.set("level",">=",30);

where.set("userName","is not null");

List users= findBean (User.class,where);

//加载满足条件的用户列表，条件为： type=1 and status=2 and channel in (‘channel001’,’ channel002’) and level>=30 and username is not null



### findBean(Object obj, Pager pager)
<T> List<T> findBean(Object obj, Pager pager)

使用案例：

Pager pager=new Pager(10,20);
List users= findBean (User.class, pager);//加载的用户列表，分页条件为： limit 200,10

### findBean(Object obj, Where where,Pager pager)
<T> List<T> findBean(Object obj, Where where, Pager pager)

使用案例：

Pager pager=new Pager(10,20);

Where where=new Where().set("id",1001);
List users= findBean (User.class, pager);

//加载的用户列表，条件为：where id=1001 limit 200,10



### findBean(Object obj, Where where, String orderField, Boolean isDesc)
<T> List<T> findBean(Object obj, Where where, String orderField, Boolean isDesc)

使用案例：

Where where=new Where();

where.set("type",1).set("status",2);

List users= findBean (User.class,where,"createTime",true);//加载满足条件的用户列表，条件为： type=1 and status=2 order by createTime desc

### findBean(Object obj,Where where,Pager pager,String orderField,Boolean isDesc)
<T> List<T> findBean(Object obj, Where where, Pager pager, String orderField, Boolean isDesc)

使用案例：

Where where=new Where();

where.set("type",1).set("status",2);

Pager pager=new Pager(10,20);

List users= findBean (User.class,where,pager,"createTime",true);//加载满足条件的用户列表，条件为： type=1 and status=2 order by createTime desc limit 200,10

### saveOrUpdateAuto(Object obj)
Long saveOrUpdateAuto(Object obj)

使用案例：

### saveOrUpdateAuto(userInfo);

当userInfo字段的主键(唯一索引)为空或在数据库中不存在，则执行insert操作。当主键(唯一索引)存在时则根据主键(唯一索引)更新其他字段。执行成功返回影响行数，执行报错返回-1

### saveOrUpdateAuto(Object obj,String... addFields)
Long saveOrUpdateAuto(Object obj, String... addFields)

使用案例：

saveOrUpdateAuto(userInfo,"account");

当userInfo字段的主键(唯一索引)为空或在数据库中不存在，则执行insert操作。当主键(唯一索引)存在时则根据主键(唯一索引)更新其他字段，account字段在原基础上增加传入的值(account=account+传入的account值)。执行成功返回影响行数，执行报错返回-1



