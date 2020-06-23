package com.dtstack.engine.master.data;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.anno.DatabaseDeleteOperation;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.utils.ValueUtils;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * 每个方法只能返回具体类型的类 方法不含有任何参数 方法禁止重名
 * 设置Id 在方法内务必用 ValueUtils.changedIdForDiffMethod()  只能设置一次
 * 设置Unique Key 或者需要别的地方引用的字符串 在方法内 用 ValueUtils.changedStrForDiffMethod(identifier) 指定identifier能设置多次 不重名
 * 利用ValueUtils.getId 和 ValueUtils.getStr可以获得别的方法的相关参数 方便关联
 */
@Component
public class DataCollection {

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleJobDao.class, method = "deleteById", field = "id")
    public ScheduleJob getScheduleJobFirst() {
        ScheduleJob sj = new ScheduleJob();
        sj.setId(ValueUtils.changedIdForDiffMethod());
        sj.setStatus(5);
        sj.setJobId(ValueUtils.changedStrForDiffMethod("jobId"));
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey(ValueUtils.changedStrForDiffMethod("jobKey"));
        sj.setExecStartTime(new Timestamp(1592559742000L));
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

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleJobDao.class, method = "deleteById", field = "id")
    public ScheduleJob getScheduleJobSecond() {
        ScheduleJob sj = new ScheduleJob();
        sj.setId(ValueUtils.changedIdForDiffMethod());
        sj.setStatus(5);
        sj.setJobId(ValueUtils.changedStrForDiffMethod("jobId"));
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey(ValueUtils.changedStrForDiffMethod("jobKey"));
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
        sj.setEngineLog("");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleJobDao.class, method = "deleteById", field = "id")
    public ScheduleJob getScheduleJobThird() {
        ScheduleJob sj = new ScheduleJob();
        sj.setId(ValueUtils.changedIdForDiffMethod());
        sj.setStatus(5);
        sj.setJobId(ValueUtils.changedStrForDiffMethod("jobId"));
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey(ValueUtils.changedStrForDiffMethod("jobKey"));
        sj.setExecStartTime(new Timestamp(1592559742000L));
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
        sj.setEngineLog("");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleJobDao.class, method = "deleteById", field = "id")
    public ScheduleJob getScheduleJobStream() {
        ScheduleJob sj = new ScheduleJob();
        sj.setId(ValueUtils.changedIdForDiffMethod());
        sj.setStatus(4);
        sj.setJobId(ValueUtils.changedStrForDiffMethod("jobId"));
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey(ValueUtils.changedStrForDiffMethod("jobKey"));
        sj.setExecStartTime(new Timestamp(1591805197000L));
        sj.setExecEndTime(new Timestamp(1591805197100L));
        sj.setTaskId(-1L);
        sj.setJobName("test");
        sj.setCreateUserId(0L);
        sj.setIsDeleted(0);
        sj.setBusinessDate("20200608234500");
        sj.setCycTime("20200609234500");
        sj.setTaskType(ComputeType.STREAM.getType());
        sj.setAppType(0);
        sj.setType(2);
        sj.setIsRestart(0);
        sj.setDependencyType(0);
        sj.setFlowJobId("0");
        sj.setPeriodType(0);
        sj.setMaxRetryNum(0);
        sj.setRetryNum(0);
        sj.setComputeType(1);
        sj.setLogInfo("{err: test_log_info}");
        sj.setEngineLog("");
        sj.setApplicationId("application_9527");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestEngineJobRetryDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestEngineJobRetryDao.class, method = "deleteById", field = "id")
    public EngineJobRetry getEngineJobRetry() {
        EngineJobRetry ej = new EngineJobRetry();
        ej.setId(ValueUtils.changedIdForDiffMethod());
        ej.setEngineJobId(ValueUtils.changedStrForDiffMethod("engineJobId"));
        ej.setJobId(ValueUtils.getStr("getScheduleJobFirst", "jobId"));
        ej.setStatus(0);
        ej.setEngineLog("{err: test_engine_log}");
        ej.setLogInfo("{err: test_log_info}");
        ej.setApplicationId(ValueUtils.changedStrForDiffMethod("applicationId"));
        ej.setRetryNum(2);
        ej.setRetryTaskParams("{err: test_retry_task_params}");
        return ej;
    }

    @DatabaseInsertOperation(dao = TestEngineJobRetryDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestEngineJobRetryDao.class, method = "deleteById", field = "id")
    public EngineJobRetry getEngineJobRetryNoEngineLog() {
        EngineJobRetry ej = new EngineJobRetry();
        ej.setId(ValueUtils.changedIdForDiffMethod());
        ej.setEngineJobId(ValueUtils.changedStrForDiffMethod("engineJobId"));
        ej.setJobId(ValueUtils.getStr("getScheduleJobSecond", "jobId"));
        ej.setStatus(0);
        ej.setEngineLog("");
        ej.setLogInfo("{err: test_log_info}");
        ej.setApplicationId(ValueUtils.changedStrForDiffMethod("applicationId"));
        ej.setRetryNum(2);
        ej.setRetryTaskParams("{err: test_retry_task_params}");
        return ej;
    }

    @DatabaseInsertOperation(dao = TestEngineJobCheckpointDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestEngineJobCheckpointDao.class, method = "deleteById", field = "id")
    public EngineJobCheckpoint getEngineJobCheckpoint() {
        EngineJobCheckpoint jc = new EngineJobCheckpoint();
        jc.setId(ValueUtils.changedIdForDiffMethod());
        jc.setTaskId(ValueUtils.changedStrForDiffMethod("taskId"));
        jc.setTaskEngineId("te-999");
        jc.setCheckpointId(ValueUtils.changedStrForDiffMethod("checkpointId"));
        jc.setCheckpointTrigger(Timestamp.valueOf("2020-06-14 12:12:12"));
        jc.setCheckpointSavepath("hdfs://tmp/flink/checkpoint/test");
        jc.setCheckpointCounts("2");
        return jc;
    }

    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestEngineJobCacheDao.class, method = "deleteById", field = "jobId")
    public EngineJobCache getEngineJobCache() {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId(ValueUtils.getStr("getScheduleJobStream", "jobId"));
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

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleTaskShadeDao.class, method = "deleteById", field = "taskId")
    public ScheduleTaskShade getScheduleTaskShadeDelete(){

        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(ValueUtils.changedIdForDiffMethod());
        scheduleTaskShade.setExtraInfo("test");
        scheduleTaskShade.setTenantId(ValueUtils.changedIdForDiffMethod());
        scheduleTaskShade.setProjectId(ValueUtils.changedIdForDiffMethod());
        scheduleTaskShade.setNodePid(ValueUtils.changedIdForDiffMethod());
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
        scheduleTaskShade.setIsDeleted(1);
        scheduleTaskShade.setMainClass("com.dtstack.engine.master.data.DataCollection");
        scheduleTaskShade.setExeArgs("null");
        scheduleTaskShade.setFlowId(1L);
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setIsExpire(1);
        scheduleTaskShade.setProjectScheduleStatus(1);

        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleTaskShadeDao.class, method = "deleteById", field = "taskId")
    public ScheduleTaskShade getScheduleTaskShade(){

        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(ValueUtils.changedIdForDiffMethod());
        scheduleTaskShade.setExtraInfo("test");
        scheduleTaskShade.setTenantId(ValueUtils.changedIdForDiffMethod());
        scheduleTaskShade.setProjectId(ValueUtils.changedIdForDiffMethod());
        scheduleTaskShade.setNodePid(ValueUtils.changedIdForDiffMethod());
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
        scheduleTaskShade.setMainClass("com.dtstack.engine.master.data.DataCollection");
        scheduleTaskShade.setExeArgs("null");
        scheduleTaskShade.setFlowId(1L);
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setIsExpire(1);
        scheduleTaskShade.setProjectScheduleStatus(1);

        return scheduleTaskShade;
    }

}
