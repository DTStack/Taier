-- ----------------------------
-- console component config add tip
-- ----------------------------
BEGIN;
## spark 组件 ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.submit.deployMode' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.submit.deployMode','spark driver的jvm扩展参数',25,'主要','1');

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'sparkPythonExtLibPath' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','sparkPythonExtLibPath','远程存储系统上pyspark.zip和py4j-0.10.7-src.zip的路径
注：pyspark.zip和py4j-0.10.7-src.zip在$SPARK_HOME/python/lib路径下获取',25,'主要','1');

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'sparkSqlProxyPath' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','sparkSqlProxyPath','远程存储系统上spark-sql-proxy.jar路径
注：spark-sql-proxy.jar是用来执行spark sql的jar包',25,'主要','1');

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.yarn.maxAppAttempts' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.yarn.maxAppAttempts','spark driver最大尝试次数, 默认为yarn上yarn.resourcemanager.am.max-attempts配置的值
注：如果spark.yarn.maxAppAttempts配置的大于yarn.resourcemanager.am.max-attempts则无效',25,'主要','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'sparkYarnArchive' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','sparkYarnArchive','远程存储系统上spark jars的路径',25,'主要','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'yarnAccepterTaskNumber' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','yarnAccepterTaskNumber','允许yarn上同时存在状态为accepter的任务数量，当达到这个值后会禁止任务提交',25,'主要','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.speculation' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.speculation','spark任务推测行为',25,'主要','1' );

## 资源 ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.executor.cores' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.executor.cores','每个executor可以使用的cpu核数',25,'资源','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.executor.memory' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.executor.memory','每个executor可以使用的内存量',25,'资源','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.executor.instances' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.executor.instances','executor实例数',25,'资源','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.cores.max' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.cores.max',' standalone模式下任务最大能申请的cpu核数',25,'资源','1' );

## 网络 ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.network.timeout' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.network.timeout','spark中所有网络交互的最大超时时间',25,'网络','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.rpc.askTimeout' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.rpc.askTimeout','RPC 请求操作在超时之前等待的持续时间',25,'网络','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.executor.heartbeatInterval' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.executor.heartbeatInterval','driver和executor之间心跳时间间隔',25,'网络','1' );

## 事件日志 ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.eventLog.compress' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.eventLog.compress','是否对spark事件日志进行压缩',25,'事件日志','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.eventLog.dir' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.eventLog.dir','spark事件日志存放路径',25,'事件日志','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.eventLog.enabled' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.eventLog.enabled','是否记录 spark 事件日志',25,'事件日志','1' );

## JVM ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.driver.extraJavaOptions' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.driver.extraJavaOptions','spark driver的jvm扩展参数',25,'JVM','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.executor.extraJavaOptions' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.executor.extraJavaOptions','spark executor的jvm扩展参数',25,'JVM','1' );

## 环境变量 ##

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON','driver中用于执行pyspark任务的python二进制可执行文件路径',25,'环境变量','1' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'spark.yarn.appMasterEnv.PYSPARK_PYTHON' AND dict_desc = '1';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','spark.yarn.appMasterEnv.PYSPARK_PYTHON','用于执行pyspark任务的python二进制可执行文件路径',25,'环境变量','1' );

## flink 组件 ##

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'jobmanager.memory.process.size' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','jobmanager.memory.process.size','JobManager 总内存(master)',25,'公共参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'taskmanager.memory.process.size' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','taskmanager.memory.process.size','TaskManager 总内存(slaves)',25,'公共参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'taskmanager.numberOfTaskSlots' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','taskmanager.numberOfTaskSlots','单个 TaskManager 可以运行的并行算子或用户函数实例的数量。',25,'公共参数','0' );

## 高可用 ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'high-availability' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','high-availability','flink ha类型',25,'高可用','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'high-availability.zookeeper.quorum' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','high-availability.zookeeper.quorum','zookeeper地址，当ha选择是zookeeper时必填',25,'高可用','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'high-availability.zookeeper.path.root' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','high-availability.zookeeper.path.root','ha节点路径',25,'高可用','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'high-availability.storageDir' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','high-availability.storageDir','ha元数据存储路径',25,'高可用','0' );

