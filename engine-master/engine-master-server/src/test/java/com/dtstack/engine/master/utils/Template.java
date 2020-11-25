package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.enums.EComponentType;

import java.sql.Timestamp;

public class Template {
    public static ScheduleJob getScheduleJobTemplate() {
        ScheduleJob sj = new ScheduleJob();
        sj.setStatus(5);
        sj.setJobId("testJobId");
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey("testJobKey");
        sj.setExecStartTime(new Timestamp(System.currentTimeMillis()));
        sj.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        sj.setTaskId(-1L);
        sj.setJobName("Python");
        sj.setCreateUserId(0L);
        sj.setIsDeleted(0);
        sj.setBusinessDate("20200608234500");
        sj.setCycTime(DateUtil.getUnStandardFormattedDate(System.currentTimeMillis()));
        sj.setTaskType(0);
        sj.setAppType(0);
        sj.setType(0);
        sj.setIsRestart(0);
        sj.setDependencyType(0);
        sj.setFlowJobId("0");
        sj.setPeriodType(0);
        sj.setMaxRetryNum(0);
        sj.setRetryNum(0);
        sj.setComputeType(1);
        sj.setLogInfo("{err: test_log_info}");
        sj.setEngineLog("{err: test_engine_log}");
        return sj;
    }

    public static EngineJobRetry getEngineJobRetryTemplate() {
        EngineJobRetry ej = new EngineJobRetry();
        ej.setEngineJobId(ValueUtils.getChangedStr());
        ej.setJobId(ValueUtils.getChangedStr());
        ej.setStatus(0);
        ej.setEngineLog("{err: test_engine_log}");
        ej.setLogInfo("{err: test_log_info}");
        ej.setApplicationId(ValueUtils.getChangedStr());
        ej.setRetryNum(2);
        ej.setRetryTaskParams("{err: test_retry_task_params}");
        return ej;
    }

    public static EngineJobCheckpoint getEngineJobCheckpointTemplate() {
        EngineJobCheckpoint jc = new EngineJobCheckpoint();
        jc.setTaskId("taskId");
        jc.setTaskEngineId("te-999");
        jc.setCheckpointId("2");
        jc.setCheckpointTrigger(Timestamp.valueOf("2020-06-14 12:12:12"));
        jc.setCheckpointSavepath("hdfs://tmp/flink/checkpoint/test");
        jc.setCheckpointCounts("2");
        return jc;
    }

    public static EngineJobCache getEngineJobCacheTemplate() {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId("jobId");
        engineJobCache.setJobName("test");
        engineJobCache.setEngineType("spark");
        engineJobCache.setJobPriority(10L);
        engineJobCache.setComputeType(1);
        engineJobCache.setJobInfo("{\"engineType\":\"spark\",\"taskType\":2,\"computeType\":0, \"tenantId\":9}");
        engineJobCache.setStage(2);
        engineJobCache.setNodeAddress("node01");
        engineJobCache.setJobResource("dtScript_dev_default_batch_Yarn");

        return engineJobCache;
    }

    public static EngineJobCache getEngineJobCacheTemplate2() {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId("jobId2");
        engineJobCache.setJobName("test");
        engineJobCache.setEngineType("spark");
        engineJobCache.setJobPriority(10L);
        engineJobCache.setComputeType(1);
        engineJobCache.setJobInfo("{\"engineType\":\"spark\",\"taskType\":2,\"computeType\":1, \"tenantId\":9, \"maxRetryNum\":3,\"taskParams\":\"openCheckpoint:true\"," +
                "\"taskId\":\"jobId2\"}");
        engineJobCache.setStage(2);
        engineJobCache.setNodeAddress(null);
        engineJobCache.setJobResource("test");

        return engineJobCache;
    }

    public static ScheduleTaskShade getScheduleTaskShadeTemplate(){
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(0L);
        scheduleTaskShade.setExtraInfo("test");
        scheduleTaskShade.setTenantId(ValueUtils.getChangedLong());
        scheduleTaskShade.setProjectId(ValueUtils.getChangedLong());
        scheduleTaskShade.setNodePid(ValueUtils.getChangedLong());
        scheduleTaskShade.setName("testJob");
        scheduleTaskShade.setTaskType(1);
        scheduleTaskShade.setEngineType(2);
        scheduleTaskShade.setComputeType(1);
        scheduleTaskShade.setSqlText("select");
        scheduleTaskShade.setTaskParams("null");
        scheduleTaskShade.setScheduleConf("null");
        scheduleTaskShade.setPeriodType(1);
        scheduleTaskShade.setScheduleStatus(1);
        scheduleTaskShade.setSubmitStatus(1);
        scheduleTaskShade.setGmtCreate(new Timestamp(1592559742000L));
        scheduleTaskShade.setGmtModified(new Timestamp(1592559742000L));
        scheduleTaskShade.setModifyUserId(1L);
        scheduleTaskShade.setCreateUserId(1L);
        scheduleTaskShade.setOwnerUserId(1L);
        scheduleTaskShade.setVersionId(1);
        scheduleTaskShade.setTaskDesc("null");
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setIsDeleted(0);
        scheduleTaskShade.setMainClass("DataCollection");
        scheduleTaskShade.setExeArgs("null");
        scheduleTaskShade.setFlowId(1L);
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setProjectScheduleStatus(1);

        return scheduleTaskShade;
    }

