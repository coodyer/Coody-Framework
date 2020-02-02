
### Coody Cache

### 注意事项：
建议系统各项业务以层的形式使用缓存，大弧度缓解数据库压力

### 面向问题：
    缓解数据库压力，提升服务端并发上限，提升业务代码执行效率。

### 核心对象(注解)：
1) @CacheWrite
    写缓存，方法执行后，将结果写入缓存。
2) @CacheWipe:
    清理单个缓存，方法执行后，清理缓存
### 功能特色：
    1) 弱化缓存“层”的概念
    2) 支持缓存KEY
    3) 支持缓存带参写入与清理
    4) 任意Bean方法均支持缓存
    5) 支持redis、memcached、localCache等缓存(凡是支持超时时间缓存均可)
### 如何为一个方法添加缓存

    1) 简单使用，不要求清理，不强调实时性(默认缓存时间10秒)


```
    @CacheWrite
    public TagForUser loadUserTags(Integer uid,Integer tid){
    
    }
```


    注：方法执行后，程序会根据类、方法、参数值生成一个方法KEY，并且将方法的结果写入缓存，并设置time有效时长，默认10秒，下次调用直接返回缓存，不会进入方法代码逻辑。

    2) 简单使用，不要求清理，区分参数，设置缓存时间

    //单个参数

```
    @CacheWrite(fields="uid")
    public TagForUser loadUserTags(Integer uid,Integer tid){ 
    
    }
```


    //多个参数

```
    @CacheWrite(fields={"uid","tid"})
    public TagForUser loadUserTags(Integer uid,Integer tid){
    
    }
```


    //设置缓存时间

```
    @CacheWrite(fields={"uid","tid"},time=60)
    public TagForUser loadUserTags(Integer uid,Integer tid){
    
    }

```

    注：方法执行后，程序会根据类、方法、fields指定的参数值生成一个方法KEY，并且将方法的结果写入缓存，并设置time有效时长，默认10秒，下次调用直接返回缓存，不会进入方法代码逻辑。

    3) 精准使用，指定key，可清理：

```
    @CacheWrite(key=CacheFinal.SYSTEM_TAGS,time=3600)
    public ListloadSysTags(){
    
    }
```


    //指定参数/时间

```
    @CacheWrite(key=CacheFinal.ANCHOR_TAGS,time=60,fields="uid")
    public ListloadAnchorTags(Integer uid){
    
    }
```


    注：方法执行后，程序会根据CacheWrite指定的key生成缓存。如果指定了fields字段，程序会根据key+fields字段的值生成缓存。并设置time有效时长，默认10秒，下次调用直接返回缓存，不会进入方法代码逻辑。


### 数据更新后，如何清理缓存1) 常规清理，清理指定key

    //不强调参数，仅清理

```
 @CacheWipe(key=CacheFinal.ANCHOR_TAGS)
    public Integer saveUserTags(TagForUser userTag){
    
    }

```

   //强调参数，清理

```
    @CacheWipe(key=CacheFinal.ANCHOR_TAGS,fields="userTag.tid")
    public Integer saveUserTags(TagForUser userTag){
    
    }
```


    注：方法执行后，程序会根据CacheWipe 指定的KEY清理缓存，如果指定了fields字段，程序会根据key+fields字段的值清理缓存。


    1)多缓存清理，清理多套key

    //不强调参数，清理多套缓存

```
    @CacheWipe(key=CacheFinal.PET_YEAR_VALUE_CACHE),
    @CacheWipe(key=CacheFinal.PET_YEAR_DAY_VALUE_CACHE)}
    public Integer addSendValue(Integer uid,Integer sendValue){
    
    }    
```

    //强调参数，清理多套缓存

```
    @CacheWipe(key=CacheFinal.PET_YEAR_VALUE_CACHE,fields="uid"),
    @CacheWipe(key=CacheFinal.PET_YEAR_DAY_VALUE_CACHE,fields="uid")}
    public Integer addSendValue(Integer uid,Integer sendValue){
    
    }
```


    注：方法执行后，程序会根据CacheWipes读取CacheWipe集合，并分别执行常规清理规则
    
### 版权说明：

在本项目源代码中，已有测试demo，包括mvc、切面等示例

作者：Coody
    
版权：©2014-2020 Test404 All right reserved. 版权所有

反馈邮箱：644556636@qq.com