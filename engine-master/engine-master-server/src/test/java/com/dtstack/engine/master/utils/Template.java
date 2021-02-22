package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.schedule.common.enums.AppType;

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
        sj.setTaskType(EJobType.SQL.getType());
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

    public static EngineJobCheckpoint getEngineJobSavepointTemplate() {
        EngineJobCheckpoint jc = new EngineJobCheckpoint();
        jc.setTaskId("taskId");
        jc.setTaskEngineId("te-9991");
        jc.setCheckpointId("savepointId");
        jc.setCheckpointTrigger(Timestamp.valueOf("2020-06-14 12:12:12"));
        jc.setCheckpointSavepath("hdfs://ns1/dtInsight/flink110/savepoints/savepoint-77aea4-a0b9f689989c");
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
        engineJobCache.setIsFailover(0);
        engineJobCache.setNodeAddress("node01");
        engineJobCache.setJobResource("dtScript_dev_default_batch_Yarn");

        return engineJobCache;
    }


    public static EngineJobCache getEngineJobCacheTemplateCopyFromJob(ScheduleJob scheduleJob) {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId(scheduleJob.getJobId());
        engineJobCache.setJobName(scheduleJob.getJobName());
        engineJobCache.setEngineType("flink");
        engineJobCache.setJobPriority(10L);
        engineJobCache.setComputeType(scheduleJob.getComputeType());
        engineJobCache.setJobInfo("{\"engineType\":\"spark\",\"taskType\":2,\"computeType\":0, \"tenantId\":9}");
        engineJobCache.setStage(2);
        engineJobCache.setIsFailover(0);
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
        engineJobCache.setIsFailover(0);
        engineJobCache.setNodeAddress(null);
        engineJobCache.setJobResource("test");

        return engineJobCache;
    }

    /**
     * stage为5
     * @return
     */
    public static EngineJobCache getEngineJobCacheTemplate3() {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId("jobId2");
        engineJobCache.setJobName("test");
        engineJobCache.setEngineType("spark");
        engineJobCache.setJobPriority(10L);
        engineJobCache.setComputeType(1);
        engineJobCache.setJobInfo("{\"engineType\":\"spark\",\"taskType\":2,\"computeType\":1, \"tenantId\":9, \"maxRetryNum\":3,\"taskParams\":\"openCheckpoint:true\"," +
                "\"taskId\":\"jobId2\"}");
        engineJobCache.setStage(5);
        engineJobCache.setIsFailover(0);
        engineJobCache.setNodeAddress(null);
        engineJobCache.setJobResource("test");

        return engineJobCache;
    }

    public static ScheduleTaskShade getScheduleTaskShadeTemplate(){
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(0L);
        scheduleTaskShade.setExtraInfo("{}");
        scheduleTaskShade.setTenantId(ValueUtils.getChangedLong());
        scheduleTaskShade.setProjectId(ValueUtils.getChangedLong());
        scheduleTaskShade.setNodePid(ValueUtils.getChangedLong());
        scheduleTaskShade.setName("testJob");
        scheduleTaskShade.setTaskType(1);
        scheduleTaskShade.setEngineType(2);
        scheduleTaskShade.setComputeType(1);
        scheduleTaskShade.setSqlText("select");
        scheduleTaskShade.setTaskParams("null");
        scheduleTaskShade.setScheduleConf("{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
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
        scheduleTaskShade.setFlowId(0L);
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setProjectScheduleStatus(0);

        return scheduleTaskShade;
    }

    public static ScheduleTaskTaskShade getTaskTask(){
        ScheduleTaskTaskShade scheduleTaskShade = new ScheduleTaskTaskShade();
        scheduleTaskShade.setTaskId(5L);
        scheduleTaskShade.setParentTaskId(6L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setGmtCreate(new Timestamp(1592559742000L));
        scheduleTaskShade.setGmtModified(new Timestamp(1592559742000L));
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setIsDeleted(0);
        scheduleTaskShade.setTenantId(ValueUtils.getChangedLong());
        scheduleTaskShade.setProjectId(ValueUtils.getChangedLong());
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

    public static ScheduleTaskShade getCronDayTask() {

        return null;
    }

    public static Engine getDefaultEngineTemplate(){
        Engine engine = new Engine();
        engine.setId(1L);
        engine.setClusterId(1L);
        engine.setEngineName("hadoop");
        engine.setEngineType(MultiEngineType.HADOOP.getType());
        engine.setSyncType(null);
        engine.setTotalCore(10);
        engine.setTotalMemory(40960);
        engine.setTotalNode(10);
        return engine;
    }

    public static Component getDefaultHdfsComponentTemplate() {
        Component component = new Component();
        component.setEngineId(1L);
        component.setComponentName(EComponentType.HDFS.name());
        component.setComponentTypeCode(EComponentType.HDFS.getTypeCode());
        component.setClusterId(1L);
        component.setHadoopVersion("hadoop3");
        component.setUploadFileName("conf.zip");
        component.setKerberosFileName("kb.zip");
        return component;
    }

    public static Component getDefaultYarnComponentTemplate() {
        Component component = new Component();
        component.setEngineId(1L);
        component.setComponentName(EComponentType.YARN.name());
        component.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        component.setClusterId(1L);
        component.setHadoopVersion("hadoop3");
        component.setUploadFileName("conf.zip");
        component.setKerberosFileName("kb.zip");
        return component;
    }

    public static Component getDefaultSftpComponentTemplate() {
        Component component = new Component();
        component.setEngineId(1L);
        component.setComponentName(EComponentType.SFTP.name());
        component.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        component.setClusterId(1L);
        component.setHadoopVersion("hadoop3");
        component.setUploadFileName("");
        component.setKerberosFileName("");
        return component;
    }

    public static Component getDefaultK8sComponentTemplate(){
        Component component = new Component();
        component.setEngineId(1L);
        component.setComponentName(EComponentType.KUBERNETES.name());
        component.setComponentTypeCode(EComponentType.KUBERNETES.getTypeCode());
        component.setClusterId(1L);
        component.setHadoopVersion("");
        component.setUploadFileName("");
        component.setKerberosFileName("");
        return component;
    }

    public static ScheduleFillDataJob getDefaultScheduleFillDataJobTemplate(){
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        scheduleFillDataJob.setRunDay("2020-11-26");
        scheduleFillDataJob.setFromDay("2020-11-26");
        scheduleFillDataJob.setToDay("2020-11-26");
        scheduleFillDataJob.setCreateUserId(1L);
        scheduleFillDataJob.setAppType(AppType.RDOS.getType());
        scheduleFillDataJob.setDtuicTenantId(1L);
        scheduleFillDataJob.setJobName("test");
        scheduleFillDataJob.setTenantId(1L);
        scheduleFillDataJob.setProjectId(1L);
        return scheduleFillDataJob;
    }

    public static ScheduleJob getDefaultScheduleJobForSpring1Template(){
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTenantId(1L);
        scheduleJob.setProjectId(11L);
        scheduleJob.setDtuicTenantId(1L);
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setJobId("0e3f500e");
        scheduleJob.setJobKey("cronTrigger_1925_20201126000000");
        scheduleJob.setJobName("cronJob_mysqll1_virtual_20201126000000");
        scheduleJob.setTaskId(1L);
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(0);
        scheduleJob.setIsRestart(0);
        scheduleJob.setBusinessDate("20201125000000");
        scheduleJob.setCycTime("20201126000000");
        scheduleJob.setDependencyType(0);
        scheduleJob.setFlowJobId("0");
        scheduleJob.setPeriodType(2);
        scheduleJob.setStatus(5);
        scheduleJob.setTaskType(-1);
        scheduleJob.setMaxRetryNum(3);
        scheduleJob.setRetryNum(0);
        scheduleJob.setNodeAddress("127.0.0.1:8099");
        scheduleJob.setVersionId(300);
        scheduleJob.setComputeType(1);
        scheduleJob.setPhaseStatus(2);
        return scheduleJob;
    }

    public static ScheduleJob getDefaultScheduleJobForSpring2Template(){
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTenantId(1L);
        scheduleJob.setProjectId(11L);
        scheduleJob.setDtuicTenantId(1L);
        scheduleJob.setAppType(AppType.RDOS.getType());
        scheduleJob.setJobId("a2114bc5");
        scheduleJob.setJobKey("cronTrigger_1927_20201126000000");
        scheduleJob.setJobName("cronJob_mysqll2hive_dt_center_cronnew_schedule_20201126000000");
        scheduleJob.setTaskId(2L);
        scheduleJob.setCreateUserId(1L);
        scheduleJob.setType(0);
        scheduleJob.setIsRestart(0);
        scheduleJob.setBusinessDate("20201125000000");
        scheduleJob.setCycTime("20201126000000");
        scheduleJob.setDependencyType(0);
        scheduleJob.setFlowJobId("0");
        scheduleJob.setPeriodType(2);
        scheduleJob.setStatus(5);
        scheduleJob.setTaskType(-1);
        scheduleJob.setMaxRetryNum(3);
        scheduleJob.setRetryNum(0);
        scheduleJob.setNodeAddress("127.0.0.1:8099");
        scheduleJob.setVersionId(300);
        scheduleJob.setComputeType(1);
        scheduleJob.setPhaseStatus(2);
        return scheduleJob;
    }

    public static ScheduleJobJob getDefaultScheduleJobJobForSpring1Template(){
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setTenantId(1L);
        scheduleJobJob.setProjectId(11L);
        scheduleJobJob.setDtuicTenantId(1L);
        scheduleJobJob.setAppType(AppType.RDOS.getType());
        scheduleJobJob.setJobKey("cronTrigger_1927_20201126000000");
        scheduleJobJob.setParentJobKey("cronTrigger_1925_20201126000000");
        scheduleJobJob.setGmtCreate(new Timestamp(1592559742000L));
        scheduleJobJob.setGmtModified(new Timestamp(1592559742000L));
        return scheduleJobJob;
    }

    public static ScheduleTaskShade getDefaultScheduleTaskFlowTemplate(){
        ScheduleTaskShade taskShade = new ScheduleTaskShade();
        taskShade.setTenantId(1L);
        taskShade.setProjectId(1L);
        taskShade.setDtuicTenantId(1L);
        taskShade.setAppType(AppType.RDOS.getType());
        taskShade.setNodePid(2955L);
        taskShade.setName("test_workflow_python1");
        taskShade.setTaskType(6);
        taskShade.setEngineType(5);
        taskShade.setComputeType(1);
        taskShade.setSqlText("");
        taskShade.setTaskParams("");
        taskShade.setTaskId(1953L);
        taskShade.setScheduleConf("");
        taskShade.setPeriodType(2);
        taskShade.setScheduleStatus(1);
        taskShade.setProjectScheduleStatus(0);
        taskShade.setSubmitStatus(1);
        taskShade.setModifyUserId(1L);
        taskShade.setCreateUserId(1L);
        taskShade.setOwnerUserId(1L);
        taskShade.setVersionId(911);
        taskShade.setTaskDesc("");
        taskShade.setMainClass("");
        taskShade.setExeArgs("");
        taskShade.setFlowId(1941L);
        taskShade.setIsPublishToProduce(0L);
        taskShade.setIsExpire(0);
        taskShade.setGmtCreate(new Timestamp(1592559742000L));
        taskShade.setGmtModified(new Timestamp(1592559742000L));
        return taskShade;
    }

    public static ScheduleTaskShade getDefaultScheduleTaskFlowParentTemplate(){
        ScheduleTaskShade taskShade = getDefaultScheduleTaskFlowTemplate();
        taskShade.setName("test_workflow");
        taskShade.setTaskType(10);
        taskShade.setEngineType(1);
        taskShade.setComputeType(1);
        taskShade.setTaskId(1941L);
        taskShade.setFlowId(0L);
        return taskShade;
    }

    public static ScheduleJob getDefaultScheduleJobFlowParentTemplate(){
        ScheduleJob scheduleJob = getDefaultScheduleJobForSpring1Template();
        scheduleJob.setJobId("a4636a9f");
        scheduleJob.setJobKey("cronTrigger_3381_20201127000000");
        scheduleJob.setJobName("cronJob_test_workflow_20201127000000");
        scheduleJob.setTaskId(1941L);
        scheduleJob.setFlowJobId("0");
        return scheduleJob;
    }

    public static ScheduleJob getDefaultScheduleJobFlowChildTemplate(){
        ScheduleJob scheduleJob = getDefaultScheduleJobForSpring1Template();
        scheduleJob.setJobId("55545a40");
        scheduleJob.setJobKey("cronTrigger_3377_20201127000000");
        scheduleJob.setJobName("cronJob_test_workflow_python1_20201127000000");
        scheduleJob.setTaskId(1953L);
        scheduleJob.setFlowJobId("a4636a9f");
        return scheduleJob;
    }

    public static ScheduleJobJob getDefaultScheduleJobJobFlowTemplate(){
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setTenantId(1L);
        scheduleJobJob.setProjectId(1L);
        scheduleJobJob.setDtuicTenantId(1L);
        scheduleJobJob.setAppType(AppType.RDOS.getType());
        scheduleJobJob.setJobKey("cronTrigger_3377_20201127000000");
        scheduleJobJob.setParentJobKey("cronTrigger_3381_20201127000000");
        scheduleJobJob.setGmtCreate(new Timestamp(1592559742000L));
        scheduleJobJob.setGmtModified(new Timestamp(1592559742000L));
        return scheduleJobJob;
    }
}