## metric监控 ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'prometheusHost' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','prometheusHost','prometheus地址，平台端使用',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'prometheusPort' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','prometheusPort','prometheus，平台端使用',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.class' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','metrics.reporter.promgateway.class','用来推送指标类',25,'metric监控','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.host' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','metrics.reporter.promgateway.host','promgateway地址',25,'metric监控','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.port' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','metrics.reporter.promgateway.port','promgateway端口',25,'metric监控','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.deleteOnShutdown' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','metrics.reporter.promgateway.deleteOnShutdown','任务结束后是否删除指标',25,'metric监控','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.jobName' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','metrics.reporter.promgateway.jobName','指标任务名',25,'metric监控','0');

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.randomJobNameSuffix'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','metrics.reporter.promgateway.randomJobNameSuffix','是否在任务名上添加随机值',25,'metric监控','0' );

## 容错和checkpointing ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'state.backend'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','state.backend','状态后端',25,'容错和checkpointing','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'state.backend.incremental'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','state.backend.incremental','是否开启增量',25,'容错和checkpointing','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'state.checkpoints.dir'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','state.checkpoints.dir','checkpoint路径地址',25,'容错和checkpointing','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'state.checkpoints.num-retained'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','state.checkpoints.num-retained','checkpoint保存个数',25,'容错和checkpointing','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'state.savepoints.dir'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','state.savepoints.dir','savepoint路径',25,'容错和checkpointing','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'checkpoint.retain.time' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','checkpoint.retain.time','检查点保留时间',25,'容错和checkpointing','0' );

## 高级 ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'classloader.resolve-order' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','classloader.resolve-order','类加载模式',25,'高级','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'jobmanager.archive.fs.dir' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','jobmanager.archive.fs.dir','任务结束后任务信息存储路径',25,'高级','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'akka.ask.timeout' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','akka.ask.timeout','akka通讯超时时间',25,'高级','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'akka.tcp.timeout' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','akka.tcp.timeout','tcp 连接的超时时间',25,'高级','0' );

## JVM参数 ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'env.java.opts' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','env.java.opts','jvm参数',25,'JVM参数','0' );

## Yarn ##
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'yarn.application-attempt-failures-validity-interval' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','yarn.application-attempt-failures-validity-interval','以毫秒为单位的时间窗口，它定义了重新启动 AM 时应用程序尝试失败的次数。不在此窗口范围内的故障不予考虑。将此值设置为 -1 以便全局计数。',25,'Yarn','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'yarn.application-attempts' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','yarn.application-attempts','ApplicationMaster 重新启动的次数。默认情况下，该值将设置为 1。如果启用了高可用性，则默认值为 2。重启次数也受 YARN 限制（通过 yarn.resourcemanager.am.max-attempts 配置）。注意整个 Flink 集群会重启，YARN Client 会失去连接。',25,'Yarn','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'pluginLoadMode' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','pluginLoadMode','插件加载类型',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'classloader.dtstack-cache' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','classloader.dtstack-cache','是否缓存classloader',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'sessionStartAuto' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','sessionStartAuto','是否允许engine启动flink session',25,'数栈平台参数','0' );