    public static Tenant getTenantTemplate(){
        Tenant tenant = new Tenant();
        tenant.setDtUicTenantId(ValueUtils.getChangedLong());
        tenant.setTenantName("testCase");
        tenant.setTenantDesc("");
        return tenant;
    }

    public static User getUserTemplate() {
        User user = new User();
        user.setDtuicUserId(-1L);
        user.setUserName("test@dtstack.com");
        user.setEmail("test@dtstack.com");
        user.setStatus(0);
        user.setPhoneNumber("");
        return user;
    }

    public static Component getDefaltHdfsComponentTemplate() {
        Component component = new Component();
        component.setEngineId(1L);
        component.setComponentName(EComponentType.HDFS.name());
        component.setComponentTypeCode(EComponentType.HDFS.getTypeCode());
        component.setComponentConfig("{\"fs.defaultFS\":\"hdfs://ns1\",\"hadoop.proxyuser.admin.groups\":\"*\",\"javax.jdo.option.ConnectionDriverName\":\"com.mysql.jdbc.Driver\",\"dfs.replication\":\"2\",\"dfs.ha.fencing.methods\":\"sshfence\",\"dfs.client.failover.proxy.provider.ns1\":\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\"typeName\":\"yarn2-hdfs2-hadoop2\",\"dfs.ha.fencing.ssh.private-key-files\":\"~/.ssh/id_rsa\",\"dfs.nameservices\":\"ns1\",\"javax.jdo.option.ConnectionURL\":\"jdbc:mysql://kudu2:3306/ide?useSSL=false\",\"dfs.safemode.threshold.pct\":\"0.5\",\"dfs.ha.namenodes.ns1\":\"nn1,nn2\",\"dfs.journalnode.rpc-address\":\"0.0.0.0:8485\",\"dfs.journalnode.http-address\":\"0.0.0.0:8480\",\"dfs.namenode.rpc-address.ns1.nn2\":\"kudu2:9000\",\"dfs.namenode.rpc-address.ns1.nn1\":\"kudu1:9000\",\"hive.metastore.warehouse.dir\":\"/user/hive/warehouse\",\"hive.server2.webui.host\":\"172.16.10.34\",\"dfs.namenode.shared.edits.dir\":\"qjournal://kudu1:8485;kudu2:8485;kudu3:8485/namenode-ha-data\",\"hive.metastore.schema.verification\":\"false\",\"javax.jdo.option.ConnectionUserName\":\"dtstack\",\"hive.server2.support.dynamic.service.discovery\":\"true\",\"javax.jdo.option.ConnectionPassword\":\"abc123\",\"hive.metastore.uris\":\"thrift://kudu1:9083\",\"hive.server2.thrift.port\":\"10000\",\"hive.exec.dynamic.partition.mode\":\"nonstrict\",\"ha.zookeeper.session-timeout.ms\":\"5000\",\"hadoop.tmp.dir\":\"/data/hadoop_${user.name}\",\"dfs.journalnode.edits.dir\":\"/data/dtstack/hadoop/journal\",\"hive.server2.zookeeper.namespace\":\"hiveserver2\",\"hive.server2.enable.doAs\":\"/false\",\"dfs.namenode.http-address.ns1.nn2\":\"kudu2:50070\",\"dfs.namenode.http-address.ns1.nn1\":\"kudu1:50070\",\"hadoop.proxyuser.admin.hosts\":\"*\",\"md5zip\":\"47a9d897ea8fbf51265cecd30536ebad\",\"hive.exec.scratchdir\":\"/user/hive/warehouse\",\"hive.server2.webui.max.threads\":\"100\",\"hive.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"datanucleus.schema.autoCreateAll\":\"true\",\"hive.exec.dynamic.partition\":\"true\",\"hive.server2.thrift.bind.host\":\"kudu1\",\"ha.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"hive.server2.thrift.min.worker.threads\":\"200\",\"hive.server2.webui.port\":\"10002\",\"dfs.ha.automatic-failover.enabled\":\"true\"}");
        component.setClusterId(1L);
        component.setHadoopVersion("hadoop3");
        component.setComponentTemplate("[]");
        component.setUploadFileName("conf.zip");
        component.setKerberosFileName("kb.zip");
        component.setStoreType(0);
        return component;
    }

