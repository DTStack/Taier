#### Taiga Quick start of task submission

This article mainly faces the users of Taiga, through an example to introduce how to use Taiga to realize the submission of data synchronization tasks
For detailed documentation on the finkx, please refer to [flinkx](https://github.com/DTStack/flinkx)

* Realize efficient data migration between multiple heterogeneous data sources through flinkx
* Realize the submission of flinkx tasks through Taiga

#### premise

* Start Taiga and configure the flink component information under  cluster
* configure flinkx plugins on Taiga
* configure engine-plugins on Taiga
* Cluster component test performance passed correctly

#### build params

The submission parameters corresponding to each task in DAGcheduleX are different

Here takes the task parameters of flinkx data synchronization as an example. The task parameters json contains three parts

**reader(Source database)** : Where to read the data, read those fields, and start reading from that incremental identifier are all configured here

**writer(Target database)** : The read data is configured here wherever it is written

**setting(Configuration information)**:The threshold number of error records and the threshold of error proportion can be set, so that the task can be stopped in time after an error, so as to avoid wasting resources. You can set the job concurrency and job rate, and submit the data synchronization task speed.



Start Taiga, you can refer to the following two ways to implement, submit the flinkx data synchronization task

##### Wizard mode build parameters

The submission can be configured through the wizard mode interface of Taiga, but the input and output data source information corresponding to the data synchronization task needs to be introduced in the data source center in advance

1. **Configure data read target**

&emsp;&emsp;&emsp;&emsp;&emsp; Data read target: the source of the read data, corresponding to the reader parameter information in the flinkx configuration

![](./images/submit/reader.png)





2. **Configuration data write destination**

&emsp;&emsp;&emsp;&emsp;&emsp;Data reading target: The target of data writing, corresponding to the writer parameter information in the flinkx configuration
![](./images/submit/writer.png)



3. **Configure read and write field mapping information**

&emsp;&emsp;&emsp;&emsp;&emsp;Set the one-to-one correspondence between the read and write fields, and verify whether the format of the read and write fields is correct
![](./images/submit/mapping.png)

4. **Configuration information submitted by the configuration task**

&emsp;&emsp;&emsp;&emsp;&emsp;The configuration information corresponds to the setting parameter information in the flinkx configuration, including (job rate, concurrent number, error record number)

![](./images/submit/setting.png)
5. **Save preview**
![](./images/submit/preview.png)






##### Script mode build parameters

The parameters of the script mode are exactly the same as the parameter information of flinkx, including the parameter information of writer, reader, and setting

The script mode does not rely on the support of data source plug-ins and operation interfaces, and is more extensible

```
{
  "job" : {
    "content" : [ {
      "reader" : {
        "parameter" : {
          "customSql" : "",
          "startLocation" : "",
          "increColumn" : "",
          "column" : [ {
            "name" : "id",
            "type" : "INT",
            "key" : "id"
          }, {
            "name" : "222",
            "format" : "",
            "type" : "STRING",
            "value" : "222",
            "key" : "222"
          }, {
            "name" : "weqwe",
            "format" : "",
            "type" : "DATE",
            "value" : "2020-08-09",
            "key" : "weqwe"
          } ],
          "connection" : [ {
            "sourceId" : 157,
            "password" : "******",
            "jdbcUrl" : [ "jdbc:mysql://172.16.100.115:3306/ide" ],
            "type" : 1,
            "table" : [ "pg_test2" ],
            "username" : "drpeco"
          } ],
          "sourceIds" : [ 157 ]
        },
        "name" : "mysqlreader"
      },
      "writer" : {
        "parameter" : {
          "postSql" : [ "delete from cata where id =1 ", "delete from cata where id =3 " ],
          "password" : "******",
          "session" : [ ],
          "column" : [ {
            "name" : "project_id",
            "type" : "INT",
            "key" : "project_id"
          }, {
            "name" : "tenant_id",
            "type" : "INT",
            "key" : "tenant_id"
          }, {
            "name" : "record_type",
            "type" : "INT",
            "key" : "record_type"
          } ],
          "connection" : [ {
            "jdbcUrl" : "jdbc:mysql://172.16.100.115:3306/ide",
            "table" : [ "cata" ]
          } ],
          "writeMode" : "insert",
          "sourceIds" : [ 157 ],
          "username" : "drpeco",
          "preSql" : [ "delete from cata where id =1 ", "delete from cata where id =3 " ]
        },
        "name" : "mysqlwriter"
      }
    } ],
    "setting" : {
      "dirty" : {
        "path" : "dirty_11_copy2",
        "hadoopConfig" : {
          "dfs.ha.namenodes.ns1" : "nn1,nn2",
          "dfs.namenode.rpc-address.ns1.nn2" : "kudu2:9000",
          "dfs.client.failover.proxy.provider.ns1" : "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider",
          "dfs.namenode.rpc-address.ns1.nn1" : "kudu1:9000",
          "dfs.nameservices" : "ns1"
        }
      },
      "restore" : {
        "maxRowNumForCheckpoint" : 0,
        "isRestore" : false,
        "restoreColumnName" : "",
        "restoreColumnIndex" : 0
      },
      "errorLimit" : {
        "record" : 101
      },
      "speed" : {
        "bytes" : 0,
        "channel" : 1
      }
    }
  }
}
```

#### Task submit

```flow
  start=>start: start task
  replace=>operation: replace task parameter
  resource=>condition: has judge slot
  submit=>operation: submit task
  timeWait=>operation: Wait for a limited time
  submit=>operation: sumbit task
  jobId=>condition: Return submitted task information
  status=>operation: get task status
  log=>operation: get task log
  ed=>end: end of task
  
  start->replace->resource
  resource(yes)->submit
  resource(no)->timeWait
  timeWait->submit
  submit->jobId
  jobId(yes)->status->log->ed
  jobId(no)->ed
 
```

##### start task

The task source is divided into three methods: temporary、schedule、fill data, but the final function is actionService.start

When the constructed task parameters are submitted to the corresponding start method, the necessary parameter information of the task will be verified: task unique ID, task engine type, task unique ID and other information



##### replace task parameter

Taiga supports system variables and custom variables

If the parameter information submitted by the task contains defined variables, after starting the task, these parameter information will be replaced according to the corresponding format



##### Conditions of submit

Before the task is submitted, it is necessary to make corresponding submission judgments to confirm whether the task meets the submission conditions

Take the session mode data synchronization task as an example: before the task is submitted, it will be judged whether there is a flink session task on yarn, whether the slot resource in the session meets the task, etc.



##### submit task
After the task meets the submission conditions, the submission logic is executed. The implementation submitted here is implemented by the task submission plugin in Taiga

After the task is submitted, it needs to return a unique identifier, such as yarn's applicationId. The status and logs of subsequent tasks depend on this unique identifier.



##### get task status

After the task is submitted, the task status will be obtained intermittently according to the unique identifier



##### get task log

After the task is executed, part of the log information of the task will be obtained

#### task run

##### temporary run 
After the task is configured on the Taiga platform, click Run. After the task is completed, you can see the output log information on the platform for task debugging.

![](./images/submit/temprun.png)

##### scheudule run
After the task parameters are configured, you can configure the scheduling properties of the task. After the submission is completed, the task will be scheduled to run periodically  
![](./images/submit/schedulerun.png)

