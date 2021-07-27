package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.common.api.WorkerApi;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.TestEngineUniqueSignDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.schedule.common.enums.AppType;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class ActionServiceTest extends AbstractTest {

    private final static List<String> mockInfos = Lists.newArrayList("Mock Info");

    @MockBean
    private WorkerApi clientOperator;

    @MockBean
    private JobDealer jobDealer;

    @Autowired
    @InjectMocks
    private ActionService actionService;

    @Autowired
    private TestEngineUniqueSignDao testEngineUniqueSignDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;


    @Before
    public void setup() throws Exception{
        when(clientOperator.containerInfos(any())).thenReturn(mockInfos);
    }


    @Test
    public void testStart() {
        try {
            Map<String, Object> params = getParams(getJsonString(getRandomStr()));
            ParamActionExt paramActionExt = com.dtstack.engine.common.util.PublicUtil.mapToObject(params, ParamActionExt.class);
            paramActionExt.setAppType(AppType.RDOS.getType());
            Boolean result = actionService.start(paramActionExt);
            Assert.assertTrue(result);
        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartError() {
        try {
            Map<String, Object> params = getParams(getJsonString(getRandomStr()));
            ParamActionExt paramActionExt = com.dtstack.engine.common.util.PublicUtil.mapToObject(params, ParamActionExt.class);
            paramActionExt.setComputeType(null);
            Boolean result = actionService.start(paramActionExt);
            Assert.assertFalse(result);
            ScheduleJob scheduleJob = scheduleJobDao.getByJobId(paramActionExt.getTaskId(),null);
            Assert.assertEquals(scheduleJob.getStatus(),RdosTaskStatus.SUBMITFAILD.getStatus());

        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testStartRepeat() {
        try {
            ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
            scheduleJobTemplate.setJobId("testRepeat");
            scheduleJobTemplate.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
            scheduleJobDao.insert(scheduleJobTemplate);
            Map<String, Object> params = getParams(getJsonString(getRandomStr()));
            ParamActionExt paramActionExt = com.dtstack.engine.common.util.PublicUtil.mapToObject(params, ParamActionExt.class);
            paramActionExt.setTaskId(scheduleJobTemplate.getJobId());
            paramActionExt.setAppType(AppType.RDOS.getType());
            Boolean result = actionService.start(paramActionExt);
            Assert.assertTrue(result);
            ScheduleJob scheduleJob = scheduleJobDao.getByJobId(paramActionExt.getTaskId(),null);
            Assert.assertEquals(scheduleJob.getStatus(),RdosTaskStatus.ENGINEACCEPTED.getStatus());

        } catch (Exception e) {
            fail("Have exception, message: " + e.getMessage());
        }
    }

    @Test
    public void testLog() {
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobFirst();
        String jobId = scheduleJob.getJobId();
        Integer computeType = scheduleJob.getComputeType();

        try {
            actionService.log(jobId, null);
            fail("Expect have a Exception");
        } catch (Exception e) {}

        try {
            String engineLog = "\"engineLog\":\"" + scheduleJob.getEngineLog() + "\"" ;
            String logInfo = "\"logInfo\":\"" + scheduleJob.getLogInfo() + "\"";
            ActionLogVO log = actionService.log(jobId, computeType);
//            Assert.assertTrue(result.contains(engineLog) && result.contains(logInfo) && result.length() == engineLog.length() + logInfo.length() + 3);
        } catch (Exception e) {
            fail("Unexpect have a Exception: " + e.getMessage());
        }

        scheduleJob = DataCollection.getData().getScheduleJobSecond();
        jobId = scheduleJob.getJobId();
        computeType = scheduleJob.getComputeType();
        String mock_engine_log = "{err: test_mock_engine_log}";
        when(jobDealer.getAndUpdateEngineLog(jobId, scheduleJob.getEngineJobId(),
                scheduleJob.getApplicationId(), scheduleJob.getDtuicTenantId())).thenReturn(mock_engine_log);
        try {
            String engineLog = "\"engineLog\":\"" + mock_engine_log + "\"" ;
            String logInfo = "\"logInfo\":\"" + scheduleJob.getLogInfo() + "\"";
            ActionLogVO log = actionService.log(jobId, computeType);
//            Assert.assertTrue(result.contains(engineLog) && result.contains(logInfo) && result.length() == engineLog.length() + logInfo.length() + 3);
        } catch (Exception e) {
            fail("Unexpect have a Exception: " + e.getMessage());
        }
    }

    @Test
    public void testContainerInfos() {
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobThird();
        try {
            ParamAction paramAction = PublicUtil.mapToObject(getParams(getJsonString(scheduleJob.getJobId())), ParamAction.class);
            paramAction.setAppType(AppType.RDOS.getType());
            List<String> result = actionService.containerInfos(paramAction);
            Assert.assertEquals(result, mockInfos);
        } catch (Exception e) {
            fail("Unexpect have a Exception: " + e.getMessage());
        }
    }

    @Test
    public void testGenerateUniqueSign() {
        String sign = actionService.generateUniqueSign();
        Assert.assertEquals(testEngineUniqueSignDao.getIdByUniqueSign(sign).size(),1);
    }

    @Test
    public void testResetTaskStatus() {
        ScheduleJob engineJob = DataCollection.getData().getScheduleJobToUnsumbittedStatus();
        String jobId = engineJob.getJobId();
        Integer computeType = engineJob.getComputeType();
        String resultId = actionService.resetTaskStatus(jobId);
        Assert.assertEquals(resultId, jobId);
        ScheduleJob result = scheduleJobDao.getRdosJobByJobId(jobId);
        Assert.assertEquals(result.getStatus(), RdosTaskStatus.UNSUBMIT.getStatus());
    }

    @Test
    public void testListJobStatus() {
        ScheduleJob engineJob = DataCollection.getData().getScheduleJobIndependentWithGmt();
        try {
            actionService.listJobStatus(null,null);
            fail("Expect have a Exception");
        } catch (Exception e) {}

        try {
            List<ActionJobStatusVO> result = actionService.listJobStatus(engineJob.getGmtModified().getTime(),1);
            Assert.assertTrue(result.size() > 0);
        } catch (Exception e) {
            fail("Unexpect have a Exception: " + e.getMessage());
        }
    }

    @Test
    public void testListJobStatusByJobIds() {
        ScheduleJob engineJob = DataCollection.getData().getScheduleJobIndependentWithGmt();
        try {
            List<ActionJobStatusVO> result = actionService.listJobStatusByJobIds(new ArrayList<>(Arrays.asList(engineJob.getJobId())));
            Assert.assertTrue(result.size() > 0);
        } catch (Exception e) {
            fail("Unexpect have a Exception: " + e.getMessage());
        }
    }


    private Map<String, Object> getParams(String json) {
        return JSONObject.parseObject(json, HashMap.class);
    }

    private String getRandomStr() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 其中利用系统时间创建不同的任务ID
     * @return
     */
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
                "on\",\"yarn.client.failover-proxy-provider\":\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\"yarn.resourcemanager.scheduler.address.rm1\":\"172.16.100.216:8030\",\"yarn.resourcemanager.scheduler.address.rm2\":\"172.16.101.136:8030\",\"yarn.nodemanager.delete.debug-delay-sec\":\"600\",\"yarn.resourcemanager.address.rm1\":\"172.16.100.216:8032\",\"yarn.log-aggregation.retain-seconds\":\"2592000\",\"yarn.nodemanager.resource.memory-mb\":\"6144\",\"yarn.resourcemanager.ha.enabled\":\"true\",\"yarn.resourcemanager.address.rm2\":\"172.16.101.136:8032\",\"yarn.resourcemanager.cluster-id\":\"yarn-rm-cluster\",\"yarn.scheduler.minimum-allocation-mb\":\"512\",\"yarn.nodemanager.aux-services\":\"mapreduce_shuffle\",\"yarn.resourcemanager.resource-tracker.address.rm1\":\"172.16.100.216:8031\",\"yarn.nodemanager.resource.cpu-vcores\":\"8\",\"yarn.resourcemanager.resource-tracker.address.rm2\":\"172.16.101.136:8031\",\"yarn.nodemanager.pmem-check-enabled\":\"true\",\"yarn.nodemanager.remote-app-log-dir\":\"/tmp/logs\",\"yarn.scheduler.maximum-allocation-mb\":\"6144\",\"yarn.resourcemanager.ha.automatic-failover.enabled\":\"true\",\"yarn.nodemanager.vmem-check-enabled\":\"false\",\"yarn.log-aggregation.retain-check-interval-seconds\":\"604800\",\"yarn.nodemanager.webapp.address\":\"0.0.0.0:8042\",\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\":\"org.apache.hadoop.mapred.ShuffleHandler\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"yarn.log-aggregation-enable\":\"true\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"yarn.nodemanager.vmem-pmem-ratio\":\"4\",\"yarn.resourcemanager.zk-state-store.address\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\",\"ha.zookeeper.quorum\":\"172.16.100.216:2181,172.16.101.136:2181,172.16.101.227:2181\"},\"sftpConf\":{\"path\":\"/data/beta4\",\"password\":\"abc123\",\"post\":\"22\",\"auth\":\"1\",\"host\":\"172.16.100.216\",\"username\":\"root\"},\"sparkPythonExtLibPath\":\"/dtInsight/pythons/pyspark.zip,hdfs://ns1/dtInsight/pythons/py4j-0.10.7-src.zip\",\"addColumnSupport\":\"true\",\"spark.eventLog.compress\":\"true\",\"sparkYarnArchive\":\"hdfs://ns1/dtInsight/sparkjars/jars\",\"spark.eventLog.enabled\":\"true\",\"spark.eventLog.dir\":\"hdfs://ns1/tmp/spark-yarn-logs\",\"md5zip\":\"\",\"tenantId\":13,\"queue\":\"default\"},\"engineType\":\"spark\",\"taskParams\":\"executor.instances=1\\nexecutor.cores=1\\njob.priority=10\",\"maxRetryNum\":0,\"taskType\":3,\"groupName\":\"test4_beta_default\",\"sourceType\":2,\"clusterName\":\"test4_beta\",\"name\":\"run_pyspark_task_1590461304653\",\"tenantId\":15,\"taskId\":\""+ taskId +"\"}";
        return spark_json;
    }

}
