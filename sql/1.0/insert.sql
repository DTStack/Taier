INSERT INTO console_cluster (id, cluster_name, gmt_create, gmt_modified, is_deleted) VALUES (-1, 'default', '2022-01-28 10:26:01', '2022-02-11 11:11:32', 0);


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'CHECKBOX', 1, 'deploymode', '["perjob","session"]', null, '', '', null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'GROUP', 1, 'perjob', '', null, 'deploymode', 'perjob', null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'akka.ask.timeout', '60 s', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'classloader.resolve-order', 'child-first', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'clusterMode', 'perjob', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'flinkJarPath', '/data/insight_plugin/flink110_lib', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'flinkPluginRoot', '/data/insight_plugin', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'gatewayJobName', 'pushgateway', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.cluster-id', '/default', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink110/ha', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink110', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'jarTmpDir', './tmp110', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink110/completed-jobs', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '110job', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusClass', 'com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'remotePluginRootDir', '/data/insight_plugin', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink110/checkpoints', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink110/savepoints', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'submitTimeout', '5', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'yarn.application-attempts', '3', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'GROUP', 1, 'session', '', null, 'deploymode', 'session', null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'blob.service.cleanup.interval', '900', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'checkSubmitJobGraphInterval', '60', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'classloader.resolve-order', 'parent-first', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'clusterMode', 'session', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=300m -Dfile.encoding=UTF-8 -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'flinkJarPath', '/data/insight_plugin/flink110_lib', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'flinkPluginRoot', '/data/insight_plugin', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'flinkSessionSlotCount', '10', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'gatewayJobName', 'pushgateway', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.cluster-id', '/default', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink110/ha', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink110', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'jarTmpDir', './tmp110', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink110/completed-jobs', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'jobmanager.memory.mb', '2048', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'jobstore.expiration-time', '900', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '110job', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusClass', 'com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'remotePluginRootDir', '/data/insight_plugin', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'sessionRetryNum', '6', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'sessionStartAuto', 'true', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink110/checkpoints', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink110/savepoints', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'submitTimeout', '5', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'taskmanager.heap.mb', '2048', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'web.timeout', '100000', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'yarn.application-attempts', '3', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'clusterMode', 'standalone', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'flinkJarPath', '/data/insight_plugin/flink110_lib', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'flinkPluginRoot', '/data/insight_plugin', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'gatewayJobName', 'pushgateway', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability', 'zookeeper', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.cluster-id', '/default', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink110/ha', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink110', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink110/completed-jobs', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'jobmanager.heap.mb', '1024', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'false', 'false', null, 'deploymode$standalone$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'true', 'true', null, 'deploymode$standalone$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '110job', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'false', 'false', null, 'deploymode$standalone$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'true', 'true', null, 'deploymode$standalone$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusClass', 'com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'remotePluginRootDir', '/data/insight_plugin', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink110/checkpoints', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink110/savepoints', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'taskmanager.heap.mb', '1024', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$standalone', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 1);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'remoteFlinkJarPath', '', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'remoteFlinkJarPath', '', null, 'deploymode$session', null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'RADIO_LINKAGE', 1, 'auth', '1', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, '', 1, 'password', '1', null, 'auth', '1', null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'PASSWORD', 1, 'password', '', null, 'auth$password', '', null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, '', 1, 'rsaPath', '2', null, 'auth', '2', null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'input', 1, 'rsaPath', '', null, 'auth$rsaPath', '', null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'fileTimeout', '300000', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'host', '', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'isUsePool', 'true', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'maxIdle', '16', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'maxTotal', '16', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'maxWaitMillis', '3600000', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'minIdle', '16', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'path', '/data/sftp', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'port', '22', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'timeout', '0', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -101, 10, 'INPUT', 1, 'username', '', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 1, 'security.kerberos.login.contexts', 'KafkaClient', null, 'deploymode$session', null, null, '2021-05-25 11:50:59', '2021-05-25 11:50:59', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'checkpoint.retain.time', '7', null, 'deploymode$perjob', null, null, '2021-08-24 17:22:06', '2021-08-24 17:22:06', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, 'INPUT', 0, 'taskmanager.heap.mb', '2048', null, 'deploymode$perjob', null, null, '2021-10-13 15:16:41', '2021-10-13 15:16:41', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'CHECKBOX', 1, 'deploymode', '["perjob"]', null, '', '', null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'GROUP', 1, 'perjob', '', null, 'deploymode', 'perjob', null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'addColumnSupport', 'true', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.cores.max', '1', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.driver.extraJavaOptions', '-Dfile.encoding=utf-8', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.eventLog.compress', 'true', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.eventLog.dir', 'hdfs://ns1/tmp/spark-yarn-logs', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.eventLog.enabled', 'true', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.executor.cores', '1', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.executor.extraJavaOptions', '-Dfile.encoding=utf-8', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.executor.heartbeatInterval', '10s', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.executor.instances', '1', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.executor.memory', '512m', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.network.timeout', '700s', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.rpc.askTimeout', '600s', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.speculation', 'true', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.submit.deployMode', 'cluster', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON', '/data/miniconda2/bin/python3', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_PYTHON', '/data/miniconda2/bin/python3', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'spark.yarn.maxAppAttempts', '1', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'sparkPythonExtLibPath', 'hdfs://ns1/dtInsight/pythons/pyspark.zip,hdfs://ns1/dtInsight/pythons/py4j-0.10.7-src.zip', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'sparkSqlProxyPath', 'hdfs://ns1/dtInsight/spark/spark-sql-proxy.jar', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 1, 'sparkYarnArchive', 'hdfs://ns1/dtInsight/sparkjars/jars', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 9, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 9, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 9, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 9, 'PASSWORD', 0, 'password', '', null, null, null, null, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 9, 'INPUT', 0, 'queue', '', null, null, null, null, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -117, 9, 'INPUT', 0, 'username', '', null, null, null, null, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);




INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('total', 100, '全部', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:42', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('mostUse', 90, '常用', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('relational', 80, '关系型', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('bigData', 70, '大数据存储', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('mpp', 60, 'MPP', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('semiStruct', 50, '半结构化', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('analytic', 40, '分析型', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('NoSQL', 30, 'NoSQL', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('actualTime', 20, '实时', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('api', 0, '接口', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO datasource_classify (classify_code, sorted, classify_name, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('sequential', 10, '时序', 0, '2021-06-09 17:19:27', '2021-06-09 17:19:27', 0, 0);



INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('dataName', '数据源名称', 'Input', 1, 0, null, null, null, 0, '{"length":{"max":128, "message":"不得超过128个字符"}}', null, null, null, 'common', 0, '2021-03-15 17:33:21', '2021-03-30 16:09:06', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('dataDesc', '描述', 'TextArea', 0, 0, null, null, null, 0, '{"length":{"max":200, "message":"不得超过200个字符"}}', null, null, null, 'common', 0, '2021-03-15 17:33:21', '2021-03-30 16:09:15', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:mysql://host:3306/dbName', null, '/jdbc:mysql:\\/\\/(.)+/', 'MySQL', 0, '2021-03-23 20:35:57', '2021-07-28 16:06:50', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'MySQL', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'MySQL', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:mysql://host:port/dbName', null, '/jdbc:mysql:\\/\\/(.)+/', 'PolarDB for MySQL8', 0, '2021-03-23 20:35:57', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'PolarDB for MySQL8', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'PolarDB for MySQL8', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', 'SID示例：jdbc:oracle:thin:@host:port:dbName
ServiceName示例：jdbc:oracle:thin:@//host:port/service_name', null, '/jdbc:oracle:thin:@(\\/\\/)?(.)+/', 'Oracle', 0, '2021-03-23 20:35:57', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'Oracle', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Oracle', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '', '示例：jdbc:sqlserver://localhost:1433;DatabaseName=dbName
或
 jdbc:jtds:sqlserver://localhost:1433/dbName', null, '', 'SQL Server', 0, '2021-03-23 20:35:57', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'SQL Server', 0, '2021-03-23 20:35:57', '2021-04-02 16:50:08', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'SQL Server', 0, '2021-03-23 20:35:57', '2021-04-02 16:50:10', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:postgresql://host:port/database', null, '/jdbc:postgresql:\\/\\/(.)+/', 'PostgreSQL', 0, '2021-03-23 20:35:57', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'PostgreSQL', 0, '2021-03-23 20:35:57', '2021-04-02 16:50:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'PostgreSQL', 0, '2021-03-23 20:35:57', '2021-04-02 16:50:18', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:db2://host:port/dbName', null, '/jdbc:db2:\\/\\/(.)+/', 'DB2', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'DB2', 0, '2021-03-23 20:35:58', '2021-04-13 16:31:26', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'DB2', 0, '2021-03-23 20:35:58', '2021-04-13 16:31:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:dm://host:port/database', null, '/jdbc:dm:\\/\\/(.)+/', 'DMDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'DMDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'DMDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:kingbase8://host:port/database', null, '/jdbc:kingbase8:\\/\\/(.)+/', 'KingbaseES8', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'KingbaseES8', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'KingbaseES8', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:pivotal:greenplum://host:port;DatabaseName=database', null, '/jdbc:pivotal:greenplum:\\/\\/(.)+/', 'Greenplum', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'Greenplum', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Greenplum', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:postgresql://host:port/database', null, '/jdbc:postgresql:\\/\\/(.)+/', 'GaussDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'GaussDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'GaussDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:gbase://host:port/dbName', null, '/jdbc:gbase:\\/\\/(.)+/', 'GBase_8a', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'GBase_8a', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'GBase_8a', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:clickhouse://<host>:<port>[/<database>]', null, '/jdbc:clickhouse:\\/\\/(.)+/', 'ClickHouse', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'ClickHouse', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'ClickHouse', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:mysql://host:port/dbName', null, '/jdbc:mysql:\\/\\/(.)+/', 'TiDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'TiDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'TiDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:mysql://host:port/dbName', null, '/jdbc:mysql:\\/\\/(.)+/', 'AnalyticDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'AnalyticDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'AnalyticDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:hive://host:port/dbName', null, '/jdbc:(\\w|:)+:\\/\\/(.)+/', 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('defaultFS', 'defaultFS', 'Input', 1, 0, null, 'hdfs://host:port', null, 1, '', null, null, null, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, null, '{
"dfs.nameservices": "defaultDfs",
"dfs.ha.namenodes.defaultDfs": "namenode1",
"dfs.namenode.rpc-address.defaultDfs.namenode1": "",
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
}', null, 0, '', '高可用模式下的填写规则：
1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；
2、所有参数以JSON格式填写；
3、格式为：
"dfs.nameservices": "nameservice名称", "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.client.failover.proxy.provider.
nameservice名称": "org.apache.hadoop.
hdfs.server.namenode.ha.
ConfiguredFailoverProxyProvider"
4、详细参数含义请参考《帮助文档》或<a href=''http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html''>Hadoop官方文档</a>', null, null, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:hive2://host:port/dbName', null, '/jdbc:(\\w|:)+:\\/\\/(.)+/', 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('defaultFS', 'defaultFS', 'Input', 1, 0, null, 'hdfs://host:port', null, 1, '', null, null, null, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, null, '{
"dfs.nameservices": "defaultDfs",
"dfs.ha.namenodes.defaultDfs": "namenode1",
"dfs.namenode.rpc-address.defaultDfs.namenode1": "",
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
}', null, 0, '', '高可用模式下的填写规则：
1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；
2、所有参数以JSON格式填写；
3、格式为：
"dfs.nameservices": "nameservice名称", "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.client.failover.proxy.provider.
nameservice名称": "org.apache.hadoop.
hdfs.server.namenode.ha.
ConfiguredFailoverProxyProvider"
4、详细参数含义请参考《帮助文档》或<a href=''http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html''>Hadoop官方文档</a>', null, null, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:hive2://host:port/dbName', null, '/jdbc:hive2:\\/\\/(.)+/', 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('defaultFS', 'defaultFS', 'Input', 1, 0, null, 'hdfs://host:port', null, 1, '', null, null, null, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, null, '{
"dfs.nameservices": "defaultDfs",
"dfs.ha.namenodes.defaultDfs": "namenode1",
"dfs.namenode.rpc-address.defaultDfs.namenode1": "",
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
}', null, 0, '', '高可用模式下的填写规则：
1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；
2、所有参数以JSON格式填写；
3、格式为：
"dfs.nameservices": "nameservice名称", "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.client.failover.proxy.provider.
nameservice名称": "org.apache.hadoop.
hdfs.server.namenode.ha.
ConfiguredFailoverProxyProvider"
4、详细参数含义请参考《帮助文档》或<a href=''http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html''>Hadoop官方文档</a>', null, null, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:impala://host:port/dbName', null, '/jdbc:impala:\\/\\/(.)+/', 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('defaultFS', 'defaultFS', 'Input', 1, 0, null, 'hdfs://host:port', null, 1, '', null, null, null, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, null, '{
"dfs.nameservices": "defaultDfs",
"dfs.ha.namenodes.defaultDfs": "namenode1",
"dfs.namenode.rpc-address.defaultDfs.namenode1": "",
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
}', null, 0, '', '高可用模式下的填写规则：
1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；
2、所有参数以JSON格式填写；
3、格式为：
"dfs.nameservices": "nameservice名称", "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.client.failover.proxy.provider.
nameservice名称": "org.apache.hadoop.
hdfs.server.namenode.ha.
ConfiguredFailoverProxyProvider"
4、详细参数含义请参考《帮助文档》或<a href=''http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html''>Hadoop官方文档</a>', null, null, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('accessId', 'AccessId', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'Maxcompute', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('accessKey', 'AccessKey', 'Input', 1, 0, null, null, null, 0, '', null, null, null, 'Maxcompute', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('project', 'Project Name', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'Maxcompute', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('endPoint', 'End Point', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'Maxcompute', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('host', '主机名/IP', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('port', '端口', 'Integer', 1, 0, null, 'FTP默认21, SFTP默认22', null, 1, '', null, null, null, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('ftpReact', 'ftpReact', 'FtpReact', 0, 0, null, null, null, 0, '', null, null, null, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:hive2://host:port/dbName', null, '/jdbc:(\\w|:)+:\\/\\/(.)+/
', 'CarbonData', 0, '2021-03-23 20:38:06', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'CarbonData', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'CarbonData', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('carbonReact', 'carbonReact', 'CarbonReact', 0, 0, null, null, null, 0, '', null, null, null, 'CarbonData', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hostPorts', '集群地址', 'TextArea', 1, 0, null, '集群地址，例如：IP1:Port,IP2:Port,IP3:Port3，多个IP地址用英文逗号隔开', null, 1, '', null, null, null, 'Kudu', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('others', '其他参数', 'TextAreaWithCopy', 0, 0, null, '输入JSON格式的参数，示例及默认参数如下：
{
    "openKerberos":false,
    "user":"",
    "keytabPath":"",
    "workerCount":4,
    "bossCount":1,
    "operationTimeout":30000,
    "adminOperationTimeout":30000
}', null, 0, '', null, null, null, 'Kudu', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Kudu', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('authURL', 'RESTful URL', 'Input', 1, 0, null, 'http://ip:port', null, 1, '', '访问Kylin的认证地址，格式为：http://host:7000', null, '/http:\\/\\/([\\w, .])+:(.)+/', 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('project', 'Project', 'Input', 1, 0, null, 'DEFAULT', null, 0, '', null, null, null, 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('config', '高可用配置', 'TextAreaWithCopy', 0, 0, null, '{
    "socketTimeout":10000,
    "connectTimeout":10000
}', null, 0, '', null, null, null, 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hbase_quorum', '集群地址', 'TextArea', 1, 0, null, '集群地址，例如：IP1:Port,IP2:Port,IP3:Port', null, 1, '', null, null, null, 'HBase-1.x', 0, '2021-03-23 20:38:06', '2021-07-28 15:33:28', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hbase_parent', '根目录', 'Input', 0, 0, null, 'ZooKeeper中hbase创建的根目录，例如：/hbase', null, 0, '', null, null, null, 'HBase-1.x', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hbase_other', '其他参数', 'TextArea', 0, 0, null, 'hbase.rootdir": "hdfs: //ip:9000/hbase', null, 0, '', null, null, null, 'HBase-1.x', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'HbaseKerberos', 0, 0, null, null, null, 0, '', null, null, null, 'HBase-1.x', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hbase_quorum', '集群地址', 'TextArea', 1, 0, null, '集群地址，例如：IP1:Port,IP2:Port,IP3:Port', null, 1, '', null, null, null, 'HBase-2.x', 0, '2021-03-23 20:38:07', '2021-07-28 15:33:28', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hbase_parent', '根目录', 'Input', 0, 0, null, 'ZooKeeper中hbase创建的根目录，例如：/hbase', null, 0, '', null, null, null, 'HBase-2.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hbase_other', '其他参数', 'TextArea', 0, 0, null, 'hbase.rootdir": "hdfs: //ip:9000/hbase', null, 0, '', null, null, null, 'HBase-2.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'HbaseKerberos', 0, 0, null, null, null, 0, '', null, null, null, 'HBase-2.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:phoenix:zk1,zk2,zk3:port/hbase2', null, '/jdbc:phoenix:(.)+/', 'Phoenix-4.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Phoenix-4.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:phoenix:zk1,zk2,zk3:port/hbase2', null, '/jdbc:phoenix:(.)+/', 'Phoenix-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Phoenix-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', '集群地址', 'TextArea', 1, 0, null, '集群地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接', null, 1, '', null, null, null, 'Elasticsearch-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('clusterName', '集群名称', 'Input', 0, 0, null, '请输入集群名称', null, 0, '{"length":{"max":128, "message":"不得超过128个字符"}}', null, null, null, 'Elasticsearch-5.x', 0, '2021-03-23 20:38:07', '2021-07-28 16:05:37', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'Elasticsearch-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Elasticsearch-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', '集群地址', 'TextArea', 1, 0, null, '集群地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接', null, 1, '', null, null, null, 'Elasticsearch-6.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('clusterName', '集群名称', 'Input', 0, 0, null, '请输入集群名称', null, 0, '{"length":{"max":128, "message":"不得超过128个字符"}}', null, null, null, 'Elasticsearch-6.x', 0, '2021-03-23 20:38:07', '2021-07-28 16:05:37', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'Elasticsearch-6.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Elasticsearch-6.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', '集群地址', 'TextArea', 1, 0, null, '集群地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接', null, 1, '', null, null, null, 'Elasticsearch-7.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('clusterName', '集群名称', 'Input', 0, 0, null, '请输入集群名称', null, 0, '{"length":{"max":128, "message":"不得超过128个字符"}}', null, null, null, 'Elasticsearch-7.x', 0, '2021-03-23 20:38:07', '2021-07-28 16:05:37', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'Elasticsearch-7.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Elasticsearch-7.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hostPorts', '集群地址', 'TextArea', 1, 0, null, 'MongoDB集群地址，例如：IP1:Port,IP2:Port,IP3:Port', null, 1, '', null, null, null, 'MongoDB', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'MongoDB', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'MongoDB', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('database', '数据库', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'MongoDB', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('redisReact', 'redisReact', 'RedisReact', 0, 0, null, null, null, 0, '', null, null, null, 'Redis', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hostname', 'hostname', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'S3', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('accessKey', 'AccessKey', 'Input', 1, 0, null, null, null, 0, '', null, null, null, 'S3', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('secretKey', 'SecretKey', 'Input', 1, 0, null, null, null, 0, '', null, null, null, 'S3', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', '集群地址', 'TextArea', 0, 1, null, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', null, 1, '', null, null, null, 'Kafka-1.x', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:38', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('brokerList', 'broker地址', 'TextArea', 1, 1, null, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', null, 1, '', null, null, null, 'Kafka-1.x', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:39', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', '集群地址', 'TextArea', 0, 1, null, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', null, 1, '', null, null, null, 'Kafka-2.x', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:40', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('brokerList', 'broker地址', 'TextArea', 1, 1, null, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', null, 1, '', null, null, null, 'Kafka-2.x', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:41', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', '集群地址', 'TextArea', 0, 1, null, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', null, 1, '', null, null, null, 'Kafka-0.9', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:41', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('brokerList', 'broker地址', 'TextArea', 1, 1, null, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', null, 1, '', null, null, null, 'Kafka-0.9', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:43', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', '集群地址', 'TextArea', 0, 1, null, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', null, 1, '', null, null, null, 'Kafka-0.10', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:43', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('brokerList', 'broker地址', 'TextArea', 1, 1, null, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', null, 1, '', null, null, null, 'Kafka-0.10', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:45', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', '集群地址', 'TextArea', 0, 1, null, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', null, 1, '', null, null, null, 'Kafka-0.11', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:45', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('brokerList', 'broker地址', 'TextArea', 1, 1, null, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', null, 1, '', null, null, null, 'Kafka-0.11', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('address', 'Broker URL', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'EMQ', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'EMQ', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'EMQ', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('url', 'URL', 'Input', 1, 0, null, null, null, 1, '', '多个鉴权参数将以”&“连接拼接在URL后，例如：ws://host:port/test?Username=sanshui&password=xx', null, null, 'WebSocket', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('webSocketParams', '鉴权参数', 'WebSocketSub', 0, 0, null, null, null, 0, '', null, null, null, 'WebSocket', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('url', 'URL', 'Input', 1, 0, null, 'host:port', null, 1, '', null, null, null, 'Socket', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('protocol', 'Protocol', 'Input', 1, 1, null, null, null, 1, '', null, null, null, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:presto://host:port/dbName', null, '/jdbc:presto:\\/\\/(.)+/', 'Presto', 0, '2021-04-07 14:47:08', '2021-04-07 14:47:14', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, null, null, null, null, 'Presto', 0, '2021-04-07 14:47:08', '2021-04-07 14:47:14', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, null, null, null, null, 'Presto', 0, '2021-04-07 14:47:08', '2021-04-07 14:47:14', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hostPort', '地址', 'TextArea', 1, 1, null, null, null, 1, null, null, null, null, 'Redis', 0, '2021-04-15 19:48:41', '2021-04-15 19:48:48', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('database', '数据库', 'Input', 1, 1, null, null, null, 1, null, null, null, null, 'Redis', 0, '2021-04-15 19:48:41', '2021-04-15 19:48:48', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('zkHost', '集群地址', 'TextArea', 1, 0, null, '集群地址，例如:ip1:port,ip2:port,ip3:port', null, 1, null, null, null, null, 'Solr-7.x', 0, '2021-05-28 14:07:00', '2021-05-28 14:07:00', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('chroot', 'chroot', 'Input', 1, 0, null, '请输入Zookeeper chroot路径,例如/Solr', null, 1, null, null, null, null, 'Solr-7.x', 0, '2021-05-28 14:07:00', '2021-05-28 14:07:00', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Solr-7.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, null, '', null, 0, '', null, null, null, 'Kafka-0.9', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, null, '', null, 0, '', null, null, null, 'Kafka-0.10', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, null, '', null, 0, '', null, null, null, 'Kafka-0.11', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, null, '', null, 0, '', null, null, null, 'Kafka-1.x', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, null, '', null, 0, '', null, null, null, 'Kafka-2.x', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('url', 'URL', 'Input', 1, 0, null, 'http://localhost:8086', null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', null, null, '/http:\\/\\/([\\w, .])+:(.)+/', 'InfluxDB-1.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'InfluxDB-1.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'InfluxDB-1.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:hive://host:port/dbName', null, '/jdbc:(\\w|:)+:\\/\\/(.)+/', 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('defaultFS', 'defaultFS', 'Input', 1, 0, null, 'hdfs://host:port', null, 1, '', null, null, null, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, null, '{
"dfs.nameservices": "defaultDfs",
"dfs.ha.namenodes.defaultDfs": "namenode1",
"dfs.namenode.rpc-address.defaultDfs.namenode1": "",
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
}', null, 0, '', '高可用模式下的填写规则：
1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；
2、所有参数以JSON格式填写；
3、格式为：
"dfs.nameservices": "nameservice名称", "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.client.failover.proxy.provider.
nameservice名称": "org.apache.hadoop.
hdfs.server.namenode.ha.
ConfiguredFailoverProxyProvider"
4、详细参数含义请参考《帮助文档》或<a href=''http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html''>Hadoop官方文档</a>', null, null, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('region', 'Region', 'Select', 1, 0, null, '', null, 0, '', null, null, null, 'AWS S3', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '[{key:''1'',value:''cn-north-1'',label:''cn-north-1 (北京)''},{key:''2'',value:''cn-northwest-1'',label:''cnnorthwest-1 (宁夏)''}]');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('accessKey', 'ACCESS KEY', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'AWS S3', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('secretKey', 'SECRET KEY', 'Password', 1, 0, null, null, null, 0, '', null, null, null, 'AWS S3', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:hive2://host:port/dbName', null, '/jdbc:(\\w|:)+:\\/\\/(.)+/', 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 0, '', null, null, null, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('defaultFS', 'defaultFS', 'Input', 1, 0, null, 'hdfs://host:port', null, 1, '', null, null, null, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('metaStoreUris', 'hive.metastore.uris', 'Input', 0, 0, null, '', null, 0, '', 'hive.metastore.uris仅在做事务表的写同步时必填', null, null, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, null, '{
"dfs.nameservices": "defaultDfs",
"dfs.ha.namenodes.defaultDfs": "namenode1",
"dfs.namenode.rpc-address.defaultDfs.namenode1": "",
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
}', null, 0, '', '高可用模式下的填写规则：
1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；
2、所有参数以JSON格式填写；
3、格式为：
"dfs.nameservices": "nameservice名称", "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.namenode.rpc-address.nameservice名称.namenode名称": "", "dfs.client.failover.proxy.provider.
nameservice名称": "org.apache.hadoop.
hdfs.server.namenode.ha.
ConfiguredFailoverProxyProvider"
4、详细参数含义请参考《帮助文档》或<a href=''http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html''>Hadoop官方文档</a>', null, null, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:postgresql://host:port/database', null, '/jdbc:postgresql:\\/\\/(.)+/', 'AnalyticDB PostgreSQL', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'AnalyticDB PostgreSQL', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 1, 0, null, null, null, 0, '', null, null, null, 'AnalyticDB PostgreSQL', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:vertica://host:port/dbName', null, '/jdbc:vertica:\\/\\/(.)+/', 'Vertica', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 1, 0, null, null, null, 1, '', null, null, null, 'Vertica', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 1, 0, null, null, null, 0, '', null, null, null, 'Vertica', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('url', 'URL', 'Input', 1, 0, null, 'http://localhost:4242', null, 1, '{"regex":{"message":"URL格式不符合规则!"}}', null, null, '/http:\\/\\/([\\w, .])+:(.)+/', 'OpenTSDB-2.x', 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:mysql://host:3306/dbName', null, '/jdbc:mysql:\\/\\/(.)+/', 'Doris-0.14.x', 0, '2021-07-06 09:35:57', '2021-07-06 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'Doris-0.14.x', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:08', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Doris-0.14.x', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:12', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:kylin://host:7070/project_name', null, '/jdbc:kylin:\\/\\/(.)+/', 'Kylin JDBC-3.x', 0, '2021-07-06 09:35:57', '2021-07-06 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'Kylin JDBC-3.x', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:08', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'Kylin JDBC-3.x', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:12', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('jdbcUrl', 'JDBC URL', 'Input', 1, 0, null, null, null, 1, '{"regex":{"message":"JDBC URL格式不符合规则!"}}', '示例：jdbc:sqlserver://localhost:1433;DatabaseName=dbName', null, '/jdbc:sqlserver:\\/\\/(.)+/', 'SQLServer JDBC', 0, '2021-07-06 09:35:57', '2021-07-06 16:07:17', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('username', '用户名', 'Input', 0, 0, null, null, null, 1, '', null, null, null, 'SQLServer JDBC', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:08', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('password', '密码', 'Password', 0, 0, null, null, null, 0, '', null, null, null, 'SQLServer JDBC', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:12', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('defaultFS', 'defaultFS', 'Input', 1, 0, null, 'hdfs://host:port', null, 1, '', null, null, null, 'HDFS-2.x', 0, '2021-03-23 20:38:06', '2021-09-17 13:51:06', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, null, null, null, 0, '', null, null, null, 'HDFS-2.x', 0, '2021-03-23 20:38:06', '2021-09-17 13:51:06', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, null, null, null, 0, '', null, null, null, 'HDFS-2.x', 0, '2021-03-23 20:38:06', '2021-09-17 13:51:06', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('defaultFS', 'defaultFS', 'Input', 1, 0, null, 'hdfs://host:port', null, 1, '', null, null, null, 'HDFS-TBDS', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, null, null, null, 0, '', null, null, null, 'HDFS-TBDS', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('tbds_id', 'ID', 'Input', 1, 0, null, '请输入ID', null, 0, '', null, null, null, 'HDFS-TBDS', 0, '2021-09-17 10:38:27', '2021-09-17 10:38:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('tbds_key', 'KEY', 'Input', 1, 0, null, '请输入KEY', null, 0, '', null, null, null, 'HDFS-TBDS', 0, '2021-09-17 10:38:27', '2021-09-17 10:38:27', 0, 0, '');
INSERT INTO datasource_form_field (name, label, widget, required, invisible, default_value, place_hold, request_api, is_link, valid_info, tooltip, style, regex, type_version, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id, options) VALUES ('tbds_username', 'USERNAME', 'Input', 1, 0, null, '请输入username', null, 0, '', null, null, null, 'HDFS-TBDS', 0, '2021-10-15 10:38:27', '2021-10-15 10:38:27', 0, 0, '');


INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('MySQL', 3, 6.5, 'MySQL.png', 2500, 0, 0, '2021-03-15 17:50:44', '2022-02-11 18:51:23', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('PolarDB for MySQL8', 3, 0.0, 'PolarDB for MySQL8.png', 2450, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Oracle', 3, 1.5, 'Oracle.png', 2400, 0, 0, '2021-03-15 17:50:44', '2021-09-17 15:46:39', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('SQL Server', 3, 0.0, 'SQLServer.png', 2350, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('PostgreSQL', 3, 1.0, 'PostgreSQL.png', 2300, 0, 0, '2021-03-15 17:50:44', '2021-09-17 16:55:58', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('DB2', 3, 0.0, 'DB2.png', 2250, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('DMDB', 3, 0.0, 'DMDB.png', 2200, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('KingbaseES8', 3, 0.0, 'KingbaseES8.png', 2100, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Hive', 4, 4.5, 'Hive.png', 2050, 0, 0, '2021-03-15 17:50:44', '2022-02-10 15:18:13', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('SparkThrift', 4, 5.0, 'SparkThrift.png', 2000, 0, 0, '2021-03-15 17:50:44', '2022-02-11 11:20:06', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Maxcompute', 4, 0.0, 'Maxcompute.png', 1950, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Phoenix', 4, 0.0, 'Phoenix.png', 1900, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Greenplum', 5, 0.0, 'Greenplum.png', 1850, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('GaussDB', 5, 0.0, 'GaussDB.png', 1800, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('GBase_8a', 5, 0.0, 'GBase_8a.png', 1750, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('HDFS', 6, 0.0, 'HDFS.png', 1700, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('FTP', 6, 0.0, 'FTP.png', 1650, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('S3', 6, 0.0, 'S3.png', 1600, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Impala', 7, 0.0, 'Impala.png', 1550, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('ClickHouse', 7, 0.0, 'ClickHouse.png', 1500, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('TiDB', 7, 0.0, 'TiDB.png', 1450, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kudu', 7, 0.0, 'Kudu.png', 1400, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('AnalyticDB', 7, 0.0, 'AnalyticDB.png', 1350, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('CarbonData', 7, 0.0, 'CarbonData.png', 1300, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kylin URL', 7, 0.0, 'Kylin.png', 1250, 0, 0, '2021-03-15 17:50:44', '2021-07-28 16:06:35', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('HBase', 8, 0.0, 'HBase.png', 1200, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Elasticsearch', 8, 0.0, 'Elasticsearch.png', 1150, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('MongoDB', 8, 0.0, 'MongoDB.png', 1050, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Redis', 8, 0.0, 'Redis.png', 1000, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kafka', 9, 0.5, 'Kafka.png', 950, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('EMQ', 9, 0.0, 'EMQ.png', 900, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('WebSocket', 10, 0.0, 'WebSocket.png', 850, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Socket', 10, 0.0, 'Socket.png', 800, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Presto', 1, 0.0, null, 750, 1, 0, '2021-03-24 12:01:00', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Solr', 8, 0.0, 'Solr.png', 1100, 0, 0, '2021-05-28 11:18:00', '2021-05-28 11:18:00', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('InfluxDB', 11, 0.0, 'InfluxDB.png', 875, 0, 0, '2021-06-09 14:49:27', '2021-07-28 16:05:37', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('AWS S3', 6, 0.0, 'AWS S3.png', 1575, 0, 0, '2021-06-21 19:48:27', '2021-06-21 19:48:27', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Inceptor', 4, 0.0, 'Inceptor.png', 1875, 0, 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('AnalyticDB PostgreSQL', 7, 0.0, 'ADB_PostgreSQL.png', 1220, 0, 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Vertica', 5, 0.0, 'Vertica.png', 1720, 0, 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('OpenTSDB', 11, 0.0, 'OpenTSDB.png', 862, 0, 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Doris', 7, 0.0, 'Doris.png', 1200, 0, 0, '2021-07-06 12:22:10', '2021-07-06 15:49:09', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kylin JDBC', 7, 0.0, 'Kylin.png', 1300, 0, 0, '2021-07-06 12:22:10', '2021-07-06 15:49:09', 0, 0);
INSERT INTO datasource_type (data_type, data_classify_id, weight, img_url, sorted, invisible, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('SQLServer JDBC', 3, 0.0, 'SQLServer.png', 1200, 0, 0, '2021-07-06 12:22:10', '2021-07-06 15:49:09', 0, 0);



INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Hive', '1.x', 0, 0, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Hive', '2.x', 1, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:16', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('HBase', '1.x', 0, 0, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('HBase', '2.x', 1, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:38', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Phoenix', '4.x', 0, 0, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Phoenix', '5.x', 1, 0, '2021-03-15 17:51:55', '2021-04-01 14:43:21', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Elasticsearch', '5.x', 0, 0, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Elasticsearch', '6.x', 1, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:24', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Elasticsearch', '7.x', 2, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:24', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kafka', '1.x', 3, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:54', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kafka', '2.x', 4, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:49', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kafka', '0.9', 0, 1, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kafka', '0.10', 1, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:56', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kafka', '0.11', 2, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:55', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Solr', '7.x', 0, 0, '2021-05-28 14:48:00', '2021-05-28 14:48:00', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('InfluxDB', '1.x', 0, 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Hive', '3.x', 2, 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('OpenTSDB', '2.x', 0, 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Doris', '0.14.x', 0, 0, '2021-07-06 14:49:27', '2021-07-06 14:49:42', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kylin URL', '3.x', 0, 0, '2021-07-06 14:49:27', '2021-07-06 14:49:42', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('Kylin JDBC', '3.x', 0, 0, '2021-07-06 14:49:27', '2021-07-06 14:49:42', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('HDFS', '2.x', 0, 0, '2022-02-17 15:51:08', '2022-02-17 15:51:08', 0, 0);
INSERT INTO datasource_version (data_type, data_version, sorted, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id) VALUES ('HDFS', 'TBDS', 1, 0, '2022-02-17 15:51:08', '2022-02-17 15:51:08', 0, 0);



INSERT INTO develop_catalogue (id, tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES (1, -1, '系统函数', -1, 3, 1, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO develop_catalogue (id, tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES (3, -1, '数学函数', 1, null, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO develop_catalogue (id, tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES (5, -1, '集合函数', 1, null, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO develop_catalogue (id, tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES (7, -1, '日期函数', 1, null, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO develop_catalogue (id, tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES (9, -1, '其它函数', 1, null, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO develop_catalogue (id, tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES (11, -1, '字符函数', 1, null, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO develop_catalogue (id, tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES (13, -1, '聚合函数', 1, null, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO develop_catalogue (id, tenant_id, node_name, node_pid, order_val, level, gmt_create, gmt_modified, create_user_id, is_deleted, catalogue_type) VALUES (15, -1, '表生成函数', 1, null, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);

INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (1, 'round', '', '取近似值', 'double  round(double a)  double  round(double a, int d)', 'round(double a)：返回对a四舍五入的bigint值  round(double a, int d)：返回double型a的保留d位小数的double型的近似值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (3, 'bround', '', '银行家舍入法', 'double  bround(double a)  double  bround(double a, int d)', 'bround(double a)：1~4：舍，6~9：进，5->前位数是偶：舍，5->前位数是奇：进  bround(double a, int d)：保留d位小数', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (5, 'floor', '', '向下取整', 'bigint  floor(double a)', '，在数轴上最接近要求的值的左边的值 ?如：6.10->6 ? -3.4->-4', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (7, 'ceil', '', '取整', 'bigint  ceil(double a)', '求其不小于小给定实数的最小整数如：ceil(6) = ceil(6.1)= ceil(6.9) = 6', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (9, 'rand', '', '取随机数', 'double  rand()  double  rand(int seed)', '返回一个0到1范围内的随机数。如果指定种子seed，则会等到一个稳定的随机数序列', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (11, 'exp', '', '计算自然指数的指数', 'double  exp(double a)  double  exp(decimal a)', '返回自然对数e的a幂次方， a可为小数', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (13, 'ln', '', '计算自然数的对数', 'double  ln(double a)  double  ln(decimal a)', '以自然数为底d的对数，a可为小数', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (15, 'log10', '', '计算10为底的对数', 'double  log10(double a)  double  log10(decimal a)', '计算以10为底d的对数，a可为小数', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (17, 'log2', '', '计算2为底数的对数', 'double  log2(double a)  double  log2(decimal a)', '以2为底数d的对数，a可为小数', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (19, 'log', '', '计算对数', 'double  log(double base, double a)  double  log(decimal base, decimal a)', '以base为底的对数，base 与 a都是double类型', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (21, 'pow', '', '计算次幂', 'double  pow(double a, double p)', '计算a的p次幂', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (23, 'sqrt', '', '计算平方根', 'double  sqrt(double a)  double  sqrt(decimal a)', '计算a的平方根', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (25, 'bin', '', '计算二进制a的string类型，a为bigint类型', 'string  bin(bigint a)', '计算二进制a的string类型，a为bigint类型', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (27, 'hex', '', '计算十六进制的string类型', 'string  hex(bigint a)   string  hex(string a)   string  hex(binary a)', '计算十六进制a的string类型，如果a为string类型就转换成字符相对应的十六进制', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (29, 'unhex', '', 'hex的逆方法', 'binary  unhex(string a)', 'hex的逆方法', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (31, 'conv', '', '进制转换', 'string  conv(bigint num, int from_base, int to_base)  string  conv(string num, int from_base, int to_base)', '将bigint/string类型的num从from_base进制转换成to_base进制', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (33, 'abs', '', '计算a的绝对值', 'double  abs(double a)', '计算a的绝对值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (35, 'pmod', '', 'a对b取模', 'int  pmod(int a, int b),   double  pmod(double a, double b)', 'a对b取模', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (37, 'sin', '', '计算正弦值', 'double  sin(double a)  double  sin(decimal a)', '计算正弦值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (39, 'asin', '', '计算反正弦值', 'double  asin(double a)  double  asin(decimal a)', '计算反正弦值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (41, 'cos', '', '计算余弦值', 'double  cos(double a)  double  cos(decimal a)', '计算余弦值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (43, 'acos', '', '计算反余弦值', 'double  acos(double a)  double  acos(decimal a)', '计算反余弦值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (45, 'tan', '', '计算正切值', 'double  tan(double a)  double  tan(decimal a)', '计算正切值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (47, 'atan', '', '计算反正切值', 'double  atan(double a)  double  atan(decimal a)', '计算反正切值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (49, 'degrees', '', '弧度值转换角度值', 'double  degrees(double a)  double  degrees(decimal a)', '弧度值转换角度值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (51, 'radians', '', '将角度值转换成弧度值', 'double  radians(double a)  double  radians(double a)', '将角度值转换成弧度值', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (53, 'positive', '', '返回a', 'int positive(int a),   double  positive(double a)', '返回a', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (55, 'negative', '', '计算相反数', 'int negative(int a),   double  negative(double a)', '返回a的相反数', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (57, 'sign', '', '计算数字的标志', 'double  sign(double a)  int  sign(decimal a)', 'sign(double a)：如果a是正数则返回1.0，是负数则返回-1.0，否则返回0.0  sign(decimal a)：同上，返回值为整型', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (59, 'e', '', '取数学常数e', 'double  e()', '取数学常数e', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (61, 'pi', '', '取数学常数pi', 'double  pi()', '取数学常数pi', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (63, 'factorial', '', '计算阶乘', 'bigint  factorial(int a)', '求a的阶乘', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (65, 'cbrt', '', '计算立方根', 'double  cbrt(double a)', '计算a的立方根', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (67, 'shiftleft', '', '按位左移', 'int   shiftleft(TINYint|SMALLint|int a, int b)  bigint  shiftleft(bigint a, int燽)', '按位左移', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (69, 'shiftright', '', '按拉右移', 'int  shiftright(TINYint|SMALLint|int a, intb)  bigint  shiftright(bigint a, int燽)', '按拉右移', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (71, 'shiftrightunsigned', '', '无符号按位右移（<<<）', 'int  shiftrightunsigned(TINYint|SMALLint|inta, int b)  bigint  shiftrightunsigned(bigint a, int b)', '无符号按位右移（<<<）', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (73, 'greatest', '', '求最大值', 'T  greatest(T v1, T v2, ...)', '返回数据列表中的最大值，当有元素时返回NULL', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (75, 'least', '', '求最小值', 'T  least(T v1, T v2, ...)', '返回数据列表中的最小值，当有NULL元素时返回NULL', 3, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (77, 'size', '', '求集合的长度', 'int  size(Map<K.V>)  int  size(Array<T>)', '参数为Map类型时，计算Map的长度，参数为数组时计算数组的长度', 5, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (79, 'map_keys', '', '返回map中的所有key', 'array<K>  map_keys(Map<K.V>)', '返回map中的所有key', 5, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (81, 'map_values', '', '返回map中的所有value', 'array<V>  map_values(Map<K.V>)', '返回map中的所有value', 5, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (83, 'array_contains', '', '判断数组是否包含指定值', 'boolean  array_contains(Array<T>, value)', '如该数组Array<T>包含value返回true。，否则返回false', 5, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (85, 'sort_array', '', '数组排序', 'array  sort_array(Array<T>)', '按自然顺序对数组进行排序并返回', 5, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (87, 'row_number', '', '排序并获得排序后的编号', 'row_number() OVER (partition by COL1 order by COL2 desc ) rank', '表示根据COL1分组，在分组内部根据 COL2排序，而此函数计算的值就表示每组内部排序后的顺序编号（组内连续的唯一的)', 5, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (89, 'from_unixtime', '', '格式化时间', 'string  from_unixtime(bigint unixtime[, string format])', '将时间的秒值转换成format格式（format可为“yyyy-MM-dd hh:mm:ss”,“yyyy-MM-dd hh”,“yyyy-MM-dd hh:mm”等等）如from_unixtime(1250111000,"yyyy-MM-dd") 得到2009-03-12', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (91, 'unix_timestamp', '', '计算时间戳', 'bigint  unix_timestamp()  bigint  unix_timestamp(string date)  bigint  unix_timestamp(string date, string pattern)', '1.unix_timestamp()：获取本地时区下的时间戳  2.unix_timestamp(string date)：将格式为yyyy-MM-dd HH:mm:ss的时间字符串转换成时间戳，如unix_timestamp("2009-03-20 11:30:01") = 1237573801  3.unix_timestamp(string date, string pattern)：将指定时间字符串格式字符串转换成Unix时间戳，如果格式不对返回0 如：unix_timestamp("2009-03-20", "yyyy-MM-dd") = 1237532400', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (93, 'to_date', '', '计算日期', 'string  to_date(string timestamp)', '返回时间字符串的日期部分，如：to_date("1970-01-01 00:00:00") = "1970-01-01".', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (95, 'year', '', '计算年份', 'int  year(string date)', '返回时间字符串的年份部分，如：year("1970-01-01 00:00:00") = 1970, year("1970-01-01") = 1970.', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (97, 'quarter', '', '计算季节', 'int  quarter(date/timestamp/string)', '返回当前时间属性哪个季度 如quarter("2015-04-08") = 2', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (99, 'month', '', '计算月份', 'int  month(string date)', '返回时间字符串的月份部分，如：month("1970-11-01 00:00:00") = 11, month("1970-11-01") = 11.', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (101, 'day', '', '计算天', 'int  day(string date)', '返回时间字符串中的天部分，如：day("1970-11-01 00:00:00") = 1, day("1970-11-01") = 1.', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (103, 'hour', '', '计算小时', 'int  hour(string date)', '返回时间字符串的小时，如： hour("2009-07-30 12:58:59") = 12, hour("12:58:59") = 12.', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (105, 'minute', '', '计算分钟', 'int  minute(string date)', '返回时间字符串的分钟', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (107, 'second', '', '计算秒数', 'int  second(string date)', '返回时间字符串的秒', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (109, 'weekofyear', '', '计算周', 'int  weekofyear(string date)', '返回时间字符串位于一年中的第几个周内，如：weekofyear("1970-11-01 00:00:00") = 44, weekofyear("1970-11-01") = 44', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (111, 'datediff', '', '计算时间差', 'int  datediff(string enddate, string startdate)', '计算开始时间startdate到结束时间enddate相差的天数，如：datediff("2009-03-01", "2009-02-27") = 2.', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (113, 'date_add', '', '从开始时间startdate加上days', 'string  date_add(string startdate, int days)', '从开始时间startdate加上days，如：date_add("2008-12-31", 1) = "2009-01-01".', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (115, 'date_sub', '', '从开始时间startdate减去days', 'string  date_sub(string startdate, int days)', '从开始时间startdate减去days，如：date_sub("2008-12-31", 1) = "2008-12-30".', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (117, 'from_utc_timestamp', '', '时间戳转换', 'timestamp  from_utc_timestamp(timestamp, string timezone)', '如果给定的时间戳是UTC，则将其转化成指定的时区下时间戳，如：from_utc_timestamp("1970-01-01 08:00:00","PST")=1970-01-01 00:00:00.', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (119, 'to_utc_timestamp', '', '时间戳转换', 'timestamp  to_utc_timestamp(timestamp, string timezone)', '将给定的时间戳转换到UTC下的时间戳，如：to_utc_timestamp("1970-01-01 00:00:00","PST") =1970-01-01 08:00:00', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (121, 'current_date', '', '返回当前时间日期', 'date  current_date', '返回当前时间日期', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (123, 'current_timestamp', '', '返回当前时间戳', 'timestamp  current_timestamp', '返回当前时间戳', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (125, 'add_months', '', '返回当前时间下再增加num_months个月的日期', 'string  add_months(string start_date, int num_months)', '返回start_date之后的num_months的日期。 start_date是一个字符串，日期或时间戳。 num_months是一个整数。 start_date的时间部分被忽略。 如果start_date是本月的最后一天，或者如果生成的月份比start_date的日期组件少，那么结果是最后一个月的最后一天。 否则，结果与start_date具有相同的日期组件', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (127, 'last_day', '', '返回这个月的最后一天的日期', 'string  last_day(string date)', '返回日期所属的月份的最后一天。 date是格式为"yyyy-MM-dd HH：mm：ss"或"yyyy-MM-dd"的字符串。 日期的时间部分被忽略。', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (129, 'next_day', '', '返回当前时间的下一个星期X所对应的日期', 'string  next_day(string start_date, string day_of_week)', '返回当前时间的下一个星期X所对应的日期，如：next_day("2015-01-14", "TU") = 2015-01-20 ?以2015-01-14为开始时间，其下一个星期二所对应的日期为2015-01-20', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (131, 'trunc', '', '返回时间的开始年份或月份', 'string  trunc(string date, string format)', '返回时间的开始年份或月份，如：trunc("2016-06-26",“MM”)=2016-06-01 ?trunc("2016-06-26",“YY”)=2016-01-01 ? 注意所支持的格式为MONTH/MON/MM, YEAR/YYYY/YY', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (133, 'months_between', '', '返回date1与date2之间相差的月份', 'double  months_between(date1, date2)', '返回date1与date2之间相差的月份，如date1>date2，则返回正，如果date1<date2,则返回负，否则返回0.0 ?如：months_between("1997-02-28 10:30:00", "1996-10-30") = 3.94959677 ?1997-02-28 10:30:00与1996-10-30相差3.94959677个月', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (135, 'date_format', '', '时间格式化', 'string  date_format(date/timestamp/string ts, string fmt)', '按指定格式返回时间date，如：date_format("2016-06-22","MM-dd")=06-22', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (137, 'if', '', '如果testCondition 为true就返回valueTrue,否则返回valueFalseOrNull ', 'T  if(boolean testCondition, T valueTrue, T valueFalseOrNull)', 'valueTrue，valueFalseOrNull为泛型', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (139, 'nvl', '', '如果value值为NULL就返回default_value,否则返回value', 'T  nvl(T value, T default_value)', '如果value值为NULL就返回default_value,否则返回value', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (141, 'coalesce', '', '返回第一非null的值', 'T  coalesce(T v1, T v2, ...)', '如果全部都为NULL就返回NULL ?如：COALESCE (NULL,44,55)=44', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (143, 'isnull', '', '判断a是否为空', 'boolean  isnull( a )', '如果a为null就返回true，否则返回false', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (145, 'isnotnull', '', '判断a是否不为空', 'boolean  isnotnull ( a )', '如果a为非null就返回true，否则返回false', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (147, 'assert_true', '', '判断条件是否为真', 'assert_true(boolean condition)', '如果“条件”不为真，则抛出异常，否则返回null（从Hive 0.8.0开始）', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (149, 'cast', '', '类型转换', 'type  cast(expr as <type>)', '将表达式expr的结果转换为<type>。 例如，cast（"1"为BIGINT）将字符串"1"转换为其整数表示。 如果转换不成功，则返回null。', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (151, 'binary', '', '将输入的值转换成二进制', 'binary  binary(string|binary)', '将输入的值转换成二进制', 7, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (153, 'ascii', '', '返回str中首个ASCII字符串的整数值', 'int  ascii(string str)', '返回str中首个ASCII字符串的整数值', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (155, 'base64', '', '二进制转字符串', 'string  base64(binary bin)', '将二进制bin转换成64位的字符串', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (157, 'concat', '', '字符串和字节拼接', 'string  concat(string|binary A, string|binary B...)', '将字符串或字节拼接，如：concat("foo", "bar") = "foobar"，次函数可以拼接任意数量的字符串或字节。', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (159, 'chr', '', '返回A对应的ASCII字符', 'string chr(bigint|double A)', '返回A对应的ASCII字符。如果A大于256，则结果相当于chr（A％256）。 示例：chr（@date）=“X”。', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (161, 'context_ngrams', '', '返回出现次数TOP K的的子序列', 'array<struct<string,double>>  context_ngrams(array<array<string>>, array<string>, int K, int pf)', '返回出现次数TOP K的的子序列，但context_ngram()允许你预算指定上下文(数组)来去查找子序列', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (163, 'concat_ws', '', '使用指定的分隔符拼接字符串', 'string  concat_ws(string SEP, string A, string B...)  string  concat_ws(string SEP, array<string>)', '使用指定的分隔符拼接字符串', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (165, 'decode', '', '解码', 'string  decode(binary bin, string charset)', '使用指定的字符集charset将二进制值bin解码成字符串，支持的字符集有："US-ASCII", "ISO-8@math9-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"，如果任意输入参数为NULL都将返回NULL', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (167, 'encode', '', '编码', 'binary  encode(string src, string charset)', '使用指定的字符集charset将字符串编码成二进制值，支持的字符集有："US-ASCII", "ISO-8@math9-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"，如果任一输入参数为NULL都将返回NULL', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (169, 'find_in_set', '', '返回以逗号分隔的字符串中str出现的位置', 'int  find_in_set(string str, string strList)', '返回以逗号分隔的字符串中str出现的位置，如果参数str为逗号或查找失败将返回0，如果任一参数为NULL将返回NULL回', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (171, 'format_number', '', '格式转换', 'string  format_number(number x, int d)', '将数值X转换成"#,###,###.##"格式字符串，并保留d位小数，如果d为0，将进行四舍五入且不保留小数', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (173, 'get_json_object', '', '从指定路径获取JSON对象', 'string  get_json_object(string json_string, string path)', '从指定路径上的JSON字符串抽取出JSON对象，并返回这个对象的JSON格式，如果输入的JSON是非法的将返回NULL,注意此路径上JSON字符串只能由数字 字母 下划线组成且不能有大写字母和特殊字符，且key不能由数字开头，这是由于Hive对列名的限制', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (175, 'in_file', '', '在指定文件中索引指定字符串', 'boolean  in_file(string str, string filename)', '如果文件名为filename的文件中有一行数据与字符串str匹配成功就返回true', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (177, 'instr', '', '查找字符串str中子字符串substr出现的位置', 'int  instr(string str, string substr)', '查找字符串str中子字符串substr出现的位置，如果查找失败将返回0，如果任一参数为Null将返回null，注意位置为从1开始的', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (179, 'length', '', '计算字符串长度', 'int  length(string A)', '返回字符串的长度', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (181, 'locate', '', '在位置pos之后返回str中第一次出现substr的位置', 'int  locate(string substr, string str[, int pos])', '在位置pos之后返回str中第一次出现substr的位置', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (183, 'lower', '', '将字符串A的所有字母转换成小写字母', 'string  lower(string A)', '将字符串A的所有字母转换成小写字母', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (185, 'lpad', '', '从左边开始对字符串str使用字符串pad填充，最终len长度为止，如果字符串str本身长度比len大的话，将去掉多余的部分', 'string  lpad(string str, int len, string pad)', '从左边开始对字符串str使用字符串pad填充，一直到长度为len为止，如果字符串str本身长度比len大的话，将去掉多余的部分', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (187, 'ltrim', '', '去掉字符串A前面的空格', 'string  ltrim(string A)', '去掉字符串A前面的空格', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (189, 'ngrams', '', '返回出现次数TOP K的的子序列', 'array<struct<string,double>>  ngrams(array<array<string>>, int N, int燢, int pf)', '返回出现次数TOP K的的子序列,n表示子序列的长度', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (191, 'parse_url', '', '返回从URL中抽取指定部分的内容', 'string  parse_url(string urlstring, string partToExtract [, string keyToExtract])', '返回从URL中抽取指定部分的内容，参数url是URL字符串，而参数partToExtract是要抽取的部分，这个参数包含(HOST, PATH, QUERY, REF, PROTOCOL, AUTHORITY, FILE, and USERINFO,例如：parse_url("http://facebook.com/path1/p.php?k1=v1&k2=v2#Ref1", "HOST") ="facebook.com"，如果参数partToExtract值为QUERY则必须指定第三个参数key ?如：parse_url("http://facebook.com/path1/p.php?k1=v1&k2=v2#Ref1", "QUERY", "k1") =‘v1’', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (193, 'printf', '', '按照printf风格格式输出字符串', 'string  printf(string format, Obj... args)', '按照printf风格格式输出字符串', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (195, 'regexp_extract', '', '返回使用模式提取的字符串', 'string  regexp_extract(string subject, string pattern, int index)', '抽取字符串subject中符合正则表达式pattern的第index个部分的子字符串， 例如，regexp_extract("foothebar", "foo(.*?)(bar)", 2)="bar"。 请注意，在使用预定义的字符类时需要注意：使用“ s”作为第二个参数将匹配字母s; ""需要匹配空格等', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (197, 'regexp_replace', '', '使用模式匹配替换字符串', 'string regexp_replace(string INITIAL_STRING, string PATTERN, string REPLACEMENT)', '按照Java正则表达式PATTERN将字符串INITIAL_STRING中符合条件的部分替换成REPLACEMENT所指定的字符串，如里REPLACEMENT为空的话，将符合正则的部分将被去掉 ?如：regexp_replace("foobar", "oo|ar", "") = "fb."。', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (199, 'repeat', '', '重复输出n次字符串str', 'string  repeat(string str, int n)', '重复输出n次字符串str', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (201, 'reverse', '', '反转字符串', 'string  reverse(string A)', '反转字符串', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (203, 'rpad', '', '字符串填充', 'string  rpad(string str, int len, string pad)', '从右边开始对字符串str使用字符串pad填充，一直到长度为len为止，如果字符串str本身长度比len大的话，将去掉多余的部分', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (205, 'rtrim', '', '去掉字符串后面出现的空格', 'string  rtrim(string A)', '去掉字符串后面出现的空格', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (207, 'sentences', '', '将字符串str将转换成单词数组', 'array<array<string>>  sentences(string str, string lang, string locale)', '字符串str将被转换成单词数组，如：sentences("Hello there! How are you?") =( ("Hello", "there"), ("How", "are", "you") )', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (209, 'space', '', '返回n个空格', 'string  space(int n)', '返回n个空格', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (211, 'split', '', '按照正则表达式pat来分割字符串str', 'array  split(string str, string pat)', '按照正则表达式pat来分割字符串str,分割后以数组字符串的形式返回', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (213, 'str_to_map', '', '将字符串str按照指定分隔符转换成Map', 'map<string,string>  str_to_map(text[, delimiter1, delimiter2])', '将字符串str按照指定分隔符转换成Map，第一个参数是需要转换的字符串，第二个参数是键值对之间的分隔符，默认为逗号;第三个参数是键值之间的分隔符，默认为"="', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (215, 'substr', '', '截取字符串', 'string  substr(string|binary A, int start) string  substr(string|binary A, int start, int len) ', '截取字符串A中start位置之后的字符串并返回  截取字符串A中start位置之后的长度为length的字符串', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (217, 'substring_index', '', '截取字符串', 'string  substring_index(string A, string delim, int count)', '截取第count分隔符之前的字符串，如count为正则从左边开始截取，如果为负则从右边开始截取', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (219, 'translate', '', '字符串替换', 'string  translate(string|char|varchar input, string|char|varchar from, string|char|varchar to)', '将input出现在from中的字符串替换成to中的字符串 如：translate("MOBIN","BIN","M")="MOM"', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (221, 'trim', '', '将字符串A前后出现的空格去掉', 'string  trim(string A)', '将字符串A前后出现的空格去掉', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (223, 'unbase64', '', '将64位的字符串转换二进制值', 'binary  unbase64(string str)', '将64位的字符串转换二进制值', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (225, 'upper', '', '将字符串A中的字母转换成大写字母', 'string  upper(string A)', '将字符串A中的字母转换成大写字母', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (227, 'initcap', '', '字符串中的单词首字母大写', 'string  initcap(string A)', '返回字符串，每个单词的第一个字母大写，所有其他字母以小写形式显示。 单词由空格分割。', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (229, 'levenshtein', '', '计算两个字符串之间的差异大小', 'int  levenshtein(string A, string B)', '计算两个字符串之间的差异大小 ?如：levenshtein("kitten", "sitting") = 3', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (231, 'soundex', '', '将普通字符串转换成soundex字符串', 'string  soundex(string A)', '将普通字符串转换成soundex字符串，如：soundex("Miller") = M460.', 11, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (233, 'count', '', '统计总行数', 'bigint  count(*)  bigint  count(expr)  bigint  count(DISTINCT expr[, expr...])', '统计总行数，包括含有NULL值的行  统计提供非NULL的expr表达式值的行数  统计提供非NULL且去重后的expr表达式值的行数', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (235, 'sum', '', '表示求指定列的和', 'double  sum(col)  double  sum(DISTINCT col)', 'sum(col),表示求指定列的和，sum(DISTINCT col)表示求去重后的列的和', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (237, 'avg', '', '求指定列的平均值', 'double  avg(col)  double  avg(DISTINCT col)', 'avg(col),表示求指定列的平均值，avg(DISTINCT col)表示求去重后的列的平均值', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (239, 'min', '', '求指定列的最小值', 'double  min(col)', '求指定列的最小值', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (241, 'max', '', '求指定列的最大值', 'double  max(col)', '求指定列的最大值', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (243, 'variance', '', '求指定列数值的方差', 'double  variance(col)  double  var_pop(col)', '求指定列数值的方差', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (245, 'var_samp', '', '求指定列数值的样本方差', 'double  var_samp(col)', '求指定列数值的样本方差', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (247, 'stddev_pop', '', '求指定列数值的标准偏差', 'double  stddev_pop(col)', '求指定列数值的标准偏差', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (249, 'stddev_samp', '', '求指定列数值的样本标准偏差', 'double  stddev_samp(col)', '求指定列数值的样本标准偏差', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (251, 'covar_pop', '', '求指定列数值的协方差', 'double  covar_pop(col1, col2)', '求指定列数值的协方差', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (253, 'covar_samp', '', '求指定列数值的样本协方差', 'double  covar_samp(col1, col2)', '求指定列数值的样本协方差', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (255, 'corr', '', '返回两列数值的相关系数', 'double  corr(col1, col2)', '返回两列数值的相关系数', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (257, 'percentile', '', '返回col的p%分位数', 'double  percentile(bigint col, p)', ' p必须在0和1之间。注意：只能对整数值计算真正的百分位数。 ', 13, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (259, 'explode', '', '对于a中的每个元素，生成包含该元素的行', 'Array Type  explode(array<TYPE> a)  N rows  explode(ARRAY)  N rows  explode(MAP)', '对于a中的每个元素，生成包含该元素的行  从数组中的每个元素返回一行  从输入映射中为每个键值对返回一行，每行中有两列：一个用于键，另一个用于该值', 15, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (261, 'posexplode', '', '从数组中的每个元素返回一行', 'N rows  posexplode(ARRAY)', '与explode类似，不同的是还返回各元素在数组中的位置', 15, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (263, 'stack', '', '把M列转换成N行', 'N rows  stack(int n, v_1, v_2, ..., v_k)', '将v_1，...，v_k分解成n行。 每行将有k / n列。 n必须是常数。', 15, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (265, 'json_tuple', '', '从一个JSON字符串中获取多个键并作为一个元组返回', 'tuple  json_tuple(jsonStr, k1, k2, ...)', '从一个JSON字符串中获取多个键并作为一个元组返回，与get_json_object不同的是此函数能一次获取多个键值', 15, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (267, 'parse_url_tuple', '', '返回从URL中抽取指定N部分的内容', 'tuple  parse_url_tuple(url, p1, p2, ...)', '返回从URL中抽取指定N部分的内容，参数url是URL字符串，而参数p1,p2,....是要抽取的部分，这个参数包含HOST, PATH, QUERY, REF, PROTOCOL, AUTHORITY, FILE, USERINFO, QUERY:<KEY>', 15, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);
INSERT INTO develop_function (id, name, class_name, purpose, command_formate, param_desc, node_pid, tenant_id, create_user_id, modify_user_id, type, task_type, gmt_create, gmt_modified, is_deleted, sql_text) VALUES (269, 'inline', '', '将结构体数组提取出来并插入到表中', 'inline(ARRAY<STRUCT[,STRUCT]>)', '将结构体数组提取出来并插入到表中', 15, -1, -1, -1, 1, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, null);


INSERT INTO develop_sys_parameter (param_name, param_command, gmt_create, gmt_modified, is_deleted) VALUES ('bdp.system.bizdate', 'yyyyMMdd-1', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_sys_parameter (param_name, param_command, gmt_create, gmt_modified, is_deleted) VALUES ('bdp.system.cyctime', 'yyyyMMddHHmmss', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_sys_parameter (param_name, param_command, gmt_create, gmt_modified, is_deleted) VALUES ('bdp.system.currmonth', 'yyyyMM-0', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_sys_parameter (param_name, param_command, gmt_create, gmt_modified, is_deleted) VALUES ('bdp.system.premonth', 'yyyyMM-1', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_sys_parameter (param_name, param_command, gmt_create, gmt_modified, is_deleted) VALUES ('bdp.system.runtime', '${bdp.system.currenttime}', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_sys_parameter (param_name, param_command, gmt_create, gmt_modified, is_deleted) VALUES ('bdp.system.bizdate2', 'yyyy-MM-dd,-1', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);


INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (0, 1, 'create table if not exists ods_order_header (
     order_header_id     string comment ''订单头id''
    ,order_date          bigint comment ''订单日期''
    ,shop_id             string comment ''店铺id''
    ,customer_id         string comment ''客户id''
    ,order_status        bigint comment ''订单状态''
    ,pay_date            bigint comment ''支付日期''

)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;

create table if not exists ods_order_detail (
     order_header_id     string comment ''订单头id''
    ,order_detail_id     string comment ''订单明细id''
    ,item_id             string comment ''商品id''
    ,quantity            double comment ''商品数量''
    ,unit_price          double comment ''商品单价''
    ,dist_amout          double comment ''折扣金额''
)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;


create table if not exists exam_ods_shop_info (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
    ,status                 string comment ''店铺状态,open/closed''
)comment ''店铺维度表''
PARTITIONED BY (ds string) ;', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (0, 2, 'create table if not exists exam_dwd_sales_ord_df (
     order_header_id     string comment ''订单头id''
    ,order_detail_id     string comment ''订单明细id''
    ,order_date          bigint comment ''订单日期''
    ,pay_date            bigint comment ''付款日期''
    ,shop_id             string comment ''店铺id''
    ,customer_id         string comment ''客户id''
    ,item_id             string comment ''商品id''
    ,quantity            bigint comment ''商品数量''
    ,unit_price          double comment ''商品单价''
    ,amount              double comment ''总金额''
)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;


INSERT OVERWRITE TABLE exam_dwd_sales_ord_df PARTITION(ds = ''${bdp.system.bizdate}'')
select
 d.order_header_id
,d.order_detail_id
,h.order_date
,h.pay_date
,h.shop_id
,h.customer_id
,d.item_id
,d.quantity
,d.unit_price
,d.quantity*d.unit_price-d.dist_amout as amount
from ods_order_header as h join ods_order_detail as d on h.order_header_id = d.order_header_id
where h.ds = ''${bdp.system.bizdate}'' and d.ds= ''${bdp.system.bizdate}''
and h.order_status = 0;
', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (0, 3, 'create table if not exists exam_dws_sales_shop_1d (
     stat_date              string comment ''统计日期''
    ,shop_id                string comment ''订单明细id''
    ,ord_quantity_1d        bigint comment ''最近一天订单数量''
    ,ord_amount_1d          double comment ''最近一天订单金额''
    ,pay_quantity_1d        bigint comment ''最近一天付款数量''
    ,pay_amount_1d          double comment ''最近一天付款金额''
)comment ''最近一天门店粒度销售汇总表''
PARTITIONED BY (ds string) ;

INSERT OVERWRITE TABLE exam_dws_sales_shop_1d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,shop_id
,sum(case when order_date = ''${bdp.system.bizdate}'' then quantity end) as ord_quantity_1d
,sum(case when order_date = ''${bdp.system.bizdate}'' then amount end)   as ord_amount_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then quantity end) as pay_quantity_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then amount end)   as pay_amount_1d
from
exam_dwd_sales_ord_df
where ds = ''${bdp.system.bizdate}''
group by shop_id;', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (0, 4, 'create table if not exists exam_ads_sales_all_d (
     stat_date              string comment ''统计日期''
    ,ord_quantity           bigint comment ''订单数量''
    ,ord_amount             double comment ''订单金额''
    ,pay_quantity           bigint comment ''付款数量''
    ,pay_amount             double comment ''付款金额''
    ,shop_cnt               bigint comment ''有交易的店铺数量''
)comment ''订单交易总表''
PARTITIONED BY (ds string) lifecycle 7;

INSERT OVERWRITE TABLE exam_ads_sales_all_d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,sum(ord_quantity_1d) as ord_quantity
,sum(ord_amount_1d)   as ord_amount
,sum(pay_quantity_1d) as pay_quantity
,sum(pay_amount_1d)   as pay_amount
,count(distinct shop_id) as shop_cnt
from
exam_dws_sales_shop_1d
where ds = ''${bdp.system.bizdate}''
group by shop_id;', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (0, 5, 'create table if not exists exam_dim_shop (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
)comment ''店铺维度表''
PARTITIONED BY (ds string) lifecycle 365;

INSERT OVERWRITE TABLE exam_dim_shop PARTITION(ds = ''${bdp.system.bizdate}'')
select
 shop_id
,shop_name
,shop_type
,address
from exam_ods_shop_info
where ds = ''${bdp.system.bizdate}''
and status = ''open'';', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (15, 0, 'create table if not exists customer_base
(
    id                    varchar(20),
	cust_name             varchar(20),
	cust_phone            varchar(20),
	cust_wechat           varchar(20),
	cust_cefi_number      varchar(20),
	cust_car_number       varchar(20),
	cust_house_number     varchar(20),
	cust_job              varchar(20),
	cust_company          varchar(20),
	cust_work_address     varchar(20),
	cust_bank_number      varchar(20),
	cust_gate_card        varchar(20)
);
-- COMMENT ON customer_base IS ''用户基础表'';


create table if not exists customer_in_call
(
    id                        varchar,
    in_call_phone_number      varchar,
    in_call_time              varchar,--本来这个字段是时间戳类型的，但是libra不支持时间戳timestamp关键字，所以改成varchar类型
    in_call_duration          bigint,
    in_call_consult_problem   varchar
);
-- COMMENT ON customer_in_call IS ''用户来电记录表'';


create table if not exists customer_in_and_out
(
    id                      varchar,
    cust_gate_card          varchar,
    in_or_out               varchar,
    in_or_out_time          varchar
);
-- COMMENT ON customer_in_and_out IS ''客户出入记录表'';


create table if not exists customer_complain(
    id                        varchar,
    complain_phone            varchar,
    complain_name             varchar,
    complain_problem          varchar,
    complain_time             varchar
);

-- COMMENT ON customer_complain IS ''客户投诉记录表'';

-- --注意，Libra不支持时间戳类型的关键字
', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (15, 1, '
create table if not exists ods_order_header (
    order_header_id     varchar,
    order_date          bigint,
    shop_id             bigint,
    customer_id         varchar,
    order_status        bigint,
    pay_date            bigint
);

create table if not exists ods_order_detail (
    order_header_id     varchar,
    order_detail_id     varchar,
    item_id             varchar,
    quantity            varchar,
    unit_price          varchar,
    dist_amout          varchar
);

create table if not exists exam_ods_shop_info (
    shop_id                bigint,
    shop_name              varchar,
    shop_type              varchar,
    address                varchar,
    status                 varchar
);
', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (15, 2, '
create table if not exists exam_dwd_sales_ord_df (
    order_header_id     varchar,
    order_detail_id     varchar,
    order_date          bigint,
    pay_date            bigint,
    shop_id             bigint,
    customer_id         varchar,
    item_id             varchar,
    quantity            varchar,
    unit_price          varchar,
    amount              varchar
);

INSERT INTO exam_dwd_sales_ord_df
select
 d.order_header_id
,d.order_detail_id
,h.order_date
,h.pay_date
,h.shop_id
,h.customer_id
,d.item_id
,d.quantity
,d.unit_price
from ods_order_header as h join ods_order_detail as d
on h.order_header_id = d.order_header_id and h.order_status = 0;
', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (15, 3, 'create table if not exists exam_dws_sales_shop_1d (
     stat_date              string comment ''统计日期''
    ,shop_id                string comment ''订单明细id''
    ,ord_quantity_1d        bigint comment ''最近一天订单数量''
    ,ord_amount_1d          double comment ''最近一天订单金额''
    ,pay_quantity_1d        bigint comment ''最近一天付款数量''
    ,pay_amount_1d          double comment ''最近一天付款金额''
)comment ''最近一天门店粒度销售汇总表''
PARTITIONED BY (ds string) ;

INSERT OVERWRITE TABLE exam_dws_sales_shop_1d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,shop_id
,sum(case when order_date = ''${bdp.system.bizdate}'' then quantity end) as ord_quantity_1d
,sum(case when order_date = ''${bdp.system.bizdate}'' then amount end)   as ord_amount_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then quantity end) as pay_quantity_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then amount end)   as pay_amount_1d
from
exam_dwd_sales_ord_df
where ds = ''${bdp.system.bizdate}''
group by shop_id;
', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (15, 4, 'create table if not exists exam_ads_sales_all_d (
     stat_date              string comment ''统计日期''
    ,ord_quantity           bigint comment ''订单数量''
    ,ord_amount             double comment ''订单金额''
    ,pay_quantity           bigint comment ''付款数量''
    ,pay_amount             double comment ''付款金额''
    ,shop_cnt               bigint comment ''有交易的店铺数量''
)comment ''订单交易总表''
PARTITIONED BY (ds string) lifecycle 7;

INSERT OVERWRITE TABLE exam_ads_sales_all_d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,sum(ord_quantity_1d) as ord_quantity
,sum(ord_amount_1d)   as ord_amount
,sum(pay_quantity_1d) as pay_quantity
,sum(pay_amount_1d)   as pay_amount
,count(distinct shop_id) as shop_cnt
from
exam_dws_sales_shop_1d
where ds = ''${bdp.system.bizdate}''
group by shop_id;
', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO develop_task_template (task_type, type, content, gmt_create, gmt_modified, is_deleted) VALUES (15, 5, 'create table if not exists exam_dim_shop (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
)comment ''店铺维度表''
PARTITIONED BY (ds string) lifecycle 365;

INSERT OVERWRITE TABLE exam_dim_shop PARTITION(ds = ''${bdp.system.bizdate}'')
select
 shop_id
,shop_name
,shop_type
,address
from exam_ods_shop_info
where ds = ''${bdp.system.bizdate}''
and status = ''open'';
', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);



INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('spark_version', '2.1', '210', null, 2, 1, 'INTEGER', '', 1, '2021-03-02 14:15:23', '2021-03-02 14:15:23', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('spark_thrift_version', '1.x', '1.x', null, 3, 1, 'STRING', '', 0, '2021-03-02 14:16:41', '2021-03-02 14:16:41', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('flink_version', '1.10', '110', null, 1, 2, 'INTEGER', '0,1,2', 0, '2021-03-02 14:14:12', '2021-03-02 14:14:12', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('spark_thrift_version', '2.x', '2.x', null, 3, 2, 'STRING', '', 1, '2021-03-02 14:16:41', '2021-03-02 14:16:41', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_config', 'HDP 3.1.x', '-200', '', 5, 0, 'LONG', 'SPARK', 0, '2021-02-05 11:53:21', '2021-02-05 11:53:21', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn3-hdfs3-spark210', '-108', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:23', '2021-03-04 17:50:23', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn2-hdfs2-spark210', '-108', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn3-hdfs3-flink110', '-109', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'dummy', '-101', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn2-hdfs2-flink110', '-109', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'hive', '-117', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'hive2', '-117', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'hive3', '-117', null, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);


INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'Apache Hadoop 2.x', '2.7.6', null, 0, 1, 'STRING', 'Apache Hadoop', 0, '2021-12-28 10:18:58', '2021-12-28 10:18:58', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'Apache Hadoop 3.x', '3.0.0', null, 0, 2, 'STRING', 'Apache Hadoop', 0, '2021-12-28 10:18:58', '2021-12-28 10:18:58', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'HDP 2.6.x', '2.7.3', null, 0, 1, 'STRING', 'HDP', 0, '2021-12-28 10:18:59', '2021-12-28 10:18:59', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'HDP 3.x', '3.1.1', null, 0, 2, 'STRING', 'HDP', 0, '2021-12-28 10:18:59', '2021-12-28 10:18:59', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDH 5.x', '2.3.0', null, 0, 1, 'STRING', 'CDH', 0, '2021-12-28 10:19:00', '2021-12-28 10:19:00', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDH 6.0.x', '3.0.0', null, 0, 11, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDH 6.1.x', '3.0.0', null, 0, 12, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDH 6.2.x', '3.0.0', null, 0, 13, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDP 7.x', '3.1.1', null, 0, 15, 'STRING', 'CDH', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'TDH 5.2.x', '2.7.0', null, 0, 1, 'STRING', 'TDH', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'TDH 7.x', '2.7.0', null, 0, 2, 'STRING', 'TDH', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'TDH 6.x', '2.7.0', null, 0, 1, 'STRING', 'TDH', 0, '2021-12-28 11:44:02', '2021-12-28 11:44:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'HDFS', '{
	"owner": "STORAGE",
	"dependsOn": ["RESOURCE"],
	"allowCoexistence": false,
	"versionDictionary": "HADOOP_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'FLINK', '{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": true,
	"versionDictionary": "FLINK_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'SPARK', '{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": true,
	"versionDictionary": "SPARK_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-28 16:54:54', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'SPARK_THRIFT', '{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false,
	"versionDictionary": "SPARK_THRIFT_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'HIVE_SERVER', '{
	"owner": "COMPUTE",
    "dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false,
	"versionDictionary": "HIVE_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'SFTP', '{
	"owner": "COMMON",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "dummy"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'YARN', '{
	"owner": "RESOURCE",
	"dependsOn": [],
	"allowCoexistence": false,
	"versionDictionary": "HADOOP_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'Apache Hadoop 2.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:01:55', '2021-12-28 11:01:55', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'Apache Hadoop 3.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:03:45', '2021-12-28 11:03:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 3.0.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:23', '2021-12-28 11:04:23', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.0.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {   "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:40', '2021-12-28 11:04:40', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.1.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:55', '2021-12-28 11:04:55', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.2.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4(CDH 6.2)":"yarn3-hdfs3-spark240cdh620"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:05:06', '2021-12-28 11:05:06', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 2.6.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:06:38', '2021-12-28 11:06:38', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 5.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:07:19', '2021-12-28 11:07:19', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 3.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:43:05', '2021-12-28 11:43:05', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 5.2.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:33', '2021-12-28 11:44:33', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 6.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:43', '2021-12-28 11:44:43', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 7.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDP 7.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '1.x', '{
    "1.x":"hive"
}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '2.x', '{
    "2.x":"hive2"
}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '3.x-apache', '{
    "3.x-apache":"hive3"
}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '3.x-cdp', '{
    "3.x-cdp":"hive3"
}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '1.x', '{
    "1.x":"hive"
}', null, 14, 1, 'STRING', 'SPARK_THRIFT', 0, '2021-12-31 15:00:16', '2021-12-31 15:00:16', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '2.x', '{
    "2.x":"hive2"
}', null, 14, 1, 'STRING', 'SPARK_THRIFT', 0, '2021-12-31 15:00:16', '2021-12-31 15:00:16', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('SPARK_SQL', 'SPARK_SQL', '0', 'SparkSQL', 30, 1, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('SYNC', '数据同步', '2', '数据同步', 30, 5, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('VIRTUAL', '虚节点', '-1', '虚节点', 30, 11, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('ResourceManager', 'ResourceManager', '3', '资源管理', 31, 3, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-02-11 10:40:14', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('SparkSQLFunction', 'SparkSQLFunction', '4', 'SparkSQL', 31, 4, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-02-11 10:40:14', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('TableQuery', 'TableQuery', '5', '表查询', 31, 5, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-02-11 10:40:14', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('TaskDevelop', 'TaskDevelop', '1', '任务开发', 31, 1, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-02-11 10:40:14', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('FunctionManager', 'FunctionManager', '4', '函数管理', 32, 4, 'STRING', '', 1, '2022-02-11 10:42:19', '2022-02-11 10:42:19', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('ResourceManager', 'ResourceManager', '3', '资源管理', 32, 3, 'STRING', '', 1, '2022-02-11 10:42:19', '2022-02-11 10:42:19', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('TaskManager', 'TaskManager', '1', '任务管理', 32, 1, 'STRING', '', 1, '2022-02-11 10:42:19', '2022-02-11 10:42:19', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('CustomFunction', 'CustomFunction', '6', '自定义函数', 33, 4, 'STRING', '', 1, '2022-02-11 10:42:57', '2022-02-11 10:42:57', 0);
INSERT INTO dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('SystemFunction', 'SystemFunction', '6', '系统函数', 33, 2, 'STRING', '', 1, '2022-02-11 10:42:57', '2022-02-11 10:42:57', 0);


INSERT INTO task_param_template (task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES (0, 'SPARK_SQL', '2.1', '## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead', '2021-11-18 10:36:13', '2021-11-18 10:36:13', 0);
INSERT INTO task_param_template (task_type, task_name, task_version, params, gmt_create, gmt_modified, is_deleted) VALUES (2, 'SYNC', '1.10', '## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job
## flinkTaskRunMode=per_job
## per_job模式下jobManager配置的内存大小，默认1024（单位M)
## jobmanager.memory.mb=1024
## per_job模式下taskManager配置的内存大小，默认1024（单位M）
## taskmanager.memory.mb=1024
## per_job模式下每个taskManager 对应 slot的数量
## slots=1
## checkpoint保存时间间隔
## flink.checkpoint.interval=300000
## 任务优先级, 范围:1-1000
## job.priority=10', '2021-11-18 10:37:24', '2021-11-18 10:37:24', 0);


INSERT INTO tenant (tenant_name, tenant_desc, gmt_create, gmt_modified, create_user_id, is_deleted) VALUES ('taier', null, '2021-08-13 16:39:40', '2021-08-13 16:39:40', 1, 0);
INSERT INTO user (user_name, password, phone_number, email, status, gmt_create, gmt_modified, is_deleted) VALUES ('admin@dtstack.com', '0192023A7BBD73250516F069DF18B500', '', 'admin@dtstack.com', 0, '2017-06-05 20:35:16', '2017-06-05 20:35:16', 0);