    public static Component getDefaultYarnComponentTemplate() {
        Component component = new Component();
        component.setEngineId(1L);
        component.setComponentName(EComponentType.YARN.name());
        component.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        component.setComponentConfig("{\"yarn.resourcemanager.zk-address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"yarn.resourcemanager.admin.address.rm1\":\"kudu1:8033\",\"yarn.resourcemanager.webapp.address.rm2\":\"kudu2:8088\",\"yarn.log.server.url\":\"http://kudu3:19888/jobhistory/logs/\",\"yarn.resourcemanager.admin.address.rm2\":\"kudu2:8033\",\"yarn.resourcemanager.webapp.address.rm1\":\"kudu1:8088\",\"yarn.resourcemanager.ha.rm-ids\":\"rm1,rm2\",\"yarn.resourcemanager.ha.automatic-failover.zk-base-path\":\"/yarn-leader-election\",\"yarn.client.failover-proxy-provider\":\"org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider\",\"yarn.resourcemanager.scheduler.address.rm1\":\"kudu1:8030\",\"yarn.resourcemanager.scheduler.address.rm2\":\"kudu2:8030\",\"yarn.nodemanager.delete.debug-delay-sec\":\"600\",\"yarn.resourcemanager.address.rm1\":\"kudu1:8032\",\"yarn.log-aggregation.retain-seconds\":\"2592000\",\"yarn.nodemanager.resource.memory-mb\":\"8000\",\"yarn.resourcemanager.ha.enabled\":\"true\",\"yarn.resourcemanager.address.rm2\":\"kudu2:8032\",\"yarn.resourcemanager.cluster-id\":\"yarn-rm-cluster\",\"yarn.scheduler.minimum-allocation-mb\":\"512\",\"yarn.nodemanager.aux-services\":\"mapreduce_shuffle\",\"yarn.resourcemanager.resource-tracker.address.rm1\":\"kudu1:8031\",\"yarn.nodemanager.resource.cpu-vcores\":\"10\",\"yarn.resourcemanager.resource-tracker.address.rm2\":\"kudu2:8031\",\"yarn.nodemanager.pmem-check-enabled\":\"false\",\"yarn.nodemanager.remote-app-log-dir\":\"/tmp/logs\",\"yarn.resourcemanager.ha.automatic-failover.enabled\":\"true\",\"yarn.nodemanager.vmem-check-enabled\":\"false\",\"yarn.resourcemanager.hostname.rm2\":\"kudu2\",\"yarn.nodemanager.webapp.address\":\"kudu1:8042\",\"yarn.resourcemanager.hostname.rm1\":\"kudu1\",\"yarn.nodemanager.aux-services.mapreduce_shuffle.class\":\"org.apache.hadoop.mapred.ShuffleHandler\",\"yarn.resourcemanager.recovery.enabled\":\"true\",\"yarn.log-aggregation-enable\":\"true\",\"yarn.resourcemanager.store.class\":\"org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore\",\"yarn.nodemanager.vmem-pmem-ratio\":\"4\",\"yarn.resourcemanager.zk-state-store.address\":\"kudu1:2181,kudu2:2181,kudu3:2181\",\"ha.zookeeper.quorum\":\"kudu1:2181,kudu2:2181,kudu3:2181\"}");
        component.setClusterId(1L);
        component.setHadoopVersion("hadoop3");
        component.setComponentTemplate("[]");
        component.setUploadFileName("conf.zip");
        component.setKerberosFileName("kb.zip");
        component.setStoreType(0);
        return component;
    }

    public static Component getDefaultSftpComponentTemplate() {
        Component component = new Component();
        component.setEngineId(1L);
        component.setComponentName(EComponentType.SFTP.name());
        component.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        component.setComponentConfig("{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"auth\":\"1\",\"port\":\"22\",\"host\":\"172.16.100.115\",\"username\":\"root\"}");
        component.setClusterId(1L);
        component.setHadoopVersion("hadoop3");
        component.setComponentTemplate("[]");
        component.setUploadFileName("");
        component.setKerberosFileName("");
        component.setStoreType(0);
        return component;
    }
}
