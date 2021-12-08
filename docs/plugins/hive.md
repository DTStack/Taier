# hive插件

## 1. 配置样例

```
{
    "computeType": 1,
    "engineType": "hive",
    "name": "dq_CALL_CENTER_test",
    "pluginInfo": {
        "dbUrl": "jdbc:hive2://node1:10000/data_science",
        "pwd": "",
        "typeName": "hive",
        "userName": ""
    },
    "sqlText": "begin select 1; end;",
    "taskId": "0abf161e",
    "taskParams": "",
    "taskType": 0,
    "tenantId": 41
}
```

## 2. 参数说明

* **pluginInfo.'typeName'**

 	* 描述：任务类型是 hive 的任务时，此处为 `hive`
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'dbUrl'**

 	* 描述：hive 数据源的jdbcurl
 		
	* 必选：是 <br />

	* 默认值：无 <br />
	
* **pluginInfo.'userName'**

 	* 描述：hive 数据源的用户名
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'pwd'**

 	* 描述：hive 数据源的密码
 		
	* 必选：是 <br />

	* 默认值：无 <br />