DELETE FROM dict WHERE `type` = 25 AND dict_name = 'checkSubmitJobGraphInterval' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','checkSubmitJobGraphInterval','session check间隔（60 * 10s）',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'flinkLibDir' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','flinkLibDir','session check间隔（60 * 10s）',25,'数栈平台参数','0');

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'flinkxDistDir' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','flinkxDistDir','flinkx plugins父级本地目录',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'remoteFlinkLibDir' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','remoteFlinkLibDir','flink lib 远程路径',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'remoteFlinkxDistDir' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','remoteFlinkxDistDir','flinkx plugins父级远程目录',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'flinkSessionName' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','flinkSessionName','yarn session名称',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'monitorAcceptedApp' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','monitorAcceptedApp','是否监控yarn accepted状态任务',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'yarnAccepterTaskNumber' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','yarnAccepterTaskNumber','允许yarn accepter任务数量，达到这个值后不允许任务提交',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'slotmanager.number-of-slots.max' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','slotmanager.number-of-slots.max','flink session允许的最大slot数',25,'公共参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'sessionRetryNum' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','sessionRetryNum','session重试次数，达到后会放缓重试的频率',25,'数栈平台参数','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'restart-strategy'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc )
VALUES ('tips','restart-strategy','none, off, disable:无重启策略。Fixed -delay, Fixed -delay:固定延迟重启策略。更多细节可以在这里找到。Failure -rate:故障率重启策略。更多细节可以在这里找到。如果检查点被禁用，默认值为none。如果检查点启用，默认值是fixed-delay with Integer。MAX_VALUE重启尝试和''1 s''延迟。',25,'容错和checkpointing','0');

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.delay'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc )
VALUES ('tips','restart-strategy.failure-rate.delay','如果restart-strategy设置为根据失败率重试，则两次连续重启尝试之间的延迟。可以用“1分钟”、“20秒”来表示',25,'容错和checkpointing','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'clusterMode' AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','clusterMode','任务执行模式：perjob,session',25,'数栈平台参数','0');

-- 删掉原先的错误参数
DELETE FROM dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.failure-rate-interval'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','restart-strategy.failure-rate.failure-rate-interval','如果重启策略设置为故障率，测量故障率的时间间隔。可以用“1分钟”、“20秒”来表示。',25,'容错和checkpointing','0' );

DELETE FROM dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.max-failures-per-interval'  AND dict_desc = '0';
INSERT INTO dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc)
VALUES ('tips','restart-strategy.failure-rate.max-failures-per-interval','如果restart-strategy设置为根据失败率重试，在给定的时间间隔内，任务失败前的最大重启次数。',25,'容错和checkpointing','0' );

-- 订正原先的错误参数
update console_component_config set `key` = 'restart-strategy.failure-rate.failure-rate-interval' where `key` = 'restart-strategy.failure-rate.failure-rate-intervalattempts' and component_type_code in (0);

### sparkThrift/hiveServer 组件 ###
-- 纠正错误枚举值
update console_component_config set component_type_code = 5 where cluster_id = -2 and component_id = -117;

DELETE FROM  dict WHERE `type` = 25
                    AND dict_name in ('jdbcUrl','username','password','maxJobPoolSize','minJobPoolSize')
                    AND dict_desc in (4, 5);

drop table if exists t_rdbs_component_id;
create temporary table t_rdbs_component_id (id int);
insert into t_rdbs_component_id (id) values (4), (5);

drop table if exists t_rdbs_component_key;
create temporary table t_rdbs_component_key (tipKey varchar(100), tipDesc varchar(200) );
insert into t_rdbs_component_key values ('jdbcUrl','jdbc url地址'),('username', 'jdbc连接用户名'),('password','jdbc连接密码'),
                                        ('maxJobPoolSize','任务最大线程数'),('minJobPoolSize', '任务最小线程数');
-- 组织成笛卡尔积插入
INSERT INTO  dict (dict_code,dict_name,dict_value,`type`,dict_desc)
select 'tips', t2.tipKey, t2.tipDesc, 25, t1.id from t_rdbs_component_id t1 join t_rdbs_component_key t2;

drop table if exists t_rdbs_component_id;
drop table if exists t_rdbs_component_key;

update console_component_config set component_type_code = 6 where component_id = -101;

UPDATE console_component_config SET value = 'perjob' WHERE component_id = -108;

DELETE FROM console_component_config WHERE component_id = -117;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 5, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 5, 'INPUT', 0, 'username', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 5, 'PASSWORD', 0, 'password', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 5, 'INPUT', 0, 'queue', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 5, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 5, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, now(), now(), 0);



alter table console_cluster_tenant add  queue_name  varchar(32) comment '队列名称';
update console_cluster_tenant
inner join console_queue on queue_id = console_queue.id
set console_cluster_tenant.queue_name = console_queue.queue_name;

alter table console_cluster_tenant drop column queue_id;

COMMIT;