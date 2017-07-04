# RdosHbaseSink插件文档

## 建表语句

```

CREATE RESULT TABLE test1(col1 STRING,col2 INT,col3 TIMESTAMP) WITH (type='datahub',projectName='dtstack',host='172.16.1.151',port='2181',parent='/flink137',columnFamily='cf1[col1:col2] cf2[col3]',rowkey='col1:col2:col3')

```

## 插入数据

```

RdosHbaseSink rdosHbaseSink = new RdosHbaseSink();
rdosHbaseSink.genStreamSink(operator);


Table table = tableEnv.fromDataStream(ds, "col1,col2,col3");
table.writeToSink(rdosHbaseSink);

```

