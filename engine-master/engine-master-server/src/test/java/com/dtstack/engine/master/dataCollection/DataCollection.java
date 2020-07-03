package com.dtstack.engine.master.dataCollection;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.anno.DataSource;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.anno.IgnoreUniqueRandomSet;
import com.dtstack.engine.master.utils.DataCollectionProxy;
import com.dtstack.engine.master.utils.ValueUtils;
import org.joda.time.DateTime;

import java.lang.reflect.Proxy;
import java.sql.Timestamp;

/**
 * 使用方法：
 * 对象类中标注@Unique的值自动加上变化值 防止因为Unique产生相同插表失败 如果想自己设置加上@IgnoreUniqueRandomSet注解
 * 对象类中的ID自动回传 自定义写的测试Dao务必增加Optional注解设置key
 * 获取其他对象的属性利用getData().get... 依赖非常方便
 * 设置每个方法不同的值使用ValueUtils.getChangedStr() 和 ValueUtils.getChangedLong()
 * 测试文件中访问对象使用DataCollection.getData()
 */
public interface DataCollection {
    static DataCollection getDataCollectionProxy() {
        return (DataCollection) Proxy.newProxyInstance(DataCollectionProxy.class.getClassLoader(),
                new Class<?>[]{DataCollection.class}, DataCollectionProxy.instance);
    }

    class SingletonHolder {
        private static final DataCollection INSTANCE = getDataCollectionProxy();
    }

    @DataSource
    static DataCollection getData() {
        return SingletonHolder.INSTANCE;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobFirst() {
        ScheduleJob sj = new ScheduleJob();
        sj.setStatus(5);
        sj.setJobId("testJobId");
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey("testJobKey");
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


    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobDefiniteTashId() {
        ScheduleJob sj = new ScheduleJob();
        sj.setStatus(5);
        sj.setJobId("testJobId");
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey("testJobKey");
        sj.setExecStartTime(new Timestamp(System.currentTimeMillis()));
        sj.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        sj.setExecTime(2000L);
        sj.setTaskId(-2021L);
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


    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobSecond() {
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
        sj.setEngineLog("");
        return sj;
    }


    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobTodayData() {
        ScheduleJob sj = new ScheduleJob();
        sj.setStatus(5);
        sj.setJobId("testJobId");
        sj.setTenantId(15L);
        sj.setProjectId(-101L);
        sj.setJobKey("testJobKey");
        sj.setExecStartTime(new Timestamp(new DateTime().plusDays(-1).getMillis()));
        sj.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        sj.setTaskId(-2001L);
        sj.setJobName("Test job1");
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
        sj.setLogInfo("{err: test_log_info_x}");
        sj.setEngineLog("");
        return sj;
    }


    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobYesterdayData() {
        ScheduleJob sj = new ScheduleJob();
        sj.setStatus(5);
        sj.setJobId("testJobId");
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey("testJobKey");
        sj.setExecStartTime(new Timestamp(new DateTime().plusDays(-1).getMillis()));
        sj.setExecEndTime(new Timestamp(System.currentTimeMillis()));
        sj.setTaskId(-1L);
        sj.setJobName("Test job2");
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
        sj.setLogInfo("{err: test_log_info_x}");
        sj.setEngineLog("");
        return sj;
    }


    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobThird() {
        ScheduleJob sj = new ScheduleJob();
        sj.setStatus(5);
        sj.setJobId("testJobId");
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey("testJobKey");
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

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobStream() {
        ScheduleJob sj = new ScheduleJob();
        sj.setStatus(4);
        sj.setJobId("testJobId");
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey("testJobKey");
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

    @DatabaseInsertOperation(dao = TestEngineJobRetryDao.class)
    default EngineJobRetry getEngineJobRetry() {
        EngineJobRetry ej = new EngineJobRetry();
        ej.setEngineJobId(ValueUtils.getChangedStr());
        ej.setJobId(getData().getScheduleJobFirst().getJobId());
        ej.setStatus(0);
        ej.setEngineLog("{err: test_engine_log}");
        ej.setLogInfo("{err: test_log_info}");
        ej.setApplicationId(ValueUtils.getChangedStr());
        ej.setRetryNum(2);
        ej.setRetryTaskParams("{err: test_retry_task_params}");
        return ej;
    }

    @DatabaseInsertOperation(dao = TestEngineJobRetryDao.class)
    default EngineJobRetry getEngineJobRetryNoEngineLog() {
        EngineJobRetry ej = new EngineJobRetry();
        ej.setEngineJobId(ValueUtils.getChangedStr());
        ej.setJobId(getData().getScheduleJobSecond().getJobId());
        ej.setStatus(0);
        ej.setEngineLog("");
        ej.setLogInfo("{err: test_log_info}");
        ej.setApplicationId(ValueUtils.getChangedStr());
        ej.setRetryNum(2);
        ej.setRetryTaskParams("{err: test_retry_task_params}");
        return ej;
    }

    @DatabaseInsertOperation(dao = TestEngineJobCheckpointDao.class)
    default EngineJobCheckpoint getEngineJobCheckpoint() {
        EngineJobCheckpoint jc = new EngineJobCheckpoint();
        jc.setTaskId("taskId");
        jc.setTaskEngineId("te-999");
        jc.setCheckpointId("checkpointId");
        jc.setCheckpointTrigger(Timestamp.valueOf("2020-06-14 12:12:12"));
        jc.setCheckpointSavepath("hdfs://tmp/flink/checkpoint/test");
        jc.setCheckpointCounts("2");
        return jc;
    }

    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class)
    @IgnoreUniqueRandomSet
    default EngineJobCache getEngineJobCache() {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId(getData().getScheduleJobStream().getJobId());
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

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getScheduleTaskShadeDelete(){
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
        scheduleTaskShade.setIsDeleted(1);
        scheduleTaskShade.setMainClass("DataCollection");
        scheduleTaskShade.setExeArgs("null");
        scheduleTaskShade.setFlowId(1L);
        scheduleTaskShade.setDtuicTenantId(1L);
        scheduleTaskShade.setIsExpire(1);
        scheduleTaskShade.setProjectScheduleStatus(1);

        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getScheduleTaskShade(){
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

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getScheduleTaskShadeDefiniteTaskId(){
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
        scheduleTaskShade.setScheduleStatus(5);
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

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getScheduleTaskShadeForSheduleJob(){
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(0L);
        scheduleTaskShade.setExtraInfo("test");
        scheduleTaskShade.setTenantId(ValueUtils.getChangedLong());
        scheduleTaskShade.setProjectId(ValueUtils.getChangedLong());
        scheduleTaskShade.setNodePid(ValueUtils.getChangedLong());
        scheduleTaskShade.setName("testJob");
        scheduleTaskShade.setTaskType(0);
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


    @DatabaseInsertOperation(dao = TestConsoleDtuicTenantDao.class)
    default Tenant getTenant(){
        Tenant tenant = new Tenant();
        tenant.setDtUicTenantId(ValueUtils.getChangedLong());
        tenant.setTenantName("testCase");
        tenant.setTenantDesc("");
        return tenant;
    }

}
