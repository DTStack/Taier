package com.dtstack.engine.master.dataCollection;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.anno.DataSource;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.anno.IgnoreUniqueRandomSet;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.utils.DataCollectionProxy;
import com.dtstack.engine.master.utils.Template;
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
        ScheduleJob sj = Template.getScheduleJobTemplate();
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
        sj.setTaskType(ComputeType.STREAM.getType());
        sj.setType(2);
        sj.setEngineLog("");
        sj.setSourceType(-1);
        sj.setApplicationId("application_9527");
        sj.setComputeType(ComputeType.STREAM.getType());
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

    @DatabaseInsertOperation(dao = TestEngineJobCacheDao.class)
    @IgnoreUniqueRandomSet
    default EngineJobCache getEngineJobCache() {
        EngineJobCache engineJobCache = Template.getEngineJobCacheTemplate();
        engineJobCache.setJobId(getData().getScheduleJobStream().getJobId());
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
    @IgnoreUniqueRandomSet
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

    @DatabaseInsertOperation(dao = TestEngineTenantDao.class)
    default EngineTenant getEngineTenant(){
        EngineTenant engineTenant = new EngineTenant();
        engineTenant.setEngineId(1L);
        engineTenant.setQueueId(1L);
        engineTenant.setTenantId(1L);
        return engineTenant;
    }

    @DatabaseInsertOperation(dao = TestClusterDao.class)
    default Cluster getDefaultCluster() {
        Cluster cluster = new Cluster();
        cluster.setId(1L);
        cluster.setClusterName("test");
        cluster.setHadoopVersion("v3");
        return cluster;
    }

    @DatabaseInsertOperation(dao = TestEngineDao.class)
    default Engine getDefaultHadoopEngine(){
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

    @DatabaseInsertOperation(dao = TestTenantDao.class)
    default Tenant getDefaultTenant(){
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setDtUicTenantId(1L);
        tenant.setTenantName("测试租户");
        tenant.setTenantDesc("测试租户");
        return tenant;
    }

    @DatabaseInsertOperation(dao = TestQueueDao.class)
    default Queue getDefaultQueue(){
        Queue queue = new Queue();
        queue.setEngineId(1L);
        queue.setQueueName("默认queue");
        queue.setCapacity("1.0");
        queue.setMaxCapacity("1.0");
        queue.setQueueState("RUNNING");
        queue.setParentQueueId(-1L);
        queue.setQueuePath("default");
        return queue;
    }


    @DatabaseInsertOperation(dao = TestTenantResourceDao.class)
    default TenantResource getDefaultTenantResource(){
        TenantResource tenantResource = new TenantResource();
        tenantResource.setTenantId(1);
        tenantResource.setDtUicTenantId(1);
        tenantResource.setTaskType(EComponentType.SPARK.getTypeCode());
        tenantResource.setEngineType(MultiEngineType.HADOOP.name());
        tenantResource.setResourceLimit("{\"cores\":1,\"memory\":250}");
        return tenantResource;
    }

}
