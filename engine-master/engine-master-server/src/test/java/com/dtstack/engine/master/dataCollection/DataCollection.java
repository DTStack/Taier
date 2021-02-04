package com.dtstack.engine.master.dataCollection;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.anno.DataSource;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.anno.IgnoreUniqueRandomSet;
import com.dtstack.engine.master.utils.DataCollectionProxy;
import com.dtstack.engine.master.utils.Template;
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
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setExecStartTime(new Timestamp(1592559742000L));
        return sj;
    }

    /**
     * @author newman
     * @Description 获取虚节点任务实例
     * @Date 2020/12/30 7:09 下午
     * @return: com.dtstack.engine.api.domain.ScheduleJob
     **/
    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobVirtual() {
        ScheduleTaskShade shade = getScheduleTaskShadeVirtual();
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setTaskId(shade.getTaskId());
        sj.setExecStartTime(new Timestamp(1592559742000L));
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobSecond() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setEngineLog("");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobToUnsumbittedStatus() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setEngineLog("");
        sj.setStatus(8);
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class, method = "insertWithCustomGmt")
    default ScheduleJob getScheduleJobIndependentWithGmt() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setGmtCreate(new Timestamp(1595822190349L));
        sj.setGmtModified(new Timestamp(1595822190349L));
        return sj;
    }



    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobDefiniteTaskId() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setExecTime(2000L);
        sj.setTaskId(-2021L);
        sj.setSourceType(-1);
        sj.setEngineLog("");
        sj.setDtuicTenantId(-1008L);
        sj.setStatus(4);
        sj.setAppType(0);
        sj.setJobName("Python-20200710");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobDefiniteJobId() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setId(-9089L);
        sj.setJobId("9089");
        return sj;
    }


    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobDefiniteProjectId() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setExecTime(2000L);
        sj.setTaskId(-2022L);
        sj.setSourceType(-1);
        sj.setEngineLog("");
        sj.setDtuicTenantId(-1008L);
        sj.setProjectId(-234L);
        sj.setStatus(4);
        sj.setExecStartTime(new Timestamp(1592559742000L));
        sj.setAppType(0);
        sj.setJobName("Python-20200710");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobDefiniteProjectIdAndStatus() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setExecTime(2000L);
        sj.setTaskId(-2022L);
        sj.setSourceType(-1);
        sj.setEngineLog("");
        sj.setDtuicTenantId(-1008L);
        sj.setProjectId(-12L);
        sj.setStatus(4);
        sj.setExecStartTime(new Timestamp(1592559742000L));
        sj.setAppType(0);
        sj.setJobName("Python-20200710");
        sj.setStatus(5);
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobTodayData() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setProjectId(-101L);
        sj.setExecStartTime(new Timestamp(new DateTime().getMillis()));
        sj.setTaskId(-2001L);
        sj.setJobName("Test job1");
        sj.setEngineLog("");
        sj.setAppType(1);
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobWithCycTime() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setProjectId(-101L);
        sj.setExecStartTime(new Timestamp(new DateTime().getMillis()));
        sj.setTaskId(-2006L);
        sj.setJobName("Test job1");
        sj.setEngineLog("");
        sj.setCycTime("2020-07-01 10:12:12");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobWithCycTime2() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setProjectId(-101L);
        sj.setExecStartTime(new Timestamp(new DateTime().getMillis()));
        sj.setTaskId(-2006L);
        sj.setJobName("Test job1");
        sj.setEngineLog("");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobYesterdayData() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setExecStartTime(new Timestamp(new DateTime().plusDays(-1).getMillis()));
        sj.setProjectId(-101L);
        sj.setJobName("Test job2");
        sj.setEngineLog("");
        sj.setAppType(1);
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobThird() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setExecStartTime(new Timestamp(1592559742000L));
        sj.setEngineLog("");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobStream() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setStatus(4);
        sj.setExecStartTime(new Timestamp(1591805197000L));
        sj.setExecEndTime(new Timestamp(1591805197100L));
        sj.setJobName("test");
        sj.setCycTime("20200609234500");
        sj.setTaskType(EJobType.SQL.getType());
        sj.setType(2);
        sj.setEngineLog("");
        sj.setSourceType(-1);
        sj.setApplicationId("application_9527");
        sj.setComputeType(ComputeType.STREAM.getType());
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobStream2() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setJobId("testJobId2");
        sj.setStatus(4);
        sj.setExecStartTime(new Timestamp(1591805197000L));
        sj.setExecEndTime(new Timestamp(1591805197100L));
        sj.setJobName("test2");
        sj.setCycTime("20200609234500");
        sj.setTaskType(EJobType.SQL.getType());
        sj.setType(2);
        sj.setEngineLog("");
        sj.setSourceType(-1);
        sj.setApplicationId("application_9527");
        sj.setEngineJobId("engineJobId2");
        sj.setComputeType(ComputeType.STREAM.getType());
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobStream3() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setJobId("testJobId3");
        sj.setStatus(4);
        sj.setExecStartTime(new Timestamp(1591805197000L));
        sj.setExecEndTime(new Timestamp(1591805197100L));
        sj.setJobName("test2");
        sj.setCycTime("20200609234500");
        sj.setTaskType(EJobType.SQL.getType());
        sj.setType(2);
        sj.setEngineLog("");
        sj.setSourceType(-1);
        sj.setApplicationId("application_9527");
        sj.setComputeType(ComputeType.STREAM.getType());
        return sj;
    }


    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobStream4() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setJobId("testJobId3");
        sj.setStatus(4);
        sj.setExecStartTime(new Timestamp(1591805197000L));
        sj.setExecEndTime(new Timestamp(1591805197100L));
        sj.setJobName("test2");
        sj.setCycTime("20200609234500");
        sj.setTaskType(EJobType.SQL.getType());
        sj.setType(2);
        sj.setEngineLog("");
        sj.setSourceType(-1);
        sj.setApplicationId("application_9527");
        sj.setComputeType(ComputeType.STREAM.getType());
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobStream5() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setJobId("testJobId4");
        sj.setStatus(4);
        sj.setExecStartTime(new Timestamp(1591805197000L));
        sj.setExecEndTime(new Timestamp(1591805197100L));
        sj.setJobName("test2");
        sj.setCycTime("20200609234500");
        sj.setTaskType(EJobType.SQL.getType());
        sj.setType(2);
        sj.setEngineLog("");
        sj.setSourceType(-1);
        sj.setApplicationId("application_9527");
        sj.setComputeType(ComputeType.STREAM.getType());
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobSubmitted() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setStatus(RdosTaskStatus.SUBMITFAILD.getStatus());
        sj.setExecStartTime(new Timestamp(1592559742000L));
        return sj;
    }

    @DatabaseInsertOperation(dao = TestEngineJobRetryDao.class)
    default EngineJobRetry getEngineJobRetry() {
        EngineJobRetry ej = Template.getEngineJobRetryTemplate();
        ej.setJobId(getData().getScheduleJobFirst().getJobId());
        return ej;
    }

    @DatabaseInsertOperation(dao = TestEngineJobRetryDao.class)
    default EngineJobRetry getEngineJobRetryNoEngineLog() {
        EngineJobRetry ej = Template.getEngineJobRetryTemplate();
        ej.setJobId(getData().getScheduleJobSecond().getJobId());
        ej.setEngineLog("");
        return ej;
    }

    @DatabaseInsertOperation(dao = TestEngineJobCheckpointDao.class)
    default EngineJobCheckpoint getEngineJobCheckpoint() {
        EngineJobCheckpoint jc = Template.getEngineJobCheckpointTemplate();
        return jc;
    }

    @DatabaseInsertOperation(dao = TestEngineJobCheckpointDao.class)
    default EngineJobCheckpoint getEngineJobSavepoint() {
        EngineJobCheckpoint jc = Template.getEngineJobSavepointTemplate();
        return jc;
    }

    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class)
    @IgnoreUniqueRandomSet
    default EngineJobCache getEngineJobCache() {
        EngineJobCache engineJobCache = Template.getEngineJobCacheTemplate();
        engineJobCache.setJobId(getData().getScheduleJobStream().getJobId());
        return engineJobCache;
    }


    /**
     * @author zyd
     * @Description 失败重试的任务
     * @Date 2020/11/14 10:02 上午
     * @return: com.dtstack.engine.api.domain.EngineJobCache
     **/
    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class)
    @IgnoreUniqueRandomSet
    default EngineJobCache getEngineJobCache2() {
        EngineJobCache engineJobCache = Template.getEngineJobCacheTemplate2();
        String jobId = getData().getScheduleJobStream2().getJobId();
        engineJobCache.setJobId(jobId);
        engineJobCache.setJobInfo(String.format("{\"pluginInfo\":{\"aa\":\"bb\"},\"engineType\":\"spark\",\"taskType\":2,\"computeType\":1, \"tenantId\":9, " +
                "\"maxRetryNum\":3,\"taskParams\":\"openCheckpoint=true \\n sql.checkpoint.interval=2\"," +
                "\"taskId\":\"%s\"}",jobId));
        engineJobCache.setIsFailover(1);
        return engineJobCache;
    }

    /**
     * @author zyd
     * @Description 构造不同jobId的jobCache，防止被其他线程删掉
     * @Date 2020/11/27 2:46 下午
     * @return: com.dtstack.engine.api.domain.EngineJobCache
     **/
    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class)
    @IgnoreUniqueRandomSet
    default EngineJobCache getEngineJobCache4() {
        EngineJobCache engineJobCache = Template.getEngineJobCacheTemplate2();
        String jobId = getData().getScheduleJobStream3().getJobId();
        engineJobCache.setJobId(jobId);
        engineJobCache.setJobInfo(String.format("{\"engineType\":\"spark\",\"taskType\":2,\"computeType\":1, \"tenantId\":9, " +
                "\"maxRetryNum\":3,\"taskParams\":\"openCheckpoint=true \\n sql.checkpoint.interval=2\"," +
                "\"taskId\":\"%s\"}",jobId));
        engineJobCache.setIsFailover(1);
        return engineJobCache;
    }


    /**
     * engineType为kylin
     * @return
     */
    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class)
    @IgnoreUniqueRandomSet
    default EngineJobCache getEngineJobCache5() {
        EngineJobCache engineJobCache = Template.getEngineJobCacheTemplate2();
        String jobId = getData().getScheduleJobStream4().getJobId();
        engineJobCache.setJobId(jobId);
        engineJobCache.setJobInfo(String.format("{\"pluginInfo\":{\"aa\":\"bb\"},\"openCheckpoint\":true,\"engineType\":\"Kylin\",\"taskType\":2,\"computeType\":1, \"tenantId\":9, " +
                "\"maxRetryNum\":3,\"taskParams\":\"openCheckpoint=true \\n sql.checkpoint.interval=2\"," +
                "\"taskId\":\"%s\"}",jobId));
        engineJobCache.setIsFailover(1);
        return engineJobCache;
    }

    /**
     * jobCache的stage为5
     * @return
     */
    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class)
    @IgnoreUniqueRandomSet
    default EngineJobCache getEngineJobCache6() {
        EngineJobCache engineJobCache = Template.getEngineJobCacheTemplate3();
        String jobId = getData().getScheduleJobStream5().getJobId();
        engineJobCache.setJobId(jobId);
        engineJobCache.setJobInfo(String.format("{\"pluginInfo\":{\"aa\":\"bb\"},\"engineType\":\"Kylin\",\"taskType\":2,\"computeType\":1, \"tenantId\":9, " +
                "\"maxRetryNum\":3,\"taskParams\":\"openCheckpoint=true \\n sql.checkpoint.interval=2\"," +
                "\"taskId\":\"%s\"}",jobId));
        engineJobCache.setIsFailover(1);
        return engineJobCache;
    }

    /**
     * stage为5的job
     * @return
     */
    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class)
    default EngineJobCache getEngineJobCache3() {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId("jobId2");
        engineJobCache.setJobName("test");
        engineJobCache.setEngineType("spark");
        engineJobCache.setJobPriority(10L);
        engineJobCache.setComputeType(1);
        engineJobCache.setJobInfo("{\"engineType\":\"spark\",\"taskType\":2,\"computeType\":1, \"tenantId\":9, \"maxRetryNum\":3,\"taskParams\":\"openCheckpoint:true\"," +
                "\"taskId\":\"jobId2\"}");
        engineJobCache.setStage(EJobCacheStage.SUBMITTED.getStage());
        engineJobCache.setNodeAddress(null);
        engineJobCache.setJobResource("test");
        String jobId = getData().getScheduleJobStream2().getJobId();
        engineJobCache.setJobId(jobId);
        engineJobCache.setJobInfo(String.format("{\"engineType\":\"spark\",\"taskType\":2,\"computeType\":1, \"tenantId\":9, " +
                "\"maxRetryNum\":3,\"taskParams\":\"openCheckpoint=true \\n sql.checkpoint.interval=2\"," +
                "\"taskId\":\"%s\"}",jobId));
        engineJobCache.setIsFailover(1);
        return engineJobCache;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getScheduleTaskShadeDelete(){
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setIsDeleted(1);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getScheduleTaskShade(){
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleTaskShade getScheduleTaskShadeDefiniteTaskId(){
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleStatus(5);
        scheduleTaskShade.setTaskId(-2021L);
        scheduleTaskShade.setTenantId(15L);
        scheduleTaskShade.setProjectId(-1L);
        scheduleTaskShade.setDtuicTenantId(-1008L);
        scheduleTaskShade.setAppType(0);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleTaskShade getScheduleTaskShadeDefiniteTaskIdSecond(){
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleStatus(5);
        scheduleTaskShade.setTaskId(-2022L);
        scheduleTaskShade.setTenantId(15L);
        scheduleTaskShade.setProjectId(-12L);
        scheduleTaskShade.setDtuicTenantId(-1008L);
        scheduleTaskShade.setAppType(0);
        scheduleTaskShade.setScheduleStatus(5);
        return scheduleTaskShade;
    }

    /**
     * @author newman
     * @Description 虚节点任务
     * @Date 2020/12/30 7:07 下午
     * @return: com.dtstack.engine.api.domain.ScheduleTaskShade
     **/
    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleTaskShade getScheduleTaskShadeVirtual(){
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleStatus(5);
        scheduleTaskShade.setTaskId(-2023L);
        scheduleTaskShade.setTenantId(15L);
        scheduleTaskShade.setProjectId(-12L);
        scheduleTaskShade.setDtuicTenantId(-1008L);
        scheduleTaskShade.setAppType(0);
        scheduleTaskShade.setScheduleStatus(5);
        scheduleTaskShade.setTaskType(-1);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleTaskShade getScheduleTaskShadeForSheduleJob(){
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setTaskType(0);
        scheduleTaskShade.setProjectId(-101L);
        scheduleTaskShade.setTenantId(15L);
        scheduleTaskShade.setTaskId(-2001L);
        scheduleTaskShade.setFlowId(0L);

        return scheduleTaskShade;
    }


    @DatabaseInsertOperation(dao = TestConsoleDtuicTenantDao.class)
    default Tenant getTenant(){
        Tenant tenant = Template.getTenantTemplate();
        return tenant;
    }

    @DatabaseInsertOperation(dao = TestConsoleUserDao.class)
    default User getUser(){
        User user = Template.getUserTemplate();
        return user;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleTaskShade getScheduleTaskShadeDefiniteTaskId2(){
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setTenantId(-20001L);
        scheduleTaskShade.setProjectId(-20001L);
        scheduleTaskShade.setDtuicTenantId(-20001L);
        scheduleTaskShade.setAppType(-1001);
        scheduleTaskShade.setTaskId(-511L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
//    @IgnoreUniqueRandomSet
    default ScheduleJob getScheduleJobDefiniteJobkey() {
        ScheduleJob scheduleJobJob = Template.getScheduleJobTemplate();
        scheduleJobJob.setTenantId(-20001L);
        scheduleJobJob.setProjectId(-20001L);
        scheduleJobJob.setDtuicTenantId(-20001L);
        scheduleJobJob.setAppType(-1001);
        scheduleJobJob.setJobKey("cronTrigger_77_20200729000000");
        scheduleJobJob.setTaskId(-511L);
        return scheduleJobJob;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobJobDao.class)
    default ScheduleJobJob getScheduleJobJobSetParentKey(){
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setTenantId(-20001L);
        scheduleJobJob.setProjectId(-20001L);
        scheduleJobJob.setDtuicTenantId(-20001L);
        scheduleJobJob.setAppType(-1001);

        scheduleJobJob.setParentJobKey("cronTrigger_77_20200729000000");
        scheduleJobJob.setJobKey("cronTrigger_77_20200729000000");
        return scheduleJobJob;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobSetCycTime() {
        ScheduleJob sj = Template.getScheduleJobTemplate();
        sj.setProjectId(-1012L);
        sj.setExecStartTime(new Timestamp(new DateTime().getMillis()));
        sj.setTaskId(-20021L);
        sj.setJobName("Test job1");
        sj.setEngineLog("");
        sj.setCycTime("20200501163446");
        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    default ScheduleJob getScheduleJobByTask(ScheduleTaskShade scheduleTaskShade) {
        return Template.getScheduleJobTemplate();
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getTask() {


        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        return scheduleTaskShade;
    }


    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronMonthTask() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"day\":5,\"hour\":0,\"min\":23,\"periodType\":\"4\",\"scheduleStatus\":false,\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":false,\"maxRetryNum\":\"3\"}");
        scheduleTaskShade.setTaskId(104L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronWeekTask() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"weekDay\":3,\"min\":0,\"hour\":23,\"periodType\":\"3\",\"scheduleStatus\":false,\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":false,\"maxRetryNum\":\"3\"}");
        scheduleTaskShade.setTaskId(103L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronDayTask() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
        scheduleTaskShade.setTaskId(102L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronWeekHour() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginHour\":0,\"endHour\":23,\"beginMin\":0,\"gapHour\":5,\"periodType\":\"1\",\"scheduleStatus\":false,\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":false,\"maxRetryNum\":\"3\"}");
        scheduleTaskShade.setTaskId(100L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronWeekMin() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginMin\":0,\"endMin\":59,\"beginHour\":0,\"endHour\":23,\"gapMin\":5,\"periodType\":\"0\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"scheduleStatus\":false,\"isFailRetry\":true,\"selfReliance\":false,\"maxRetryNum\":\"3\"}");
        scheduleTaskShade.setTaskId(101L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronJobBySelfReliance1() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginHour\":\"0\",\"endHour\":\"23\",\"beginMin\":\"0\",\"gapHour\":\"1\",\"periodType\":\"1\",\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":0,\"maxRetryNum\":\"3\",\"isLastInstance\":true,\"endMin\":\"59\",\"isExpire\":true}");
        scheduleTaskShade.setTaskId(1L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronJobBySelfReliance2() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginHour\":\"0\",\"endHour\":\"23\",\"beginMin\":\"0\",\"gapHour\":\"1\",\"periodType\":\"1\",\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":1,\"maxRetryNum\":\"3\",\"isLastInstance\":true,\"endMin\":\"59\"}");
        scheduleTaskShade.setTaskId(5L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronJobBySelfReliance3() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginHour\":\"0\",\"endHour\":\"23\",\"beginMin\":\"0\",\"gapHour\":\"1\",\"periodType\":\"1\",\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":2,\"maxRetryNum\":\"3\",\"isLastInstance\":true,\"endMin\":\"59\"}");
        scheduleTaskShade.setTaskId(2L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronJobBySelfReliance4() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginHour\":\"0\",\"endHour\":\"23\",\"beginMin\":\"0\",\"gapHour\":\"1\",\"periodType\":\"1\",\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":3,\"maxRetryNum\":\"3\",\"isLastInstance\":true,\"endMin\":\"59\"}");
        scheduleTaskShade.setTaskId(3L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronJobBySelfReliance5() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginHour\":\"0\",\"endHour\":\"23\",\"beginMin\":\"0\",\"gapHour\":\"1\",\"periodType\":\"1\",\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":4,\"maxRetryNum\":\"3\",\"isLastInstance\":true,\"endMin\":\"59\"}");
        scheduleTaskShade.setTaskId(4L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronJobBySelfRelianceTaskTask() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginHour\":\"0\",\"endHour\":\"23\",\"beginMin\":\"0\",\"gapHour\":\"1\",\"periodType\":\"1\",\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":4,\"maxRetryNum\":\"3\",\"isLastInstance\":true,\"endMin\":\"59\"}");
        scheduleTaskShade.setTaskId(5L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    default ScheduleTaskShade getCronJobBySelfRelianceTaskTask2() {
        ScheduleTaskShade scheduleTaskShade = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShade.setScheduleConf("{\"beginHour\":\"0\",\"endHour\":\"23\",\"beginMin\":\"0\",\"gapHour\":\"1\",\"periodType\":\"1\",\"isFailRetry\":true,\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"selfReliance\":4,\"maxRetryNum\":\"3\",\"isLastInstance\":true,\"endMin\":\"59\"}");
        scheduleTaskShade.setTaskId(6L);
        return scheduleTaskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleJob getDefaultJobForSpring1(){
        return Template.getDefaultScheduleJobForSpring1Template();
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleTaskShade getDefaultTaskForSpring1(){
        ScheduleTaskShade taskShade = Template.getDefaultScheduleTaskFlowParentTemplate();
        taskShade.setTaskId(1L);
        return taskShade;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleJob getDefaultJobForSpring2(){
        return Template.getDefaultScheduleJobForSpring2Template();
    }

    @DatabaseInsertOperation(dao = TestScheduleJobJobDao.class)
    default ScheduleJobJob getDefaultJobJobForSpring1(){
        return Template.getDefaultScheduleJobJobForSpring1Template();
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleTaskShade getDefaultTaskForFlowParent(){
        return Template.getDefaultScheduleTaskFlowParentTemplate();
    }

    @DatabaseInsertOperation(dao = TestScheduleTaskShadeDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleTaskShade getDefaultTaskForFlowChild(){
        return Template.getDefaultScheduleTaskFlowTemplate();
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleJob getDefaultJobForFlowParent(){
        return Template.getDefaultScheduleJobFlowParentTemplate();
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class)
    @IgnoreUniqueRandomSet
    default ScheduleJob getDefaultJobForFlowChild(){
        return Template.getDefaultScheduleJobFlowChildTemplate();
    }

    @DatabaseInsertOperation(dao = TestScheduleJobJobDao.class)
    default ScheduleJobJob getDefaultJobJobForFlow(){
        return Template.getDefaultScheduleJobJobFlowTemplate();
    }
}
