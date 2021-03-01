INSERT INTO `task_param_template` VALUES ('1', now(), now(), '0', '0', '0', '0',
'## sql任务并发度设置
sql.env.parallelism=1

## 时间窗口类型（ProcessingTime或者EventTime）
time.characteristic=ProcessingTime

## 窗口提前触发时间，单位为秒(填写正整数即可)
# early.trigger=1

## ttl状态控制
## 最小过期时间,大于0的整数,如1d、1h(dD:天,hH:小时,mM:分钟,ss:秒)
# sql.ttl.min=1h
## 最大过期时间,大于0的整数,如2d、2h(dD:天,hH:小时,mM:分钟,ss:秒),需同时设置最小时间,且比最小时间大5分钟
# sql.ttl.max=2h

## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成
flink.checkpoint.interval=300000

## 设置checkpoint生成超时（以毫秒为单位），默认:10分钟
sql.checkpoint.timeout=600000

## 任务出现故障的时候一致性处理,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE
# sql.checkpoint.mode=EXACTLY_ONCE

## 最大并发生成 checkpoint 数量，默认：1 次
# sql.max.concurrent.checkpoints=1

## checkpoint 外存的清理动作
## true（任务结束之后删除checkpoint外部存储信息）
## false（任务结束之后保留checkpoint外部存储信息）
sql.checkpoint.cleanup.mode=false

## jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024

## taskManager 对应 slot的数量
slots=1

## logLevel: error,debug,info(默认),warn
logLevel=info

## Watermark发送周期，单位毫秒
# autoWatermarkInterval=200

## 设置输出缓冲区的最大刷新时间频率（毫秒）
# sql.buffer.timeout.millis=100

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## kafka kerberos 数据源开启Kerberos
## security.kerberos.login.contexts=Client,KafkaClient


## 异步访问维表是否开启连接池共享,开启则 1.一个tm上多个task共享该池, 2.一个tm上多个url相同的维表单/多个task共享该池 (默认false)
# async.side.clientShare=false
## 连接池中连接的个数,上面参数为true才生效(默认5)
# async.side.poolSize=5');
INSERT INTO `task_param_template` VALUES ('2', now(), now(), '0', '0', '1', '0',
'## Driver程序使用的CPU核数,默认为1\r\n# driver.cores=1\r\n
## Driver程序使用内存大小,默认512m\r\n# driver.memory=512m\r\n
## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\r\n# driver.maxResultSize=1g\r\n
## SparkContext 启动时是否记录有效 SparkConf信息,默认false\r\n# logConf=false
## 启动的executor的数量，默认为1\r\nexecutor.instances=1\r\n
## 每个executor使用的CPU核数，默认为1\r\nexecutor.cores=1\r\n
## 每个executor内存大小,默认512m\r\n#executor.memory=512m\r\n
## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('3', now(), now(), '0', '1', '1', '0',
'## Driver程序使用的CPU核数,默认为1\r\n# driver.cores=1\r\n
## Driver程序使用内存大小,默认512m\r\n# driver.memory=512m\r\n
## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\r\n# driver.maxResultSize=1g\r\n
## SparkContext 启动时是否记录有效 SparkConf信息,默认false\r\n# logConf=false\r\n
## 启动的executor的数量，默认为1\r\nexecutor.instances=1\r\n
## 每个executor使用的CPU核数，默认为1\r\nexecutor.cores=1\r\n
## 每个executor内存大小,默认512m\r\n# executor.memory=512m\r\n
## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10\r\n
## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\r\n# logLevel = INFO\r\n
## spark中所有网络交互的最大超时时间\r\n# spark.network.timeout=120s\r\n
## executor的OffHeap内存，和spark.executor.memory配置使用\r\n# spark.yarn.executor.memoryOverhead');
INSERT INTO `task_param_template` VALUES ('4', now(), now(), '0', '1', '0', '0',
'## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session
## flinkTaskRunMode=per_job
## per_job模式下jobManager配置的内存大小，默认1024（单位M)
## jobmanager.memory.mb=1024
## per_job模式下taskManager配置的内存大小，默认1024（单位M）
## taskmanager.memory.mb=1024
## per_job模式下启动的taskManager数量
## container=1
## per_job模式下每个taskManager 对应 slot的数量
## slots=1
## checkpoint保存时间间隔
## flink.checkpoint.interval=300000
## 任务优先级, 范围:1-1000
## job.priority=10
');
INSERT INTO `task_param_template` VALUES ('5', now(), now(), '0', '1', '3', '0',
'## 每个worker所占内存，比如512m\r\nworker.memory=512m\r\n
## worker的数量\r\nworker.num=1\r\n
## 每个worker所占的cpu核的数量\r\nworker.cores=1\r\n
## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('6', now(), now(), '0', '1', '4', '0',
'## 每个worker所占内存，比如512m \r\nworker.memory=512m\r\n
## 每个worker所占的cpu核的数量 \r\nworker.cores=1\r\n
## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('7', now(), now(), '0', '1', '5', '0', '
## 每个worker所占内存，比如512m\r\nworker.memory=512m\r\n
## 每个worker所占的cpu核的数量\r\nworker.cores=1\r\n
## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('8', now(), now(), '0', '0', '6', '0',
'## 每个worker所占内存，比如512m\r\nworker.memory=512m\r\n
## 每个worker所占的cpu核的数量\r\nworker.cores=1\r\n
## 是否独占机器节点\r\nexclusive=false\r\n
## worker数量\r\nworker.num=1\r\n
## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('9', now(), now(), '0', '1', '7', '0',
'## 每个worker所占内存，比如512m\r\nworker.memory=512m\r\n
## 每个worker所占的cpu核的数量\r\nworker.cores=1\r\n
## 任务优先级, 范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('10',now(), now(), '0', '1', '6', '0', '
## 每个worker所占内存，比如512m\r\nworker.memory=512m\r\n
## 每个worker所占的cpu核的数量\r\nworker.cores=1\r\n
## 是否独占机器节点 \r\nexclusive=false\r\n
## worker数量 \r\nworker.num=1\r\n
## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('11',now(), now(), '0', '1', '8', '0', '');
INSERT INTO `task_param_template` VALUES ('12',now(), now(), '0', '1', '9', '0',
'## Driver程序使用的CPU核数,默认为1\r\n# driver.cores=1\r\n
## Driver程序使用内存大小,默认512m\r\n# driver.memory=512m\r\n
## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\r\n# driver.maxResultSize=1g\r\n
## SparkContext 启动时是否记录有效 SparkConf信息,默认false\r\n# logConf=false\r\n
## 启动的executor的数量，默认为1\r\nexecutor.instances=1\r\n
## 每个executor使用的CPU核数，默认为1\r\nexecutor.cores=1\r\n
## 每个executor内存大小,默认512m\r\n# executor.memory=512m\r\nisCarbondata=true
## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('13',now(), now(), '0', '1', '10', '0',
'##任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10');
INSERT INTO `task_param_template` VALUES ('14',now(), now(), '0', '1', '12', '0',
'## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复\r\n#hiveconf:mapreduce.job.name=\r\n
## 指定mapreduce运行的队列，默认走控制台配置的queue\r\n# hiveconf:mapreduce.job.queuename=default_queue_name\r\n
## hivevar配置,用户自定义变量\r\n#hivevar:ageParams=30');
INSERT INTO `task_param_template` VALUES ('15',now(), now(), '0', '1', '13', '0', '');
INSERT INTO `task_param_template` VALUES ('16',now(), now(), '0', '0', '0', '11',
'## per_job模式下jobManager配置的内存大小，默认1024（单位M）\r\n# jobmanager.memory.mb=1024\r\ngetEngineParamTmplByComputeType
## per_job模式下taskManager配置的内存大小，默认1024（单位M）\r\n# taskmanager.memory.mb=1024
## per_job模式下启动的taskManager数量\r\n #container=1\r\n
## per_job模式下每个taskManager 对应 slot的数量\r\nslots=1\r\n
## logLevel: error,debug,info(默认),warn\r\n
## 任务优先级, 范围:1-1000\r\njob.priority=10\r\n
## checkpoint保存时间间隔\r\nflink.checkpoint.interval=3600000\r\n
## kafka kerberos相关参数\r\n## security.kerberos.login.use-ticket-cache=true\r\n## security.kerberos.login.contexts=Client,KafkaClient\r\n## security.kerberos.login.keytab=/opt/keytab/kafka.keytab\r\n## security.kerberos.login.principal=kafka@HADOOP.COM\r\n## zookeeper.sasl.service-name=zookeeper\r\n## zookeeper.sasl.login-context-name=Client\r\n');

INSERT INTO `task_param_template` VALUES ('17', now(), now(), '0', '0', '0', '1',
'## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10\r\n
## jobManager配置的内存大小，默认1024（单位M）\r\n# jobmanager.memory.mb=1024\r\n
## taskManager配置的内存大小，默认1024（单位M）\r\n# taskmanager.memory.mb=1024\r\n
## taskManager数量\r\n# container=1\r\n
## taskManager 对应 slot的数量\r\nslots=1\r\n');

-- TiDB SQL
INSERT INTO `task_param_template` (`gmt_create`,`gmt_modified`,`is_deleted`,`compute_type`,`engine_type`,`task_type`,`params`) VALUES (now(),now(),0,1,14,0,'');
-- greenplum sql
INSERT INTO `task_param_template` (`gmt_create`,`gmt_modified`,`is_deleted`,`compute_type`,`engine_type`,`task_type`,`params`) VALUES (now(),now(),0,1,16,0,'');