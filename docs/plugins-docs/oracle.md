# oracle插件

## 1. 配置样例

```
{
    "computeType": 1,
    "engineType": "oracle",
    "name": "dq_CALL_CENTER_1561903200063",
    "pluginInfo": {
        "dbUrl": "jdbc:oracle:thin:@172.16.8.178:1521:xe",
        "pwd": "abc123",
        "typeName": "oracle",
        "userName": "dtstack"
    },
    "sqlText": "begin execute immediate 'create table temp_data_0abf161a as select 41 as tenant_id,13 as monitor_id,25 as rule_id,3604 as record_id, count(1)  as  val  from call_center where 1=1  union select 41 as tenant_id,13 as monitor_id,64 as rule_id,3604 as record_id, count(1)  as  val  from call_center where 1=1 a union select 41 as tenant_id,13 as monitor_id,67 as rule_id,3604 as record_id, count(1)  as  val  from call_center where 1=1 1 '; end;",
    "taskId": "0abf161a",
    "taskParams": "",
    "taskType": 0,
    "tenantId": 41
}
```

## 2. 参数说明

* **pluginInfo.'typeName'**

 	* 描述：任务类型是 oracle 的任务时，此处为 `oracle`
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'dbUrl'**

 	* 描述：oracle 数据源的jdbcurl
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'userName'**

 	* 描述：oracle 数据源的用户名
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'pwd'**

 	* 描述：oracle 数据源的密码
 		
	* 必选：是 <br />

	* 默认值：无 <br />