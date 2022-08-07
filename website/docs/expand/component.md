---
title: 组件
sidebar_label: 组件
---
# 任务
## 自定义开发组件

### 定义组件
在 `com.dtstack.taier.common.enums.EComponentType` 枚举中定义一个新的组件类型

枚举中有对应的六个属性:  

| 属性   | 含义 | 能否为空 | 
| --------- | ------- | ----|
| typeCode      |  组件类型值| 否|
| name      |  组件名 | 否|
| confName      | 组件配置名称|是
| sort     | 排序 |否
| componentScheduleType     | 组件类型 |是


:::caution
componentScheduleType主要用于区分组件所属组件类型  
`公共组件`: 默认所有任务都可以使用该组件配置信息  
`资源调度组件`：任务提交依赖调度组件  
`存储组件`：任务数据存储组件  
`计算组件`：任务提交组件  
:::



这里我们以oceanBase为例:
如:
```java
    OCEAN_BASE(7, "OceanBase", "oceanBaseConf",EComponentScheduleType.COMPUTE),
```


配置含义:
:::info
 组件名称为 OceanBase 归宿为计算组件分组组件
:::


### 配置组件模版
新增完组件类型，我们需要给组件添加配置模版
1. 定义组件模型
在taier的数据库插入模型数据  

这里我们以oceanBase为例:
```sql
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'OCEAN_BASE', '{"owner": "COMPUTE", "dependsOn": [], "allowKerberos": "false", "allowCoexistence": false, "uploadConfigType": "0", "versionDictionary": "","nameTemplate":"oceanBase"}', null, 12, 0, 'STRING', '', 0, now(), now(), 0);
```
模型json内容为
```json
{
    "owner":"COMPUTE",
    "dependsOn":[

    ],
    "allowKerberos":"false",
    "allowCoexistence":false,
    "uploadConfigType":"0",
    "versionDictionary":"",
    "nameTemplate":"oceanBase"
}
```
| 属性   | 含义 |
| --------- | ------- |
| owner      |  组件类型|
| dependsOn      |  依赖组件类型 |
| allowKerberos      | 是否允许开启kerberos|
| allowCoexistence     | 是否允许多个版本共存|
| uploadConfigType     | 是否允许上传配置文件 |
| versionDictionary     | 组件版本选择 |
| nameTemplate     | 组件对应plugins插件名称 |

2. 定义组件模版

这里我们以oceanBase为例:  
```sql
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'oceanBase', '-118', null, 6, 0, 'LONG', '', 0, now(),now(), 0);


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -118, 5, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -118, 5, 'INPUT', 0, 'username', '', null, null, null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -118, 5, 'PASSWORD', 0, 'password', '', null, null, null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -118, 5, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -118, 5, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, now(),now(), 0);
```

定义`console_component_config`表中 component_id为-118类型为oceanBase的组件模版配置参数

:::tip
组件模版参数`cluster_id` 默认统一为-2 `component_id` 统一为负数  
更多复杂的组件模版渲染配置 可以参考flink组件
:::


:::caution
变更组件的配置SQL，需要`重启`Taier才会生效
:::