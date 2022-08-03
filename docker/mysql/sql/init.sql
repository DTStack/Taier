SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for console_cluster
-- ----------------------------
DROP TABLE IF EXISTS `console_cluster`;
CREATE TABLE `console_cluster` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `cluster_name` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '集群名称',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_cluster_name` (`cluster_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of console_cluster
-- ----------------------------
BEGIN;
INSERT INTO `console_cluster` VALUES (-1, 'default', '2022-01-28 10:26:01', '2022-02-11 11:11:32', 0);
COMMIT;

-- ----------------------------
-- Table structure for console_cluster_tenant
-- ----------------------------
DROP TABLE IF EXISTS `console_cluster_tenant`;
CREATE TABLE `console_cluster_tenant` (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                          `cluster_id` int(11) NOT NULL COMMENT '集群id',
                                          `queue_id` int(11) DEFAULT NULL COMMENT '队列id',
                                          `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for console_component
-- ----------------------------
DROP TABLE IF EXISTS `console_component`;
CREATE TABLE `console_component` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `component_name` varchar(24) COLLATE utf8_bin NOT NULL COMMENT '组件名称',
                                     `component_type_code` tinyint(1) NOT NULL COMMENT '组件类型',
                                     `version_value` varchar(25) COLLATE utf8_bin DEFAULT '' COMMENT '组件hadoop版本',
                                     `upload_file_name` varchar(126) COLLATE utf8_bin DEFAULT '' COMMENT '上传文件zip名称',
                                     `kerberos_file_name` varchar(126) COLLATE utf8_bin DEFAULT '' COMMENT '上传kerberos文件zip名称',
                                     `store_type` tinyint(1) DEFAULT '4' COMMENT '组件存储类型: HDFS、NFS 默认HDFS',
                                     `is_metadata` tinyint(1) DEFAULT '0' COMMENT '/*1 metadata*/',
                                     `is_default` tinyint(1) NOT NULL DEFAULT '1' COMMENT '组件默认版本',
                                     `deploy_type` tinyint(1) DEFAULT NULL COMMENT '/* 0 standalone 1 yarn  */',
                                     `cluster_id` int(11) DEFAULT NULL COMMENT '集群id',
                                     `version_name` varchar(25) COLLATE utf8_bin DEFAULT NULL,
                                     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `index_component` (`cluster_id`,`component_type_code`,`version_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for console_component_config
-- ----------------------------
DROP TABLE IF EXISTS `console_component_config`;
CREATE TABLE `console_component_config` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `cluster_id` int(11) NOT NULL COMMENT '集群id',
                                            `component_id` int(11) NOT NULL COMMENT '组件id',
                                            `component_type_code` tinyint(1) NOT NULL COMMENT '组件类型',
                                            `type` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '配置类型',
                                            `required` tinyint(1) NOT NULL COMMENT 'true/false',
                                            `key` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '配置键',
                                            `value` text COLLATE utf8_bin COMMENT '默认配置项',
                                            `values` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '可配置项',
                                            `dependencyKey` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '依赖键',
                                            `dependencyValue` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '依赖值',
                                            `desc` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`),
                                            KEY `index_cluster_id` (`cluster_id`),
                                            KEY `index_componentId` (`component_id`)
) ENGINE=InnoDB AUTO_INCREMENT=535 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of console_component_config
-- ----------------------------
BEGIN;
INSERT INTO `console_component_config` VALUES (255, -2, -101, 10, 'RADIO_LINKAGE', 1, 'auth', '1', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (257, -2, -101, 10, '', 1, 'password', '1', NULL, 'auth', '1', NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (259, -2, -101, 10, 'PASSWORD', 1, 'password', '', NULL, 'auth$password', '', NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (261, -2, -101, 10, '', 1, 'rsaPath', '2', NULL, 'auth', '2', NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (263, -2, -101, 10, 'input', 1, 'rsaPath', '', NULL, 'auth$rsaPath', '', NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (265, -2, -101, 10, 'INPUT', 1, 'fileTimeout', '300000', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (267, -2, -101, 10, 'INPUT', 1, 'host', '', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (269, -2, -101, 10, 'INPUT', 1, 'isUsePool', 'true', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (271, -2, -101, 10, 'INPUT', 1, 'maxIdle', '16', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (273, -2, -101, 10, 'INPUT', 1, 'maxTotal', '16', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (275, -2, -101, 10, 'INPUT', 1, 'maxWaitMillis', '3600000', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (277, -2, -101, 10, 'INPUT', 1, 'minIdle', '16', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (279, -2, -101, 10, 'INPUT', 1, 'path', '/data/sftp', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (281, -2, -101, 10, 'INPUT', 1, 'port', '22', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (283, -2, -101, 10, 'INPUT', 1, 'timeout', '0', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (285, -2, -101, 10, 'INPUT', 1, 'username', '', NULL, NULL, NULL, NULL, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO `console_component_config` VALUES (293, -2, -108, 1, 'CHECKBOX', 1, 'deploymode', '[\"perjob\"]', NULL, '', '', NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (295, -2, -108, 1, 'GROUP', 1, 'perjob', '', NULL, 'deploymode', 'perjob', NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (297, -2, -108, 1, 'INPUT', 0, 'addColumnSupport', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (299, -2, -108, 1, 'INPUT', 1, 'spark.cores.max', '1', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (301, -2, -108, 1, 'INPUT', 0, 'spark.driver.extraJavaOptions', '-Dfile.encoding=utf-8', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (303, -2, -108, 1, 'INPUT', 0, 'spark.eventLog.compress', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (305, -2, -108, 1, 'INPUT', 0, 'spark.eventLog.dir', 'hdfs://ns1/tmp/spark-yarn-logs', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (307, -2, -108, 1, 'INPUT', 0, 'spark.eventLog.enabled', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (309, -2, -108, 1, 'INPUT', 1, 'spark.executor.cores', '1', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (311, -2, -108, 1, 'INPUT', 0, 'spark.executor.extraJavaOptions', '-Dfile.encoding=utf-8', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (313, -2, -108, 1, 'INPUT', 1, 'spark.executor.heartbeatInterval', '600s', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (315, -2, -108, 1, 'INPUT', 1, 'spark.executor.instances', '1', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (317, -2, -108, 1, 'INPUT', 1, 'spark.executor.memory', '512m', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (319, -2, -108, 1, 'INPUT', 1, 'spark.network.timeout', '600s', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (321, -2, -108, 1, 'INPUT', 1, 'spark.rpc.askTimeout', '600s', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (323, -2, -108, 1, 'INPUT', 1, 'spark.speculation', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (325, -2, -108, 1, 'INPUT', 1, 'spark.submit.deployMode', 'cluster', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (327, -2, -108, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON', '/data/miniconda2/bin/python3', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (329, -2, -108, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_PYTHON', '/data/miniconda2/bin/python3', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (331, -2, -108, 1, 'INPUT', 1, 'spark.yarn.maxAppAttempts', '1', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (333, -2, -108, 1, 'INPUT', 1, 'sparkPythonExtLibPath', 'hdfs://ns1/dtInsight/pythons/pyspark.zip,hdfs://ns1/dtInsight/pythons/py4j-0.10.7-src.zip', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (335, -2, -108, 1, 'INPUT', 1, 'sparkSqlProxyPath', 'hdfs://ns1/dtInsight/spark/spark-sql-proxy.jar', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (337, -2, -108, 1, 'INPUT', 1, 'sparkYarnArchive', 'hdfs://ns1/dtInsight/sparkjars/jars', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (339, -2, -108, 1, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', NULL, 'deploymode$perjob', NULL, NULL, '2021-02-25 18:12:53', '2021-02-25 18:12:53', 0);
INSERT INTO `console_component_config` VALUES (341, -2, -117, 9, 'INPUT', 1, 'jdbcUrl', '', NULL, NULL, NULL, NULL, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO `console_component_config` VALUES (343, -2, -117, 9, 'INPUT', 0, 'maxJobPoolSize', '', NULL, NULL, NULL, NULL, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO `console_component_config` VALUES (345, -2, -117, 9, 'INPUT', 0, 'minJobPoolSize', '', NULL, NULL, NULL, NULL, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO `console_component_config` VALUES (347, -2, -117, 9, 'PASSWORD', 0, 'password', '', NULL, NULL, NULL, NULL, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO `console_component_config` VALUES (349, -2, -117, 9, 'INPUT', 0, 'queue', '', NULL, NULL, NULL, NULL, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO `console_component_config` VALUES (351, -2, -117, 9, 'INPUT', 0, 'username', '', NULL, NULL, NULL, NULL, '2022-02-14 11:27:44', '2022-02-14 11:27:44', 0);
INSERT INTO `console_component_config` VALUES (353, -2, -115, 0, 'CHECKBOX', 1, 'deploymode', '[\"perjob\",\"session\"]', NULL, '', '', NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (355, -2, -115, 0, 'GROUP', 1, 'perjob', 'perjob', NULL, 'deploymode', 'perjob', NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (357, -2, -115, 0, 'INPUT', 1, 'akka.ask.timeout', '60 s', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (359, -2, -115, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (361, -2, -115, 0, 'INPUT', 0, 'classloader.resolve-order', 'child-first', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (363, -2, -115, 0, 'INPUT', 1, 'clusterMode', 'perjob', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (365, -2, -115, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (367, -2, -115, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.12/flink_lib', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (369, -2, -115, 0, 'INPUT', 1, 'chunjunDistDir', '/data/insight_plugin1.12/chunjunplugin', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (371, -2, -115, 0, 'INPUT', 0, 'jobmanager.memory.process.size', '1600m', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (373, -2, -115, 0, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (375, -2, -115, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (377, -2, -115, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink112/ha', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (379, -2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink112', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (381, -2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (383, -2, -115, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (385, -2, -115, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink112/completed-jobs', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (387, -2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (389, -2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (391, -2, -115, 0, '', 1, 'false', 'false', NULL, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (393, -2, -115, 0, '', 1, 'true', 'true', NULL, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (395, -2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (397, -2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (399, -2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (401, -2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (403, -2, -115, 0, '', 1, 'false', 'false', NULL, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (405, -2, -115, 0, '', 1, 'true', 'true', NULL, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (407, -2, -115, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (409, -2, -115, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (411, -2, -115, 0, 'INPUT', 0, 'prometheusHost', '', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (413, -2, -115, 0, 'INPUT', 0, 'prometheusPort', '9090', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (415, -2, -115, 0, 'INPUT', 0, 'state.backend', 'RocksDB', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (417, -2, -115, 0, 'INPUT', 0, 'state.backend.incremental', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (419, -2, -115, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink112/checkpoints', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (421, -2, -115, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (423, -2, -115, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink112/savepoints', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (425, -2, -115, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (427, -2, -115, 0, 'INPUT', 0, 'yarn.application-attempts', '3', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (429, -2, -115, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (431, -2, -115, 0, 'GROUP', 1, 'session', 'session', NULL, 'deploymode', 'session', NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (433, -2, -115, 0, 'INPUT', 1, 'checkSubmitJobGraphInterval', '60', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (435, -2, -115, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (437, -2, -115, 0, 'INPUT', 0, 'classloader.resolve-order', 'parent-first', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (439, -2, -115, 0, 'INPUT', 1, 'clusterMode', 'session', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (441, -2, -115, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (443, -2, -115, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.12/flink_lib', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (445, -2, -115, 0, 'INPUT', 1, 'chunjunDistDir', '/data/insight_plugin1.12/chunjunplugin', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (447, -2, -115, 0, 'INPUT', 1, 'slotmanager.number-of-slots.max', '10', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (449, -2, -115, 0, 'INPUT', 0, 'jobmanager.memory.process.size', '1600m', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (451, -2, -115, 0, 'INPUT', 0, 'high-availability', 'NONE', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (453, -2, -115, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (455, -2, -115, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink112/ha', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (457, -2, -115, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink112', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (459, -2, -115, 0, 'INPUT', 0, 'high-availability.zookeeper.quorum', '', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (461, -2, -115, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink112/completed-jobs', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (463, -2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (465, -2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (467, -2, -115, 0, '', 1, 'false', 'false', NULL, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (469, -2, -115, 0, '', 1, 'true', 'true', NULL, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (471, -2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (473, -2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (475, -2, -115, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (477, -2, -115, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (479, -2, -115, 0, '', 1, 'false', 'false', NULL, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (481, -2, -115, 0, '', 1, 'true', 'true', NULL, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (483, -2, -115, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (485, -2, -115, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (487, -2, -115, 0, 'INPUT', 0, 'prometheusHost', '', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (489, -2, -115, 0, 'INPUT', 0, 'prometheusPort', '9090', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (491, -2, -115, 0, 'INPUT', 0, 'sessionRetryNum', '5', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (493, -2, -115, 0, 'INPUT', 1, 'sessionStartAuto', 'true', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (495, -2, -115, 0, 'INPUT', 0, 'state.backend', 'RocksDB', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (497, -2, -115, 0, 'INPUT', 0, 'state.backend.incremental', 'true', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (499, -2, -115, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink112/checkpoints', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (501, -2, -115, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (503, -2, -115, 0, 'INPUT', 0, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink112/savepoints', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (505, -2, -115, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (507, -2, -115, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (509, -2, -115, 0, 'INPUT', 0, 'yarn.application-attempts', '3', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (511, -2, -115, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (513, -2, -115, 0, 'INPUT', 1, 'remotechunjunDistDir', '/data/insight_plugin1.12/chunjunplugin', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:56:15', '2021-07-27 13:56:15', 0);
INSERT INTO `console_component_config` VALUES (515, -2, -115, 0, 'INPUT', 1, 'akka.tcp.timeout', '60 s', NULL, 'deploymode$perjob', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (517, -2, -115, 0, 'INPUT', 1, 'remotechunjunDistDir', '/data/insight_plugin1.12/chunjunplugin', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (519, -2, -115, 0, 'INPUT', 1, 'flinkSessionName', 'flink_session', NULL, 'deploymode$session', NULL, NULL, '2021-07-27 13:52:46', '2021-07-27 13:52:46', 0);
INSERT INTO `console_component_config` VALUES (521, -2, -115, 0, 'INPUT', 0, 'checkpoint.retain.time', '7', NULL, 'deploymode$perjob', NULL, NULL, '2021-08-24 17:22:06', '2021-08-24 17:22:06', 0);
INSERT INTO `console_component_config` VALUES (523, -2, -115, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.12/flink_lib', NULL, 'deploymode$perjob', NULL, NULL, '2021-08-24 20:40:39', '2021-08-24 20:40:39', 0);
INSERT INTO `console_component_config` VALUES (525, -2, -115, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.12/flink_lib', NULL, 'deploymode$session', NULL, NULL, '2021-08-24 20:41:46', '2021-08-24 20:41:46', 0);
INSERT INTO `console_component_config` VALUES (527, -2, -115, 0, 'INPUT', 0, 'restart-strategy', 'failure-rate', NULL, 'deploymode$perjob', NULL, NULL, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO `console_component_config` VALUES (529, -2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.delay', '10s', NULL, 'deploymode$perjob', NULL, NULL, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO `console_component_config` VALUES (531, -2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.failure-rate-intervalattempts', '5 min', NULL, 'deploymode$perjob', NULL, NULL, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
INSERT INTO `console_component_config` VALUES (533, -2, -115, 0, 'INPUT', 0, 'restart-strategy.failure-rate.max-failures-per-interval', '3', NULL, 'deploymode$perjob', NULL, NULL, '2021-09-24 12:07:31', '2021-09-24 12:07:31', 0);
COMMIT;

-- ----------------------------
-- Table structure for console_kerberos
-- ----------------------------
DROP TABLE IF EXISTS `console_kerberos`;
CREATE TABLE `console_kerberos` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `cluster_id` int(11) NOT NULL COMMENT '集群id',
                                    `open_kerberos` tinyint(1) NOT NULL COMMENT '是否开启kerberos配置',
                                    `name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'kerberos文件名称',
                                    `remote_path` varchar(200) COLLATE utf8_bin NOT NULL COMMENT 'sftp存储路径',
                                    `principal` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'principal',
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                    `krb_name` varchar(26) COLLATE utf8_bin DEFAULT NULL COMMENT 'krb5_conf名称',
                                    `component_type` int(11) DEFAULT NULL COMMENT '组件类型',
                                    `principals` text COLLATE utf8_bin COMMENT 'keytab用户文件列表',
                                    `merge_krb_content` text COLLATE utf8_bin COMMENT '合并后的krb5',
                                    `component_version` varchar(25) COLLATE utf8_bin DEFAULT NULL COMMENT '组件版本',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for console_queue
-- ----------------------------
DROP TABLE IF EXISTS `console_queue`;
CREATE TABLE `console_queue` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                 `queue_name` varchar(126) COLLATE utf8_bin NOT NULL COMMENT '队列名称',
                                 `capacity` varchar(24) COLLATE utf8_bin NOT NULL COMMENT '最小容量',
                                 `max_capacity` varchar(24) COLLATE utf8_bin NOT NULL COMMENT '最大容量',
                                 `queue_state` varchar(24) COLLATE utf8_bin NOT NULL COMMENT '运行状态',
                                 `parent_queue_id` int(11) NOT NULL COMMENT '父队列id',
                                 `queue_path` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '队列路径',
                                 `cluster_id` int(11) DEFAULT NULL COMMENT '集群id',
                                 `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                 PRIMARY KEY (`id`),
                                 KEY `console_queue_cluster_id_index` (`cluster_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for datasource_classify
-- ----------------------------
DROP TABLE IF EXISTS `datasource_classify`;
CREATE TABLE `datasource_classify` (
                                       `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                       `classify_code` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '类型栏唯一编码',
                                       `sorted` int(5) NOT NULL DEFAULT '0' COMMENT '类型栏排序字段 默认从0开始',
                                       `classify_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '类型名称 包含全部和常用栏',
                                       `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
                                       `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                       `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       `create_user_id` int(11) DEFAULT '0',
                                       `modify_user_id` int(11) DEFAULT '0',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `classify_code` (`classify_code`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据源分类表';

-- ----------------------------
-- Records of datasource_classify
-- ----------------------------
BEGIN;
INSERT INTO `datasource_classify` VALUES (1, 'total', 100, '全部', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:42', 0, 0);
INSERT INTO `datasource_classify` VALUES (3, 'mostUse', 90, '常用', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (5, 'relational', 80, '关系型', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (7, 'bigData', 70, '大数据存储', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (9, 'mpp', 60, 'MPP', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (11, 'semiStruct', 50, '半结构化', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:43', 0, 0);
INSERT INTO `datasource_classify` VALUES (13, 'analytic', 40, '分析型', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO `datasource_classify` VALUES (15, 'NoSQL', 30, 'NoSQL', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO `datasource_classify` VALUES (17, 'actualTime', 20, '实时', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO `datasource_classify` VALUES (19, 'api', 0, '接口', 0, '2021-03-15 17:49:27', '2021-03-15 17:50:44', 0, 0);
INSERT INTO `datasource_classify` VALUES (21, 'sequential', 10, '时序', 0, '2021-06-09 17:19:27', '2021-06-09 17:19:27', 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for datasource_form_field
-- ----------------------------
DROP TABLE IF EXISTS `datasource_form_field`;
CREATE TABLE `datasource_form_field` (
                                         `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                         `name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '表单属性名称，同一模版表单中不重复',
                                         `label` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '属性前label名称',
                                         `widget` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '属性格式 如Input, Radio等',
                                         `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必填 0-非必填 1-必填',
                                         `invisible` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为隐藏 0-否 1-隐藏',
                                         `default_value` text COLLATE utf8_bin COMMENT '表单属性中默认值, 默认为空',
                                         `place_hold` text COLLATE utf8_bin COMMENT '输入框placeHold, 默认为空',
                                         `request_api` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '请求数据Api接口地址，一般用于关联下拉框类型，如果不需要请求则为空',
                                         `is_link` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为数据源需要展示的连接信息字段。0-否; 1-是',
                                         `valid_info` text COLLATE utf8_bin COMMENT '校验返回信息文案',
                                         `tooltip` text COLLATE utf8_bin COMMENT '输入框后问号的提示信息',
                                         `style` text COLLATE utf8_bin COMMENT '前端表单样式参数',
                                         `regex` text COLLATE utf8_bin COMMENT '正则校验表达式',
                                         `type_version` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '对应数据源版本信息',
                                         `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
                                         `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                         `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         `create_user_id` int(11) DEFAULT '0',
                                         `modify_user_id` int(11) DEFAULT '0',
                                         `options` varchar(256) COLLATE utf8_bin DEFAULT '' COMMENT 'select组件下拉内容',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `name` (`name`,`type_version`)
) ENGINE=InnoDB AUTO_INCREMENT=389 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据源表单属性表';

-- ----------------------------
-- Records of datasource_form_field
-- ----------------------------
BEGIN;
INSERT INTO `datasource_form_field` VALUES (1, 'dataName', '数据源名称', 'Input', 1, 0, NULL, NULL, NULL, 0, '{\"length\":{\"max\":128, \"message\":\"不得超过128个字符\"}}', NULL, NULL, NULL, 'common', 0, '2021-03-15 17:33:21', '2021-03-30 16:09:06', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (3, 'dataDesc', '描述', 'TextArea', 0, 0, NULL, NULL, NULL, 0, '{\"length\":{\"max\":200, \"message\":\"不得超过200个字符\"}}', NULL, NULL, NULL, 'common', 0, '2021-03-15 17:33:21', '2021-03-30 16:09:15', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (5, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:mysql://host:3306/dbName', NULL, '/jdbc:mysql:\\/\\/(.)+/', 'MySQL', 0, '2021-03-23 20:35:57', '2021-07-28 16:06:50', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (7, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'MySQL', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (9, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'MySQL', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (11, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:mysql://host:port/dbName', NULL, '/jdbc:mysql:\\/\\/(.)+/', 'PolarDB for MySQL8', 0, '2021-03-23 20:35:57', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (13, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'PolarDB for MySQL8', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (15, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'PolarDB for MySQL8', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (17, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', 'SID示例：jdbc:oracle:thin:@host:port:dbName\nServiceName示例：jdbc:oracle:thin:@//host:port/service_name', NULL, '/jdbc:oracle:thin:@(\\/\\/)?(.)+/', 'Oracle', 0, '2021-03-23 20:35:57', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (19, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Oracle', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (21, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Oracle', 0, '2021-03-23 20:35:57', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (23, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '', '示例：jdbc:sqlserver://localhost:1433;DatabaseName=dbName\n或\n jdbc:jtds:sqlserver://localhost:1433/dbName', NULL, '', 'SQL Server', 0, '2021-03-23 20:35:57', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (25, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'SQL Server', 0, '2021-03-23 20:35:57', '2021-04-02 16:50:08', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (27, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'SQL Server', 0, '2021-03-23 20:35:57', '2021-04-02 16:50:10', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (29, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:postgresql://host:port/database', NULL, '/jdbc:postgresql:\\/\\/(.)+/', 'PostgreSQL', 0, '2021-03-23 20:35:57', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (31, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'PostgreSQL', 0, '2021-03-23 20:35:57', '2021-04-02 16:50:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (33, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'PostgreSQL', 0, '2021-03-23 20:35:57', '2021-04-02 16:50:18', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (35, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:db2://host:port/dbName', NULL, '/jdbc:db2:\\/\\/(.)+/', 'DB2', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (37, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'DB2', 0, '2021-03-23 20:35:58', '2021-04-13 16:31:26', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (39, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'DB2', 0, '2021-03-23 20:35:58', '2021-04-13 16:31:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (41, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:dm://host:port/database', NULL, '/jdbc:dm:\\/\\/(.)+/', 'DMDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (43, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'DMDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (45, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'DMDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (47, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:kingbase8://host:port/database', NULL, '/jdbc:kingbase8:\\/\\/(.)+/', 'KingbaseES8', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (49, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'KingbaseES8', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (51, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'KingbaseES8', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (53, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:pivotal:greenplum://host:port;DatabaseName=database', NULL, '/jdbc:pivotal:greenplum:\\/\\/(.)+/', 'Greenplum', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (55, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Greenplum', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (57, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Greenplum', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (59, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:postgresql://host:port/database', NULL, '/jdbc:postgresql:\\/\\/(.)+/', 'GaussDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (61, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'GaussDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (63, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'GaussDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (65, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:gbase://host:port/dbName', NULL, '/jdbc:gbase:\\/\\/(.)+/', 'GBase_8a', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (67, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'GBase_8a', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (69, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'GBase_8a', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (71, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:clickhouse://<host>:<port>[/<database>]', NULL, '/jdbc:clickhouse:\\/\\/(.)+/', 'ClickHouse', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (73, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'ClickHouse', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (75, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'ClickHouse', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (77, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:mysql://host:port/dbName', NULL, '/jdbc:mysql:\\/\\/(.)+/', 'TiDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (79, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'TiDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (81, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'TiDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (83, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:mysql://host:port/dbName', NULL, '/jdbc:mysql:\\/\\/(.)+/', 'AnalyticDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (85, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'AnalyticDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (87, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'AnalyticDB', 0, '2021-03-23 20:35:58', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (89, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:hive://host:port/dbName', NULL, '/jdbc:(\\w|:)+:\\/\\/(.)+/', 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (91, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (93, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (95, 'defaultFS', 'defaultFS', 'Input', 1, 0, NULL, 'hdfs://host:port', NULL, 1, '', NULL, NULL, NULL, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (97, 'hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, '{\n\"dfs.nameservices\": \"defaultDfs\",\n\"dfs.ha.namenodes.defaultDfs\": \"namenode1\",\n\"dfs.namenode.rpc-address.defaultDfs.namenode1\": \"\",\n\"dfs.client.failover.proxy.provider.defaultDfs\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\"\n}', NULL, 0, '', '高可用模式下的填写规则：\n1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；\n2、所有参数以JSON格式填写；\n3、格式为：\n\"dfs.nameservices\": \"nameservice名称\", \"dfs.ha.namenodes.nameservice名称\": \"namenode名称，以逗号分隔\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.client.failover.proxy.provider.\nnameservice名称\": \"org.apache.hadoop.\nhdfs.server.namenode.ha.\nConfiguredFailoverProxyProvider\"\n4、详细参数含义请参考《帮助文档》或<a href=\'http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html\'>Hadoop官方文档</a>', NULL, NULL, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (99, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-1.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (101, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:hive2://host:port/dbName', NULL, '/jdbc:(\\w|:)+:\\/\\/(.)+/', 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (103, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (105, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (107, 'defaultFS', 'defaultFS', 'Input', 1, 0, NULL, 'hdfs://host:port', NULL, 1, '', NULL, NULL, NULL, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (109, 'hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, '{\n\"dfs.nameservices\": \"defaultDfs\",\n\"dfs.ha.namenodes.defaultDfs\": \"namenode1\",\n\"dfs.namenode.rpc-address.defaultDfs.namenode1\": \"\",\n\"dfs.client.failover.proxy.provider.defaultDfs\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\"\n}', NULL, 0, '', '高可用模式下的填写规则：\n1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；\n2、所有参数以JSON格式填写；\n3、格式为：\n\"dfs.nameservices\": \"nameservice名称\", \"dfs.ha.namenodes.nameservice名称\": \"namenode名称，以逗号分隔\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.client.failover.proxy.provider.\nnameservice名称\": \"org.apache.hadoop.\nhdfs.server.namenode.ha.\nConfiguredFailoverProxyProvider\"\n4、详细参数含义请参考《帮助文档》或<a href=\'http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html\'>Hadoop官方文档</a>', NULL, NULL, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (111, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-2.x', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (113, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:hive2://host:port/dbName', NULL, '/jdbc:hive2:\\/\\/(.)+/', 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (115, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (117, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (119, 'defaultFS', 'defaultFS', 'Input', 1, 0, NULL, 'hdfs://host:port', NULL, 1, '', NULL, NULL, NULL, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (121, 'hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, '{\n\"dfs.nameservices\": \"defaultDfs\",\n\"dfs.ha.namenodes.defaultDfs\": \"namenode1\",\n\"dfs.namenode.rpc-address.defaultDfs.namenode1\": \"\",\n\"dfs.client.failover.proxy.provider.defaultDfs\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\"\n}', NULL, 0, '', '高可用模式下的填写规则：\n1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；\n2、所有参数以JSON格式填写；\n3、格式为：\n\"dfs.nameservices\": \"nameservice名称\", \"dfs.ha.namenodes.nameservice名称\": \"namenode名称，以逗号分隔\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.client.failover.proxy.provider.\nnameservice名称\": \"org.apache.hadoop.\nhdfs.server.namenode.ha.\nConfiguredFailoverProxyProvider\"\n4、详细参数含义请参考《帮助文档》或<a href=\'http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html\'>Hadoop官方文档</a>', NULL, NULL, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (123, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'SparkThrift', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (125, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:impala://host:port/dbName', NULL, '/jdbc:impala:\\/\\/(.)+/', 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (127, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (129, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (131, 'defaultFS', 'defaultFS', 'Input', 1, 0, NULL, 'hdfs://host:port', NULL, 1, '', NULL, NULL, NULL, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (133, 'hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, '{\n\"dfs.nameservices\": \"defaultDfs\",\n\"dfs.ha.namenodes.defaultDfs\": \"namenode1\",\n\"dfs.namenode.rpc-address.defaultDfs.namenode1\": \"\",\n\"dfs.client.failover.proxy.provider.defaultDfs\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\"\n}', NULL, 0, '', '高可用模式下的填写规则：\n1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；\n2、所有参数以JSON格式填写；\n3、格式为：\n\"dfs.nameservices\": \"nameservice名称\", \"dfs.ha.namenodes.nameservice名称\": \"namenode名称，以逗号分隔\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.client.failover.proxy.provider.\nnameservice名称\": \"org.apache.hadoop.\nhdfs.server.namenode.ha.\nConfiguredFailoverProxyProvider\"\n4、详细参数含义请参考《帮助文档》或<a href=\'http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html\'>Hadoop官方文档</a>', NULL, NULL, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (135, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Impala', 0, '2021-03-23 20:37:30', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (137, 'accessId', 'AccessId', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Maxcompute', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (139, 'accessKey', 'AccessKey', 'Input', 1, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Maxcompute', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (141, 'project', 'Project Name', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Maxcompute', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (143, 'endPoint', 'End Point', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Maxcompute', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (145, 'host', '主机名/IP', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (147, 'port', '端口', 'Integer', 1, 0, NULL, 'FTP默认21, SFTP默认22', NULL, 1, '', NULL, NULL, NULL, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (149, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (151, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (153, 'ftpReact', 'ftpReact', 'FtpReact', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (155, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:hive2://host:port/dbName', NULL, '/jdbc:(\\w|:)+:\\/\\/(.)+/\n', 'CarbonData', 0, '2021-03-23 20:38:06', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (157, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'CarbonData', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (159, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'CarbonData', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (161, 'carbonReact', 'carbonReact', 'CarbonReact', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'CarbonData', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (163, 'hostPorts', '集群地址', 'TextArea', 1, 0, NULL, '集群地址，例如：IP1:Port,IP2:Port,IP3:Port3，多个IP地址用英文逗号隔开', NULL, 1, '', NULL, NULL, NULL, 'Kudu', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (165, 'others', '其他参数', 'TextAreaWithCopy', 0, 0, NULL, '输入JSON格式的参数，示例及默认参数如下：\n{\n    \"openKerberos\":false,\n    \"user\":\"\",\n    \"keytabPath\":\"\",\n    \"workerCount\":4,\n    \"bossCount\":1,\n    \"operationTimeout\":30000,\n    \"adminOperationTimeout\":30000\n}', NULL, 0, '', NULL, NULL, NULL, 'Kudu', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (167, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Kudu', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (169, 'authURL', 'RESTful URL', 'Input', 1, 0, NULL, 'http://ip:port', NULL, 1, '', '访问Kylin的认证地址，格式为：http://host:7000', NULL, '/http:\\/\\/([\\w, .])+:(.)+/', 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (171, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (173, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (175, 'project', 'Project', 'Input', 1, 0, NULL, 'DEFAULT', NULL, 0, '', NULL, NULL, NULL, 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (177, 'config', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, '{\n    \"socketTimeout\":10000,\n    \"connectTimeout\":10000\n}', NULL, 0, '', NULL, NULL, NULL, 'Kylin URL-3.x', 0, '2021-03-23 20:38:06', '2021-07-28 16:06:35', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (179, 'hbase_quorum', '集群地址', 'TextArea', 1, 0, NULL, '集群地址，例如：IP1:Port,IP2:Port,IP3:Port', NULL, 1, '', NULL, NULL, NULL, 'HBase-1.x', 0, '2021-03-23 20:38:06', '2021-07-28 15:33:28', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (181, 'hbase_parent', '根目录', 'Input', 0, 0, NULL, 'ZooKeeper中hbase创建的根目录，例如：/hbase', NULL, 0, '', NULL, NULL, NULL, 'HBase-1.x', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (183, 'hbase_other', '其他参数', 'TextArea', 0, 0, NULL, 'hbase.rootdir\": \"hdfs: //ip:9000/hbase', NULL, 0, '', NULL, NULL, NULL, 'HBase-1.x', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (185, 'openKerberos', '开启Kerberos认证', 'HbaseKerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'HBase-1.x', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (187, 'hbase_quorum', '集群地址', 'TextArea', 1, 0, NULL, '集群地址，例如：IP1:Port,IP2:Port,IP3:Port', NULL, 1, '', NULL, NULL, NULL, 'HBase-2.x', 0, '2021-03-23 20:38:07', '2021-07-28 15:33:28', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (189, 'hbase_parent', '根目录', 'Input', 0, 0, NULL, 'ZooKeeper中hbase创建的根目录，例如：/hbase', NULL, 0, '', NULL, NULL, NULL, 'HBase-2.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (191, 'hbase_other', '其他参数', 'TextArea', 0, 0, NULL, 'hbase.rootdir\": \"hdfs: //ip:9000/hbase', NULL, 0, '', NULL, NULL, NULL, 'HBase-2.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (193, 'openKerberos', '开启Kerberos认证', 'HbaseKerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'HBase-2.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (195, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:phoenix:zk1,zk2,zk3:port/hbase2', NULL, '/jdbc:phoenix:(.)+/', 'Phoenix-4.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (197, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Phoenix-4.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (199, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:phoenix:zk1,zk2,zk3:port/hbase2', NULL, '/jdbc:phoenix:(.)+/', 'Phoenix-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (201, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Phoenix-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (203, 'address', '集群地址', 'TextArea', 1, 0, NULL, '集群地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接', NULL, 1, '', NULL, NULL, NULL, 'Elasticsearch-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (205, 'clusterName', '集群名称', 'Input', 0, 0, NULL, '请输入集群名称', NULL, 0, '{\"length\":{\"max\":128, \"message\":\"不得超过128个字符\"}}', NULL, NULL, NULL, 'Elasticsearch-5.x', 0, '2021-03-23 20:38:07', '2021-07-28 16:05:37', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (207, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Elasticsearch-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (209, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Elasticsearch-5.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (211, 'address', '集群地址', 'TextArea', 1, 0, NULL, '集群地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接', NULL, 1, '', NULL, NULL, NULL, 'Elasticsearch-6.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (213, 'clusterName', '集群名称', 'Input', 0, 0, NULL, '请输入集群名称', NULL, 0, '{\"length\":{\"max\":128, \"message\":\"不得超过128个字符\"}}', NULL, NULL, NULL, 'Elasticsearch-6.x', 0, '2021-03-23 20:38:07', '2021-07-28 16:05:37', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (215, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Elasticsearch-6.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (217, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Elasticsearch-6.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (219, 'address', '集群地址', 'TextArea', 1, 0, NULL, '集群地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接', NULL, 1, '', NULL, NULL, NULL, 'Elasticsearch-7.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (221, 'clusterName', '集群名称', 'Input', 0, 0, NULL, '请输入集群名称', NULL, 0, '{\"length\":{\"max\":128, \"message\":\"不得超过128个字符\"}}', NULL, NULL, NULL, 'Elasticsearch-7.x', 0, '2021-03-23 20:38:07', '2021-07-28 16:05:37', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (223, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Elasticsearch-7.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (225, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Elasticsearch-7.x', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (227, 'hostPorts', '集群地址', 'TextArea', 1, 0, NULL, 'MongoDB集群地址，例如：IP1:Port,IP2:Port,IP3:Port', NULL, 1, '', NULL, NULL, NULL, 'MongoDB', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (229, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'MongoDB', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (231, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'MongoDB', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (233, 'database', '数据库', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'MongoDB', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (235, 'redisReact', 'redisReact', 'RedisReact', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Redis', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (237, 'hostname', 'hostname', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'S3', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (239, 'accessKey', 'AccessKey', 'Input', 1, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'S3', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (241, 'secretKey', 'SecretKey', 'Input', 1, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'S3', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (243, 'address', '集群地址', 'TextArea', 0, 1, NULL, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-1.x', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:38', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (245, 'brokerList', 'broker地址', 'TextArea', 1, 1, NULL, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-1.x', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:39', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (247, 'address', '集群地址', 'TextArea', 0, 1, NULL, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-2.x', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:40', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (249, 'brokerList', 'broker地址', 'TextArea', 1, 1, NULL, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-2.x', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:41', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (251, 'address', '集群地址', 'TextArea', 0, 1, NULL, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-0.9', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:41', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (253, 'brokerList', 'broker地址', 'TextArea', 1, 1, NULL, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-0.9', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:43', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (255, 'address', '集群地址', 'TextArea', 0, 1, NULL, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-0.10', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:43', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (257, 'brokerList', 'broker地址', 'TextArea', 1, 1, NULL, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-0.10', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:45', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (259, 'address', '集群地址', 'TextArea', 0, 1, NULL, '请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-0.11', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:45', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (261, 'brokerList', 'broker地址', 'TextArea', 1, 1, NULL, 'Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录', NULL, 1, '', NULL, NULL, NULL, 'Kafka-0.11', 0, '2021-03-23 20:38:07', '2021-04-13 17:37:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (263, 'address', 'Broker URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'EMQ', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (265, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'EMQ', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (267, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'EMQ', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (269, 'url', 'URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '', '多个鉴权参数将以”&“连接拼接在URL后，例如：ws://host:port/test?Username=sanshui&password=xx', NULL, NULL, 'WebSocket', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (271, 'webSocketParams', '鉴权参数', 'WebSocketSub', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'WebSocket', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (273, 'url', 'URL', 'Input', 1, 0, NULL, 'host:port', NULL, 1, '', NULL, NULL, NULL, 'Socket', 0, '2021-03-23 20:38:07', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (275, 'protocol', 'Protocol', 'Input', 1, 1, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'FTP', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (277, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:presto://host:port/dbName', NULL, '/jdbc:presto:\\/\\/(.)+/', 'Presto', 0, '2021-04-07 14:47:08', '2021-04-07 14:47:14', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (279, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'Presto', 0, '2021-04-07 14:47:08', '2021-04-07 14:47:14', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (281, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, 'Presto', 0, '2021-04-07 14:47:08', '2021-04-07 14:47:14', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (283, 'hostPort', '地址', 'TextArea', 1, 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'Redis', 0, '2021-04-15 19:48:41', '2021-04-15 19:48:48', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (285, 'database', '数据库', 'Input', 1, 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'Redis', 0, '2021-04-15 19:48:41', '2021-04-15 19:48:48', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (287, 'zkHost', '集群地址', 'TextArea', 1, 0, NULL, '集群地址，例如:ip1:port,ip2:port,ip3:port', NULL, 1, NULL, NULL, NULL, NULL, 'Solr-7.x', 0, '2021-05-28 14:07:00', '2021-05-28 14:07:00', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (289, 'chroot', 'chroot', 'Input', 1, 0, NULL, '请输入Zookeeper chroot路径,例如/Solr', NULL, 1, NULL, NULL, NULL, NULL, 'Solr-7.x', 0, '2021-05-28 14:07:00', '2021-05-28 14:07:00', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (291, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Solr-7.x', 0, '2021-03-23 20:37:29', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (293, 'kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, NULL, '', NULL, 0, '', NULL, NULL, NULL, 'Kafka-0.9', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (295, 'kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, NULL, '', NULL, 0, '', NULL, NULL, NULL, 'Kafka-0.10', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (297, 'kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, NULL, '', NULL, 0, '', NULL, NULL, NULL, 'Kafka-0.11', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (299, 'kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, NULL, '', NULL, 0, '', NULL, NULL, NULL, 'Kafka-1.x', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (301, 'kafkaReact', 'kafkaReact', 'KafkaReact', 0, 0, NULL, '', NULL, 0, '', NULL, NULL, NULL, 'Kafka-2.x', 0, '2021-06-01 11:50:07', '2021-06-01 11:50:07', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (303, 'url', 'URL', 'Input', 1, 0, NULL, 'http://localhost:8086', NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', NULL, NULL, '/http:\\/\\/([\\w, .])+:(.)+/', 'InfluxDB-1.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (305, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'InfluxDB-1.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (307, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'InfluxDB-1.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (309, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:hive://host:port/dbName', NULL, '/jdbc:(\\w|:)+:\\/\\/(.)+/', 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (311, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (313, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (315, 'defaultFS', 'defaultFS', 'Input', 1, 0, NULL, 'hdfs://host:port', NULL, 1, '', NULL, NULL, NULL, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (317, 'hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, '{\n\"dfs.nameservices\": \"defaultDfs\",\n\"dfs.ha.namenodes.defaultDfs\": \"namenode1\",\n\"dfs.namenode.rpc-address.defaultDfs.namenode1\": \"\",\n\"dfs.client.failover.proxy.provider.defaultDfs\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\"\n}', NULL, 0, '', '高可用模式下的填写规则：\n1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；\n2、所有参数以JSON格式填写；\n3、格式为：\n\"dfs.nameservices\": \"nameservice名称\", \"dfs.ha.namenodes.nameservice名称\": \"namenode名称，以逗号分隔\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.client.failover.proxy.provider.\nnameservice名称\": \"org.apache.hadoop.\nhdfs.server.namenode.ha.\nConfiguredFailoverProxyProvider\"\n4、详细参数含义请参考《帮助文档》或<a href=\'http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html\'>Hadoop官方文档</a>', NULL, NULL, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (319, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Hive-3.x', 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (321, 'region', 'Region', 'Select', 1, 0, NULL, '', NULL, 0, '', NULL, NULL, NULL, 'AWS S3', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '[{key:\'1\',value:\'cn-north-1\',label:\'cn-north-1 (北京)\'},{key:\'2\',value:\'cn-northwest-1\',label:\'cnnorthwest-1 (宁夏)\'}]');
INSERT INTO `datasource_form_field` VALUES (323, 'accessKey', 'ACCESS KEY', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'AWS S3', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (325, 'secretKey', 'SECRET KEY', 'Password', 1, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'AWS S3', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (327, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:hive2://host:port/dbName', NULL, '/jdbc:(\\w|:)+:\\/\\/(.)+/', 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (329, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (331, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (333, 'defaultFS', 'defaultFS', 'Input', 1, 0, NULL, 'hdfs://host:port', NULL, 1, '', NULL, NULL, NULL, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (335, 'metaStoreUris', 'hive.metastore.uris', 'Input', 0, 0, NULL, '', NULL, 0, '', 'hive.metastore.uris仅在做事务表的写同步时必填', NULL, NULL, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (337, 'hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, '{\n\"dfs.nameservices\": \"defaultDfs\",\n\"dfs.ha.namenodes.defaultDfs\": \"namenode1\",\n\"dfs.namenode.rpc-address.defaultDfs.namenode1\": \"\",\n\"dfs.client.failover.proxy.provider.defaultDfs\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\"\n}', NULL, 0, '', '高可用模式下的填写规则：\n1、分别要填写：nameservice名称、 namenode名称（多个以逗号分隔）、proxy.provider参数；\n2、所有参数以JSON格式填写；\n3、格式为：\n\"dfs.nameservices\": \"nameservice名称\", \"dfs.ha.namenodes.nameservice名称\": \"namenode名称，以逗号分隔\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.namenode.rpc-address.nameservice名称.namenode名称\": \"\", \"dfs.client.failover.proxy.provider.\nnameservice名称\": \"org.apache.hadoop.\nhdfs.server.namenode.ha.\nConfiguredFailoverProxyProvider\"\n4、详细参数含义请参考《帮助文档》或<a href=\'http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html\'>Hadoop官方文档</a>', NULL, NULL, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (339, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Inceptor', 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (341, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:postgresql://host:port/database', NULL, '/jdbc:postgresql:\\/\\/(.)+/', 'AnalyticDB PostgreSQL', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (343, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'AnalyticDB PostgreSQL', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (345, 'password', '密码', 'Password', 1, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'AnalyticDB PostgreSQL', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (347, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:vertica://host:port/dbName', NULL, '/jdbc:vertica:\\/\\/(.)+/', 'Vertica', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (349, 'username', '用户名', 'Input', 1, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Vertica', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (351, 'password', '密码', 'Password', 1, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Vertica', 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (353, 'url', 'URL', 'Input', 1, 0, NULL, 'http://localhost:4242', NULL, 1, '{\"regex\":{\"message\":\"URL格式不符合规则!\"}}', NULL, NULL, '/http:\\/\\/([\\w, .])+:(.)+/', 'OpenTSDB-2.x', 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (355, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:mysql://host:3306/dbName', NULL, '/jdbc:mysql:\\/\\/(.)+/', 'Doris-0.14.x', 0, '2021-07-06 09:35:57', '2021-07-06 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (357, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Doris-0.14.x', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:08', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (359, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Doris-0.14.x', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:12', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (361, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:kylin://host:7070/project_name', NULL, '/jdbc:kylin:\\/\\/(.)+/', 'Kylin JDBC-3.x', 0, '2021-07-06 09:35:57', '2021-07-06 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (363, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'Kylin JDBC-3.x', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:08', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (365, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'Kylin JDBC-3.x', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:12', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (367, 'jdbcUrl', 'JDBC URL', 'Input', 1, 0, NULL, NULL, NULL, 1, '{\"regex\":{\"message\":\"JDBC URL格式不符合规则!\"}}', '示例：jdbc:sqlserver://localhost:1433;DatabaseName=dbName', NULL, '/jdbc:sqlserver:\\/\\/(.)+/', 'SQLServer JDBC', 0, '2021-07-06 09:35:57', '2021-07-06 16:07:17', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (369, 'username', '用户名', 'Input', 0, 0, NULL, NULL, NULL, 1, '', NULL, NULL, NULL, 'SQLServer JDBC', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:08', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (371, 'password', '密码', 'Password', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'SQLServer JDBC', 0, '2021-07-06 09:35:57', '2021-07-06 10:08:12', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (373, 'defaultFS', 'defaultFS', 'Input', 1, 0, NULL, 'hdfs://host:port', NULL, 1, '', NULL, NULL, NULL, 'HDFS-2.x', 0, '2021-03-23 20:38:06', '2021-09-17 13:51:06', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (375, 'hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'HDFS-2.x', 0, '2021-03-23 20:38:06', '2021-09-17 13:51:06', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (377, 'openKerberos', '开启Kerberos认证', 'Kerberos', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'HDFS-2.x', 0, '2021-03-23 20:38:06', '2021-09-17 13:51:06', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (379, 'defaultFS', 'defaultFS', 'Input', 1, 0, NULL, 'hdfs://host:port', NULL, 1, '', NULL, NULL, NULL, 'HDFS-TBDS', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (381, 'hadoopConfig', '高可用配置', 'TextAreaWithCopy', 0, 0, NULL, NULL, NULL, 0, '', NULL, NULL, NULL, 'HDFS-TBDS', 0, '2021-03-23 20:38:06', '2021-03-30 16:08:46', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (383, 'tbds_id', 'ID', 'Input', 1, 0, NULL, '请输入ID', NULL, 0, '', NULL, NULL, NULL, 'HDFS-TBDS', 0, '2021-09-17 10:38:27', '2021-09-17 10:38:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (385, 'tbds_key', 'KEY', 'Input', 1, 0, NULL, '请输入KEY', NULL, 0, '', NULL, NULL, NULL, 'HDFS-TBDS', 0, '2021-09-17 10:38:27', '2021-09-17 10:38:27', 0, 0, '');
INSERT INTO `datasource_form_field` VALUES (387, 'tbds_username', 'USERNAME', 'Input', 1, 0, NULL, '请输入username', NULL, 0, '', NULL, NULL, NULL, 'HDFS-TBDS', 0, '2021-10-15 10:38:27', '2021-10-15 10:38:27', 0, 0, '');
COMMIT;

-- ----------------------------
-- Table structure for datasource_info
-- ----------------------------
DROP TABLE IF EXISTS `datasource_info`;
CREATE TABLE `datasource_info` (
                                   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                   `data_type` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '数据源类型唯一 如Mysql, Oracle, Hive',
                                   `data_version` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '数据源版本 如1.x, 0.9, 创建下的实例可能会没有版本号',
                                   `data_name` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '数据源名称',
                                   `data_desc` text COLLATE utf8_bin COMMENT '数据源描述',
                                   `link_json` text COLLATE utf8_bin COMMENT '数据源连接信息, 不同数据源展示连接信息不同, 保存为json',
                                   `data_json` text COLLATE utf8_bin COMMENT '数据源填写的表单信息, 保存为json, key键要与表单的name相同',
                                   `status` tinyint(4) NOT NULL COMMENT '连接状态 0-连接失败, 1-正常',
                                   `is_meta` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有meta标志 0-否 1-是',
                                   `tenant_id` int(11) NOT NULL COMMENT '租户主键id **可能不是id 其他唯一凭证',
                                   `data_type_code` tinyint(4) NOT NULL DEFAULT '0' COMMENT '数据源类型编码',
                                   `schema_name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '数据源schemaName',
                                   `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `create_user_id` int(11) DEFAULT '0',
                                   `modify_user_id` int(11) DEFAULT '0',
                                   PRIMARY KEY (`id`),
                                   KEY `MODIFY_TIME` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据源详细信息表';

-- ----------------------------
-- Table structure for datasource_type
-- ----------------------------
DROP TABLE IF EXISTS `datasource_type`;
CREATE TABLE `datasource_type` (
                                   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                   `data_type` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '数据源类型唯一 如Mysql, Oracle, Hive',
                                   `data_classify_id` int(11) NOT NULL COMMENT '数据源分类栏主键id',
                                   `weight` decimal(20,1) NOT NULL DEFAULT '0.0' COMMENT '数据源权重',
                                   `img_url` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '数据源logo图片地址',
                                   `sorted` int(5) NOT NULL DEFAULT '0' COMMENT '数据源类型排序字段, 默认从0开始',
                                   `invisible` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否可见',
                                   `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `create_user_id` int(11) DEFAULT '0',
                                   `modify_user_id` int(11) DEFAULT '0',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `data_type` (`data_type`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据源类型信息表';

-- ----------------------------
-- Records of datasource_type
-- ----------------------------
BEGIN;
INSERT INTO `datasource_type` VALUES (1, 'MySQL', 3, 6.5, 'MySQL.png', 2500, 0, 0, '2021-03-15 17:50:44', '2022-02-11 18:51:23', 0, 0);
INSERT INTO `datasource_type` VALUES (3, 'PolarDB for MySQL8', 3, 0.0, 'PolarDB for MySQL8.png', 2450, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (5, 'Oracle', 3, 1.5, 'Oracle.png', 2400, 0, 0, '2021-03-15 17:50:44', '2021-09-17 15:46:39', 0, 0);
INSERT INTO `datasource_type` VALUES (7, 'SQL Server', 3, 0.0, 'SQLServer.png', 2350, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (9, 'PostgreSQL', 3, 1.0, 'PostgreSQL.png', 2300, 0, 0, '2021-03-15 17:50:44', '2021-09-17 16:55:58', 0, 0);
INSERT INTO `datasource_type` VALUES (11, 'DB2', 3, 0.0, 'DB2.png', 2250, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (13, 'DMDB', 3, 0.0, 'DMDB.png', 2200, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (15, 'KingbaseES8', 3, 0.0, 'KingbaseES8.png', 2100, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (17, 'Hive', 4, 4.5, 'Hive.png', 2050, 0, 0, '2021-03-15 17:50:44', '2022-02-10 15:18:13', 0, 0);
INSERT INTO `datasource_type` VALUES (19, 'SparkThrift', 4, 5.0, 'SparkThrift.png', 2000, 0, 0, '2021-03-15 17:50:44', '2022-02-11 11:20:06', 0, 0);
INSERT INTO `datasource_type` VALUES (21, 'Maxcompute', 4, 0.0, 'Maxcompute.png', 1950, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (23, 'Phoenix', 4, 0.0, 'Phoenix.png', 1900, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (25, 'Greenplum', 5, 0.0, 'Greenplum.png', 1850, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (27, 'GaussDB', 5, 0.0, 'GaussDB.png', 1800, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (29, 'GBase_8a', 5, 0.0, 'GBase_8a.png', 1750, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (31, 'HDFS', 6, 0.0, 'HDFS.png', 1700, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (33, 'FTP', 6, 0.0, 'FTP.png', 1650, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (35, 'S3', 6, 0.0, 'S3.png', 1600, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (37, 'Impala', 7, 0.0, 'Impala.png', 1550, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (39, 'ClickHouse', 7, 0.0, 'ClickHouse.png', 1500, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (41, 'TiDB', 7, 0.0, 'TiDB.png', 1450, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (43, 'Kudu', 7, 0.0, 'Kudu.png', 1400, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (45, 'AnalyticDB', 7, 0.0, 'AnalyticDB.png', 1350, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (47, 'CarbonData', 7, 0.0, 'CarbonData.png', 1300, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (49, 'Kylin URL', 7, 0.0, 'Kylin.png', 1250, 0, 0, '2021-03-15 17:50:44', '2021-07-28 16:06:35', 0, 0);
INSERT INTO `datasource_type` VALUES (51, 'HBase', 8, 0.0, 'HBase.png', 1200, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (53, 'Elasticsearch', 8, 0.0, 'Elasticsearch.png', 1150, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (55, 'MongoDB', 8, 0.0, 'MongoDB.png', 1050, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (57, 'Redis', 8, 0.0, 'Redis.png', 1000, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (59, 'Kafka', 9, 0.5, 'Kafka.png', 950, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (61, 'EMQ', 9, 0.0, 'EMQ.png', 900, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (63, 'WebSocket', 10, 0.0, 'WebSocket.png', 850, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (65, 'Socket', 10, 0.0, 'Socket.png', 800, 0, 0, '2021-03-15 17:50:44', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (67, 'Presto', 1, 0.0, NULL, 750, 1, 0, '2021-03-24 12:01:00', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (69, 'Solr', 8, 0.0, 'Solr.png', 1100, 0, 0, '2021-05-28 11:18:00', '2021-05-28 11:18:00', 0, 0);
INSERT INTO `datasource_type` VALUES (71, 'InfluxDB', 11, 0.0, 'InfluxDB.png', 875, 0, 0, '2021-06-09 14:49:27', '2021-07-28 16:05:37', 0, 0);
INSERT INTO `datasource_type` VALUES (73, 'AWS S3', 6, 0.0, 'AWS S3.png', 1575, 0, 0, '2021-06-21 19:48:27', '2021-06-21 19:48:27', 0, 0);
INSERT INTO `datasource_type` VALUES (75, 'Inceptor', 4, 0.0, 'Inceptor.png', 1875, 0, 0, '2021-06-21 22:07:27', '2021-06-21 22:07:27', 0, 0);
INSERT INTO `datasource_type` VALUES (77, 'AnalyticDB PostgreSQL', 7, 0.0, 'ADB_PostgreSQL.png', 1220, 0, 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0);
INSERT INTO `datasource_type` VALUES (79, 'Vertica', 5, 0.0, 'Vertica.png', 1720, 0, 0, '2021-06-01 12:22:10', '2021-06-01 12:22:10', 0, 0);
INSERT INTO `datasource_type` VALUES (81, 'OpenTSDB', 11, 0.0, 'OpenTSDB.png', 862, 0, 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0);
INSERT INTO `datasource_type` VALUES (83, 'Doris', 7, 0.0, 'Doris.png', 1200, 0, 0, '2021-07-06 12:22:10', '2021-07-06 15:49:09', 0, 0);
INSERT INTO `datasource_type` VALUES (85, 'Kylin JDBC', 7, 0.0, 'Kylin.png', 1300, 0, 0, '2021-07-06 12:22:10', '2021-07-06 15:49:09', 0, 0);
INSERT INTO `datasource_type` VALUES (87, 'SQLServer JDBC', 3, 0.0, 'SQLServer.png', 1200, 0, 0, '2021-07-06 12:22:10', '2021-07-06 15:49:09', 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for datasource_version
-- ----------------------------
DROP TABLE IF EXISTS `datasource_version`;
CREATE TABLE `datasource_version` (
                                      `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                      `data_type` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '数据源类型唯一 如Mysql, Oracle, Hive',
                                      `data_version` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '数据源版本 如1.x, 0.9',
                                      `sorted` int(5) NOT NULL DEFAULT '0' COMMENT '版本排序字段,高版本排序,默认从0开始',
                                      `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除,1删除，0未删除',
                                      `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                      `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      `create_user_id` int(11) DEFAULT '0',
                                      `modify_user_id` int(11) DEFAULT '0',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `data_type` (`data_type`,`data_version`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据源版本表';

-- ----------------------------
-- Records of datasource_version
-- ----------------------------
BEGIN;
INSERT INTO `datasource_version` VALUES (1, 'Hive', '1.x', 0, 0, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO `datasource_version` VALUES (3, 'Hive', '2.x', 1, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:16', 0, 0);
INSERT INTO `datasource_version` VALUES (5, 'HBase', '1.x', 0, 0, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO `datasource_version` VALUES (7, 'HBase', '2.x', 1, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:38', 0, 0);
INSERT INTO `datasource_version` VALUES (9, 'Phoenix', '4.x', 0, 0, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO `datasource_version` VALUES (11, 'Phoenix', '5.x', 1, 0, '2021-03-15 17:51:55', '2021-04-01 14:43:21', 0, 0);
INSERT INTO `datasource_version` VALUES (13, 'Elasticsearch', '5.x', 0, 0, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO `datasource_version` VALUES (15, 'Elasticsearch', '6.x', 1, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:24', 0, 0);
INSERT INTO `datasource_version` VALUES (17, 'Elasticsearch', '7.x', 2, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:24', 0, 0);
INSERT INTO `datasource_version` VALUES (19, 'Kafka', '1.x', 3, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:54', 0, 0);
INSERT INTO `datasource_version` VALUES (21, 'Kafka', '2.x', 4, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:49', 0, 0);
INSERT INTO `datasource_version` VALUES (23, 'Kafka', '0.9', 0, 1, '2021-03-15 17:51:55', '2021-03-15 17:53:11', 0, 0);
INSERT INTO `datasource_version` VALUES (25, 'Kafka', '0.10', 1, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:56', 0, 0);
INSERT INTO `datasource_version` VALUES (27, 'Kafka', '0.11', 2, 0, '2021-03-15 17:51:55', '2021-03-15 17:54:55', 0, 0);
INSERT INTO `datasource_version` VALUES (29, 'Solr', '7.x', 0, 0, '2021-05-28 14:48:00', '2021-05-28 14:48:00', 0, 0);
INSERT INTO `datasource_version` VALUES (31, 'InfluxDB', '1.x', 0, 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0);
INSERT INTO `datasource_version` VALUES (33, 'Hive', '3.x', 2, 0, '2021-06-09 14:49:27', '2021-06-09 14:49:42', 0, 0);
INSERT INTO `datasource_version` VALUES (35, 'OpenTSDB', '2.x', 0, 0, '2021-07-06 10:37:27', '2021-07-06 10:37:42', 0, 0);
INSERT INTO `datasource_version` VALUES (37, 'Doris', '0.14.x', 0, 0, '2021-07-06 14:49:27', '2021-07-06 14:49:42', 0, 0);
INSERT INTO `datasource_version` VALUES (39, 'Kylin URL', '3.x', 0, 0, '2021-07-06 14:49:27', '2021-07-06 14:49:42', 0, 0);
INSERT INTO `datasource_version` VALUES (41, 'Kylin JDBC', '3.x', 0, 0, '2021-07-06 14:49:27', '2021-07-06 14:49:42', 0, 0);
INSERT INTO `datasource_version` VALUES (43, 'HDFS', '2.x', 0, 0, '2022-02-17 15:51:08', '2022-02-17 15:51:08', 0, 0);
INSERT INTO `datasource_version` VALUES (45, 'HDFS', 'TBDS', 1, 0, '2022-02-17 15:51:08', '2022-02-17 15:51:08', 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for develop_catalogue
-- ----------------------------
DROP TABLE IF EXISTS `develop_catalogue`;
CREATE TABLE `develop_catalogue` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                     `node_name` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '文件夹名称',
                                     `node_pid` int(11) NOT NULL DEFAULT '-1' COMMENT '父文件夹id -1:没有上级目录',
                                     `order_val` int(3) DEFAULT NULL,
                                     `level` tinyint(1) NOT NULL DEFAULT '3' COMMENT '目录层级 0:一级 1:二级 n:n+1级',
                                     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `create_user_id` int(11) NOT NULL COMMENT '创建用户',
                                     `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                     `catalogue_type` tinyint(1) DEFAULT '0' COMMENT '目录类型 0任务目录 1 项目目录',
                                     PRIMARY KEY (`id`),
                                     KEY `index_catalogue_name` (`node_pid`,`node_name`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='文件夹、目录表';

-- ----------------------------
-- Records of develop_catalogue
-- ----------------------------
BEGIN;
INSERT INTO `develop_catalogue` VALUES (1, -1, '系统函数', -1, 3, 1, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (3, -1, '数学函数', 1, NULL, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (5, -1, '集合函数', 1, NULL, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (7, -1, '日期函数', 1, NULL, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (9, -1, '其它函数', 1, NULL, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (11, -1, '字符函数', 1, NULL, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (13, -1, '聚合函数', 1, NULL, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (15, -1, '表生成函数', 1, NULL, 2, '2022-02-12 23:33:10', '2022-02-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (17, -1, 'Flink系统函数', 0, 3, 1, '2022-05-03 22:20:58', '2022-05-03 22:20:58', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (19, -1, '数学函数', 17, 3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (21, -1, '日期函数', 17, 3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (23, -1, '字符函数', 17, 3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (25, -1, '聚合函数', 17, 3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0);
INSERT INTO `develop_catalogue` VALUES (27, -1, '其它函数', 17, 3, 1, '2022-04-12 23:33:10', '2022-04-12 23:33:10', -1, 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for develop_function
-- ----------------------------
DROP TABLE IF EXISTS `develop_function`;
CREATE TABLE `develop_function` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `name` varchar(512) COLLATE utf8_bin NOT NULL COMMENT '函数名称',
                                    `class_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT 'main函数类名',
                                    `purpose` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '函数用途',
                                    `command_formate` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '函数命令格式',
                                    `param_desc` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '函数参数说明',
                                    `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
                                    `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                    `create_user_id` int(11) NOT NULL COMMENT '创建的用户',
                                    `modify_user_id` int(11) NOT NULL COMMENT '创建的用户',
                                    `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0自定义 1系统',
                                    `udf_type` int(11) DEFAULT NULL COMMENT '函数类型',
                                    `task_type` int(11) NOT NULL DEFAULT '0' COMMENT '0: SparkSQL ',
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                    `sql_text` text COLLATE utf8_bin COMMENT 'sql文本',
                                    PRIMARY KEY (`id`),
                                    KEY `index_develop_function` (`name`(128))
) ENGINE=InnoDB AUTO_INCREMENT=383 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='函数管理表';

-- ----------------------------
-- Records of develop_function
-- ----------------------------
BEGIN;
INSERT INTO `develop_function` VALUES (1, 'round', '', '取近似值', 'double  round(double a)  double  round(double a, int d)', 'round(double a)：返回对a四舍五入的bigint值  round(double a, int d)：返回double型a的保留d位小数的double型的近似值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (3, 'bround', '', '银行家舍入法', 'double  bround(double a)  double  bround(double a, int d)', 'bround(double a)：1~4：舍，6~9：进，5->前位数是偶：舍，5->前位数是奇：进  bround(double a, int d)：保留d位小数', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (5, 'floor', '', '向下取整', 'bigint  floor(double a)', '，在数轴上最接近要求的值的左边的值 ?如：6.10->6 ? -3.4->-4', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (7, 'ceil', '', '取整', 'bigint  ceil(double a)', '求其不小于小给定实数的最小整数如：ceil(6) = ceil(6.1)= ceil(6.9) = 6', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (9, 'rand', '', '取随机数', 'double  rand()  double  rand(int seed)', '返回一个0到1范围内的随机数。如果指定种子seed，则会等到一个稳定的随机数序列', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (11, 'exp', '', '计算自然指数的指数', 'double  exp(double a)  double  exp(decimal a)', '返回自然对数e的a幂次方， a可为小数', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (13, 'ln', '', '计算自然数的对数', 'double  ln(double a)  double  ln(decimal a)', '以自然数为底d的对数，a可为小数', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (15, 'log10', '', '计算10为底的对数', 'double  log10(double a)  double  log10(decimal a)', '计算以10为底d的对数，a可为小数', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (17, 'log2', '', '计算2为底数的对数', 'double  log2(double a)  double  log2(decimal a)', '以2为底数d的对数，a可为小数', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (19, 'log', '', '计算对数', 'double  log(double base, double a)  double  log(decimal base, decimal a)', '以base为底的对数，base 与 a都是double类型', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (21, 'pow', '', '计算次幂', 'double  pow(double a, double p)', '计算a的p次幂', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (23, 'sqrt', '', '计算平方根', 'double  sqrt(double a)  double  sqrt(decimal a)', '计算a的平方根', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (25, 'bin', '', '计算二进制a的string类型，a为bigint类型', 'string  bin(bigint a)', '计算二进制a的string类型，a为bigint类型', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (27, 'hex', '', '计算十六进制的string类型', 'string  hex(bigint a)   string  hex(string a)   string  hex(binary a)', '计算十六进制a的string类型，如果a为string类型就转换成字符相对应的十六进制', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (29, 'unhex', '', 'hex的逆方法', 'binary  unhex(string a)', 'hex的逆方法', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (31, 'conv', '', '进制转换', 'string  conv(bigint num, int from_base, int to_base)  string  conv(string num, int from_base, int to_base)', '将bigint/string类型的num从from_base进制转换成to_base进制', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (33, 'abs', '', '计算a的绝对值', 'double  abs(double a)', '计算a的绝对值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (35, 'pmod', '', 'a对b取模', 'int  pmod(int a, int b),   double  pmod(double a, double b)', 'a对b取模', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (37, 'sin', '', '计算正弦值', 'double  sin(double a)  double  sin(decimal a)', '计算正弦值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (39, 'asin', '', '计算反正弦值', 'double  asin(double a)  double  asin(decimal a)', '计算反正弦值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (41, 'cos', '', '计算余弦值', 'double  cos(double a)  double  cos(decimal a)', '计算余弦值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (43, 'acos', '', '计算反余弦值', 'double  acos(double a)  double  acos(decimal a)', '计算反余弦值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (45, 'tan', '', '计算正切值', 'double  tan(double a)  double  tan(decimal a)', '计算正切值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (47, 'atan', '', '计算反正切值', 'double  atan(double a)  double  atan(decimal a)', '计算反正切值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (49, 'degrees', '', '弧度值转换角度值', 'double  degrees(double a)  double  degrees(decimal a)', '弧度值转换角度值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (51, 'radians', '', '将角度值转换成弧度值', 'double  radians(double a)  double  radians(double a)', '将角度值转换成弧度值', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (53, 'positive', '', '返回a', 'int positive(int a),   double  positive(double a)', '返回a', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (55, 'negative', '', '计算相反数', 'int negative(int a),   double  negative(double a)', '返回a的相反数', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (57, 'sign', '', '计算数字的标志', 'double  sign(double a)  int  sign(decimal a)', 'sign(double a)：如果a是正数则返回1.0，是负数则返回-1.0，否则返回0.0  sign(decimal a)：同上，返回值为整型', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (59, 'e', '', '取数学常数e', 'double  e()', '取数学常数e', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (61, 'pi', '', '取数学常数pi', 'double  pi()', '取数学常数pi', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (63, 'factorial', '', '计算阶乘', 'bigint  factorial(int a)', '求a的阶乘', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (65, 'cbrt', '', '计算立方根', 'double  cbrt(double a)', '计算a的立方根', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (67, 'shiftleft', '', '按位左移', 'int   shiftleft(TINYint|SMALLint|int a, int b)  bigint  shiftleft(bigint a, int燽)', '按位左移', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (69, 'shiftright', '', '按拉右移', 'int  shiftright(TINYint|SMALLint|int a, intb)  bigint  shiftright(bigint a, int燽)', '按拉右移', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (71, 'shiftrightunsigned', '', '无符号按位右移（<<<）', 'int  shiftrightunsigned(TINYint|SMALLint|inta, int b)  bigint  shiftrightunsigned(bigint a, int b)', '无符号按位右移（<<<）', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (73, 'greatest', '', '求最大值', 'T  greatest(T v1, T v2, ...)', '返回数据列表中的最大值，当有元素时返回NULL', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (75, 'least', '', '求最小值', 'T  least(T v1, T v2, ...)', '返回数据列表中的最小值，当有NULL元素时返回NULL', 3, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (77, 'size', '', '求集合的长度', 'int  size(Map<K.V>)  int  size(Array<T>)', '参数为Map类型时，计算Map的长度，参数为数组时计算数组的长度', 5, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (79, 'map_keys', '', '返回map中的所有key', 'array<K>  map_keys(Map<K.V>)', '返回map中的所有key', 5, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (81, 'map_values', '', '返回map中的所有value', 'array<V>  map_values(Map<K.V>)', '返回map中的所有value', 5, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (83, 'array_contains', '', '判断数组是否包含指定值', 'boolean  array_contains(Array<T>, value)', '如该数组Array<T>包含value返回true。，否则返回false', 5, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (85, 'sort_array', '', '数组排序', 'array  sort_array(Array<T>)', '按自然顺序对数组进行排序并返回', 5, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (87, 'row_number', '', '排序并获得排序后的编号', 'row_number() OVER (partition by COL1 order by COL2 desc ) rank', '表示根据COL1分组，在分组内部根据 COL2排序，而此函数计算的值就表示每组内部排序后的顺序编号（组内连续的唯一的)', 5, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (89, 'from_unixtime', '', '格式化时间', 'string  from_unixtime(bigint unixtime[, string format])', '将时间的秒值转换成format格式（format可为“yyyy-MM-dd hh:mm:ss”,“yyyy-MM-dd hh”,“yyyy-MM-dd hh:mm”等等）如from_unixtime(1250111000,\"yyyy-MM-dd\") 得到2009-03-12', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (91, 'unix_timestamp', '', '计算时间戳', 'bigint  unix_timestamp()  bigint  unix_timestamp(string date)  bigint  unix_timestamp(string date, string pattern)', '1.unix_timestamp()：获取本地时区下的时间戳  2.unix_timestamp(string date)：将格式为yyyy-MM-dd HH:mm:ss的时间字符串转换成时间戳，如unix_timestamp(\"2009-03-20 11:30:01\") = 1237573801  3.unix_timestamp(string date, string pattern)：将指定时间字符串格式字符串转换成Unix时间戳，如果格式不对返回0 如：unix_timestamp(\"2009-03-20\", \"yyyy-MM-dd\") = 1237532400', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (93, 'to_date', '', '计算日期', 'string  to_date(string timestamp)', '返回时间字符串的日期部分，如：to_date(\"1970-01-01 00:00:00\") = \"1970-01-01\".', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (95, 'year', '', '计算年份', 'int  year(string date)', '返回时间字符串的年份部分，如：year(\"1970-01-01 00:00:00\") = 1970, year(\"1970-01-01\") = 1970.', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (97, 'quarter', '', '计算季节', 'int  quarter(date/timestamp/string)', '返回当前时间属性哪个季度 如quarter(\"2015-04-08\") = 2', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (99, 'month', '', '计算月份', 'int  month(string date)', '返回时间字符串的月份部分，如：month(\"1970-11-01 00:00:00\") = 11, month(\"1970-11-01\") = 11.', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (101, 'day', '', '计算天', 'int  day(string date)', '返回时间字符串中的天部分，如：day(\"1970-11-01 00:00:00\") = 1, day(\"1970-11-01\") = 1.', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (103, 'hour', '', '计算小时', 'int  hour(string date)', '返回时间字符串的小时，如： hour(\"2009-07-30 12:58:59\") = 12, hour(\"12:58:59\") = 12.', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (105, 'minute', '', '计算分钟', 'int  minute(string date)', '返回时间字符串的分钟', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (107, 'second', '', '计算秒数', 'int  second(string date)', '返回时间字符串的秒', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (109, 'weekofyear', '', '计算周', 'int  weekofyear(string date)', '返回时间字符串位于一年中的第几个周内，如：weekofyear(\"1970-11-01 00:00:00\") = 44, weekofyear(\"1970-11-01\") = 44', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (111, 'datediff', '', '计算时间差', 'int  datediff(string enddate, string startdate)', '计算开始时间startdate到结束时间enddate相差的天数，如：datediff(\"2009-03-01\", \"2009-02-27\") = 2.', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (113, 'date_add', '', '从开始时间startdate加上days', 'string  date_add(string startdate, int days)', '从开始时间startdate加上days，如：date_add(\"2008-12-31\", 1) = \"2009-01-01\".', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (115, 'date_sub', '', '从开始时间startdate减去days', 'string  date_sub(string startdate, int days)', '从开始时间startdate减去days，如：date_sub(\"2008-12-31\", 1) = \"2008-12-30\".', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (117, 'from_utc_timestamp', '', '时间戳转换', 'timestamp  from_utc_timestamp(timestamp, string timezone)', '如果给定的时间戳是UTC，则将其转化成指定的时区下时间戳，如：from_utc_timestamp(\"1970-01-01 08:00:00\",\"PST\")=1970-01-01 00:00:00.', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (119, 'to_utc_timestamp', '', '时间戳转换', 'timestamp  to_utc_timestamp(timestamp, string timezone)', '将给定的时间戳转换到UTC下的时间戳，如：to_utc_timestamp(\"1970-01-01 00:00:00\",\"PST\") =1970-01-01 08:00:00', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (121, 'current_date', '', '返回当前时间日期', 'date  current_date', '返回当前时间日期', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (123, 'current_timestamp', '', '返回当前时间戳', 'timestamp  current_timestamp', '返回当前时间戳', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (125, 'add_months', '', '返回当前时间下再增加num_months个月的日期', 'string  add_months(string start_date, int num_months)', '返回start_date之后的num_months的日期。 start_date是一个字符串，日期或时间戳。 num_months是一个整数。 start_date的时间部分被忽略。 如果start_date是本月的最后一天，或者如果生成的月份比start_date的日期组件少，那么结果是最后一个月的最后一天。 否则，结果与start_date具有相同的日期组件', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (127, 'last_day', '', '返回这个月的最后一天的日期', 'string  last_day(string date)', '返回日期所属的月份的最后一天。 date是格式为\"yyyy-MM-dd HH：mm：ss\"或\"yyyy-MM-dd\"的字符串。 日期的时间部分被忽略。', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (129, 'next_day', '', '返回当前时间的下一个星期X所对应的日期', 'string  next_day(string start_date, string day_of_week)', '返回当前时间的下一个星期X所对应的日期，如：next_day(\"2015-01-14\", \"TU\") = 2015-01-20 ?以2015-01-14为开始时间，其下一个星期二所对应的日期为2015-01-20', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (131, 'trunc', '', '返回时间的开始年份或月份', 'string  trunc(string date, string format)', '返回时间的开始年份或月份，如：trunc(\"2016-06-26\",“MM”)=2016-06-01 ?trunc(\"2016-06-26\",“YY”)=2016-01-01 ? 注意所支持的格式为MONTH/MON/MM, YEAR/YYYY/YY', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (133, 'months_between', '', '返回date1与date2之间相差的月份', 'double  months_between(date1, date2)', '返回date1与date2之间相差的月份，如date1>date2，则返回正，如果date1<date2,则返回负，否则返回0.0 ?如：months_between(\"1997-02-28 10:30:00\", \"1996-10-30\") = 3.94959677 ?1997-02-28 10:30:00与1996-10-30相差3.94959677个月', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (135, 'date_format', '', '时间格式化', 'string  date_format(date/timestamp/string ts, string fmt)', '按指定格式返回时间date，如：date_format(\"2016-06-22\",\"MM-dd\")=06-22', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (137, 'if', '', '如果testCondition 为true就返回valueTrue,否则返回valueFalseOrNull ', 'T  if(boolean testCondition, T valueTrue, T valueFalseOrNull)', 'valueTrue，valueFalseOrNull为泛型', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (139, 'nvl', '', '如果value值为NULL就返回default_value,否则返回value', 'T  nvl(T value, T default_value)', '如果value值为NULL就返回default_value,否则返回value', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (141, 'coalesce', '', '返回第一非null的值', 'T  coalesce(T v1, T v2, ...)', '如果全部都为NULL就返回NULL ?如：COALESCE (NULL,44,55)=44', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (143, 'isnull', '', '判断a是否为空', 'boolean  isnull( a )', '如果a为null就返回true，否则返回false', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (145, 'isnotnull', '', '判断a是否不为空', 'boolean  isnotnull ( a )', '如果a为非null就返回true，否则返回false', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (147, 'assert_true', '', '判断条件是否为真', 'assert_true(boolean condition)', '如果“条件”不为真，则抛出异常，否则返回null（从Hive 0.8.0开始）', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (149, 'cast', '', '类型转换', 'type  cast(expr as <type>)', '将表达式expr的结果转换为<type>。 例如，cast（\"1\"为BIGINT）将字符串\"1\"转换为其整数表示。 如果转换不成功，则返回null。', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (151, 'binary', '', '将输入的值转换成二进制', 'binary  binary(string|binary)', '将输入的值转换成二进制', 7, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (153, 'ascii', '', '返回str中首个ASCII字符串的整数值', 'int  ascii(string str)', '返回str中首个ASCII字符串的整数值', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (155, 'base64', '', '二进制转字符串', 'string  base64(binary bin)', '将二进制bin转换成64位的字符串', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (157, 'concat', '', '字符串和字节拼接', 'string  concat(string|binary A, string|binary B...)', '将字符串或字节拼接，如：concat(\"foo\", \"bar\") = \"foobar\"，次函数可以拼接任意数量的字符串或字节。', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (159, 'chr', '', '返回A对应的ASCII字符', 'string chr(bigint|double A)', '返回A对应的ASCII字符。如果A大于256，则结果相当于chr（A％256）。 示例：chr（@date）=“X”。', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (161, 'context_ngrams', '', '返回出现次数TOP K的的子序列', 'array<struct<string,double>>  context_ngrams(array<array<string>>, array<string>, int K, int pf)', '返回出现次数TOP K的的子序列，但context_ngram()允许你预算指定上下文(数组)来去查找子序列', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (163, 'concat_ws', '', '使用指定的分隔符拼接字符串', 'string  concat_ws(string SEP, string A, string B...)  string  concat_ws(string SEP, array<string>)', '使用指定的分隔符拼接字符串', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (165, 'decode', '', '解码', 'string  decode(binary bin, string charset)', '使用指定的字符集charset将二进制值bin解码成字符串，支持的字符集有：\"US-ASCII\", \"ISO-8@math9-1\", \"UTF-8\", \"UTF-16BE\", \"UTF-16LE\", \"UTF-16\"，如果任意输入参数为NULL都将返回NULL', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (167, 'encode', '', '编码', 'binary  encode(string src, string charset)', '使用指定的字符集charset将字符串编码成二进制值，支持的字符集有：\"US-ASCII\", \"ISO-8@math9-1\", \"UTF-8\", \"UTF-16BE\", \"UTF-16LE\", \"UTF-16\"，如果任一输入参数为NULL都将返回NULL', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (169, 'find_in_set', '', '返回以逗号分隔的字符串中str出现的位置', 'int  find_in_set(string str, string strList)', '返回以逗号分隔的字符串中str出现的位置，如果参数str为逗号或查找失败将返回0，如果任一参数为NULL将返回NULL回', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (171, 'format_number', '', '格式转换', 'string  format_number(number x, int d)', '将数值X转换成\"#,###,###.##\"格式字符串，并保留d位小数，如果d为0，将进行四舍五入且不保留小数', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (173, 'get_json_object', '', '从指定路径获取JSON对象', 'string  get_json_object(string json_string, string path)', '从指定路径上的JSON字符串抽取出JSON对象，并返回这个对象的JSON格式，如果输入的JSON是非法的将返回NULL,注意此路径上JSON字符串只能由数字 字母 下划线组成且不能有大写字母和特殊字符，且key不能由数字开头，这是由于Hive对列名的限制', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (175, 'in_file', '', '在指定文件中索引指定字符串', 'boolean  in_file(string str, string filename)', '如果文件名为filename的文件中有一行数据与字符串str匹配成功就返回true', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (177, 'instr', '', '查找字符串str中子字符串substr出现的位置', 'int  instr(string str, string substr)', '查找字符串str中子字符串substr出现的位置，如果查找失败将返回0，如果任一参数为Null将返回null，注意位置为从1开始的', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (179, 'length', '', '计算字符串长度', 'int  length(string A)', '返回字符串的长度', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (181, 'locate', '', '在位置pos之后返回str中第一次出现substr的位置', 'int  locate(string substr, string str[, int pos])', '在位置pos之后返回str中第一次出现substr的位置', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (183, 'lower', '', '将字符串A的所有字母转换成小写字母', 'string  lower(string A)', '将字符串A的所有字母转换成小写字母', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (185, 'lpad', '', '从左边开始对字符串str使用字符串pad填充，最终len长度为止，如果字符串str本身长度比len大的话，将去掉多余的部分', 'string  lpad(string str, int len, string pad)', '从左边开始对字符串str使用字符串pad填充，一直到长度为len为止，如果字符串str本身长度比len大的话，将去掉多余的部分', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (187, 'ltrim', '', '去掉字符串A前面的空格', 'string  ltrim(string A)', '去掉字符串A前面的空格', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (189, 'ngrams', '', '返回出现次数TOP K的的子序列', 'array<struct<string,double>>  ngrams(array<array<string>>, int N, int燢, int pf)', '返回出现次数TOP K的的子序列,n表示子序列的长度', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (191, 'parse_url', '', '返回从URL中抽取指定部分的内容', 'string  parse_url(string urlstring, string partToExtract [, string keyToExtract])', '返回从URL中抽取指定部分的内容，参数url是URL字符串，而参数partToExtract是要抽取的部分，这个参数包含(HOST, PATH, QUERY, REF, PROTOCOL, AUTHORITY, FILE, and USERINFO,例如：parse_url(\"http://facebook.com/path1/p.php?k1=v1&k2=v2#Ref1\", \"HOST\") =\"facebook.com\"，如果参数partToExtract值为QUERY则必须指定第三个参数key ?如：parse_url(\"http://facebook.com/path1/p.php?k1=v1&k2=v2#Ref1\", \"QUERY\", \"k1\") =‘v1’', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (193, 'printf', '', '按照printf风格格式输出字符串', 'string  printf(string format, Obj... args)', '按照printf风格格式输出字符串', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (195, 'regexp_extract', '', '返回使用模式提取的字符串', 'string  regexp_extract(string subject, string pattern, int index)', '抽取字符串subject中符合正则表达式pattern的第index个部分的子字符串， 例如，regexp_extract(\"foothebar\", \"foo(.*?)(bar)\", 2)=\"bar\"。 请注意，在使用预定义的字符类时需要注意：使用“ s”作为第二个参数将匹配字母s; \"\"需要匹配空格等', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (197, 'regexp_replace', '', '使用模式匹配替换字符串', 'string regexp_replace(string INITIAL_STRING, string PATTERN, string REPLACEMENT)', '按照Java正则表达式PATTERN将字符串INITIAL_STRING中符合条件的部分替换成REPLACEMENT所指定的字符串，如里REPLACEMENT为空的话，将符合正则的部分将被去掉 ?如：regexp_replace(\"foobar\", \"oo|ar\", \"\") = \"fb.\"。', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (199, 'repeat', '', '重复输出n次字符串str', 'string  repeat(string str, int n)', '重复输出n次字符串str', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (201, 'reverse', '', '反转字符串', 'string  reverse(string A)', '反转字符串', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (203, 'rpad', '', '字符串填充', 'string  rpad(string str, int len, string pad)', '从右边开始对字符串str使用字符串pad填充，一直到长度为len为止，如果字符串str本身长度比len大的话，将去掉多余的部分', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (205, 'rtrim', '', '去掉字符串后面出现的空格', 'string  rtrim(string A)', '去掉字符串后面出现的空格', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (207, 'sentences', '', '将字符串str将转换成单词数组', 'array<array<string>>  sentences(string str, string lang, string locale)', '字符串str将被转换成单词数组，如：sentences(\"Hello there! How are you?\") =( (\"Hello\", \"there\"), (\"How\", \"are\", \"you\") )', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (209, 'space', '', '返回n个空格', 'string  space(int n)', '返回n个空格', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (211, 'split', '', '按照正则表达式pat来分割字符串str', 'array  split(string str, string pat)', '按照正则表达式pat来分割字符串str,分割后以数组字符串的形式返回', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (213, 'str_to_map', '', '将字符串str按照指定分隔符转换成Map', 'map<string,string>  str_to_map(text[, delimiter1, delimiter2])', '将字符串str按照指定分隔符转换成Map，第一个参数是需要转换的字符串，第二个参数是键值对之间的分隔符，默认为逗号;第三个参数是键值之间的分隔符，默认为\"=\"', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (215, 'substr', '', '截取字符串', 'string  substr(string|binary A, int start) string  substr(string|binary A, int start, int len) ', '截取字符串A中start位置之后的字符串并返回  截取字符串A中start位置之后的长度为length的字符串', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (217, 'substring_index', '', '截取字符串', 'string  substring_index(string A, string delim, int count)', '截取第count分隔符之前的字符串，如count为正则从左边开始截取，如果为负则从右边开始截取', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (219, 'translate', '', '字符串替换', 'string  translate(string|char|varchar input, string|char|varchar from, string|char|varchar to)', '将input出现在from中的字符串替换成to中的字符串 如：translate(\"MOBIN\",\"BIN\",\"M\")=\"MOM\"', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (221, 'trim', '', '将字符串A前后出现的空格去掉', 'string  trim(string A)', '将字符串A前后出现的空格去掉', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (223, 'unbase64', '', '将64位的字符串转换二进制值', 'binary  unbase64(string str)', '将64位的字符串转换二进制值', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (225, 'upper', '', '将字符串A中的字母转换成大写字母', 'string  upper(string A)', '将字符串A中的字母转换成大写字母', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (227, 'initcap', '', '字符串中的单词首字母大写', 'string  initcap(string A)', '返回字符串，每个单词的第一个字母大写，所有其他字母以小写形式显示。 单词由空格分割。', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (229, 'levenshtein', '', '计算两个字符串之间的差异大小', 'int  levenshtein(string A, string B)', '计算两个字符串之间的差异大小 ?如：levenshtein(\"kitten\", \"sitting\") = 3', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (231, 'soundex', '', '将普通字符串转换成soundex字符串', 'string  soundex(string A)', '将普通字符串转换成soundex字符串，如：soundex(\"Miller\") = M460.', 11, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (233, 'count', '', '统计总行数', 'bigint  count(*)  bigint  count(expr)  bigint  count(DISTINCT expr[, expr...])', '统计总行数，包括含有NULL值的行  统计提供非NULL的expr表达式值的行数  统计提供非NULL且去重后的expr表达式值的行数', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (235, 'sum', '', '表示求指定列的和', 'double  sum(col)  double  sum(DISTINCT col)', 'sum(col),表示求指定列的和，sum(DISTINCT col)表示求去重后的列的和', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (237, 'avg', '', '求指定列的平均值', 'double  avg(col)  double  avg(DISTINCT col)', 'avg(col),表示求指定列的平均值，avg(DISTINCT col)表示求去重后的列的平均值', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (239, 'min', '', '求指定列的最小值', 'double  min(col)', '求指定列的最小值', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (241, 'max', '', '求指定列的最大值', 'double  max(col)', '求指定列的最大值', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (243, 'variance', '', '求指定列数值的方差', 'double  variance(col)  double  var_pop(col)', '求指定列数值的方差', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (245, 'var_samp', '', '求指定列数值的样本方差', 'double  var_samp(col)', '求指定列数值的样本方差', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (247, 'stddev_pop', '', '求指定列数值的标准偏差', 'double  stddev_pop(col)', '求指定列数值的标准偏差', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (249, 'stddev_samp', '', '求指定列数值的样本标准偏差', 'double  stddev_samp(col)', '求指定列数值的样本标准偏差', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (251, 'covar_pop', '', '求指定列数值的协方差', 'double  covar_pop(col1, col2)', '求指定列数值的协方差', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (253, 'covar_samp', '', '求指定列数值的样本协方差', 'double  covar_samp(col1, col2)', '求指定列数值的样本协方差', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (255, 'corr', '', '返回两列数值的相关系数', 'double  corr(col1, col2)', '返回两列数值的相关系数', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (257, 'percentile', '', '返回col的p%分位数', 'double  percentile(bigint col, p)', ' p必须在0和1之间。注意：只能对整数值计算真正的百分位数。 ', 13, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (259, 'explode', '', '对于a中的每个元素，生成包含该元素的行', 'Array Type  explode(array<TYPE> a)  N rows  explode(ARRAY)  N rows  explode(MAP)', '对于a中的每个元素，生成包含该元素的行  从数组中的每个元素返回一行  从输入映射中为每个键值对返回一行，每行中有两列：一个用于键，另一个用于该值', 15, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (261, 'posexplode', '', '从数组中的每个元素返回一行', 'N rows  posexplode(ARRAY)', '与explode类似，不同的是还返回各元素在数组中的位置', 15, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (263, 'stack', '', '把M列转换成N行', 'N rows  stack(int n, v_1, v_2, ..., v_k)', '将v_1，...，v_k分解成n行。 每行将有k / n列。 n必须是常数。', 15, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (265, 'json_tuple', '', '从一个JSON字符串中获取多个键并作为一个元组返回', 'tuple  json_tuple(jsonStr, k1, k2, ...)', '从一个JSON字符串中获取多个键并作为一个元组返回，与get_json_object不同的是此函数能一次获取多个键值', 15, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (267, 'parse_url_tuple', '', '返回从URL中抽取指定N部分的内容', 'tuple  parse_url_tuple(url, p1, p2, ...)', '返回从URL中抽取指定N部分的内容，参数url是URL字符串，而参数p1,p2,....是要抽取的部分，这个参数包含HOST, PATH, QUERY, REF, PROTOCOL, AUTHORITY, FILE, USERINFO, QUERY:<KEY>', 15, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (269, 'inline', '', '将结构体数组提取出来并插入到表中', 'inline(ARRAY<STRUCT[,STRUCT]>)', '将结构体数组提取出来并插入到表中', 15, -1, -1, -1, 1, NULL, 0, '2022-02-12 23:33:10', '2022-02-12 23:33:10', 0, NULL);
INSERT INTO `develop_function` VALUES (271, 'POWER', '', '计算次幂', 'POWER(numeric1, numeric2)', '返回 numeric1 的 numeric2 次幂.', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (273, 'ABS', '', '计算numeric的绝对值', 'ABS(numeric)', '计算numeric的绝对值.', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (275, 'MOD', '', 'numeric1 对 numeric2 取模', 'MOD(numeric1, numeric2)', '返回numeric1除以numeric2的余数(模数). 仅当numeric1为负时结果为负.', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (277, 'SQRT', '', '计算平方根', 'SQRT(numeric)', '计算numeric平方根.', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (279, 'LN', '', '	计算自然数的对数', 'LN(numeric)', '返回numeric的自然对数(以e为底)', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (281, 'LOG10', '', '	返回数字10的对数', 'LOG10(numeric) ', '返回numeric的对数(以10为底)', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (283, 'EXP', '', '	计算自然指数的指数', 'EXP(numeric)', '返回自然对数e的numeric幂次方', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (285, 'CEIL', '', '向上取整', 'CEIL(numeric) or CEILING(numeric)', '求其不小于小给定实数的最小整数如：ceil(6.1)= ceil(6.9) = 7', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (287, 'FLOOR', '', '向下取整', 'FLOOR(numeric)', '求其不大于给定实数的最小整数如：FLOOR(6.1)= FLOOR(6.9) = 6', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (289, 'SIN', '', '计算正弦值', 'SIN(numeric)', '计算正弦值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (291, 'COS', '', '计算余弦值', 'COS(numeric)', '计算余弦值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (293, 'TAN', '', '计算正切值', 'TAN(numeric)', '计算正切值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (295, 'COT', '', '计算余切值', 'COT(numeric)', '计算余切值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (297, 'ASIN', '', '计算反正弦值', 'ASIN(numeric)', '计算反正弦值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (299, 'ACOS', '', '计算反余弦值', 'ACOS(numeric)', '计算反余弦值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (301, 'ATAN', '', '计算反正切值', 'ATAN(numeric)', '计算反正切值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (303, 'DEGREES', '', '弧度值转换角度值', 'DEGREES(numeric)', '弧度值转换角度值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (305, 'RADIANS', '', '将角度值转换成弧度值', 'RADIANS(numeric)', '将角度值转换成弧度值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (307, 'SIGN', '', '计算数字的标志', 'SIGN(numeric)', '如果numeric是正数则返回1.0, 是负数则返回-1.0, 否则返回0.0 ', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (309, 'ROUND', '', '取近似值', 'ROUND(numeric, int)', '返回numeric的保留int位小数的近似值', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (311, 'PI', '', '取数学常数pi', 'PI()', '取数学常数pi', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (313, 'E', '', '取数学常数e', 'E()', '取数学常数e', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (315, 'RAND', '', '取随机数', 'RAND() or RAND(seed integer)', '返回一个0到1范围内的随机数,如果指定种子seed，则会等到一个稳定的随机数序列.', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (317, 'RAND_INTEGER', '', '取随机数', 'RAND_INTEGER(bound integer) or RAND_INTEGER(seed integer, bound integer) ', '返回0.0(包含)和指定值(不包括)之间的伪随机整数值, 如果指定种子seed，则会等到一个稳定的随机数序列 ', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (319, 'LOG', '', '计算对数', 'LOG(x numeric) or LOG(base numeric, x numeric)', '未指定base 则以自然数e为底', 19, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:03', '2022-05-03 22:21:03', 0, NULL);
INSERT INTO `develop_function` VALUES (321, 'EXTRACT', '', '提取指定单位的时间数值', 'EXTRACT(timeintervalunit FROM temporal)', '提取部分的时间数值,并返回长整形, 比如 EXTRACT(DAY FROM DATE \'2006-06-05\') 返回 5.', 21, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:04', '2022-05-03 22:21:04', 0, NULL);
INSERT INTO `develop_function` VALUES (323, 'QUARTER', '', '计算季节', 'QUARTER(date)', '返回当前时间属性哪个季度 如QUARTER(DATE \'1994-09-27\') 返回 3', 21, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:04', '2022-05-03 22:21:04', 0, NULL);
INSERT INTO `develop_function` VALUES (325, 'DATE_FORMAT', '', '时间格式化', 'DATE_FORMAT(timestamp, format)', '根据指定format 格式化timestamp 并返回字符串, format 必须和mysql的格式化语法兼容(date_parse), 比如:DATE_FORMAT(ts, \'%Y, %d %M\') results in strings formatted as \'2017, 05 May\'', 21, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:04', '2022-05-03 22:21:04', 0, NULL);
INSERT INTO `develop_function` VALUES (327, 'TIMESTAMPADD', '', '时间加减操作', 'TIMESTAMPADD(unit, interval, timestamp)', '将(有符号)整数interval添加到timestamp. interval的单位由unit参数给出, 它应该是以下值之一：SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, or YEAR. 比如：TIMESTAMPADD(WEEK, 1, \'2003-01-02\') 返回 2003-01-09', 21, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:04', '2022-05-03 22:21:04', 0, NULL);
INSERT INTO `develop_function` VALUES (329, 'CHAR_LENGTH', '', '计算字符串长度', 'CHAR_LENGTH(string)', '返回字符串的长度', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (331, 'CHARACTER_LENGTH', '', '计算字符串长度', 'CHARACTER_LENGTH(string)', '返回字符串的长度', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (333, 'UPPER', '', '将字符串的字母转换成大写字母', 'UPPER(string)', '将字符串的字母转换成大写字母', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (335, 'LOWER', '', '将字符串的字母转换成小写字母', 'LOWER(string)', '将字符串的字母转换成小写字母', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (337, 'POSITION', '', '返回string2中第一次出现string1的位置', 'POSITION(string1 IN string2)', '返回string2中第一次出现string1的位置', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (339, 'TRIM', '', '删除指定字符', 'TRIM( { BOTH | LEADING | TRAILING } string1 FROM string2)', '从string2 中删除指定位置的String1, 默认是删除前后的空格', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (341, 'OVERLAY', '', '替换字符串', 'OVERLAY(string1 PLACING string2 FROM integer [ FOR integer2 ])', '用string2替换string1的子字符串', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (343, 'SUBSTRING', '', '截取字符串', 'SUBSTRING(string FROM integer) or SUBSTRING(string FROM integer FOR integer)', '截取字符串中start位置之后的字符串并返回 截取字符串中start位置之后的长度为length的字符串', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (345, 'INITCAP', '', '字符串中的单词首字母大写', 'INITCAP(string)', '返回字符串，每个单词的第一个字母大写, 所有其他字母以小写形式显示. 单词由空格分割.', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (347, 'CONCAT', '', '字符串和字节拼接', 'CONCAT(string1, string2,...)', '将字符串或字节拼接，如：concat(\'foo\', \'bar\') = \'foobar\', 函数可以拼接任意数量的字符串或字节。', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (349, 'CONCAT_WS', '', '使用指定的分隔符拼接字符串', 'CONCAT_WS(separator, string1, string2,...)', '使用指定的分隔符拼接字符串', 23, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:06', '2022-05-03 22:21:06', 0, NULL);
INSERT INTO `develop_function` VALUES (351, 'COUNT', '', '统计总行数', 'COUNT(*) or COUNT(value [, value]* )', '统计总行数，包括含有NULL值的行, 统计提供非NULL的expr表达式值的行数.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (353, 'AVG', '', '求指定列的平均值', 'AVG(numeric)', '求指定列的平均值.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (355, 'SUM', '', '求指定列的和', 'SUM(numeric)', '求指定列的和.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (357, 'MAX', '', '求指定列的最大值', 'MAX(value)', '求指定列的最大值.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (359, 'MIN', '', '求指定列的最小值', 'MIN(value)', '求指定列的最小值.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (361, 'STDDEV_POP', '', '求指定列数值的标准偏差', 'STDDEV_POP(value)', '求指定列数值的标准偏差.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (363, 'STDDEV_SAMP', '', '求指定列数值的样本标准偏差', 'STDDEV_SAMP(value)', '求指定列数值的样本标准偏差.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (365, 'VAR_POP', '', '求指定列数值的方差', 'VAR_POP(value)', '求指定列数值的方差.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (367, 'VAR_SAMP', '', '求指定列数值的样本方差', 'VAR_POP(value)', '求指定列数值的样本方差.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (369, 'COLLECT', '', '返回包含值的multiset', 'COLLECT(value)', '返回包含值的multiset. null将被忽略.如果仅添加null,则返回一个空multiset.', 25, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:07', '2022-05-03 22:21:07', 0, NULL);
INSERT INTO `develop_function` VALUES (371, 'NULLIF', '', '如果值相同着返回null', 'NULLIF(value, value)', '如果值相同着返回null, 比如 NULLIF(5, 5) 返回 NULL; NULLIF(5, 0) 返回 5.', 27, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:09', '2022-05-03 22:21:09', 0, NULL);
INSERT INTO `develop_function` VALUES (373, 'COALESCE', '', '返回第一非null的值', 'COALESCE(value, value [, value ]* )', '返回第一非null的值, 比如: COALESCE(NULL, 5) 返回 5.', 27, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:09', '2022-05-03 22:21:09', 0, NULL);
INSERT INTO `develop_function` VALUES (375, 'CAST', '', '类型转换', 'CAST(value AS type)', '将value 转换为指定type', 27, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:09', '2022-05-03 22:21:09', 0, NULL);
INSERT INTO `develop_function` VALUES (377, 'GROUP_ID', '', '返回一个唯一标识分组键的整数', 'GROUP_ID()', '返回一个唯一标识分组键的整数.', 27, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:09', '2022-05-03 22:21:09', 0, NULL);
INSERT INTO `develop_function` VALUES (379, 'GROUPING', '', '如果表达式在当前行的分组集合中返回1, 否则返回0', 'GROUPING(expression)', '如果表达式在当前行的分组集合中返回1, 否则返回0.', 27, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:09', '2022-05-03 22:21:09', 0, NULL);
INSERT INTO `develop_function` VALUES (381, 'GROUPING_ID', '', '返回给定分组表达式的位向量', 'GROUPING_ID(expression [, expression]* )', '返回给定分组表达式的位向量.', 27, -1, -1, -1, 1, 0, 5, '2022-05-03 22:21:09', '2022-05-03 22:21:09', 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for develop_function_resource
-- ----------------------------
DROP TABLE IF EXISTS `develop_function_resource`;
CREATE TABLE `develop_function_resource` (
                                             `id` int(11) NOT NULL AUTO_INCREMENT,
                                             `function_id` int(11) NOT NULL COMMENT '函数id',
                                             `resource_id` int(11) NOT NULL COMMENT '对应batch资源的id',
                                             `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                             `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                             `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                             `tenant_id` bigint(20) DEFAULT NULL,
                                             PRIMARY KEY (`id`),
                                             UNIQUE KEY `index_rdos_function_resource` (`function_id`,`resource_id`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='函数关联的资源表';

-- ----------------------------
-- Table structure for develop_hive_select_sql
-- ----------------------------
DROP TABLE IF EXISTS `develop_hive_select_sql`;
CREATE TABLE `develop_hive_select_sql` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `job_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '工作任务id',
                                           `temp_table_name` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '临时表名',
                                           `is_select_sql` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-否 1-是',
                                           `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                           `user_id` int(11) DEFAULT NULL COMMENT '执行用户',
                                           `sql_text` longtext COLLATE utf8_bin COMMENT 'sql',
                                           `parsed_columns` longtext COLLATE utf8_bin COMMENT '字段信息',
                                           `task_type` int(11) DEFAULT NULL COMMENT '任务类型',
                                           `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                           `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                           `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `idx` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='sql查询临时表';

-- ----------------------------
-- Table structure for stream_metric_support
-- ----------------------------
DROP TABLE IF EXISTS `stream_metric_support`;
CREATE TABLE `stream_metric_support` (
                                          `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                          `name` varchar(255) NOT NULL COMMENT '指标中文名称',
                                          `task_type` tinyint(4) NOT NULL COMMENT '指标支持的任务类型',
                                          `value` varchar(255) NOT NULL COMMENT '指标key',
                                          `metric_tag` int(11) NOT NULL COMMENT 'metric匹配模式',
                                          `component_version` varchar(255) NOT NULL DEFAULT '1.10' COMMENT '组件版本',
                                          `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `is_deleted` tinyint(4) DEFAULT NULL COMMENT '是否删除',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=256 DEFAULT CHARSET=utf8 COMMENT='任务支持的metric指标';

-- ----------------------------
-- Records of stream_metric_support
-- ----------------------------
BEGIN;
INSERT INTO `stream_metric_support` VALUES (143, 'jobmanager cpu负载', 99, 'flink_jobmanager_Status_JVM_CPU_Load', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (145, 'jobmanager cpu使用时间', 99, 'flink_jobmanager_Status_JVM_CPU_Time', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (147, 'taskmanager cpu负载', 99, 'flink_taskmanager_Status_JVM_CPU_Load', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (149, 'taskmanager cpu使用时间', 99, 'flink_taskmanager_Status_JVM_CPU_Time', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (151, 'bmanager jvm最大堆内存', 99, 'flink_jobmanager_Status_JVM_Memory_Heap_Max', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (153, 'taskmanager jvm最大堆内存', 99, 'flink_taskmanager_Status_JVM_Memory_Heap_Max', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (155, 'jobmanager jvm堆内存使用量', 99, 'flink_jobmanager_Status_JVM_Memory_Heap_Used', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (157, 'taskmanager jvm堆内存使用量', 99, 'flink_taskmanager_Status_JVM_Memory_Heap_Used', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (159, 'jobmanager jvm最大非堆内存', 99, 'flink_jobmanager_Status_JVM_Memory_NonHeap_Max', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (161, 'taskmanager jvm最大非堆内存', 99, 'flink_taskmanager_Status_JVM_Memory_NonHeap_Max', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (163, 'jobmanager jvm非堆内存使用量', 99, 'flink_jobmanager_Status_JVM_Memory_NonHeap_Used', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (165, 'taskmanager jvm非堆内存使用量', 99, 'flink_taskmanager_Status_JVM_Memory_NonHeap_Used', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (167, 'jobmanager直接缓冲区总容量', 99, 'flink_jobmanager_Status_JVM_Memory_Direct_TotalCapacity', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (169, 'taskmanager直接缓冲区总容量', 99, 'flink_taskmanager_Status_JVM_Memory_Direct_TotalCapacity', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (171, 'jobmanager直接缓冲区内存使用量', 99, 'flink_jobmanager_Status_JVM_Memory_Direct_MemoryUsed', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (173, 'taskmanager直接缓冲区内存使用量', 99, 'flink_taskmanager_Status_JVM_Memory_Direct_MemoryUsed', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (175, 'jobmanager 线程数', 99, 'flink_jobmanager_Status_JVM_Threads_Count', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (177, 'taskmanager 线程数', 99, 'flink_taskmanager_Status_JVM_Threads_Count', 2, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (179, '排队进入输入缓存区的数量', 99, 'flink_taskmanager_job_task_buffers_inputQueueLength', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (181, '排队进入输出缓存区的数量', 99, 'flink_taskmanager_job_task_buffers_outputQueueLength', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (183, '任务重启次数', 99, 'flink_jobmanager_job_numRestarts', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (185, '任务重启花费时间', 99, 'flink_jobmanager_job_restartingTime', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (187, '任务最后一次checkpoint花费时间', 99, 'flink_jobmanager_job_lastCheckpointDuration', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (189, '任务最后一次checkpoint大小', 99, 'flink_jobmanager_job_lastCheckpointSize', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (191, '任务checkpoint总数量', 99, 'flink_jobmanager_job_totalNumberOfCheckpoints', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (193, '任务checkpoint失败数量', 99, 'flink_jobmanager_job_numberOfFailedCheckpoints', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (195, 'barrier 对齐花费时间', 99, 'flink_taskmanager_job_task_checkpointAlignmentTime', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (231, '各source rps数据输入', 5, 'flink_taskmanager_job_task_operator_flinkx_numReadPerSecond', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (233, '各source bps数据输入', 5, 'flink_taskmanager_job_task_operator_flinkx_byteReadPerSecond', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (235, '各sink bps数据输出', 5, 'flink_taskmanager_job_task_operator_flinkx_byteWritePerSecond', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (237, '各sink rps输出', 5, 'flink_taskmanager_job_task_operator_flinkx_numWritePerSecond', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (239, '数据延迟', 5, 'flink_taskmanager_job_task_operator_flinkx_KafkaConsumer_topic_partition_lag', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (241, '输入rps', 6, 'flink_taskmanager_job_task_operator_flinkx_numReadPerSecond', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (243, '输出rps', 6, 'flink_taskmanager_job_task_operator_flinkx_numWritePerSecond', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (245, '输入bps', 6, 'flink_taskmanager_job_task_operator_flinkx_byteReadPerSecond', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (247, '输出bps', 6, 'flink_taskmanager_job_task_operator_flinkx_byteWritePerSecond', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (249, '累计输入记录数', 6, 'flink_taskmanager_job_task_operator_flinkx_numRead', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (251, '累计输出记录数', 6, 'flink_taskmanager_job_task_operator_flinkx_numWrite', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (253, '累计输入数据量', 6, 'flink_taskmanager_job_task_operator_flinkx_byteRead', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
INSERT INTO `stream_metric_support` VALUES (255, '累计输出数据量', 6, 'flink_taskmanager_job_task_operator_flinkx_byteWrite', 1, '1.12', '2021-09-26 17:04:01', '2021-09-26 17:04:01', 0);
COMMIT;


-- ----------------------------
-- Table structure for develop_resource
-- ----------------------------
DROP TABLE IF EXISTS `develop_resource`;
CREATE TABLE `develop_resource` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                    `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
                                    `url` varchar(1028) COLLATE utf8_bin NOT NULL COMMENT '资源路径',
                                    `resource_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '资源类型 0:other, 1:jar, 2:py, 3:zip, 4:egg',
                                    `resource_name` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '资源名称',
                                    `origin_file_name` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '源文件名',
                                    `resource_desc` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '源文描述',
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `create_user_id` int(11) NOT NULL COMMENT '新建资源的用户',
                                    `modify_user_id` int(11) NOT NULL COMMENT '修改人',
                                    `compute_type` int(11) NOT NULL DEFAULT '0' COMMENT '上传组建类型',
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                    `node_id` bigint(20) DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `index_resource_name` (`resource_name`(128)),
                                    KEY `index_resource_type` (`resource_type`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='资源表';

-- ----------------------------
-- Table structure for develop_sys_parameter
-- ----------------------------
DROP TABLE IF EXISTS `develop_sys_parameter`;
CREATE TABLE `develop_sys_parameter` (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `param_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '参数名称',
                                         `param_command` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '参数替换指令',
                                         `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                         `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                         `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务开发-系统参数表';

-- ----------------------------
-- Records of develop_sys_parameter
-- ----------------------------
BEGIN;
INSERT INTO `develop_sys_parameter` VALUES (1, 'bdp.system.bizdate', 'yyyyMMdd-1', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO `develop_sys_parameter` VALUES (3, 'bdp.system.cyctime', 'yyyyMMddHHmmss', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO `develop_sys_parameter` VALUES (5, 'bdp.system.currmonth', 'yyyyMM-0', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO `develop_sys_parameter` VALUES (7, 'bdp.system.premonth', 'yyyyMM-1', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO `develop_sys_parameter` VALUES (9, 'bdp.system.runtime', '${bdp.system.currenttime}', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
INSERT INTO `develop_sys_parameter` VALUES (11, 'bdp.system.bizdate2', 'yyyy-MM-dd,-1', '2022-02-12 23:31:50', '2022-02-12 23:31:50', 0);
COMMIT;

-- ----------------------------
-- Table structure for develop_task
-- ----------------------------
DROP TABLE IF EXISTS `develop_task`;
CREATE TABLE `develop_task` (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                `node_pid` int(11) NOT NULL COMMENT '父文件夹id',
                                `name` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '任务名称',
                                `task_type` tinyint(1) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL ',
                                `compute_type` tinyint(1) NOT NULL COMMENT '计算类型 0实时，1 离线',
                                `sql_text` longtext COLLATE utf8_bin NOT NULL COMMENT 'sql 文本',
                                `task_params` text COLLATE utf8_bin NOT NULL COMMENT '任务参数',
                                `schedule_conf` varchar(512) COLLATE utf8_bin NOT NULL COMMENT '调度配置 json格式',
                                `period_type` tinyint(2) DEFAULT NULL COMMENT '周期类型',
                                `schedule_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未开始,1正常调度,2暂停',
                                `submit_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未提交,1已提交',
                                `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                `modify_user_id` int(11) NOT NULL COMMENT '最后修改task的用户',
                                `create_user_id` int(11) NOT NULL COMMENT '新建task的用户',
                                `version` int(11) NOT NULL DEFAULT '0' COMMENT 'task版本',
                                `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                `task_desc` varchar(256) COLLATE utf8_bin NOT NULL,
                                `main_class` varchar(256) COLLATE utf8_bin NOT NULL,
                                `exe_args` text COLLATE utf8_bin,
                                `flow_id` int(11) NOT NULL DEFAULT '0' COMMENT '工作流id',
                                `component_version` varchar(25) COLLATE utf8_bin DEFAULT NULL COMMENT '组件版本',
                                `source_str` longtext COLLATE utf8_bin COMMENT '输入源',
                                `target_str` longtext COLLATE utf8_bin COMMENT '输出源',
                                `side_str` longtext COLLATE utf8_bin COMMENT '维表',
                                `setting_str` longtext COLLATE utf8_bin COMMENT '设置',
                                `create_model` tinyint(4) DEFAULT NULL COMMENT '任务模式 0 向导模式  1 脚本模式',
                                `job_id` varchar(64) DEFAULT null,
                                PRIMARY KEY (`id`),
                                KEY `index_name` (`name`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务表';

-- ----------------------------
-- Table structure for develop_task_param
-- ----------------------------
DROP TABLE IF EXISTS `develop_task_param`;
CREATE TABLE `develop_task_param` (
                                      `id` int(11) NOT NULL AUTO_INCREMENT,
                                      `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                      `type` int(2) NOT NULL COMMENT '0:系统参数, 1:自定义参数',
                                      `param_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '参数名称',
                                      `param_command` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '参数替换指令',
                                      `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                      `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                      PRIMARY KEY (`id`),
                                      KEY `index_batch_task_parameter` (`task_id`,`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务开发-任务参数配置表';

-- ----------------------------
-- Table structure for develop_task_param_shade
-- ----------------------------
DROP TABLE IF EXISTS `develop_task_param_shade`;
CREATE TABLE `develop_task_param_shade` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                            `type` int(2) NOT NULL COMMENT '0:系统参数, 1:自定义参数',
                                            `param_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '参数名称',
                                            `param_command` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '参数替换指令',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`),
                                            KEY `index_batch_task_parameter` (`task_id`,`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务参数配置- 提交表';

-- ----------------------------
-- Table structure for develop_task_resource
-- ----------------------------
DROP TABLE IF EXISTS `develop_task_resource`;
CREATE TABLE `develop_task_resource` (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                         `resource_id` int(11) DEFAULT NULL COMMENT '对应batch资源的id',
                                         `resource_type` int(11) DEFAULT NULL COMMENT '使用资源的类型 1:主体资源, 2:引用资源',
                                         `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                         `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                         `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                         `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `index_project_task_resource_id` (`task_id`,`resource_id`,`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务和资源关联表';

-- ----------------------------
-- Table structure for develop_task_resource_shade
-- ----------------------------
DROP TABLE IF EXISTS `develop_task_resource_shade`;
CREATE TABLE `develop_task_resource_shade` (
                                               `id` int(11) NOT NULL AUTO_INCREMENT,
                                               `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                               `resource_id` int(11) DEFAULT NULL COMMENT '对应batch资源的id',
                                               `resource_type` int(11) DEFAULT NULL COMMENT '使用资源的类型 1:主体资源, 2:引用资源',
                                               `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                               `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                               `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                               `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                               PRIMARY KEY (`id`),
                                               UNIQUE KEY `index_project_task_resource_shade_id` (`task_id`,`resource_id`,`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务资源关联信息- 提交表';

-- ----------------------------
-- Table structure for develop_task_task
-- ----------------------------
DROP TABLE IF EXISTS `develop_task_task`;
CREATE TABLE `develop_task_task` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                     `parent_task_id` int(11) DEFAULT NULL COMMENT '对应batch任务父节点的id',
                                     `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                     `parent_apptype` int(2) NOT NULL DEFAULT '1' COMMENT '对应任务父节点的产品类型',
                                     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                     `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `index_batch_task_task` (`parent_task_id`,`task_id`,`parent_apptype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务上下游关联关系表';

-- ----------------------------
-- Table structure for develop_task_version
-- ----------------------------
DROP TABLE IF EXISTS `develop_task_version`;
CREATE TABLE `develop_task_version` (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                        `task_id` int(11) NOT NULL COMMENT '父文件夹id',
                                        `origin_sql` longtext COLLATE utf8_bin COMMENT '原始sql',
                                        `sql_text` longtext COLLATE utf8_bin NOT NULL COMMENT 'sql 文本',
                                        `publish_desc` text COLLATE utf8_bin NOT NULL COMMENT '任务参数',
                                        `create_user_id` int(11) NOT NULL COMMENT '新建的用户',
                                        `version` int(11) NOT NULL DEFAULT '0' COMMENT 'task版本',
                                        `task_params` text COLLATE utf8_bin NOT NULL COMMENT '任务参数',
                                        `schedule_conf` varchar(512) COLLATE utf8_bin NOT NULL COMMENT '调度配置 json格式',
                                        `schedule_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未开始,1正常调度,2暂停',
                                        `dependency_task_ids` text COLLATE utf8_bin NOT NULL COMMENT '依赖的任务id，多个以,号隔开',
                                        `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                        `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务具体版本信息表';

-- ----------------------------
-- Table structure for develop_tenant_component
-- ----------------------------
DROP TABLE IF EXISTS `develop_tenant_component`;
CREATE TABLE `develop_tenant_component` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                            `task_type` tinyint(1) NOT NULL COMMENT '任务类型',
                                            `component_identity` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '组件的标识信息，也就是组件配置的dbname',
                                            `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '项目状态0：初始化，1：正常,2:禁用,3:失败',
                                            `create_user_id` int(11) DEFAULT NULL COMMENT '创建人id',
                                            `modify_user_id` int(11) DEFAULT NULL COMMENT '修改人id',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='项目与engine的关联关系表';

-- ----------------------------
-- Table structure for dict
-- ----------------------------
DROP TABLE IF EXISTS `dict`;
CREATE TABLE `dict` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `dict_code` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '字典标识',
                        `dict_name` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '字典名称',
                        `dict_value` text COLLATE utf8_bin COMMENT '字典值',
                        `dict_desc` text COLLATE utf8_bin COMMENT '字典描述',
                        `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '枚举值',
                        `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
                        `data_type` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT 'STRING' COMMENT '数据类型',
                        `depend_name` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '依赖字典名称',
                        `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为默认值选项',
                        `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                        `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                        PRIMARY KEY (`id`),
                        KEY `index_dict_code` (`dict_code`),
                        KEY `index_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=169 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='通用数据字典';

-- ----------------------------
-- Records of dict
-- ----------------------------
BEGIN;
INSERT INTO `dict` VALUES (1, 'spark_version', '2.1', '210', NULL, 2, 1, 'INTEGER', '', 1, '2021-03-02 14:15:23', '2021-03-02 14:15:23', 0);
INSERT INTO `dict` VALUES (3, 'spark_thrift_version', '1.x', '1.x', NULL, 3, 1, 'STRING', '', 0, '2021-03-02 14:16:41', '2021-03-02 14:16:41', 0);
INSERT INTO `dict` VALUES (7, 'spark_thrift_version', '2.x', '2.x', NULL, 3, 2, 'STRING', '', 1, '2021-03-02 14:16:41', '2021-03-02 14:16:41', 0);
INSERT INTO `dict` VALUES (9, 'hadoop_config', 'HDP 3.1.x', '-200', '', 5, 0, 'LONG', 'SPARK', 0, '2021-02-05 11:53:21', '2021-02-05 11:53:21', 0);
INSERT INTO `dict` VALUES (11, 'typename_mapping', 'yarn3-hdfs3-spark210', '-108', NULL, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:23', '2021-03-04 17:50:23', 0);
INSERT INTO `dict` VALUES (13, 'typename_mapping', 'yarn2-hdfs2-spark210', '-108', NULL, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO `dict` VALUES (15, 'typename_mapping', 'yarn3-hdfs3-flink110', '-109', NULL, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO `dict` VALUES (17, 'typename_mapping', 'dummy', '-101', NULL, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO `dict` VALUES (19, 'typename_mapping', 'yarn2-hdfs2-flink110', '-109', NULL, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO `dict` VALUES (21, 'typename_mapping', 'hive', '-117', NULL, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO `dict` VALUES (23, 'typename_mapping', 'hive2', '-117', NULL, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO `dict` VALUES (25, 'typename_mapping', 'hive3', '-117', NULL, 6, 0, 'LONG', '', 0, '2021-03-04 17:50:24', '2021-03-04 17:50:24', 0);
INSERT INTO `dict` VALUES (27, 'hadoop_version', 'Apache Hadoop 2.x', '2.7.6', NULL, 0, 1, 'STRING', 'Apache Hadoop', 0, '2021-12-28 10:18:58', '2021-12-28 10:18:58', 0);
INSERT INTO `dict` VALUES (29, 'hadoop_version', 'Apache Hadoop 3.x', '3.0.0', NULL, 0, 2, 'STRING', 'Apache Hadoop', 0, '2021-12-28 10:18:58', '2021-12-28 10:18:58', 0);
INSERT INTO `dict` VALUES (31, 'hadoop_version', 'HDP 2.6.x', '2.7.3', NULL, 0, 1, 'STRING', 'HDP', 0, '2021-12-28 10:18:59', '2021-12-28 10:18:59', 0);
INSERT INTO `dict` VALUES (33, 'hadoop_version', 'HDP 3.x', '3.1.1', NULL, 0, 2, 'STRING', 'HDP', 0, '2021-12-28 10:18:59', '2021-12-28 10:18:59', 0);
INSERT INTO `dict` VALUES (35, 'hadoop_version', 'CDH 5.x', '2.3.0', NULL, 0, 1, 'STRING', 'CDH', 0, '2021-12-28 10:19:00', '2021-12-28 10:19:00', 0);
INSERT INTO `dict` VALUES (37, 'hadoop_version', 'CDH 6.0.x', '3.0.0', NULL, 0, 11, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO `dict` VALUES (39, 'hadoop_version', 'CDH 6.1.x', '3.0.0', NULL, 0, 12, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO `dict` VALUES (41, 'hadoop_version', 'CDH 6.2.x', '3.0.0', NULL, 0, 13, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO `dict` VALUES (43, 'hadoop_version', 'CDP 7.x', '3.1.1', NULL, 0, 15, 'STRING', 'CDH', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO `dict` VALUES (45, 'hadoop_version', 'TDH 5.2.x', '2.7.0', NULL, 0, 1, 'STRING', 'TDH', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO `dict` VALUES (47, 'hadoop_version', 'TDH 7.x', '2.7.0', NULL, 0, 2, 'STRING', 'TDH', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO `dict` VALUES (49, 'hadoop_version', 'TDH 6.x', '2.7.0', NULL, 0, 1, 'STRING', 'TDH', 0, '2021-12-28 11:44:02', '2021-12-28 11:44:02', 0);
INSERT INTO `dict` VALUES (51, 'component_model', 'HDFS', '{\n	\"owner\": \"STORAGE\",\n	\"dependsOn\": [\"RESOURCE\"],\n	\"allowCoexistence\": false,\n	\"versionDictionary\": \"HADOOP_VERSION\"\n}', NULL, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO `dict` VALUES (53, 'component_model', 'FLINK', '{\n	\"owner\": \"COMPUTE\",\n	\"dependsOn\": [\"RESOURCE\", \"STORAGE\"],\n	\"allowCoexistence\": true,\n	\"versionDictionary\": \"FLINK_VERSION\"\n}', NULL, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO `dict` VALUES (55, 'component_model', 'SPARK', '{\n	\"owner\": \"COMPUTE\",\n	\"dependsOn\": [\"RESOURCE\", \"STORAGE\"],\n	\"allowCoexistence\": true,\n	\"versionDictionary\": \"SPARK_VERSION\"\n}', NULL, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-28 16:54:54', 0);
INSERT INTO `dict` VALUES (57, 'component_model', 'SPARK_THRIFT', '{\n	\"owner\": \"COMPUTE\",\n	\"dependsOn\": [\"RESOURCE\", \"STORAGE\"],\n	\"allowCoexistence\": false,\n	\"versionDictionary\": \"SPARK_THRIFT_VERSION\"\n}', NULL, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO `dict` VALUES (59, 'component_model', 'HIVE_SERVER', '{\n	\"owner\": \"COMPUTE\",\n    \"dependsOn\": [\"RESOURCE\", \"STORAGE\"],\n	\"allowCoexistence\": false,\n	\"versionDictionary\": \"HIVE_VERSION\"\n}', NULL, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO `dict` VALUES (61, 'component_model', 'SFTP', '{\n	\"owner\": \"COMMON\",\n	\"dependsOn\": [],\n	\"allowCoexistence\": false,\n	\"nameTemplate\": \"dummy\"\n}', NULL, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO `dict` VALUES (63, 'component_model', 'YARN', '{\n	\"owner\": \"RESOURCE\",\n	\"dependsOn\": [],\n	\"allowCoexistence\": false,\n	\"versionDictionary\": \"HADOOP_VERSION\"\n}', NULL, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO `dict` VALUES (91, 'component_model_config', '1.x', '{\n    \"1.x\":\"hive\"\n}', NULL, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO `dict` VALUES (93, 'component_model_config', '2.x', '{\n    \"2.x\":\"hive2\"\n}', NULL, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO `dict` VALUES (95, 'component_model_config', '3.x-apache', '{\n    \"3.x-apache\":\"hive3\"\n}', NULL, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO `dict` VALUES (97, 'component_model_config', '3.x-cdp', '{\n    \"3.x-cdp\":\"hive3\"\n}', NULL, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO `dict` VALUES (99, 'component_model_config', '1.x', '{\n    \"1.x\":\"hive\"\n}', NULL, 14, 1, 'STRING', 'SPARK_THRIFT', 0, '2021-12-31 15:00:16', '2021-12-31 15:00:16', 0);
INSERT INTO `dict` VALUES (101, 'component_model_config', '2.x', '{\n    \"2.x\":\"hive2\"\n}', NULL, 14, 1, 'STRING', 'SPARK_THRIFT', 0, '2021-12-31 15:00:16', '2021-12-31 15:00:16', 0);
INSERT INTO `dict` VALUES (103, 'SPARK_SQL', 'SPARK_SQL', '0', 'SparkSQL', 30, 1, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO `dict` VALUES (105, 'SYNC', '数据同步', '2', '数据同步', 30, 5, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO `dict` VALUES (107, 'VIRTUAL', '虚节点', '-1', '虚节点', 30, 11, 'STRING', '', 1, '2022-02-11 10:28:45', '2022-02-11 10:28:45', 0);
INSERT INTO `dict` VALUES (109, 'ResourceManager', 'ResourceManager', '3', '资源管理', 31, 3, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-02-11 10:40:14', 0);
INSERT INTO `dict` VALUES (111, 'SparkSQLFunction', 'SparkSQLFunction', '4', 'SparkSQL', 31, 4, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-02-11 10:40:14', 0);
INSERT INTO `dict` VALUES (113, 'TableQuery', 'TableQuery', '5', '表查询', 31, 5, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-02-11 10:40:14', 0);
INSERT INTO `dict` VALUES (115, 'TaskDevelop', 'TaskDevelop', '1', '任务开发', 31, 1, 'STRING', '', 1, '2022-02-11 10:40:14', '2022-02-11 10:40:14', 0);
INSERT INTO `dict` VALUES (117, 'FunctionManager', 'FunctionManager', '4', '函数管理', 32, 4, 'STRING', '', 1, '2022-02-11 10:42:19', '2022-02-11 10:42:19', 0);
INSERT INTO `dict` VALUES (119, 'ResourceManager', 'ResourceManager', '3', '资源管理', 32, 3, 'STRING', '', 1, '2022-02-11 10:42:19', '2022-02-11 10:42:19', 0);
INSERT INTO `dict` VALUES (121, 'TaskManager', 'TaskManager', '1', '任务管理', 32, 1, 'STRING', '', 1, '2022-02-11 10:42:19', '2022-02-11 10:42:19', 0);
INSERT INTO `dict` VALUES (123, 'CustomFunction', 'CustomFunction', '6', '自定义函数', 33, 4, 'STRING', '', 1, '2022-02-11 10:42:57', '2022-02-11 10:42:57', 0);
INSERT INTO `dict` VALUES (125, 'SystemFunction', 'SystemFunction', '6', '系统函数', 33, 2, 'STRING', '', 1, '2022-02-11 10:42:57', '2022-02-11 10:42:57', 0);
INSERT INTO `dict` VALUES (127, 'flink_version', '1.12', '112', NULL, 1, 2, 'INTEGER', '0,1,2', 0, '2022-05-03 22:13:12', '2022-05-03 22:13:12', 0);
INSERT INTO `dict` VALUES (129, 'component_model_config', 'Apache Hadoop 2.x', '{\n    \"YARN\":\"yarn2\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn2-hdfs2-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn2-hdfs2-spark210\",\n                \"2.4\":\"yarn2-hdfs2-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn2-hdfs2-dtscript\",\n        \"HDFS\":\"yarn2-hdfs2-hadoop2\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:01:55', '2021-12-28 11:01:55', 0);
INSERT INTO `dict` VALUES (131, 'component_model_config', 'Apache Hadoop 3.x', '{\n    \"YARN\":\"yarn3\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn3-hdfs3-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn3-hdfs3-spark210\",\n                \"2.4\":\"yarn3-hdfs3-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn3-hdfs3-dtscript\",\n        \"HDFS\":\"yarn3-hdfs3-hadoop3\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:03:45', '2021-12-28 11:03:45', 0);
INSERT INTO `dict` VALUES (133, 'component_model_config', 'HDP 3.0.x', '{\n    \"YARN\":\"yarn3\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn3-hdfs3-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn3-hdfs3-spark210\",\n                \"2.4\":\"yarn3-hdfs3-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn3-hdfs3-dtscript\",\n        \"HDFS\":\"yarn3-hdfs3-hadoop3\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:23', '2021-12-28 11:04:23', 0);
INSERT INTO `dict` VALUES (135, 'component_model_config', 'CDH 6.0.x', '{\n    \"YARN\":\"yarn3\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.8\":\"yarn3-hdfs3-flink180\"\n            },\n            {\n                \"1.10\":\"yarn3-hdfs3-flink110\"\n            },\n            {\n                \"1.12\":\"yarn3-hdfs3-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {   \"2.1\":\"yarn3-hdfs3-spark210\",\n                \"2.4\":\"yarn3-hdfs3-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn3-hdfs3-dtscript\",\n        \"HDFS\":\"yarn3-hdfs3-hadoop3\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:40', '2021-12-28 11:04:40', 0);
INSERT INTO `dict` VALUES (137, 'component_model_config', 'CDH 6.1.x', '{\n    \"YARN\":\"yarn3\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn3-hdfs3-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn3-hdfs3-spark210\",\n                \"2.4\":\"yarn3-hdfs3-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn3-hdfs3-dtscript\",\n        \"HDFS\":\"yarn3-hdfs3-hadoop3\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:55', '2021-12-28 11:04:55', 0);
INSERT INTO `dict` VALUES (139, 'component_model_config', 'CDH 6.2.x', '{\n    \"YARN\":\"yarn3\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.8\":\"yarn3-hdfs3-flink180\"\n            },\n            {\n                \"1.10\":\"yarn3-hdfs3-flink110\"\n            },\n            {\n                \"1.12\":\"yarn3-hdfs3-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn3-hdfs3-spark210\",\n                \"2.4(CDH 6.2)\":\"yarn3-hdfs3-spark240cdh620\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn3-hdfs3-dtscript\",\n        \"HDFS\":\"yarn3-hdfs3-hadoop3\",\n        \"TONY\":\"yarn3-hdfs3-tony\",\n        \"LEARNING\":\"yarn3-hdfs3-learning\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:05:06', '2021-12-28 11:05:06', 0);
INSERT INTO `dict` VALUES (141, 'component_model_config', 'HDP 2.6.x', '{\n    \"YARN\":\"yarn2\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn2-hdfs2-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn2-hdfs2-spark210\",\n                \"2.4\":\"yarn2-hdfs2-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn2-hdfs2-dtscript\",\n        \"HDFS\":\"yarn2-hdfs2-hadoop2\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:06:38', '2021-12-28 11:06:38', 0);
INSERT INTO `dict` VALUES (143, 'component_model_config', 'CDH 5.x', '{\n    \"YARN\":\"yarn2\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn2-hdfs2-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn2-hdfs2-spark210\",\n                \"2.4\":\"yarn2-hdfs2-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn2-hdfs2-dtscript\",\n        \"HDFS\":\"yarn2-hdfs2-hadoop2\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:07:19', '2021-12-28 11:07:19', 0);
INSERT INTO `dict` VALUES (145, 'component_model_config', 'HDP 3.x', '{\n    \"YARN\":\"yarn3\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn3-hdfs3-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn3-hdfs3-spark210\",\n                \"2.4\":\"yarn3-hdfs3-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn3-hdfs3-dtscript\",\n        \"HDFS\":\"yarn3-hdfs3-hadoop3\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:43:05', '2021-12-28 11:43:05', 0);
INSERT INTO `dict` VALUES (147, 'component_model_config', 'TDH 5.2.x', '{\n    \"YARN\":\"yarn2\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn2-hdfs2-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn2-hdfs2-spark210\",\n                \"2.4\":\"yarn2-hdfs2-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn2-hdfs2-dtscript\",\n        \"HDFS\":\"yarn2-hdfs2-hadoop2\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:33', '2021-12-28 11:44:33', 0);
INSERT INTO `dict` VALUES (149, 'component_model_config', 'TDH 6.x', '{\n    \"YARN\":\"yarn2\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn2-hdfs2-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn2-hdfs2-spark210\",\n                \"2.4\":\"yarn2-hdfs2-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn2-hdfs2-dtscript\",\n        \"HDFS\":\"yarn2-hdfs2-hadoop2\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:43', '2021-12-28 11:44:43', 0);
INSERT INTO `dict` VALUES (151, 'component_model_config', 'TDH 7.x', '{\n    \"YARN\":\"yarn2\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn2-hdfs2-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn2-hdfs2-spark210\",\n                \"2.4\":\"yarn2-hdfs2-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn2-hdfs2-dtscript\",\n        \"HDFS\":\"yarn2-hdfs2-hadoop2\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO `dict` VALUES (153, 'component_model_config', 'CDP 7.x', '{\n    \"YARN\":\"yarn3\",\n    \"HDFS\":{\n        \"FLINK\":[\n            {\n                \"1.12\":\"yarn3-hdfs3-flink112\"\n            }\n        ],\n        \"SPARK\":[\n            {\n                \"2.1\":\"yarn3-hdfs3-spark210\",\n                \"2.4\":\"yarn3-hdfs3-spark240\"\n            }\n        ],\n        \"DT_SCRIPT\":\"yarn3-hdfs3-dtscript\",\n        \"HDFS\":\"yarn3-hdfs3-hadoop3\"\n    }\n}', NULL, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO `dict` VALUES (155, 'typename_mapping', 'yarn2-hdfs2-flink112', '-115', NULL, 6, 0, 'LONG', '', 0, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO `dict` VALUES (157, 'typename_mapping', 'yarn3-hdfs3-flink112', '-115', NULL, 6, 0, 'LONG', '', 0, '2021-05-18 11:29:00', '2021-05-18 11:29:00', 0);
INSERT INTO `dict` VALUES (159, 'hive_version', '1.x', '1.x', NULL, 4, 1, 'STRING', '', 0, '2022-05-03 22:20:53', '2022-05-03 22:20:53', 0);
INSERT INTO `dict` VALUES (161, 'hive_version', '2.x', '2.x', NULL, 4, 2, 'STRING', '', 1, '2022-05-03 22:20:54', '2022-05-03 22:20:54', 0);
INSERT INTO `dict` VALUES (163, 'hive_version', '3.x-apache', '3.x-apache', NULL, 4, 3, 'STRING', '', 1, '2022-05-03 22:20:54', '2022-05-03 22:20:54', 0);
INSERT INTO `dict` VALUES (165, 'hive_version', '3.x-cdp', '3.x-cdp', NULL, 4, 3, 'STRING', '', 1, '2022-05-03 22:20:55', '2022-05-03 22:20:55', 0);
INSERT INTO `dict` VALUES (167, 'FlinkSQLFunction', 'FlinkSQLFunction', '4', 'FlinkSQL', 31, 4, 'STRING', '', 1, '2022-05-03 22:21:10', '2022-05-03 22:21:10', 0);
COMMIT;

-- ----------------------------
-- Table structure for schedule_engine_job_cache
-- ----------------------------
DROP TABLE IF EXISTS `schedule_engine_job_cache`;
CREATE TABLE `schedule_engine_job_cache` (
                                             `id` int(11) NOT NULL AUTO_INCREMENT,
                                             `job_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '任务id',
                                             `job_name` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '任务名称',
                                             `compute_type` tinyint(2) NOT NULL COMMENT '计算类型stream/batch',
                                             `stage` tinyint(2) NOT NULL COMMENT '处于master等待队列：1 还是exe等待队列 2',
                                             `job_info` longtext COLLATE utf8_bin NOT NULL COMMENT 'job信息',
                                             `node_address` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '节点地址',
                                             `job_resource` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT 'job的计算引擎资源类型',
                                             `job_priority` bigint(20) DEFAULT NULL COMMENT '任务优先级',
                                             `is_failover` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：不是，1：由故障恢复来的任务',
                                             `wait_reason` text COLLATE utf8_bin COMMENT '任务等待原因',
                                             `tenant_id` int(11) DEFAULT NULL COMMENT '租户id',
                                             `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                             `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                             `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                             PRIMARY KEY (`id`),
                                             UNIQUE KEY `index_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_engine_job_retry
-- ----------------------------
DROP TABLE IF EXISTS `schedule_engine_job_retry`;
CREATE TABLE `schedule_engine_job_retry` (
                                             `id` int(11) NOT NULL AUTO_INCREMENT,
                                             `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
                                             `job_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '离线任务id',
                                             `engine_job_id` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '离线任务计算引擎id',
                                             `application_id` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
                                             `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
                                             `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
                                             `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数',
                                             `log_info` mediumtext COLLATE utf8_bin COMMENT '错误信息',
                                             `engine_log` longtext COLLATE utf8_bin COMMENT '引擎错误信息',
                                             `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                             `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                             `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                             `retry_task_params` text COLLATE utf8_bin COMMENT '重试任务参数',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_job_id` (`job_id`) COMMENT '任务实例 id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_fill_data_job
-- ----------------------------
DROP TABLE IF EXISTS `schedule_fill_data_job`;
CREATE TABLE `schedule_fill_data_job` (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                          `job_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '补数据任务名称',
                                          `run_day` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '补数据运行日期yyyy-MM-dd',
                                          `from_day` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '补数据开始业务日期yyyy-MM-dd',
                                          `to_day` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '补数据结束业务日期yyyy-MM-dd',
                                          `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `create_user_id` int(11) NOT NULL COMMENT '发起操作的用户',
                                          `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                          `fill_data_info` mediumtext COLLATE utf8_bin COMMENT '补数据信息',
                                          `fill_generate_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '补数据生成状态：0默认值，按照原来的接口逻辑走。1 表示正在生成，2 完成生成补数据实例，3生成补数据失败',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `index_task_id` (`tenant_id`,`job_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_job
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job`;
CREATE TABLE `schedule_job` (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                `job_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '工作任务id',
                                `job_key` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '工作任务key',
                                `job_name` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '工作任务名称',
                                `task_id` int(11) NOT NULL COMMENT '任务id',
                                `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                `create_user_id` int(11) NOT NULL COMMENT '发起操作的用户',
                                `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                `type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '0正常调度 1补数据 2临时运行',
                                `is_restart` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：非重启任务, 1：重启任务',
                                `cyc_time` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '调度时间 yyyyMMddHHmmss',
                                `dependency_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '依赖类型',
                                `flow_job_id` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '0' COMMENT '工作流实例id',
                                `period_type` tinyint(2) DEFAULT NULL COMMENT '周期类型',
                                `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
                                `task_type` tinyint(1) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
                                `fill_id` int(11) DEFAULT '0' COMMENT '补数据id，默认为0',
                                `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
                                `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
                                `exec_time` int(11) DEFAULT '0' COMMENT '执行时间',
                                `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
                                `max_retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '最大重试次数',
                                `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '执行时，重试的次数',
                                `node_address` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '节点地址',
                                `version_id` int(10) DEFAULT '0' COMMENT '任务运行时候版本号',
                                `next_cyc_time` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '下一次调度时间 yyyyMMddHHmmss',
                                `engine_job_id` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '离线任务计算引擎id',
                                `application_id` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
                                `compute_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '计算类型STREAM(0), BATCH(1)',
                                `phase_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '运行状态: CREATE(0):创建,JOIN_THE_TEAM(1):入队,LEAVE_THE_TEAM(2):出队',
                                `job_execute_order` bigint(20) NOT NULL DEFAULT '0' COMMENT '按照计算时间排序字段',
                                `fill_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0 默认值 周期实例，立即运行等非补数据实例的默认值 1 可执行补数据实例 2 中间实例 3 黑名单',
                                `submit_user_name` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '提交用户名',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `idx_jobKey` (`job_key`),
                                UNIQUE KEY `index_job_id` (`job_id`,`is_deleted`),
                                KEY `idx_cyc_time` (`cyc_time`),
                                KEY `idx_exec_start_time` (`exec_start_time`),
                                KEY `idx_name_type` (`job_name`(128),`type`),
                                KEY `index_engine_job_id` (`engine_job_id`(128)),
                                KEY `index_fill_id` (`fill_id`),
                                KEY `index_flow_job_id` (`flow_job_id`),
                                KEY `index_gmt_modified` (`gmt_modified`),
                                KEY `index_job_execute_order` (`job_execute_order`),
                                KEY `index_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_job_expand
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_expand`;
CREATE TABLE `schedule_job_expand` (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `job_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '工作任务id',
                                       `retry_task_params` mediumtext COLLATE utf8_bin COMMENT '重试任务参数',
                                       `job_graph` mediumtext COLLATE utf8_bin COMMENT 'jobGraph构建json',
                                       `job_extra_info` mediumtext COLLATE utf8_bin COMMENT '任务提交额外信息',
                                       `engine_log` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
                                       `log_info` longtext COLLATE utf8_bin COMMENT '错误信息',
                                       `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                       `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `index_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_job_graph_trigger
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_graph_trigger`;
CREATE TABLE `schedule_job_graph_trigger` (
                                              `id` int(11) NOT NULL AUTO_INCREMENT,
                                              `trigger_type` tinyint(3) NOT NULL COMMENT '0:正常调度 1补数据',
                                              `trigger_time` datetime NOT NULL COMMENT '调度时间',
                                              `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                              `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                              `is_deleted` int(10) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `index_trigger_time` (`trigger_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_job_history
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_history`;
CREATE TABLE `schedule_job_history` (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `job_id` varchar(32) NOT NULL COMMENT '工作任务id',
                                        `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
                                        `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
                                        `engine_job_id` varchar(256) DEFAULT NULL COMMENT '额外id',
                                        `application_id` varchar(256) DEFAULT NULL COMMENT 'applicationId',
                                        `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                        `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                        PRIMARY KEY (`id`),
                                        KEY `index_engine_job_id` (`engine_job_id`(128)),
                                        KEY `index_job_id` (`job_id`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for schedule_job_job
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_job`;
CREATE TABLE `schedule_job_job` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                    `job_key` varchar(256) COLLATE utf8_bin NOT NULL COMMENT 'batch 任务key',
                                    `parent_job_key` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '对应batch任务父节点的key',
                                    `job_key_type` int(11) NOT NULL DEFAULT '2' COMMENT 'parentJobKey类型： RelyType 1. 自依赖实例key 2. 上游任务key 3. 上游任务的下一个周期key',
                                    `rule` int(11) DEFAULT NULL COMMENT 'parentJobKey类型： RelyType 1. 自依赖实例key 2. 上游任务key 3. 上游任务的下一个周期key',
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_job_jobKey` (`parent_job_key`(128)),
                                    KEY `idx_job_parentJobKey` (`job_key`(255),`parent_job_key`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_job_operator_record
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_operator_record`;
CREATE TABLE `schedule_job_operator_record` (
                                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                                `job_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '任务id',
                                                `version` int(10) DEFAULT '0' COMMENT '版本号',
                                                `operator_expired` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作过期时间',
                                                `operator_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '操作类型 0杀死 1重跑 2 补数据',
                                                `force_cancel_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '强制标志 0非强制 1强制',
                                                `node_address` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '节点地址',
                                                `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                                `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                                `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                                PRIMARY KEY (`id`),
                                                UNIQUE KEY `job_id` (`job_id`,`operator_type`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_plugin_job_info
-- ----------------------------
DROP TABLE IF EXISTS `schedule_plugin_job_info`;
CREATE TABLE `schedule_plugin_job_info` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `job_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '任务id',
                                            `job_info` longtext COLLATE utf8_bin NOT NULL COMMENT '任务信息',
                                            `log_info` text COLLATE utf8_bin COMMENT '任务信息',
                                            `status` tinyint(2) NOT NULL COMMENT '任务状态',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `index_job_id` (`job_id`),
                                            KEY `idx_gmt_modified` (`gmt_modified`) COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_task_shade
-- ----------------------------
DROP TABLE IF EXISTS `schedule_task_shade`;
CREATE TABLE `schedule_task_shade` (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `tenant_id` int(11) NOT NULL DEFAULT '-1' COMMENT '租户id',
                                       `name` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '任务名称',
                                       `task_type` tinyint(1) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
                                       `compute_type` tinyint(1) NOT NULL COMMENT '计算类型 0实时，1 离线',
                                       `sql_text` longtext COLLATE utf8_bin NOT NULL COMMENT 'sql 文本',
                                       `task_params` text COLLATE utf8_bin NOT NULL COMMENT '任务参数',
                                       `task_id` int(11) NOT NULL COMMENT '任务id',
                                       `schedule_conf` varchar(512) COLLATE utf8_bin NOT NULL COMMENT '调度配置 json格式',
                                       `period_type` tinyint(2) DEFAULT NULL COMMENT '周期类型',
                                       `schedule_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未开始,1正常调度,2暂停',
                                       `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                       `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `modify_user_id` int(11) NOT NULL COMMENT '最后修改task的用户',
                                       `create_user_id` int(11) NOT NULL COMMENT '新建task的用户',
                                       `version_id` int(11) NOT NULL DEFAULT '0' COMMENT 'task版本',
                                       `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                       `task_desc` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '任务描述',
                                       `exe_args` text COLLATE utf8_bin COMMENT '额外参数',
                                       `flow_id` int(11) NOT NULL DEFAULT '0' COMMENT '工作流id',
                                       `component_version` varchar(25) COLLATE utf8_bin DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `index_task_id` (`task_id`),
                                       KEY `index_name` (`name`(128))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_task_shade_info
-- ----------------------------
DROP TABLE IF EXISTS `schedule_task_shade_info`;
CREATE TABLE `schedule_task_shade_info` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `task_id` int(11) NOT NULL COMMENT '任务id',
                                            `info` text COLLATE utf8_bin COMMENT '任务运行信息',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `index_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for schedule_task_task_shade
-- ----------------------------
DROP TABLE IF EXISTS `schedule_task_task_shade`;
CREATE TABLE `schedule_task_task_shade` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `tenant_id` int(11) NOT NULL COMMENT '租户id',
                                            `task_id` int(11) NOT NULL COMMENT 'batch 任务id',
                                            `parent_task_id` int(11) DEFAULT NULL COMMENT '对应batch任务父节点的id',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `index_batch_task_task` (`task_id`,`parent_task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for task_template
-- ----------------------------
DROP TABLE IF EXISTS `task_template`;
CREATE TABLE `task_template` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `task_type` int(11) DEFAULT '0' COMMENT '任务类型',
                                 `type` int(11) DEFAULT NULL COMMENT '业务类型',
                                 `value_type` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '模版值类型',
                                 `content` text COLLATE utf8_bin COMMENT '模版值',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of task_template
-- ----------------------------
BEGIN;
INSERT INTO `task_template` VALUES (1, 0, 1, '1', 'create table if not exists ods_order_header (\n     order_header_id     string comment \'订单头id\'\n    ,order_date          bigint comment \'订单日期\'\n    ,shop_id             string comment \'店铺id\'\n    ,customer_id         string comment \'客户id\'\n    ,order_status        bigint comment \'订单状态\'\n    ,pay_date            bigint comment \'支付日期\'\n\n)comment \'销售订单明细表\'\nPARTITIONED BY (ds string) ;\n\ncreate table if not exists ods_order_detail (\n     order_header_id     string comment \'订单头id\'\n    ,order_detail_id     string comment \'订单明细id\'\n    ,item_id             string comment \'商品id\'\n    ,quantity            double comment \'商品数量\'\n    ,unit_price          double comment \'商品单价\'\n    ,dist_amout          double comment \'折扣金额\'\n)comment \'销售订单明细表\'\nPARTITIONED BY (ds string) ;\n\n\ncreate table if not exists exam_ods_shop_info (\n     shop_id                string comment \'店铺id\'\n    ,shop_name              string comment \'店铺名称\'\n    ,shop_type              string comment \'店铺类型\'\n    ,address                string comment \'店铺地址\'\n    ,status                 string comment \'店铺状态,open/closed\'\n)comment \'店铺维度表\'\nPARTITIONED BY (ds string) ;', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (3, 0, 1, '2', 'create table if not exists exam_dwd_sales_ord_df (\n     order_header_id     string comment \'订单头id\'\n    ,order_detail_id     string comment \'订单明细id\'\n    ,order_date          bigint comment \'订单日期\'\n    ,pay_date            bigint comment \'付款日期\'\n    ,shop_id             string comment \'店铺id\'\n    ,customer_id         string comment \'客户id\'\n    ,item_id             string comment \'商品id\'\n    ,quantity            bigint comment \'商品数量\'\n    ,unit_price          double comment \'商品单价\'\n    ,amount              double comment \'总金额\'\n)comment \'销售订单明细表\'\nPARTITIONED BY (ds string) ;\n\n\nINSERT OVERWRITE TABLE exam_dwd_sales_ord_df PARTITION(ds = \'${bdp.system.bizdate}\')\nselect\n d.order_header_id\n,d.order_detail_id\n,h.order_date\n,h.pay_date\n,h.shop_id\n,h.customer_id\n,d.item_id\n,d.quantity\n,d.unit_price\n,d.quantity*d.unit_price-d.dist_amout as amount\nfrom ods_order_header as h join ods_order_detail as d on h.order_header_id = d.order_header_id\nwhere h.ds = \'${bdp.system.bizdate}\' and d.ds= \'${bdp.system.bizdate}\'\nand h.order_status = 0;\n', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (5, 0, 1, '3', 'create table if not exists exam_dws_sales_shop_1d (\n     stat_date              string comment \'统计日期\'\n    ,shop_id                string comment \'订单明细id\'\n    ,ord_quantity_1d        bigint comment \'最近一天订单数量\'\n    ,ord_amount_1d          double comment \'最近一天订单金额\'\n    ,pay_quantity_1d        bigint comment \'最近一天付款数量\'\n    ,pay_amount_1d          double comment \'最近一天付款金额\'\n)comment \'最近一天门店粒度销售汇总表\'\nPARTITIONED BY (ds string) ;\n\nINSERT OVERWRITE TABLE exam_dws_sales_shop_1d PARTITION(ds = \'${bdp.system.bizdate}\')\nselect\n \'${bdp.system.bizdate}\' as stat_date\n,shop_id\n,sum(case when order_date = \'${bdp.system.bizdate}\' then quantity end) as ord_quantity_1d\n,sum(case when order_date = \'${bdp.system.bizdate}\' then amount end)   as ord_amount_1d\n,sum(case when pay_date = \'${bdp.system.bizdate}\'   then quantity end) as pay_quantity_1d\n,sum(case when pay_date = \'${bdp.system.bizdate}\'   then amount end)   as pay_amount_1d\nfrom\nexam_dwd_sales_ord_df\nwhere ds = \'${bdp.system.bizdate}\'\ngroup by shop_id;', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (7, 0, 1, '4', 'create table if not exists exam_ads_sales_all_d (\n     stat_date              string comment \'统计日期\'\n    ,ord_quantity           bigint comment \'订单数量\'\n    ,ord_amount             double comment \'订单金额\'\n    ,pay_quantity           bigint comment \'付款数量\'\n    ,pay_amount             double comment \'付款金额\'\n    ,shop_cnt               bigint comment \'有交易的店铺数量\'\n)comment \'订单交易总表\'\nPARTITIONED BY (ds string) lifecycle 7;\n\nINSERT OVERWRITE TABLE exam_ads_sales_all_d PARTITION(ds = \'${bdp.system.bizdate}\')\nselect\n \'${bdp.system.bizdate}\' as stat_date\n,sum(ord_quantity_1d) as ord_quantity\n,sum(ord_amount_1d)   as ord_amount\n,sum(pay_quantity_1d) as pay_quantity\n,sum(pay_amount_1d)   as pay_amount\n,count(distinct shop_id) as shop_cnt\nfrom\nexam_dws_sales_shop_1d\nwhere ds = \'${bdp.system.bizdate}\'\ngroup by shop_id;', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (9, 0, 1, '5', 'create table if not exists exam_dim_shop (\n     shop_id                string comment \'店铺id\'\n    ,shop_name              string comment \'店铺名称\'\n    ,shop_type              string comment \'店铺类型\'\n    ,address                string comment \'店铺地址\'\n)comment \'店铺维度表\'\nPARTITIONED BY (ds string) lifecycle 365;\n\nINSERT OVERWRITE TABLE exam_dim_shop PARTITION(ds = \'${bdp.system.bizdate}\')\nselect\n shop_id\n,shop_name\n,shop_type\n,address\nfrom exam_ods_shop_info\nwhere ds = \'${bdp.system.bizdate}\'\nand status = \'open\';', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (11, 15, 1, '0', 'create table if not exists customer_base\n(\n    id                    varchar(20),\n	cust_name             varchar(20),\n	cust_phone            varchar(20),\n	cust_wechat           varchar(20),\n	cust_cefi_number      varchar(20),\n	cust_car_number       varchar(20),\n	cust_house_number     varchar(20),\n	cust_job              varchar(20),\n	cust_company          varchar(20),\n	cust_work_address     varchar(20),\n	cust_bank_number      varchar(20),\n	cust_gate_card        varchar(20)\n);\n-- COMMENT ON customer_base IS \'用户基础表\';\n\n\ncreate table if not exists customer_in_call\n(\n    id                        varchar,\n    in_call_phone_number      varchar,\n    in_call_time              varchar,--本来这个字段是时间戳类型的，但是libra不支持时间戳timestamp关键字，所以改成varchar类型\n    in_call_duration          bigint,\n    in_call_consult_problem   varchar\n);\n-- COMMENT ON customer_in_call IS \'用户来电记录表\';\n\n\ncreate table if not exists customer_in_and_out\n(\n    id                      varchar,\n    cust_gate_card          varchar,\n    in_or_out               varchar,\n    in_or_out_time          varchar\n);\n-- COMMENT ON customer_in_and_out IS \'客户出入记录表\';\n\n\ncreate table if not exists customer_complain(\n    id                        varchar,\n    complain_phone            varchar,\n    complain_name             varchar,\n    complain_problem          varchar,\n    complain_time             varchar\n);\n\n-- COMMENT ON customer_complain IS \'客户投诉记录表\';\n\n-- --注意，Libra不支持时间戳类型的关键字\n', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (13, 15, 1, '1', '\ncreate table if not exists ods_order_header (\n    order_header_id     varchar,\n    order_date          bigint,\n    shop_id             bigint,\n    customer_id         varchar,\n    order_status        bigint,\n    pay_date            bigint\n);\n\ncreate table if not exists ods_order_detail (\n    order_header_id     varchar,\n    order_detail_id     varchar,\n    item_id             varchar,\n    quantity            varchar,\n    unit_price          varchar,\n    dist_amout          varchar\n);\n\ncreate table if not exists exam_ods_shop_info (\n    shop_id                bigint,\n    shop_name              varchar,\n    shop_type              varchar,\n    address                varchar,\n    status                 varchar\n);\n', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (15, 15, 1, '2', '\ncreate table if not exists exam_dwd_sales_ord_df (\n    order_header_id     varchar,\n    order_detail_id     varchar,\n    order_date          bigint,\n    pay_date            bigint,\n    shop_id             bigint,\n    customer_id         varchar,\n    item_id             varchar,\n    quantity            varchar,\n    unit_price          varchar,\n    amount              varchar\n);\n\nINSERT INTO exam_dwd_sales_ord_df\nselect\n d.order_header_id\n,d.order_detail_id\n,h.order_date\n,h.pay_date\n,h.shop_id\n,h.customer_id\n,d.item_id\n,d.quantity\n,d.unit_price\nfrom ods_order_header as h join ods_order_detail as d\non h.order_header_id = d.order_header_id and h.order_status = 0;\n', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (17, 15, 1, '3', 'create table if not exists exam_dws_sales_shop_1d (\n     stat_date              string comment \'统计日期\'\n    ,shop_id                string comment \'订单明细id\'\n    ,ord_quantity_1d        bigint comment \'最近一天订单数量\'\n    ,ord_amount_1d          double comment \'最近一天订单金额\'\n    ,pay_quantity_1d        bigint comment \'最近一天付款数量\'\n    ,pay_amount_1d          double comment \'最近一天付款金额\'\n)comment \'最近一天门店粒度销售汇总表\'\nPARTITIONED BY (ds string) ;\n\nINSERT OVERWRITE TABLE exam_dws_sales_shop_1d PARTITION(ds = \'${bdp.system.bizdate}\')\nselect\n \'${bdp.system.bizdate}\' as stat_date\n,shop_id\n,sum(case when order_date = \'${bdp.system.bizdate}\' then quantity end) as ord_quantity_1d\n,sum(case when order_date = \'${bdp.system.bizdate}\' then amount end)   as ord_amount_1d\n,sum(case when pay_date = \'${bdp.system.bizdate}\'   then quantity end) as pay_quantity_1d\n,sum(case when pay_date = \'${bdp.system.bizdate}\'   then amount end)   as pay_amount_1d\nfrom\nexam_dwd_sales_ord_df\nwhere ds = \'${bdp.system.bizdate}\'\ngroup by shop_id;\n', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (19, 15, 1, '4', 'create table if not exists exam_ads_sales_all_d (\n     stat_date              string comment \'统计日期\'\n    ,ord_quantity           bigint comment \'订单数量\'\n    ,ord_amount             double comment \'订单金额\'\n    ,pay_quantity           bigint comment \'付款数量\'\n    ,pay_amount             double comment \'付款金额\'\n    ,shop_cnt               bigint comment \'有交易的店铺数量\'\n)comment \'订单交易总表\'\nPARTITIONED BY (ds string) lifecycle 7;\n\nINSERT OVERWRITE TABLE exam_ads_sales_all_d PARTITION(ds = \'${bdp.system.bizdate}\')\nselect\n \'${bdp.system.bizdate}\' as stat_date\n,sum(ord_quantity_1d) as ord_quantity\n,sum(ord_amount_1d)   as ord_amount\n,sum(pay_quantity_1d) as pay_quantity\n,sum(pay_amount_1d)   as pay_amount\n,count(distinct shop_id) as shop_cnt\nfrom\nexam_dws_sales_shop_1d\nwhere ds = \'${bdp.system.bizdate}\'\ngroup by shop_id;\n', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (21, 15, 1, '5', 'create table if not exists exam_dim_shop (\n     shop_id                string comment \'店铺id\'\n    ,shop_name              string comment \'店铺名称\'\n    ,shop_type              string comment \'店铺类型\'\n    ,address                string comment \'店铺地址\'\n)comment \'店铺维度表\'\nPARTITIONED BY (ds string) lifecycle 365;\n\nINSERT OVERWRITE TABLE exam_dim_shop PARTITION(ds = \'${bdp.system.bizdate}\')\nselect\n shop_id\n,shop_name\n,shop_type\n,address\nfrom exam_ods_shop_info\nwhere ds = \'${bdp.system.bizdate}\'\nand status = \'open\';\n', '2022-04-13 14:30:03', '2022-04-13 14:30:03', 0);
INSERT INTO `task_template` VALUES (31, 0, 0, '2.1', '## Driver程序使用的CPU核数,默认为1\n# driver.cores=1\n\n## Driver程序使用内存大小,默认512m\n# driver.memory=512m\n\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\n# driver.maxResultSize=1g\n\n## 启动的executor的数量，默认为1\nexecutor.instances=1\n\n## 每个executor使用的CPU核数，默认为1\nexecutor.cores=1\n\n## 每个executor内存大小,默认512m\nexecutor.memory=512m\n\n## 任务优先级, 值越小，优先级越高，范围:1-1000\njob.priority=10\n\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\n# logLevel = INFO\n\n## spark中所有网络交互的最大超时时间\n# spark.network.timeout=120s\n\n## executor的OffHeap内存，和spark.executor.memory配置使用\n# spark.yarn.executor.memoryOverhead', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO `task_template` VALUES (33, 2, 0, '1.12', '## 任务运行方式：\n## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步\n## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job\n## flinkTaskRunMode=per_job\n## per_job模式下jobManager配置的内存大小，默认1024（单位M)\n## jobmanager.memory.mb=1024\n## per_job模式下taskManager配置的内存大小，默认1024（单位M）\n## taskmanager.memory.mb=1024\n## per_job模式下每个taskManager 对应 slot的数量\n## slots=1\n## checkpoint保存时间间隔\n## flink.checkpoint.interval=300000\n## 任务优先级, 范围:1-1000\n## job.priority=10', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO `task_template` VALUES (35, 5, 0, '1.12', '## 资源相关\nparallelism.default=1\ntaskmanager.numberOfTaskSlots=1\njobmanager.memory.process.size=1g\ntaskmanager.memory.process.size=2g\n\n## 时间相关\n## 设置Flink时间选项，有ProcessingTime,EventTime,IngestionTime可选\n## 非脚本模式会根据Kafka自动设置。脚本模式默认为ProcessingTime\n# pipeline.time-characteristic=EventTime\n\n## Checkpoint相关\n## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成\nexecution.checkpointing.interval=5min\n## 状态恢复语义,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE\n# execution.checkpointing.mode=EXACTLY_ONCE\n##任务取消后保留hdfs上的checkpoint文件\nexecution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION\n\n# Flink SQL独有，状态过期时间\ntable.exec.state.ttl=1d\n\nlog.level=INFO\n\n## 使用Iceberg和Hive维表开启\n# table.dynamic-table-options.enabled=true\n\n## Kerberos相关\n# security.kerberos.login.contexts=Client,KafkaClient\n\n\n## 高阶参数\n## 窗口提前触发时间\n# table.exec.emit.early-fire.enabled=true\n# table.exec.emit.early-fire.delay=1s\n\n## 当一个源在超时时间内没有收到任何元素时，它将被标记为临时空闲\n# table.exec.source.idle-timeout=10ms\n\n## 是否开启minibatch\n## 可以减少状态开销。这可能会增加一些延迟，因为它会缓冲一些记录而不是立即处理它们。这是吞吐量和延迟之间的权衡\n# table.exec.mini-batch.enabled=true\n## 状态缓存时间\n# table.exec.mini-batch.allow-latency=5s\n## 状态最大缓存条数\n# table.exec.mini-batch.size=5000\n\n## 是否开启Local-Global 聚合。前提需要开启minibatch\n## 聚合是为解决数据倾斜问题提出的，类似于 MapReduce 中的 Combine + Reduce 模式\n# table.optimizer.agg-phase-strategy=TWO_PHASE\n\n## 是否开启拆分 distinct 聚合\n## Local-Global 可以解决数据倾斜，但是在处理 distinct 聚合时，其性能并不令人满意。\n## 如：SELECT day, COUNT(DISTINCT user_id) FROM T GROUP BY day 如果 distinct key （即 user_id）的值分布稀疏，建议开启\n# table.optimizer.distinct-agg.split.enabled=true\n\n\n## Flink算子chaining开关。默认为true。排查性能问题时会暂时设置成false，但降低性能。\n# pipeline.operator-chaining=true', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO `task_template` VALUES (36, 6, 0, '1.12', '## 资源相关\nparallelism.default=1\ntaskmanager.numberOfTaskSlots=1\njobmanager.memory.process.size=1g\ntaskmanager.memory.process.size=2g\n\n## 时间相关\n## 设置Flink时间选项，有ProcessingTime,EventTime,IngestionTime可选\n## 非脚本模式会根据Kafka自动设置。脚本模式默认为ProcessingTime\n# pipeline.time-characteristic=EventTime\n\n## Checkpoint相关\n## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成\nexecution.checkpointing.interval=5min\n## 状态恢复语义,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE\n# execution.checkpointing.mode=EXACTLY_ONCE\n##任务取消后保留hdfs上的checkpoint文件\nexecution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION\n\n# Flink SQL独有，状态过期时间\ntable.exec.state.ttl=1d\n\nlog.level=INFO\n\n## 使用Iceberg和Hive维表开启\n# table.dynamic-table-options.enabled=true\n\n## Kerberos相关\n# security.kerberos.login.contexts=Client,KafkaClient\n\n\n## 高阶参数\n## 窗口提前触发时间\n# table.exec.emit.early-fire.enabled=true\n# table.exec.emit.early-fire.delay=1s\n\n## 当一个源在超时时间内没有收到任何元素时，它将被标记为临时空闲\n# table.exec.source.idle-timeout=10ms\n\n## 是否开启minibatch\n## 可以减少状态开销。这可能会增加一些延迟，因为它会缓冲一些记录而不是立即处理它们。这是吞吐量和延迟之间的权衡\n# table.exec.mini-batch.enabled=true\n## 状态缓存时间\n# table.exec.mini-batch.allow-latency=5s\n## 状态最大缓存条数\n# table.exec.mini-batch.size=5000\n\n## 是否开启Local-Global 聚合。前提需要开启minibatch\n## 聚合是为解决数据倾斜问题提出的，类似于 MapReduce 中的 Combine + Reduce 模式\n# table.optimizer.agg-phase-strategy=TWO_PHASE\n\n## 是否开启拆分 distinct 聚合\n## Local-Global 可以解决数据倾斜，但是在处理 distinct 聚合时，其性能并不令人满意。\n## 如：SELECT day, COUNT(DISTINCT user_id) FROM T GROUP BY day 如果 distinct key （即 user_id）的值分布稀疏，建议开启\n# table.optimizer.distinct-agg.split.enabled=true\n\n\n## Flink算子chaining开关。默认为true。排查性能问题时会暂时设置成false，但降低性能。\n# pipeline.operator-chaining=true', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO `task_template` VALUES (37, 17, 0, '', '## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复\n#hiveconf:mapreduce.job.name=\n\n## 指定mapreduce运行的队列，默认走控制台配置的queue\n# hiveconf:mapreduce.job.queuename=default_queue_name\n\n## hivevar配置,用户自定义变量\n#hivevar:ageParams=30## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复\n#hiveconf:mapreduce.job.name=\n\n## 指定mapreduce运行的队列，默认走控制台配置的queue\n# hiveconf:mapreduce.job.queuename=default_queue_name\n\n## hivevar配置,用户自定义变量\n#hivevar:ageParams=30', '2022-04-13 14:30:53', '2022-04-13 14:30:53', 0);
INSERT INTO `task_template` VALUES (61, 7, 0, '', '## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复\n#hiveconf:mapreduce.job.name=\n\n## 指定mapreduce运行的队列，默认走控制台配置的queue\n# hiveconf:mapreduce.job.queuename=default_queue_name\n\n## hivevar配置,用户自定义变量\n#hivevar:ageParams=30## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复\n#hiveconf:mapreduce.job.name=\n\n## 指定mapreduce运行的队列，默认走控制台配置的queue\n# hiveconf:mapreduce.job.queuename=default_queue_name\n\n## hivevar配置,用户自定义变量\n#hivevar:ageParams=30', '2021-11-18 10:36:13', '2021-11-18 10:36:13', 0);
COMMIT;

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant` (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `tenant_name` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '用户名称',
                          `tenant_desc` varchar(256) COLLATE utf8_bin DEFAULT '' COMMENT '租户描述',
                          `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                          `create_user_id` int(11) NOT NULL,
                          `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                          `tenant_identity` varchar(64) COLLATE utf8_bin DEFAULT '' COMMENT '租户标识',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of tenant
-- ----------------------------
BEGIN;
INSERT INTO `tenant` VALUES (1, 'taier', NULL, '2021-08-13 16:39:40', '2021-08-13 16:39:40', 1, 0, 'taier');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `user_name` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '用户名称',
                        `password` varchar(128) COLLATE utf8_bin NOT NULL,
                        `phone_number` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '用户手机号',
                        `email` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '用户手机号',
                        `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户状态0：正常，1：禁用',
                        `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                        `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                        PRIMARY KEY (`id`),
                        KEY `index_user_name` (`user_name`(128))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'admin@dtstack.com', '0192023A7BBD73250516F069DF18B500', '', 'admin@dtstack.com', 0, '2017-06-05 20:35:16', '2017-06-05 20:35:16', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
