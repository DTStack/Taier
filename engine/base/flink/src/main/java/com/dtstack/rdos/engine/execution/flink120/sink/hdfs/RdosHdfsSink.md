# RdosHdfsSink插件文档

## 建表语句

```

CREATE RESULT TABLE sb4(col1 STRING,col2 INT,col3 INT,col4 INT) WITH (type='datahub',projectName='dtstack',defaultFS='hdfs://172.16.1.151:9000',path='/hyf',fileType='text',delimiter=':')

```

## 插入数据

```

RdosHdfsSink rdosHdfsSink = new RdosHdfsSink();
rdosHdfsSink.genStreamSink(operator);


Table table = tableEnv.fromDataStream(ds, "col1,col2,col3");
table.writeToSink(rdosHdfsSink);

```
