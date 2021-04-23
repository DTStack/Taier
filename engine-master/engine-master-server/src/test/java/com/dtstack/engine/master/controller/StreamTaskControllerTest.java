package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.akka.MasterServer;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;

/**
 * @author haier
 * @Description
 * @date 2021/3/5 10:36 上午
 */
public class StreamTaskControllerTest extends AbstractTest {

    @Autowired
    private StreamTaskController streamTaskController;

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired(required = false)
    private MasterServer masterServer;




    @Test
    public void testGetFailedCheckPoint() {
        try {
            long time1 = new Date().getTime();
            EngineJobCheckpoint ejc = DataCollection.getData().getFailedEngineJobCheckpoint();
            long time2 = new Date().getTime();
            List<EngineJobCheckpoint> list = streamTaskController.getFailedCheckPoint(ejc.getTaskId(), time1, time2, 1);
            System.out.println(CollectionUtils.isEmpty(list) ? list : list.get(0));
        } catch (Exception e) {
            fail("GetFailedCheckPoint failed: " + e.getMessage());
        }
    }

    @Test
    public void testGrammarCheck(){
        try {
            AkkaConfig.init(ConfigFactory.load());
            Map<String, Object> params = getParams(getJsonString(getRandomStr()));
            ParamActionExt paramActionExt = com.dtstack.engine.common.util.PublicUtil.mapToObject(params, ParamActionExt.class);
//            paramActionExt.setTaskType(0);
//            paramActionExt.setComputeType(0);
//            paramActionExt.setEngineType("flink");
//            paramActionExt.setSqlText("select * from a");
//            paramActionExt.setAppType(0);
//            paramActionExt.setDeployMode("1");
//            paramActionExt.setGenerateTime(1615534634216L);
//            paramActionExt.setIsFailRetry(true);
//            paramActionExt.setLackingCount(0);
//            paramActionExt.setName("test");
//            paramActionExt.setPriority(0);
//            paramActionExt.setRequestStart(0);
//            paramActionExt.setStopJobId(0);
//            paramActionExt.setTaskId("440dc30b");
//            paramActionExt.setTaskParams("sql.env.parallelism=1\\nflink.checkpoint.interval=300000\\nsql.checkpoint.timeout=180000\\nsql.checkpoint.cleanup.mode=false\\nslots=1\\nlogLevel=info\\njob.priority=10\\nsecurity.kerberos.login.contexts=KafkaClient");
//            paramActionExt.setTaskType(0);
//            paramActionExt.setTenantId(1L);
            CheckResult checkResult = streamTaskController.grammarCheck(paramActionExt);
        } catch (Exception e) {
            fail("GrammarCheck failed: " + e.getMessage());
        }
    }

    private Map<String, Object> getParams(String json) {
        return JSONObject.parseObject(json, HashMap.class);
    }

