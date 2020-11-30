package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.datasource.DataSourceType;
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
        jc.setCheckpointId("checkpointId");
        jc.setCheckpointTrigger(Timestamp.valueOf("2020-06-14 12:12:12"));
        jc.setCheckpointSavepath("hdfs://tmp/flink/checkpoint/test");
        jc.setCheckpointCounts("2");
        return jc;
    }

    public static EngineJobCache getEngineJobCacheTemplate() {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId("jobId");
        engineJobCache.setJobName("test");
        engineJobCache.setEngineType("1");
        engineJobCache.setJobPriority(10L);
        engineJobCache.setComputeType(1);
        engineJobCache.setJobInfo("{\"engineType\":\"2\",\"taskType\":2,\"computeType\":0, \"tenantId\":9}");
        engineJobCache.setStage(2);
        engineJobCache.setNodeAddress("node01");
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
        scheduleTaskShade.setIsExpire(1);
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

    public static LineageTableTable getLineageTableTableTemplate(){
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

    public static LineageRealDataSource getDefaultHiveRealDataSourceTemplate(){
        LineageRealDataSource lineageRealDataSource = new LineageRealDataSource();
        lineageRealDataSource.setSourceName("testHive1");
        lineageRealDataSource.setSourceKey("172.16.8.107#10000");
        lineageRealDataSource.setSourceType(DataSourceType.HIVE2.getType());
        lineageRealDataSource.setDataJason("{\"jdbcUrl\": \"jdbc:hive2://172.16.8.107:10000/default\", \"password\": \"\", \"typeName\": \"hive\", \"username\": \"admin\", \"maxJobPoolSize\": \"\", \"minJobPoolSize\": \"\"}");
        lineageRealDataSource.setKerberosConf("-1");
        lineageRealDataSource.setOpenKerberos(0);
        return lineageRealDataSource;
    }

    public static LineageDataSource getDefaultHiveDataSourceTemplate(){
        LineageDataSource lineageDataSource = new LineageDataSource();
        lineageDataSource.setRealSourceId(null);
        lineageDataSource.setSourceKey(null);
        lineageDataSource.setSourceName(null);
        lineageDataSource.setAppType(null);
        lineageDataSource.setSourceType(null);
        lineageDataSource.setDataJson(null);
        lineageDataSource.setKerberosConf(null);
        lineageDataSource.setOpenKerberos(null);
        lineageDataSource.setAppSourceId(null);
        lineageDataSource.setInnerSource(null);
        lineageDataSource.setComponentId(null);
        return lineageDataSource;
    }
}
