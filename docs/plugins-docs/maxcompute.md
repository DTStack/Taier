# maxcompute插件

## 1. 配置样例

```
{
    "computeType": 1,
    "engineType": "maxcompute",
    "name": "dq_109_1561907101131",
    "pluginInfo": {
        "accessId": "LTAIljBeC8ei9Yy0",
        "accessKey": "gwTWasH7sEE0pSUEuiXnw7JecXyfGF",
        "endPoint": "https://service.odps.aliyun.com/api",
        "project": "dtstack_dev",
        "typeName": "maxcompute"
    },
    "sqlText": "create table dq_109_1561907101131 lifecycle 3 as select * from (select L.id1 as L_id1,L.id2 as L_id2,L.dt as L_dt,L.month as L_month,L._c1 as L__c1,R.id1 as R_id1,R.id2 as R_id2,R.dt as R_dt,R.month as R_month,R._c1 as R__c1 from aa L left join aa R on 1=1 AND L.id1 = R.id1 where (R.id1 is null or L.id2!=R.id2 or L.dt!=R.dt or L._c1!=R._c1 or (L._c1 is null and R._c1 is not null or L._c1 is not null and R._c1 is null)) union select L.id1 as L_id1,L.id2 as L_id2,L.dt as L_dt,L.month as L_month,L._c1 as L__c1,R.id1 as R_id1,R.id2 as R_id2,R.dt as R_dt,R.month as R_month,R._c1 as R__c1 from aa L right join aa R on 1=1 AND L.id1 = R.id1 where (L.id1 is null or L.id2!=R.id2 or L.dt!=R.dt or L._c1!=R._c1 or (L._c1 is null and R._c1 is not null or L._c1 is not null and R._c1 is null))) t;",
    "taskId": "ade0dd87",
    "taskParams": "",
    "taskType": 0,
    "tenantId": 41
}
```

## 2. 参数说明

* **pluginInfo.'typeName'**

 	* 描述：任务类型是 maxcompute 的任务时，此处为 `maxcompute`
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'accessId'**

 	* 描述：maxcompute 连接信息中的 Access Id
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'accessKey'**

 	* 描述：maxcompute 连接信息中的 Access Key
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'endPoint'**

 	* 描述：maxcompute 连接信息中的 end point
 		
	* 必选：是 <br />

	* 默认值：无 <br />

* **pluginInfo.'project'**

 	* 描述：maxcompute 连接信息中 project
 		
	* 必选：是 <br />

	* 默认值：无 <br />