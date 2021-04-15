package com.dtstack.engine.master.utils;

import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.domain.AlertRecord;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.DataSourceType;

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
        sj.setBusinessDate(DateUtil.getUnStandardFormattedDate(System.currentTimeMillis()));
        sj.setCycTime(DateUtil.getUnStandardFormattedDate(System.currentTimeMillis()));
        sj.setTaskType(EJobType.SQL.getType());
        sj.setAppType(1);
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

    public static EngineJobCheckpoint getFailedEngineJobCheckpointTemplate() {
        EngineJobCheckpoint jc = new EngineJobCheckpoint();
        jc.setTaskId("myTaskId");
        jc.setTaskEngineId("haier111");
        return jc;
    }


    public static EngineJobCheckpoint getEngineJobSavepointTemplate() {
        EngineJobCheckpoint jc = new EngineJobCheckpoint();
        jc.setTaskId("taskId");
        jc.setTaskEngineId("te-9991");
        jc.setCheckpointId("2");
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

    public static ScheduleTaskTaskShade getTaskTask() {
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

    public static Tenant getTenantTemplate() {
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

    public static Engine getDefaultEngineTemplate() {
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
        component.setStoreType(EComponentType.HDFS.getTypeCode());
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
        component.setStoreType(EComponentType.HDFS.getTypeCode());
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
        component.setStoreType(EComponentType.SFTP.getTypeCode());
        return component;
    }

    public static Component getDefaultK8sComponentTemplate() {
        Component component = new Component();
        component.setEngineId(1L);
        component.setComponentName(EComponentType.KUBERNETES.name());
        component.setComponentTypeCode(EComponentType.KUBERNETES.getTypeCode());
        component.setClusterId(1L);
        component.setHadoopVersion("");
        component.setUploadFileName("");
        component.setKerberosFileName("");
        component.setStoreType(0);
        return component;
    }

    public static ScheduleFillDataJob getDefaultScheduleFillDataJobTemplate() {
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

    public static ScheduleJob getDefaultScheduleJobForSpring1Template() {
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

    public static ScheduleJob getDefaultScheduleJobForSpring2Template() {
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

    public static ScheduleJobJob getDefaultScheduleJobJobForSpring1Template() {
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

    public static ScheduleTaskShade getDefaultScheduleTaskFlowTemplate() {
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

    public static ScheduleTaskShade getDefaultScheduleTaskFlowParentTemplate() {
        ScheduleTaskShade taskShade = getDefaultScheduleTaskFlowTemplate();
        taskShade.setName("test_workflow");
        taskShade.setTaskType(10);
        taskShade.setEngineType(1);
        taskShade.setComputeType(1);
        taskShade.setTaskId(1941L);
        taskShade.setFlowId(0L);
        return taskShade;
    }

    public static ScheduleJob getDefaultScheduleJobFlowParentTemplate() {
        ScheduleJob scheduleJob = getDefaultScheduleJobForSpring1Template();
        scheduleJob.setJobId("a4636a9f");
        scheduleJob.setJobKey("cronTrigger_3381_20201127000000");
        scheduleJob.setJobName("cronJob_test_workflow_20201127000000");
        scheduleJob.setTaskId(1941L);
        scheduleJob.setFlowJobId("0");
        return scheduleJob;
    }

    public static ScheduleJob getDefaultScheduleJobFlowChildTemplate() {
        ScheduleJob scheduleJob = getDefaultScheduleJobForSpring1Template();
        scheduleJob.setJobId("55545a40");
        scheduleJob.setJobKey("cronTrigger_3377_20201127000000");
        scheduleJob.setJobName("cronJob_test_workflow_python1_20201127000000");
        scheduleJob.setTaskId(1953L);
        scheduleJob.setFlowJobId("a4636a9f");
        return scheduleJob;
    }

    public static ScheduleJobJob getDefaultScheduleJobJobFlowTemplate() {
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setTenantId(1L);
        scheduleJobJob.setProjectId(1L);
        scheduleJobJob.setDtuicTenantId(1L);
        scheduleJobJob.setParentAppType(1);
        scheduleJobJob.setAppType(AppType.RDOS.getType());
        scheduleJobJob.setJobKey("cronTrigger_3377_20201127000000");
        scheduleJobJob.setParentJobKey("cronTrigger_3381_20201127000000");
        scheduleJobJob.setGmtCreate(new Timestamp(1592559742000L));
        scheduleJobJob.setGmtModified(new Timestamp(1592559742000L));
        return scheduleJobJob;
    }

    public static LineageTableTable getLineageTableTableTemplate() {
        LineageTableTable lineageTableTable = new LineageTableTable();
        lineageTableTable.setDtUicTenantId(1L);
        lineageTableTable.setAppType(AppType.RDOS.getType());
        lineageTableTable.setLineageSource(0);
        lineageTableTable.setInputTableId(-2020L);
        lineageTableTable.setInputTableKey("-2020");
        lineageTableTable.setResultTableId(-2021L);
        lineageTableTable.setResultTableKey("-2021");
        lineageTableTable.setTableLineageKey("");
        return lineageTableTable;
    }

    public static LineageRealDataSource getDefaultHiveRealDataSourceTemplate() {
        LineageRealDataSource lineageRealDataSource = new LineageRealDataSource();
        lineageRealDataSource.setSourceName("testHive1");
        lineageRealDataSource.setSourceKey("172.16.8.107#10000");
        lineageRealDataSource.setSourceType(DataSourceType.HIVE.getVal());
        lineageRealDataSource.setDataJason("{\"jdbcUrl\": \"jdbc:hive2://172.16.8.107:10000/default\", \"password\": \"\", \"typeName\": \"hive\", \"username\": \"admin\", \"maxJobPoolSize\": \"\", \"minJobPoolSize\": \"\"}");
        lineageRealDataSource.setKerberosConf("-1");
        lineageRealDataSource.setOpenKerberos(0);
        return lineageRealDataSource;
    }

    public static LineageDataSource getDefaultHiveDataSourceTemplate() {
        LineageDataSource lineageDataSource = new LineageDataSource();
        lineageDataSource.setDtUicTenantId(1L);
        lineageDataSource.setRealSourceId(1L);
        lineageDataSource.setSourceKey("172.16.8.107#10000");
        lineageDataSource.setSourceName("hive");
        lineageDataSource.setAppType(AppType.DATAASSETS.getType());
        lineageDataSource.setSourceType(DataSourceType.HIVE.getVal());
        lineageDataSource.setDataJson("{}");
        lineageDataSource.setKerberosConf("-1");
        lineageDataSource.setOpenKerberos(0);
        lineageDataSource.setAppSourceId(-1);
        lineageDataSource.setInnerSource(-1);
        lineageDataSource.setComponentId(-1);
        return lineageDataSource;
    }

    public static LineageTableTable getDefaultLineageTableTableTemplate() {
        LineageTableTable lineageTableTable = new LineageTableTable();
        lineageTableTable.setDtUicTenantId(1L);
        lineageTableTable.setAppType(AppType.DATAASSETS.getType());
        lineageTableTable.setLineageSource(0);
        lineageTableTable.setInputTableId(-2020L);
        lineageTableTable.setInputTableKey("-2020");
        lineageTableTable.setResultTableId(-2021L);
        lineageTableTable.setResultTableKey("-2021");
        lineageTableTable.setTableLineageKey("");
        return lineageTableTable;
    }

    public static LineageDataSetInfo getDefaultDataSetInfoTemplate() {
        LineageDataSetInfo lineageDataSetInfo = new LineageDataSetInfo();
        lineageDataSetInfo.setAppType(AppType.DATAASSETS.getType());
        lineageDataSetInfo.setDtUicTenantId(1L);
        lineageDataSetInfo.setSourceId(1L);
        lineageDataSetInfo.setRealSourceId(1L);
        lineageDataSetInfo.setSourceName("hive");
        lineageDataSetInfo.setSourceType(DataSourceType.HIVE.getVal());
        lineageDataSetInfo.setSourceKey("172.16.8.107#10000");
        lineageDataSetInfo.setSetType(0);
        lineageDataSetInfo.setDbName("default");
        lineageDataSetInfo.setSchemaName("default");
        lineageDataSetInfo.setTableName("test");
        lineageDataSetInfo.setTableKey("1defaulttest");
        lineageDataSetInfo.setIsManual(0);
        return lineageDataSetInfo;
    }

    public static LineageDataSetInfo getHiveDataSetInfoTemplate() {
        LineageDataSetInfo lineageDataSetInfo = new LineageDataSetInfo();
        lineageDataSetInfo.setAppType(AppType.DATAASSETS.getType());
        lineageDataSetInfo.setSourceId(1L);
        lineageDataSetInfo.setRealSourceId(1L);
        lineageDataSetInfo.setSourceName("hive");
        lineageDataSetInfo.setSourceType(DataSourceType.HIVE.getVal());
        lineageDataSetInfo.setSourceKey("172.16.8.107#10000");
        lineageDataSetInfo.setSetType(0);
        lineageDataSetInfo.setDbName("default");
        lineageDataSetInfo.setSchemaName("default");
        lineageDataSetInfo.setTableName("test1");
        lineageDataSetInfo.setTableKey("1defaulttest1");
        lineageDataSetInfo.setIsManual(0);
        lineageDataSetInfo.setDtUicTenantId(1L);
        return lineageDataSetInfo;
    }

    public static LineageTableTable getDefaultTableTable() {
        LineageDataSetInfo lineageDataSetInfo = Template.getDefaultDataSetInfoTemplate();
        LineageDataSetInfo lineageDataSetInfo2 = Template.getHiveDataSetInfoTemplate();
        LineageTableTable lineageTableTable = new LineageTableTable();
        lineageTableTable.setAppType(AppType.DATAASSETS.getType());
        lineageTableTable.setDtUicTenantId(1L);
        lineageTableTable.setInputTableId(lineageDataSetInfo.getId());
        lineageTableTable.setInputTableKey(lineageDataSetInfo.getTableKey());
        lineageTableTable.setResultTableId(lineageDataSetInfo2.getId());
        lineageTableTable.setResultTableKey(lineageDataSetInfo2.getTableKey());
        lineageTableTable.setTableLineageKey(lineageDataSetInfo.getId() + "_" + lineageDataSetInfo2.getId());
        lineageTableTable.setLineageSource(LineageOriginType.SQL_PARSE.getType());
        return lineageTableTable;
    }

    public static LineageColumnColumn getDefaultColumnColumn() {
        LineageDataSetInfo lineageDataSetInfo = Template.getDefaultDataSetInfoTemplate();
        LineageDataSetInfo lineageDataSetInfo2 = Template.getHiveDataSetInfoTemplate();

        LineageColumnColumn lineageColumnColumn = new LineageColumnColumn();
        lineageColumnColumn.setAppType(AppType.DATAASSETS.getType());
        lineageColumnColumn.setInputTableKey(lineageDataSetInfo.getTableKey());
        lineageColumnColumn.setInputTableId(lineageDataSetInfo.getId());
        lineageColumnColumn.setInputColumnName("id");
        lineageColumnColumn.setResultTableKey(lineageDataSetInfo2.getTableKey());
        lineageColumnColumn.setResultTableId(lineageDataSetInfo2.getId());
        lineageColumnColumn.setResultColumnName("tid");
        lineageColumnColumn.setDtUicTenantId(1L);
        lineageColumnColumn.setLineageSource(LineageOriginType.SQL_PARSE.getType());
        String rawKey = String.format("%s.%s_%s.%s", lineageDataSetInfo.getId(), "id", lineageColumnColumn.getResultTableId(), lineageColumnColumn.getResultColumnName());
        lineageColumnColumn.setColumnLineageKey(MD5Util.getMd5String(rawKey));
        return lineageColumnColumn;
    }

    public static AlertChannel getDefaultAlterChannelTemplateSmsJar() {
        AlertChannel alertContent = new AlertChannel();
        alertContent.setId(1L);
        alertContent.setAlertGateSource("sms_test");
        alertContent.setIsDefault(IsDefaultEnum.DEFAULT.getType());
        alertContent.setAlertGateType(AlertGateTypeEnum.SMS.getType());
        alertContent.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertContent.setAlertGateName("短信测试");
        alertContent.setAlertGateJson("{\"className\":\"com.dtstack.sdk.example.ISmsChannelExample\"}");
        alertContent.setAlertTemplate("");
        alertContent.setClusterId(1L);
        alertContent.setAlertGateCode(AlertGateCode.AG_GATE_SMS_JAR.code());
        String classPath = Template.class.getResource("/").getPath();
        alertContent.setFilePath(classPath+"/alter/console-alert-plugin-sdk-example-4.0.0.jar");
        return alertContent;
    }

    public static AlertChannel getDefaultAlterChannelTemplateDingJar() {
        AlertChannel alertContent = new AlertChannel();
        alertContent.setId(2L);
        alertContent.setAlertGateSource("ding_test");
        alertContent.setIsDefault(IsDefaultEnum.DEFAULT.getType());
        alertContent.setAlertGateType(AlertGateTypeEnum.DINGDING.getType());
        alertContent.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertContent.setAlertGateName("钉钉jar测试");
        alertContent.setAlertGateJson("{\"className\":\"com.dtstack.sdk.example.IDingChannelExample\"}");
        alertContent.setAlertTemplate("");
        alertContent.setClusterId(1L);
        alertContent.setAlertGateCode(AlertGateCode.AG_GATE_DING_JAR.code());
        String classPath = Template.class.getResource("/").getPath();
        alertContent.setFilePath(classPath+"/alter/console-alert-plugin-sdk-example-4.0.0.jar");
        return alertContent;
    }

    public static AlertChannel getDefaultAlterChannelTemplateMailJar() {
        AlertChannel alertContent = new AlertChannel();
        alertContent.setId(3L);
        alertContent.setAlertGateSource("mail_test");
        alertContent.setIsDefault(IsDefaultEnum.DEFAULT.getType());
        alertContent.setAlertGateType(AlertGateTypeEnum.MAIL.getType());
        alertContent.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertContent.setAlertGateName("邮箱jar测试");
        alertContent.setAlertGateJson("{\"className\":\"com.dtstack.sdk.example.IMailChannelExample\"}");
        alertContent.setAlertTemplate("");
        alertContent.setClusterId(1L);
        alertContent.setAlertGateCode(AlertGateCode.AG_GATE_MAIL_JAR.code());
        String classPath = Template.class.getResource("/").getPath();
        alertContent.setFilePath(classPath+"/alter/console-alert-plugin-sdk-example-4.0.0.jar");
        return alertContent;
    }

    public static AlertChannel getDefaultAlterChannelTemplateICustomizeJar() {
        AlertChannel alertContent = new AlertChannel();
        alertContent.setId(4L);
        alertContent.setAlertGateSource("customize_test");
        alertContent.setIsDefault(IsDefaultEnum.NOT_DEFAULT.getType());
        alertContent.setAlertGateType(AlertGateTypeEnum.CUSTOMIZE.getType());
        alertContent.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertContent.setAlertGateName("自定义jar测试");
        alertContent.setAlertGateJson("{\"className\":\"com.dtstack.sdk.example.ICustomizeChannelExample\"}");
        alertContent.setAlertTemplate("");
        alertContent.setClusterId(1L);
        alertContent.setAlertGateCode(AlertGateCode.AG_GATE_CUSTOM_JAR.code());
        String classPath = Template.class.getResource("/").getPath();
        alertContent.setFilePath(classPath+"/alter/console-alert-plugin-sdk-example-4.0.0.jar");
        return alertContent;
    }

    public static AlertChannel getDefaultAlterChannelTemplateDingDt(){
        AlertChannel alertContent = new AlertChannel();
        alertContent.setId(5L);
        alertContent.setAlertGateSource("ding_dt_test");
        alertContent.setIsDefault(IsDefaultEnum.NOT_DEFAULT.getType());
        alertContent.setAlertGateType(AlertGateTypeEnum.DINGDING.getType());
        alertContent.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertContent.setAlertGateName("钉钉DT测试");
        alertContent.setAlertGateJson("{}");
        alertContent.setAlertTemplate("");
        alertContent.setClusterId(1L);
        alertContent.setAlertGateCode(AlertGateCode.AG_GATE_DING_DT.code());
        return alertContent;
    }

    public static AlertChannel getDefaultAlterChannelTemplateMailDt() {
        AlertChannel alertContent = new AlertChannel();
        alertContent.setId(6L);
        alertContent.setAlertGateSource("mail_dt_test");
        alertContent.setIsDefault(IsDefaultEnum.NOT_DEFAULT.getType());
        alertContent.setAlertGateType(AlertGateTypeEnum.MAIL.getType());
        alertContent.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertContent.setAlertGateName("邮箱DT测试");
        alertContent.setAlertGateJson("{\n" +
                "    \"mail.smtp.host\":\"smtp.yeah.net\",\n" +
                "    \"mail.smtp.port\":\"25\",\n" +
                "    \"mail.smtp.ssl.enable\":true,\n" +
                "    \"mail.smtp.username\":\"dashuww@yeah.net\",\n" +
                "    \"mail.smtp.password\":\"dt1234\",\n" +
                "    \"mail.smtp.from\":\"dashuww@yeah.net\"\n" +
                "}");
        alertContent.setAlertTemplate("");
        alertContent.setClusterId(1L);
        alertContent.setAlertGateCode(AlertGateCode.AG_GATE_MAIL_DT.code());
        return alertContent;
    }

    public static AlertRecord getDefaultRecord() {
        AlertRecord alertRecord = new AlertRecord();
        alertRecord.setId(0L);
        alertRecord.setContext("");
        alertRecord.setNodeAddress("");
        alertRecord.setFailureReason("");
        alertRecord.setSendTime("");
        alertRecord.setSendEndTime("");
        alertRecord.setUserId(1L);
        alertRecord.setAppType(1);
        alertRecord.setTenantId(1L);
        alertRecord.setReadStatus(0);
        alertRecord.setIsDeleted(0);
        alertRecord.setAlertRecordSendStatus(0);
        alertRecord.setAlertRecordStatus(0);
        alertRecord.setJobId("");
        alertRecord.setStatus(0);
        alertRecord.setTitle("");
        alertRecord.setAlertContentId(1L);
        alertRecord.setAlertChannelId(1L);
        return alertRecord;
    }
}
