### url请求样例
    {"modifyUser":{"phoneNumber":"18888888888","dtUicUserId":7,"defaultProjectId":null,"status":0,"userName":"sweepingmonk@163.com","email":"sweepingmonk@163.com","isDeleted":0,"gmtCreate":1496499746000,"gmtModified":1496499746000,"id":3},"taskDesc":"a","version":0,"sqlText":"CREATE SOURCE TABLE MyTable(name STRING,age INT) WITH (type='ELASTIC5',es.cluster.name='poc_dtstack',es.address='172.16.1.232,172.16.1.142',es.index='xcindex',es.query='John Doe');select * from MyTable;CREATE RESULT TABLE MyResult(name STRING, age INT) WITH ( type='ELASTIC5',es.cluster.name='poc_dtstack',es.address='172.16.1.232,172.16.1.142',es.index='xcindex2',es.type='my-type')","engineType":3,"computeType":1,"nodePid":31,"status":9,"createUser":{"phoneNumber":"18888888888","dtUicUserId":7,"defaultProjectId":null,"status":0,"userName":"sweepingmonk@163.com","email":"sweepingmonk@163.com","isDeleted":0,"gmtCreate":1496499746000,"gmtModified":1496499746000,"id":3},"taskParams":"","taskType":0,"createUserId":3,"taskId":"7770f229","engineTaskId":null,"modifyUserId":3,"name":"client61","projectId":7,"tenantId":2,"isDeleted":0,"gmtCreate":1496288461000,"gmtModified":1496500306000,"id":2,"isRestoration":0,"actionLogId":2,"exeArgs":""}

## stream

 ####ES5 sink
    CREATE RESULT TABLE MyResult(name STRING, age INT) WITH ( type='ELASTIC5',es.cluster.name='poc_dtstack',es.address='172.16.1.232,172.16.1.142',es.index='xcindex2',es.type='my-type', es.id.fields.index='0,1')

 #### mysql sink
    CREATE RESULT TABLE MyResult(channel STRING, pv INT, time STRING) WITH ( type='mysql', dbURL='jdbc:mysql://172.16.1.203:3306/nb?useUnicode=true&characterEncoding=utf-8',userName='dtstack_xc',password='dtstack_xc', tableName='pv')
 
 #### kafka source
    CREATE SOURCE TABLE MyTable(channel STRING, pv INT, time STRING) WITH ( type='KAFKA09', bootstrapServers='172.16.1.151:9092', offsetReset='latest',topic='nbTest1')
 
## batch 

  #### ES5 sink
    CREATE RESULT TABLE MyResult(name STRING, age INT) WITH ( type='ELASTIC5',es.cluster.name='poc_dtstack',es.address='172.16.1.232,172.16.1.142',es.index='xcindex2',es.type='my-type')

  #### ES5 source
    CREATE SOURCE TABLE MyTable(name STRING,age INT) WITH (type='ELASTIC5',es.cluster.name='poc_dtstack',es.address='172.16.1.232,172.16.1.142',es.index='xcindex',es.query='John Doe')

## 注意
所有的属性都必须使用 单引号(eg:'xxx') 包含起来

### TODO
暂时排除flink130的netty包。之后修改成插件的类型只需要提交插件就好。

