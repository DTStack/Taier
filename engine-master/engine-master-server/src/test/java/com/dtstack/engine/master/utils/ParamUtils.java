/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.utils;

import com.dtstack.engine.pluginapi.util.PublicUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ParamUtils {
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getParamsForSpark(String jobId) {
        try {
            String requestParams = getParamsStringForSpark(jobId);
            return PublicUtil.jsonStrToObject(requestParams, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String getParamsStringForSpark(String jobId) {
        return "{\"isFailRetry\":false,\"sqlText\":\"use www; create table select_sql_temp_table_189834455428984 stored as orc as select * from (SELECT * FROM foo WHERE id = 1)temp\",\"computeType\":1,\"pluginInfo\":" + getPluginInfoParamsStringForSpark() + ",\"engineType\":\"spark\",\"taskParams\":\"## Driver程序使用的CPU核数,默认为1\\r\\n# driver.cores=1\\r\\n\\n## Driver程序使用内存大小,默认512m\\r\\n# driver.memory=512m\\r\\n\\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\\r\\n# driver.maxResultSize=1g\\r\\n\\n## SparkContext 启动时是否记录有效 SparkConf信息,默认false\\r\\n# logConf=false\\r\\n\\n## 启动的executor的数量，默认为1\\r\\nexecutor.instances=1\\r\\n\\n## 每个executor使用的CPU核数，默认为1\\r\\nexecutor.cores=1\\r\\n\\n## 每个executor内存大小,默认512m\\r\\n# executor.memory=512m\\r\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\r\\njob.priority=10\\r\\n\\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\r\\n# logLevel = INFO\\r\\n\\n## spark中所有网络交互的最大超时时间\\r\\n# spark.network.timeout=120s\\r\\n\\n## executor的OffHeap内存，和spark.executor.memory配置使用\\r\\n# spark.yarn.executor.memoryOverhead\",\"maxRetryNum\":0,\"taskType\":0,\"groupName\":\"default_c\",\"sourceType\":2,\"clusterName\":\"default\",\"name\":\"run_sql_task_1591858250372\",\"tenantId\":1,\"taskId\":\"" + jobId + "\"}";
    }

    public static String getParamsStringForSpark(String jobId, Integer maxRetryNum) {
        return "{\"isFailRetry\":false,\"sqlText\":\"use www; create table select_sql_temp_table_189834455428984 stored as orc as select * from (SELECT * FROM foo WHERE id = 1)temp\",\"computeType\":1,\"pluginInfo\":" + getPluginInfoParamsStringForSpark() + ",\"engineType\":\"spark\",\"taskParams\":\"## Driver程序使用的CPU核数,默认为1\\r\\n# driver.cores=1\\r\\n\\n## Driver程序使用内存大小,默认512m\\r\\n# driver.memory=512m\\r\\n\\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\\r\\n# driver.maxResultSize=1g\\r\\n\\n## SparkContext 启动时是否记录有效 SparkConf信息,默认false\\r\\n# logConf=false\\r\\n\\n## 启动的executor的数量，默认为1\\r\\nexecutor.instances=1\\r\\n\\n## 每个executor使用的CPU核数，默认为1\\r\\nexecutor.cores=1\\r\\n\\n## 每个executor内存大小,默认512m\\r\\n# executor.memory=512m\\r\\n\\n## 任务优先级, 值越小，优先级越高，范围:1-1000\\r\\njob.priority=10\\r\\n\\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\\r\\n# logLevel = INFO\\r\\n\\n## spark中所有网络交互的最大超时时间\\r\\n# spark.network.timeout=120s\\r\\n\\n## executor的OffHeap内存，和spark.executor.memory配置使用\\r\\n# spark.yarn.executor.memoryOverhead\",\"maxRetryNum\":" + maxRetryNum + ",\"taskType\":0,\"groupName\":\"default_c\",\"sourceType\":2,\"clusterName\":\"default\",\"name\":\"run_sql_task_1591858250372\",\"tenantId\":1,\"taskId\":\"" + jobId + "\"}";
    }

    public static String getParamsStringForFlink(String jobId) {
        return "{\"isFailRetry\":true,\"sqlText\":\"\",\"computeType\":1,\"exeArgs\":\"-jobid P_shixi_sync_2020_06_23_27_38-shixi_sync-20200623000000 -job %7B%22job%22%3A%7B%22content%22%3A%5B%7B%22reader%22%3A%7B%22parameter%22%3A%7B%22path%22%3A%22hdfs%3A%2F%2Fns1%2Fuser%2Fhive%2Fwarehouse%2Fdev.db%2Fchener0619%22%2C%22hadoopConfig%22%3A%7B%22fs.defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22dfs.namenode.shared.edits.dir%22%3A%22qjournal%3A%2F%2Fkudu1%3A8485%3Bkudu2%3A8485%3Bkudu3%3A8485%2Fnamenode-ha-data%22%2C%22hadoop.proxyuser.admin.groups%22%3A%22*%22%2C%22dfs.replication%22%3A%222%22%2C%22dfs.ha.fencing.methods%22%3A%22sshfence%22%2C%22dfs.client.failover.proxy.provider.ns1%22%3A%22org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider%22%2C%22dfs.ha.fencing.ssh.private-key-files%22%3A%22%7E%2F.ssh%2Fid_rsa%22%2C%22dfs.nameservices%22%3A%22ns1%22%2C%22dfs.safemode.threshold.pct%22%3A%220.5%22%2C%22dfs.ha.namenodes.ns1%22%3A%22nn1%2Cnn2%22%2C%22ha.zookeeper.session-timeout.ms%22%3A%225000%22%2C%22hadoop.tmp.dir%22%3A%22%2Fdata%2Fhadoop_%24%7Buser.name%7D%22%2C%22dfs.journalnode.edits.dir%22%3A%22%2Fdata%2Fdtstack%2Fhadoop%2Fjournal%22%2C%22dfs.namenode.http-address.ns1.nn2%22%3A%22kudu2%3A50070%22%2C%22dfs.journalnode.rpc-address%22%3A%220.0.0.0%3A8485%22%2C%22dfs.namenode.http-address.ns1.nn1%22%3A%22kudu1%3A50070%22%2C%22dfs.journalnode.http-address%22%3A%220.0.0.0%3A8480%22%2C%22hadoop.proxyuser.admin.hosts%22%3A%22*%22%2C%22dfs.namenode.rpc-address.ns1.nn2%22%3A%22kudu2%3A9000%22%2C%22dfs.namenode.rpc-address.ns1.nn1%22%3A%22kudu1%3A9000%22%2C%22ha.zookeeper.quorum%22%3A%22kudu1%3A2181%2Ckudu2%3A2181%2Ckudu3%3A2181%22%2C%22dfs.ha.automatic-failover.enabled%22%3A%22true%22%7D%2C%22column%22%3A%5B%7B%22name%22%3A%22id%22%2C%22index%22%3A0%2C%22type%22%3A%22int%22%2C%22key%22%3A%22id%22%7D%2C%7B%22name%22%3A%22name%22%2C%22index%22%3A1%2C%22type%22%3A%22string%22%2C%22key%22%3A%22name%22%7D%5D%2C%22defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22fieldDelimiter%22%3A%22%5Cu0001%22%2C%22encoding%22%3A%22utf-8%22%2C%22fileType%22%3A%22parquet%22%2C%22sourceIds%22%3A%5B1%5D%7D%2C%22name%22%3A%22hdfsreader%22%7D%2C%22writer%22%3A%7B%22parameter%22%3A%7B%22fileName%22%3A%22%22%2C%22column%22%3A%5B%7B%22name%22%3A%22id%22%2C%22index%22%3A0%2C%22type%22%3A%22int%22%2C%22key%22%3A%22id%22%7D%2C%7B%22name%22%3A%22name%22%2C%22index%22%3A1%2C%22type%22%3A%22string%22%2C%22key%22%3A%22name%22%7D%5D%2C%22writeMode%22%3A%22overwrite%22%2C%22fieldDelimiter%22%3A%22%5Cu0001%22%2C%22encoding%22%3A%22utf-8%22%2C%22fullColumnName%22%3A%5B%22id%22%2C%22name%22%5D%2C%22path%22%3A%22hdfs%3A%2F%2Fns1%2Fuser%2Fhive%2Fwarehouse%2Fdev.db%2Fchener0619%22%2C%22hadoopConfig%22%3A%7B%22fs.defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22dfs.namenode.shared.edits.dir%22%3A%22qjournal%3A%2F%2Fkudu1%3A8485%3Bkudu2%3A8485%3Bkudu3%3A8485%2Fnamenode-ha-data%22%2C%22hadoop.proxyuser.admin.groups%22%3A%22*%22%2C%22dfs.replication%22%3A%222%22%2C%22dfs.ha.fencing.methods%22%3A%22sshfence%22%2C%22dfs.client.failover.proxy.provider.ns1%22%3A%22org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider%22%2C%22dfs.ha.fencing.ssh.private-key-files%22%3A%22%7E%2F.ssh%2Fid_rsa%22%2C%22dfs.nameservices%22%3A%22ns1%22%2C%22dfs.safemode.threshold.pct%22%3A%220.5%22%2C%22dfs.ha.namenodes.ns1%22%3A%22nn1%2Cnn2%22%2C%22ha.zookeeper.session-timeout.ms%22%3A%225000%22%2C%22hadoop.tmp.dir%22%3A%22%2Fdata%2Fhadoop_%24%7Buser.name%7D%22%2C%22dfs.journalnode.edits.dir%22%3A%22%2Fdata%2Fdtstack%2Fhadoop%2Fjournal%22%2C%22dfs.namenode.http-address.ns1.nn2%22%3A%22kudu2%3A50070%22%2C%22dfs.journalnode.rpc-address%22%3A%220.0.0.0%3A8485%22%2C%22dfs.namenode.http-address.ns1.nn1%22%3A%22kudu1%3A50070%22%2C%22dfs.journalnode.http-address%22%3A%220.0.0.0%3A8480%22%2C%22hadoop.proxyuser.admin.hosts%22%3A%22*%22%2C%22dfs.namenode.rpc-address.ns1.nn2%22%3A%22kudu2%3A9000%22%2C%22dfs.namenode.rpc-address.ns1.nn1%22%3A%22kudu1%3A9000%22%2C%22ha.zookeeper.quorum%22%3A%22kudu1%3A2181%2Ckudu2%3A2181%2Ckudu3%3A2181%22%2C%22dfs.ha.automatic-failover.enabled%22%3A%22true%22%7D%2C%22defaultFS%22%3A%22hdfs%3A%2F%2Fns1%22%2C%22connection%22%3A%5B%7B%22jdbcUrl%22%3A%22jdbc%3Ahive2%3A%2F%2F172.16.8.107%3A10000%2Fdev%22%2C%22table%22%3A%5B%22chener0619%22%5D%7D%5D%2C%22fileType%22%3A%22parquet%22%2C%22sourceIds%22%3A%5B1%5D%2C%22username%22%3A%22admin%22%2C%22fullColumnType%22%3A%5B%22int%22%2C%22string%22%5D%7D%2C%22name%22%3A%22hdfswriter%22%7D%7D%5D%2C%22setting%22%3A%7B%22restore%22%3A%7B%22maxRowNumForCheckpoint%22%3A0%2C%22isRestore%22%3Afalse%2C%22restoreColumnName%22%3A%22%22%2C%22restoreColumnIndex%22%3A0%7D%2C%22errorLimit%22%3A%7B%22record%22%3A100%7D%2C%22speed%22%3A%7B%22bytes%22%3A0%2C%22channel%22%3A1%7D%7D%7D%7D\",\"pluginInfo\":" + getPluginInfoParamsStringForFlink() + ",\"engineType\":\"flink\",\"taskParams\":\"mr.job.parallelism = 1\\n\",\"maxRetryNum\":3,\"taskType\":2,\"groupName\":\"default_a\",\"clusterName\":\"default\",\"name\":\"P_shixi_sync_2020_06_23_27_38-shixi_sync-20200623000000\",\"tenantId\":1,\"taskId\":\"" + jobId + "\"}";
    }

    public static String getPluginInfoParamsStringForFlink() {
        return "{\"historyserver.web.address\":\"0.0.0.0\",\"cluster\":\"default\",\"historyserver.web.port\":\"8082\",\"remotePluginRootDir\":\"/opt/dtstack/180_flinkplugin\",\"state.checkpoints.num-retained\":\"1\",\"high-availability.cluster-id\":\"/rdos\",\"flinkJarPath\":\"/opt/dtstack/flink-1.8.1/lib\",\"hiveConf\":{\"password\":\"\",\"openKerberos\":false,\"jdbcUrl\":\"jdbc:hive2://172.16.8.107:10000/%s\",\"driverClassName\":\"org.apache.hive.jdbc.HiveDriver\",\"username\":\"admin\"},\"typeName\":\"flink180-hadoop2\",\"jarTmpDir\":\"../tmp140\",\"yarn.taskmanager.heap.mb\":1024,\"sftpConf\":{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"port\":\"22\",\"auth\":\"1\",\"host\":\"172.16.101.249\",\"username\":\"root\"},\"clusterMode\":\"yarn\",\"high-availability.zookeeper.path.root\":\"/flink180\",\"yarn.jobmanager.heap.mb\":1024,\"high-availability.storageDir\":\"/flink180/ha\",\"flinkPluginRoot\":\"/opt/dtstack/180_flinkplugin\",\"yarn.taskmanager.numberOfTaskManager\":2,\"jobmanager.archive.fs.dir\":\"/completed-jobs\",\"openKerberos\":false,\"hadoopConf\":{\"fs.defaultFS\":\"hdfs://ns1\",\"dfs.namenode.shared.edits.dir\":\"qjournal://kudu1:8485;kudu2:8485;kudu3:8485/namenode-ha-data\",\"hadoop.proxyuser.admin.groups\":\"*\",\"dfs.replication\":\"2\",\"dfs.ha.fencing.methods\":\"sshfence\",\"dfs.client.failover.proxy.provider.ns1\":\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"dfs.ha.fencing.ssh.private-key-files\":\"~/.ssh/id_rsa\",\"dfs.nameservices\":\"ns1\",\"dfs.safemode.threshold.pct\":\"0.5\",\"dfs.ha.namenodes.ns1\":\"nn1,nn2\",\"ha.zookeeper.session-timeout.ms\":\"5000\",\"hadoop.tmp.dir\":\"/data/hadoop_${user.name}\",\"dfs.journalnode.edits.dir\":\"/data/dtstack/hadoop/journal\",\"dfs.namenode.http-address.ns1.nn2\":\"kudu2:50070\",\"dfs.journalnode.rpc-address\":\"0.0.0.0:8485\",\"dfs.namenode.http-address.ns1.nn1\":\"kudu1:50070\",\"dfs.journalnode.http-address\":\"0.0.0.0:8480\",\"hadoop.proxyuser.admin.hosts\":\"*\",\"dfs.namenode.rpc-address.ns1.nn2\":\"kudu2:9000\",\"dfs.namenode.rpc-address.ns1.nn1\":\"kudu1:9000\",\"ha.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"dfs.ha.automatic-failover.enabled\":\"true\"},\"yarnConf\":{\"yarn.resourcemanager.zk-address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"yarn.resourcemanager.admin.address.rm1\":\"kudu1:8033\",\"yarn.resourcemanager.webapp.address.rm2\":\"kudu2:8088\",\"yarn.log.server.url\":\"http://kudu3:19888/jobhistory/logs/\",\"yarn.resourcemanager.admin.address.rm2\":\"kudu2:8033\",\"yarn.resourcemanager.webapp.address.rm1\":\"kudu1:8088\",\"yarn.resourcemanager.ha.rm-ids\":\"rm1,rm2\",\"yarn.resourcemanager.ha.automatic-failover.zk-base-path\":\"/yarn-leader-election\",\"yarn.client.failover-proxy-provider\":\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\"yarn.resourcemanager.scheduler.address.rm1\":\"kudu1:8030\",\"yarn.resourcemanager.scheduler.address.rm2\":\"kudu2:8030\",\"yarn.nodemanager.delete.debug-delay-sec\":\"600\",\"yarn.resourcemanager.address.rm1\":\"kudu1:8032\",\"yarn.log-aggregation.retain-seconds\":\"2592000\",\"yarn.nodemanager.resource.memory-mb\":\"8000\",\"yarn.resourcemanager.ha.enabled\":\"true\",\"yarn.resourcemanager.address.rm2\":\"kudu2:8032\",\"yarn.resourcemanager.cluster-id\":\"yarn-rm-cluster\",\"yarn.scheduler.minimum-allocation-mb\":\"512\",\"yarn.nodemanager.aux-services\":\"mapreduce_shuffle\",\"yarn.resourcemanager.resource-tracker.address.rm1\":\"kudu1:8031\",\"yarn.nodemanager.resource.cpu-vcores\":\"10\",\"yarn.resourcemanager.resource-tracker.address.rm2\":\"kudu2:8031\",\"yarn.nodemanager.pmem-check-enabled\":\"false\",\"yarn.nodemanager.remote-app-log-dir\":\"/tmp/logs\",\"yarn.resourcemanager.ha.automatic-failover.enabled\":\"true\",\"yarn.nodemanager.vmem-check-enabled\":\"false\",\"yarn.resourcemanager.hostname.rm2\":\"kudu2\",\"yarn.nodemanager.webapp.address\":\"kudu1:8042\",\"yarn.resourcemanager.hostname.rm1\":\"kudu1\",\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\":\"org.apache.hadoop.mapred.ShuffleHandler\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"yarn.log-aggregation-enable\":\"true\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"yarn.nodemanager.vmem-pmem-ratio\":\"4\",\"yarn.resourcemanager.zk-state-store.address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"ha.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\"},\"flinkSessionSlotCount\":\"4\",\"high-availability.zookeeper.quorum\":\"172.16.8.107:2181,172.16.8.108:2181,172.16.8.109:2181\",\"md5zip\":\"\",\"tenantId\":81,\"yarn.taskmanager.numberOfTaskSlots\":2,\"queue\":\"a\",\"state.checkpoints.dir\":\"/rdos/flink180/checkpoints/metadata\"}";
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getStopParams(String jobId, String groupName) {
        try {
            String requestParams = getStopParamsString(jobId, groupName);
            return PublicUtil.jsonStrToObject(requestParams, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String getStopParamsString(String jobId, String groupName) {
        return "{\"jobs\":[{\"taskType\":2,\"computeType\":1,\"groupName\":\"" + groupName +"\",\"engineType\":\"spark\",\"taskId\":\"" + jobId + "\"}]}";
    }

    public static String getPluginInfoParamsStringForSpark() {
        return "{\"sparkSqlProxyPath\":\"/dtInsight/sparkjars/spark-sql-proxy-1.0.0.jar\",\"spark.logConf\":\"true\",\"spark.yarn.appMasterEnv.PYSPARK_PYTHON\":\"/dtInsight/sparkjars/pythons/pyspark.zip\",\"cluster\":\"default\",\"openKerberos\":false,\"spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON\":\"/dtInsight/sparkjars/pythons/pyspark.zip\",\"hiveConf\":{\"jdbcIdel\":\"1\",\"queryTimeout\":\"1000\",\"openKerberos\":false,\"checkTimeout\":\"10000\",\"password\":\"\",\"maxRows\":\"1000\",\"minPoolSize\":\"5\",\"useConnectionPool\":\"false\",\"jdbcUrl\":\"jdbc:hive2://kudu3:10000/%s\",\"driverClassName\":\"org.apache.hive.jdbc.HiveDriver\",\"maxPoolSize\":\"20\",\"initialPoolSize\":\"5\",\"username\":\"\"},\"typeName\":\"spark-yarn-hadoop2\",\"hadoopConf\":{\"fs.defaultFS\":\"hdfs://ns1\",\"dfs.namenode.shared.edits.dir\":\"qjournal://kudu1:8485;kudu2:8485;kudu3:8485/namenode-ha-data\",\"hadoop.proxyuser.admin.groups\":\"*\",\"dfs.replication\":\"2\",\"dfs.ha.fencing.methods\":\"sshfence\",\"dfs.client.failover.proxy.provider.ns1\":\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"dfs.ha.fencing.ssh.private-key-files\":\"~/.ssh/id_rsa\",\"dfs.nameservices\":\"ns1\",\"dfs.safemode.threshold.pct\":\"0.5\",\"dfs.ha.namenodes.ns1\":\"nn1,nn2\",\"hadoop.tmp.dir\":\"/data/${user.name}\",\"dfs.journalnode.edits.dir\":\"/opt/dtstack/hadoop/journal\",\"dfs.journalnode.rpc-address\":\"0.0.0.0:8485\",\"dfs.namenode.http-address.ns1.nn2\":\"kudu2:50070\",\"dfs.namenode.http-address.ns1.nn1\":\"kudu1:50070\",\"dfs.journalnode.http-address\":\"0.0.0.0:8480\",\"hadoop.proxyuser.admin.hosts\":\"*\",\"dfs.namenode.rpc-address.ns1.nn2\":\"kudu2:9000\",\"dfs.namenode.rpc-address.ns1.nn1\":\"kudu1:9000\",\"dfs.ha.automatic-failover.enabled\":\"true\"},\"confHdfsPath\":\"/home/admin/app/tmp/console/hadoop_config/default\",\"yarnConf\":{\"yarn.resourcemanager.zk-address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"yarn.resourcemanager.admin.address.rm1\":\"kudu1:8033\",\"yarn.log.server.url\":\"http://kudu2:19888/jobhistory/logs/\",\"yarn.resourcemanager.webapp.address.rm2\":\"kudu2:8088\",\"yarn.resourcemanager.admin.address.rm2\":\"kudu2:8033\",\"yarn.resourcemanager.webapp.address.rm1\":\"kudu1:8088\",\"yarn.resourcemanager.ha.rm-ids\":\"rm1,rm2\",\"yarn.resourcemanager.ha.automatic-failover.zk-base-path\":\"/yarn-leader-election\",\"yarn.client.failover-proxy-provider\":\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\"yarn.resourcemanager.scheduler.address.rm1\":\"kudu1:8030\",\"yarn.resourcemanager.scheduler.address.rm2\":\"kudu2:8030\",\"yarn.nodemanager.delete.debug-delay-sec\":\"600\",\"yarn.resourcemanager.address.rm1\":\"kudu1:8032\",\"yarn.log-aggregation.retain-seconds\":\"2592000\",\"yarn.nodemanager.resource.memory-mb\":\"8000\",\"yarn.resourcemanager.ha.enabled\":\"true\",\"yarn.resourcemanager.address.rm2\":\"kudu2:8032\",\"yarn.resourcemanager.cluster-id\":\"yarn-rm-cluster\",\"yarn.scheduler.minimum-allocation-mb\":\"512\",\"yarn.nodemanager.aux-services\":\"mapreduce_shuffle\",\"yarn.nodemanager.resource.cpu-vcores\":\"6\",\"yarn.resourcemanager.resource-tracker.address.rm1\":\"kudu1:8031\",\"yarn.resourcemanager.resource-tracker.address.rm2\":\"kudu2:8031\",\"yarn.nodemanager.remote-app-log-dir\":\"/tmp/logs\",\"yarn.resourcemanager.ha.automatic-failover.enabled\":\"true\",\"yarn.nodemanager.vmem-check-enabled\":\"false\",\"yarn.resourcemanager.hostname.rm2\":\"kudu2\",\"yarn.nodemanager.webapp.address\":\"kudu3:8042\",\"yarn.resourcemanager.hostname.rm1\":\"kudu1\",\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\":\"org.apache.hadoop.mapred.ShuffleHandler\",\"yarn.log-aggregation-enable\":\"true\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"yarn.nodemanager.vmem-pmem-ratio\":\"4\",\"yarn.resourcemanager.zk-state-store.address\":\"kudu1:2181,kudu2:2181,kudu3:2181\"},\"sftpConf\":{\"path\":\"/home/admin/app/tmp\",\"password\":\"abc123\",\"port\":\"22\",\"auth\":\"1\",\"host\":\"kudu1\",\"username\":\"root\"},\"sparkPythonExtLibPath\":\"/dtInsight/sparkjars/pythons/py4j-0.10.4-src.zip\",\"addColumnSupport\":\"true\",\"spark.eventLog.compress\":\"true\",\"sparkYarnArchive\":\"/dtInsight/sparkjars213\",\"spark.eventLog.enabled\":\"true\",\"spark.eventLog.dir\":\"hdfs://ns1/tmp/history\",\"md5zip\":\"6a3551b91451b79caf658e35a8995e3a\",\"tenantId\":1,\"queue\":\"c\"}";
    }

    public static String generateStrWithTime() {
        return "test0001";
    }

    public static List<String> getMockContainerInfos() {
        return new ArrayList<>(Arrays.asList("mockContainerInfos"));
    }

    public static String getMockEngineLog() {
        return "{err: mockEngineLog}";
    }

    public static String getMockLocalAddress() {
        return "127.0.0.1:8090";
    }

    public static String getMockEngineTaskId() {
        return "application_1592361600331_mock";
    }

    public static String getMockEngineAppId() {
        return "application_mock_001";
    }

    public static Integer getUpRetryCount() {
        return 50;
    }
}