    private String getRandomStr() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String getJsonString(String taskId) {
        String spark_json = "{\"isFailRetry\":false,\"sqlText\":\"ADD JAR WITH hdfs://ns1/dtInsight/batch/pyspark_13_49_bb_1590461304653.py AS " +
                ";\",\"computeType\":1,\"exeArgs\":\"\",\"pluginInfo\":{\"sparkSqlProxyPath\":\"hdfs://ns1/dtInsight/spark/client/spark-sql-proxy.jar" +
                "\",\"spark.yarn.appMasterEnv.PYSPARK_PYTHON\":\"/data/anaconda3/bin/python3\",\"cluster\":\"test4_beta\",\"spark.yarn.appMasterEnv." +
                "PYSPARK_DRIVER_PYTHON\":\"/data/anaconda3/bin/python3\",\"hiveConf\":{\"password\":\"\",\"maxJobPoolSize\":\"\",\"minJobPoolSize\":\"\",\"" +
                "jdbcUrl\":\"jdbc:hive2://172.16.101.227:10000/%s\",\"queue\":\"\",\"username\":\"\"},\"typeName\":\"yarn2-hdfs2-spark210\",\"hadoopConf\":" +
                "{\"fs.defaultFS\":\"hdfs://ns1\",\"dfs.replication\":\"1\",\"dfs.ha.fencing.methods\":\"sshfence\",\"dfs.client.failover.proxy.provider.ns1\":" +
                "\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"typeName\":\"yarn2-hdfs2-hadoop2\",\"dfs.ha.fencing.ssh.private" +
                "-key-files\":\"~/.ssh/id_rsa\",\"dfs.nameservices\":\"ns1\",\"fs.hdfs.impl.disable.cache\":\"true\",\"dfs.safemode.threshold.pct\":\"0.5\",\"dfs." +
                "ha.namenodes.ns1\":\"nn1,nn2\",\"dfs.namenode.name.dir\":\"file:/data/hadoop/hdfs/name\",\"dfs.journalnode.rpc-address\":\"0.0.0.0:8485\",\"fs.trash" +
                ".interval\":\"14400\",\"dfs.journalnode.http-address\":\"0.0.0.0:8480\",\"dfs.namenode.rpc-address.ns1.nn2\":\"172.16.101.136:9000\",\"dfs.namenode." +
                "rpc-address.ns1.nn1\":\"172.16.100.216:9000\",\"dfs.datanode.data.dir\":\"file:/data/hadoop/hdfs/data\",\"dfs.namenode.shared.edits.dir\":\"qjournal://" +
                "172.16.100.216:8485;172.16.101.136:8485;172.16.101.227:8485/namenode-ha-data\",\"ha.zookeeper.session-timeout.ms\":\"5000\",\"hadoop.tmp.dir\":\"/data/" +
                "hadoop_${user.name}\",\"dfs.journalnode.edits.dir\":\"/data/hadoop/hdfs/journal\",\"dfs.namenode.http-address.ns1.nn2\":\"172.16.101.136:50070\",\"dfs.nam" +
                "enode.http-address.ns1.nn1\":\"172.16.100.216:50070\",\"dfs.namenode.datanode.registration.ip-hostname-check\":\"false\",\"hadoop.proxyuser.${user.name}.host" +
                "s\":\"*\",\"hadoop.proxyuser.${user.name}.groups\":\"*\",\"ha.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"dfs.ha.aut" +
                "omatic-failover.enabled\":\"true\"},\"confHdfsPath\":\"\",\"yarnConf\":{\"yarn.resourcemanager.zk-address\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.1" +
                "01.227:2181\",\"yarn.resourcemanager.admin.address.rm1\":\"172.16.100.216:8033\",\"yarn.resourcemanager.webapp.address.rm2\":\"172.16.101.136:8088\",\"yarn.log.s" +
                "erver.url\":\"http://172.16.101.136:19888/jobhistory/logs/\",\"yarn.resourcemanager.admin.address.rm2\":\"172.16.101.136:8033\",\"yarn.resourcemanager.webapp.add" +
                "ress.rm1\":\"172.16.100.216:8088\",\"yarn.resourcemanager.ha.rm-ids\":\"rm1,rm2\",\"yarn.resourcemanager.ha.automatic-failover.zk-base-path\":\"/yarn-leader-electi" +
                "on\",\"yarn.client.failover-proxy-provider\":\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\"yarn.resourcemanager.scheduler.address.rm1\":\"172.16.100.216:8030\",\"yarn.resourcemanager.scheduler.address.rm2\":\"172.16.101.136:8030\",\"yarn.nodemanager.delete.debug-delay-sec\":\"600\",\"yarn.resourcemanager.address.rm1\":\"172.16.100.216:8032\",\"yarn.log-aggregation.retain-seconds\":\"2592000\",\"yarn.nodemanager.resource.memory-mb\":\"6144\",\"yarn.resourcemanager.ha.enabled\":\"true\",\"yarn.resourcemanager.address.rm2\":\"172.16.101.136:8032\",\"yarn.resourcemanager.cluster-id\":\"yarn-rm-cluster\",\"yarn.scheduler.minimum-allocation-mb\":\"512\",\"yarn.nodemanager.aux-services\":\"mapreduce_shuffle\",\"yarn.resourcemanager.resource-tracker.address.rm1\":\"172.16.100.216:8031\",\"yarn.nodemanager.resource.cpu-vcores\":\"8\",\"yarn.resourcemanager.resource-tracker.address.rm2\":\"172.16.101.136:8031\",\"yarn.nodemanager.pmem-check-enabled\":\"true\",\"yarn.nodemanager.remote-app-log-dir\":\"/tmp/logs\",\"yarn.scheduler.maximum-allocation-mb\":\"6144\",\"yarn.resourcemanager.ha.automatic-failover.enabled\":\"true\",\"yarn.nodemanager.vmem-check-enabled\":\"false\",\"yarn.log-aggregation.retain-check-interval-seconds\":\"604800\",\"yarn.nodemanager.webapp.address\":\"0.0.0.0:8042\",\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\":\"org.apache.hadoop.mapred.ShuffleHandler\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"yarn.log-aggregation-enable\":\"true\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"yarn.nodemanager.vmem-pmem-ratio\":\"4\",\"yarn.resourcemanager.zk-state-store.address\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"ha.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\"},\"sftpConf\":{\"path\":\"/data/beta4\",\"password\":\"abc123\",\"post\":\"22\",\"auth\":\"1\",\"host\":\"172.16.100.216\",\"username\":\"root\"},\"sparkPythonExtLibPath\":\"/dtInsight/pythons/pyspark.zip,hdfs://ns1/dtInsight/pythons/py4j-0.10.7-src.zip\",\"addColumnSupport\":\"true\",\"spark.eventLog.compress\":\"true\",\"sparkYarnArchive\":\"hdfs://ns1/dtInsight/sparkjars/jars\",\"spark.eventLog.enabled\":\"true\",\"spark.eventLog.dir\":\"hdfs://ns1/tmp/spark-yarn-logs\",\"md5zip\":\"\",\"tenantId\":13,\"queue\":\"default\"},\"engineType\":\"spark\",\"taskParams\":\"executor.instances=1\\nexecutor.cores=1\\njob.priority=10\",\"maxRetryNum\":0,\"taskType\":3,\"groupName\":\"test4_beta_default\",\"sourceType\":2,\"clusterName\":\"test4_beta\",\"name\":\"run_pyspark_task_1590461304653\",\"tenantId\":15,\"taskId\":\"" + taskId + "\"}";
        return spark_json;
    }
}
