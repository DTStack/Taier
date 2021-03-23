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

    @Autowired
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
            String pluginInfo = "{\"cluster\":\"tiezhu\",\"metrics.reporter.promgateway.port\":\"9091\",\"prometheusHost\":\"\",\"flinkJarPath\":\"/opt/dtstack/flink-1.10.1/lib\",\"high-availability.cluster-id\":\"/default\",\"metrics.reporter.promgateway.jobName\":\"110job\",\"high-availability.zookeeper.path.root\":\"/flink110\",\"metrics.reporter.promgateway.class\":\"org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter\",\"yarn.jobmanager.heap.mb\":\"1024\",\"high-availability.storageDir\":\"hdfs://ns1/flink110/ha\",\"flink.env.java.opts\":\"-XX:MaxMetaspaceSize=500m\",\"yarn.taskmanager.numberOfTaskManager\":\"2\",\"prometheusClass\":\"com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter\",\"gatewayJobName\":\"pushgateway\",\"pluginLoadMode\":\"shipfile\",\"hadoopConf\":{\"fs.defaultFS\":\"hdfs://ns1\",\"hadoop.proxyuser.admin.groups\":\"*\",\"javax.jdo.option.ConnectionDriverName\":\"com.mysql.jdbc.Driver\",\"dfs.replication\":\"2\",\"dfs.ha.fencing.methods\":\"sshfence\",\"dfs.client.failover.proxy.provider.ns1\":\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"typeName\":\"yarn2-hdfs2-hadoop2\",\"dfs.ha.fencing.ssh.private-key-files\":\"~/.ssh/id_rsa\",\"dfs.nameservices\":\"ns1\",\"javax.jdo.option.ConnectionURL\":\"jdbc:mysql://kudu2:3306/ide?useSSL=false\",\"dfs.safemode.threshold.pct\":\"0.5\",\"dfs.qjournal.write-txns.timeout.ms\":\"60000\",\"dfs.ha.namenodes.ns1\":\"nn1,nn2\",\"dfs.journalnode.rpc-address\":\"0.0.0.0:8485\",\"dfs.journalnode.http-address\":\"0.0.0.0:8480\",\"dfs.namenode.rpc-address.ns1.nn2\":\"kudu2:9000\",\"dfs.namenode.rpc-address.ns1.nn1\":\"kudu1:9000\",\"hive.metastore.warehouse.dir\":\"/user/hive/warehouse\",\"hive.server2.webui.host\":\"172.16.10.34\",\"dfs.namenode.shared.edits.dir\":\"qjournal://kudu1:8485;kudu2:8485;kudu3:8485/namenode-ha-data\",\"hive.metastore.schema.verification\":\"false\",\"javax.jdo.option.ConnectionUserName\":\"dtstack\",\"hive.server2.support.dynamic.service.discovery\":\"true\",\"javax.jdo.option.ConnectionPassword\":\"abc123\",\"hive.metastore.uris\":\"thrift://kudu1:9083\",\"hive.server2.thrift.port\":\"10000\",\"hive.exec.dynamic.partition.mode\":\"nonstrict\",\"ha.zookeeper.session-timeout.ms\":\"5000\",\"hadoop.tmp.dir\":\"/data/hadoop_${user.name}\",\"dfs.journalnode.edits.dir\":\"/data/dtstack/hadoop/journal\",\"hive.server2.zookeeper.namespace\":\"hiveserver2\",\"hive.server2.enable.doAs\":\"/false\",\"dfs.namenode.http-address.ns1.nn2\":\"kudu2:50070\",\"dfs.namenode.http-address.ns1.nn1\":\"kudu1:50070\",\"hadoop.proxyuser.admin.hosts\":\"*\",\"hive.exec.scratchdir\":\"/user/hive/warehouse\",\"hive.server2.webui.max.threads\":\"100\",\"hive.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"datanucleus.schema.autoCreateAll\":\"true\",\"hive.exec.dynamic.partition\":\"true\",\"hive.server2.thrift.bind.host\":\"kudu1\",\"ha.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"hive.server2.thrift.min.worker.threads\":\"200\",\"hive.server2.webui.port\":\"10002\",\"dfs.ha.automatic-failover.enabled\":\"true\"},\"state.backend.incremental\":\"true\",\"metrics.reporter.promgateway.deleteOnShutdown\":\"true\",\"flinkInterval\":\"5 SECONDS\",\"high-availability.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"yarn.application-attempt-failures-validity-interval\":\"3600000\",\"state.backend\":\"RocksDB\",\"yarn.taskmanager.numberOfTaskSlots\":\"2\",\"metrics.reporter.promgateway.host\":\"172.16.8.92\",\"prometheusPort\":\"9090\",\"historyserver.web.address\":\"kudu3\",\"historyserver.web.port\":\"8082\",\"remotePluginRootDir\":\"/opt/dtstack/110_flinkplugin\",\"state.checkpoints.num-retained\":\"11\",\"hiveConf\":{\"maxJobPoolSize\":\"\",\"password\":\"\",\"minJobPoolSize\":\"\",\"jdbcUrl\":\"jdbc:hive2://172.16.100.195:10000/%s\",\"queue\":\"a\",\"username\":\"admin\"},\"jarTmpDir\":\"./tmp110\",\"yarn.taskmanager.heap.mb\":\"1024\",\"typeName\":\"yarn2-hdfs2-flink110\",\"monitorAcceptedApp\":\"false\",\"state.savepoints.dir\":\"hdfs://ns1/dtInsight/flink110/savepoints\",\"sftpConf\":{\"maxWaitMillis\":\"3600000\",\"minIdle\":\"16\",\"auth\":\"1\",\"isUsePool\":\"true\",\"timeout\":\"0\",\"path\":\"/data/sftp\",\"password\":\"dt@sz.com\",\"maxIdle\":\"16\",\"port\":\"22\",\"maxTotal\":\"16\",\"host\":\"172.16.100.38\",\"fileTimeout\":\"300000\",\"username\":\"root\"},\"clusterMode\":\"perjob\",\"flinkPluginRoot\":\"/opt/dtstack/110_flinkplugin\",\"metrics.reporter.promgateway.randomJobNameSuffix\":\"true\",\"yarn.application-attempts\":\"0\",\"taskparams.taskmanager.heap.mb\":\"20\",\"jobmanager.archive.fs.dir\":\"hdfs://ns1/dtInsight/flink110/completed-jobs\",\"classloader.resolve-order\":\"parent-first\",\"yarnConf\":{\"fs.defaultFS\":\"hdfs://ns1\",\"yarn.resourcemanager.zk-address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"yarn.resourcemanager.admin.address.rm1\":\"kudu1:8033\",\"yarn.resourcemanager.webapp.address.rm2\":\"kudu2:8088\",\"yarn.log.server.url\":\"http://kudu3:19888/jobhistory/logs/\",\"yarn.resourcemanager.admin.address.rm2\":\"kudu2:8033\",\"yarn.resourcemanager.webapp.address.rm1\":\"kudu1:8088\",\"yarn.resourcemanager.ha.rm-ids\":\"rm1,rm2\",\"yarn.resourcemanager.ha.automatic-failover.zk-base-path\":\"/yarn-leader-election\",\"yarn.client.failover-proxy-provider\":\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\"hadoop.proxyuser.admin.groups\":\"*\",\"yarn.resourcemanager.scheduler.address.rm1\":\"kudu1:8030\",\"yarn.resourcemanager.scheduler.address.rm2\":\"kudu2:8030\",\"yarn.nodemanager.delete.debug-delay-sec\":\"600\",\"yarn.resourcemanager.address.rm1\":\"kudu1:8032\",\"yarn.log-aggregation.retain-seconds\":\"2592000\",\"yarn.nodemanager.resource.memory-mb\":\"8000\",\"yarn.resourcemanager.ha.enabled\":\"true\",\"yarn.resourcemanager.address.rm2\":\"kudu2:8032\",\"yarn.resourcemanager.cluster-id\":\"yarn-rm-cluster\",\"yarn.scheduler.minimum-allocation-mb\":\"512\",\"yarn.nodemanager.aux-services\":\"mapreduce_shuffle\",\"yarn.resourcemanager.resource-tracker.address.rm1\":\"kudu1:8031\",\"yarn.nodemanager.resource.cpu-vcores\":\"10\",\"yarn.resourcemanager.resource-tracker.address.rm2\":\"kudu2:8031\",\"yarn.nodemanager.pmem-check-enabled\":\"false\",\"yarn.nodemanager.remote-app-log-dir\":\"/tmp/logs\",\"yarn.resourcemanager.ha.automatic-failover.enabled\":\"true\",\"yarn.nodemanager.vmem-check-enabled\":\"false\",\"hadoop.tmp.dir\":\"/data/hadoop_${user.name}\",\"yarn.resourcemanager.hostname.rm2\":\"kudu2\",\"yarn.nodemanager.webapp.address\":\"kudu1:8042\",\"yarn.resourcemanager.hostname.rm1\":\"kudu1\",\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\":\"org.apache.hadoop.mapred.ShuffleHandler\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"yarn.log-aggregation-enable\":\"true\",\"hadoop.proxyuser.admin.hosts\":\"*\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"yarn.nodemanager.vmem-pmem-ratio\":\"4\",\"yarn.resourcemanager.zk-state-store.address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"ha.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\"},\"high-availability\":\"ZOOKEEPER\",\"submitTimeout\":\"5\",\"md5zip\":\"9deb7912b27a9c5dfc9574a2af5ba3ce\",\"tenantId\":99,\"queue\":\"a\",\"state.checkpoints.dir\":\"hdfs://ns1/dtInsight/flink110/checkpoints\"}";
            ParamActionExt paramActionExt = new ParamActionExt();
            paramActionExt.setPluginInfo(JSONObject.parseObject(pluginInfo, HashMap.class));
            paramActionExt.setTaskType(0);
            paramActionExt.setComputeType(0);
            paramActionExt.setEngineType("flink");
            CheckResult checkResult = streamTaskController.grammarCheck(paramActionExt);
            System.out.println(checkResult);
        } catch (Exception e) {
            fail("GrammarCheck failed: " + e.getMessage());
        }
    }
}
