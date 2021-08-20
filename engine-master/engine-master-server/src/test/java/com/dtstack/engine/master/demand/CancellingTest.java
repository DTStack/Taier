package com.dtstack.engine.master.demand;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.JobStatusDealer;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.utils.Template;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2021-01-26
 */
public class CancellingTest extends AbstractTest implements ApplicationContextAware {

    @Autowired
    private ActionService actionService;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @MockBean
    private JobDealer jobDealer;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @MockBean
    private ShardCache shardCache;

    @MockBean
    private WorkerOperator workerOperator;

    @Autowired
    private JobStopDealer jobStopDealer;

    private ApplicationContext applicationContext;


    @Before
    public void init(){
        when(shardCache.updateLocalMemTaskStatus(any(),any())).thenReturn(true);
        when(workerOperator.getJobStatus(any())).thenReturn(RdosTaskStatus.KILLED);
    }

    @Test
    @Rollback
    public void testStreamCancellingStop() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobKey(UUID.randomUUID().toString());
        scheduleJobTemplate.setComputeType(ComputeType.STREAM.getType());
        scheduleJobTemplate.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJobDao.insert(scheduleJobTemplate);
        Boolean stop = actionService.stop(Lists.newArrayList(scheduleJobTemplate.getJobId()));
        if (Boolean.TRUE.equals(stop)) {
            ScheduleJob dbSchedule = scheduleJobDao.getByJobId(scheduleJobTemplate.getJobId(), null);
            Assert.assertNotNull(dbSchedule);
            Assert.assertEquals(dbSchedule.getStatus(), RdosTaskStatus.CANCELLING.getStatus());
        }
    }

    @Test
    @Rollback
    public void testBatchCancellingStop() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobKey(UUID.randomUUID().toString());
        scheduleJobTemplate.setComputeType(ComputeType.BATCH.getType());
        scheduleJobTemplate.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJobDao.insert(scheduleJobTemplate);
        Boolean stop = actionService.stop(Lists.newArrayList(scheduleJobTemplate.getJobId()));
        if (Boolean.TRUE.equals(stop)) {
            ScheduleJob dbSchedule = scheduleJobDao.getByJobId(scheduleJobTemplate.getJobId(), null);
            Assert.assertNotNull(dbSchedule);
            Assert.assertNotEquals(dbSchedule.getStatus(), RdosTaskStatus.CANCELLING.getStatus());
        }
    }


    @Test
    @Rollback
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void testUnSubmitStop() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setComputeType(ComputeType.STREAM.getType());
        scheduleJobTemplate.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        scheduleJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobKey(UUID.randomUUID().toString());
        scheduleJobDao.insert(scheduleJobTemplate);
        Boolean stop = actionService.stop(Lists.newArrayList(scheduleJobTemplate.getJobId()));
        if (Boolean.TRUE.equals(stop)) {
            ScheduleJob dbSchedule = scheduleJobDao.getByJobId(scheduleJobTemplate.getJobId(), null);
            Assert.assertNotNull(dbSchedule);
            Assert.assertEquals(dbSchedule.getStatus(), RdosTaskStatus.CANCELED.getStatus());
        }
    }


    @Test
    @Rollback
    public void testCancellingStatus() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobKey(UUID.randomUUID().toString());
        scheduleJobTemplate.setComputeType(ComputeType.STREAM.getType());
        scheduleJobTemplate.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJobTemplate.setEngineJobId("testCancelling");
        scheduleJobDao.insert(scheduleJobTemplate);
        scheduleJobDao.updateJobSubmitSuccess(scheduleJobTemplate.getJobId(),scheduleJobTemplate.getEngineJobId()
        ,scheduleJobTemplate.getApplicationId(),"","");

        EngineJobCache engineJobCacheTemplate = Template.getEngineJobCacheTemplateCopyFromJob(scheduleJobTemplate);
        engineJobCacheDao.insert(engineJobCacheTemplate.getJobId(),
                EngineType.Flink.name(), ComputeType.STREAM.getType(), EJobCacheStage.SUBMITTED.getStage(), "{}",
                "127.0.0.1:8090", engineJobCacheTemplate.getJobName(), 100L, "");
        Boolean stop = actionService.stop(Lists.newArrayList(scheduleJobTemplate.getJobId()));
        if (Boolean.TRUE.equals(stop)) {
            try {
                JobStatusDealer jobStatusDealer = new JobStatusDealer();
                jobStatusDealer.setJobResource(engineJobCacheTemplate.getJobResource());
                jobStatusDealer.setShardCache(shardCache);
                jobStatusDealer.setApplicationContext(applicationContext);
                Method[] methods = JobStatusDealer.class.getDeclaredMethods();
                for (Method method : methods) {
                    boolean dealJob = method.getName().equalsIgnoreCase("dealJob");
                    if (dealJob) {
                        ReflectionUtils.makeAccessible(method);
                        ReflectionUtils.invokeMethod(method, jobStatusDealer, scheduleJobTemplate.getJobId());
                        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(scheduleJobTemplate.getJobId());
                        Assert.assertNotNull(scheduleJob);
                        Assert.assertEquals(scheduleJob.getStatus(),RdosTaskStatus.KILLED.getStatus());
                    }
                }

            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }

        }
    }


    @Test
    @Rollback
    public void testJobStop(){
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobKey(UUID.randomUUID().toString());
        scheduleJobTemplate.setComputeType(ComputeType.STREAM.getType());
        scheduleJobTemplate.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
        scheduleJobTemplate.setEngineJobId("testCancelling");
        scheduleJobDao.insert(scheduleJobTemplate);
        scheduleJobDao.updateJobSubmitSuccess(scheduleJobTemplate.getJobId(),scheduleJobTemplate.getEngineJobId()
                ,scheduleJobTemplate.getApplicationId(),"","");

        EngineJobCache engineJobCacheTemplate = Template.getEngineJobCacheTemplateCopyFromJob(scheduleJobTemplate);
        engineJobCacheDao.insert(engineJobCacheTemplate.getJobId(),
                EngineType.Flink.name(), ComputeType.STREAM.getType(), EJobCacheStage.PRIORITY.getStage(), "{}",
                "127.0.0.1:8090", engineJobCacheTemplate.getJobName(), 100L, "");

        Method[] methods = JobStopDealer.class.getDeclaredMethods();
        for (Method method : methods) {
            boolean stopJob = method.getName().equalsIgnoreCase("stopJob");
            if (stopJob) {
                ReflectionUtils.makeAccessible(method);
                try {
                    Class<?> jobElement = Class.forName("com.dtstack.engine.master.jobdealer.JobStopDealer$JobElement");
                    Constructor<?>[] constructors = jobElement.getConstructors();
                    Object jobElementObj = null;
                    for (Constructor<?> declaredConstructor : constructors) {
                        if (declaredConstructor.getParameterCount() == 4) {
                            ReflectionUtils.makeAccessible(declaredConstructor);
                            jobElementObj = declaredConstructor.newInstance(jobStopDealer,scheduleJobTemplate.getJobId(), -1L,false);
                            break;
                        }
                    }
                    StoppedStatus stopping = (StoppedStatus)ReflectionUtils.invokeMethod(method, jobStopDealer, jobElementObj);
                    Assert.assertEquals(stopping,StoppedStatus.STOPPING);
                    scheduleJobDao.updateJobStatus(scheduleJobTemplate.getJobId(),RdosTaskStatus.UNSUBMIT.getStatus());
                    StoppedStatus stoppedStatus = (StoppedStatus)ReflectionUtils.invokeMethod(method, jobStopDealer, jobElementObj);
                    Assert.assertEquals(stoppedStatus,StoppedStatus.STOPPED);

                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }

            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
