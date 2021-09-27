package com.dtstack.engine.master.failover;

import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.server.FailoverStrategy;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.server.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.server.scheduler.JobGraphBuilderTrigger;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.master.zookeeper.data.BrokerHeartNode;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2020-12-30
 */
public class FailoverStrategyTest extends AbstractTest {

    String otherNodeAddress = "127.0.0.2";
    @Autowired
    private FailoverStrategy failoverStrategy;
    @MockBean
    private ZkService zkService;
    @Autowired
    private ScheduleJobDao scheduleJobDao;
    @Autowired
    private EngineJobCacheDao engineJobCacheDao;
    @MockBean
    private JobGraphBuilderTrigger jobGraphBuilderTrigger;
    @MockBean
    private JobGraphBuilder jobGraphBuilder;

    @Before
    public void init() {
        BrokerHeartNode brokerHeartNode = new BrokerHeartNode();
        brokerHeartNode.setAlive(false);
        when(zkService.getBrokerHeartNode(anyString())).thenReturn(brokerHeartNode);
        doNothing().when(zkService).updateSynchronizedLocalBrokerHeartNode(anyString(), any(), anyBoolean());
        List<String> brokers = Lists.newArrayList("127.0.0.1", otherNodeAddress);
        when(zkService.getAliveBrokersChildren()).thenReturn(brokers);
        doNothing().when(jobGraphBuilderTrigger).dealMaster(anyBoolean());
        doNothing().when(jobGraphBuilder).buildTaskJobGraph(anyString());
    }

    @Test
    public void testDataMigration() {
        failoverStrategy.dataMigration(otherNodeAddress);
    }

    @Test
    @Rollback
    public void testFaultTolerantRecoverBatchJob() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setNodeAddress(otherNodeAddress);
        scheduleJobTemplate.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJobTemplate.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobKey(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobName(UUID.randomUUID().toString());
        scheduleJobTemplate.setPhaseStatus(JobPhaseStatus.JOIN_THE_TEAM.getCode());
        scheduleJobDao.insert(scheduleJobTemplate);

        ScheduleJob scheduleFillDataJobTemplate = Template.getScheduleJobTemplate();
        scheduleFillDataJobTemplate.setNodeAddress(otherNodeAddress);
        scheduleFillDataJobTemplate.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleFillDataJobTemplate.setType(EScheduleType.FILL_DATA.getType());
        scheduleFillDataJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleFillDataJobTemplate.setJobKey(UUID.randomUUID().toString());
        scheduleFillDataJobTemplate.setJobName(UUID.randomUUID().toString());
        scheduleFillDataJobTemplate.setPhaseStatus(JobPhaseStatus.JOIN_THE_TEAM.getCode());

        scheduleJobDao.insert(scheduleFillDataJobTemplate);
        failoverStrategy.faultTolerantRecoverBatchJob(otherNodeAddress);
    }


    @Test
    @Rollback
    public void testFaultTolerantRecoverJobCache() {
        EngineJobCache scheduleJobTemplate = Template.getEngineJobCacheTemplate();
        scheduleJobTemplate.setNodeAddress(otherNodeAddress);
        scheduleJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobName(UUID.randomUUID().toString());
        engineJobCacheDao.insert(scheduleJobTemplate.getJobId(),
                scheduleJobTemplate.getEngineType(), scheduleJobTemplate.getComputeType(),
                scheduleJobTemplate.getStage(), scheduleJobTemplate.getJobInfo(),
                scheduleJobTemplate.getNodeAddress(), scheduleJobTemplate.getJobName(),
                scheduleJobTemplate.getJobPriority(), scheduleJobTemplate.getJobResource());

        EngineJobCache scheduleJobSubmittedTemplate = Template.getEngineJobCacheTemplate();
        scheduleJobSubmittedTemplate.setNodeAddress(otherNodeAddress);
        scheduleJobSubmittedTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobSubmittedTemplate.setJobName(UUID.randomUUID().toString());
        scheduleJobSubmittedTemplate.setStage(EJobCacheStage.SUBMITTED.getStage());
        engineJobCacheDao.insert(scheduleJobSubmittedTemplate.getJobId(),
                scheduleJobSubmittedTemplate.getEngineType(), scheduleJobSubmittedTemplate.getComputeType(),
                scheduleJobSubmittedTemplate.getStage(), scheduleJobSubmittedTemplate.getJobInfo(),
                scheduleJobSubmittedTemplate.getNodeAddress(), scheduleJobSubmittedTemplate.getJobName(),
                scheduleJobSubmittedTemplate.getJobPriority(), scheduleJobSubmittedTemplate.getJobResource());
        failoverStrategy.faultTolerantRecoverJobCache(otherNodeAddress);
    }


    @Test
    @Rollback
    public void testSetIsMaster() {
        try {
            ReflectionTestUtils.setField(failoverStrategy, "currIsMaster", false);
            failoverStrategy.setIsMaster(true);

            ReflectionTestUtils.setField(failoverStrategy, "currIsMaster", true);
            failoverStrategy.setIsMaster(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    @Rollback
    public void testDeal() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setNodeAddress(otherNodeAddress);
        scheduleJobTemplate.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJobTemplate.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        scheduleJobTemplate.setJobId(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobKey(UUID.randomUUID().toString());
        scheduleJobTemplate.setJobName(UUID.randomUUID().toString());
        scheduleJobTemplate.setPhaseStatus(JobPhaseStatus.JOIN_THE_TEAM.getCode());
        scheduleJobDao.insert(scheduleJobTemplate);

        EngineJobCache engineJobCacheTemplate = Template.getEngineJobCacheTemplate();
        engineJobCacheTemplate.setNodeAddress(otherNodeAddress);
        engineJobCacheTemplate.setJobId(scheduleJobTemplate.getJobId());
        engineJobCacheTemplate.setJobName(UUID.randomUUID().toString());
        engineJobCacheDao.insert(engineJobCacheTemplate.getJobId(),
                engineJobCacheTemplate.getEngineType(), engineJobCacheTemplate.getComputeType(),
                engineJobCacheTemplate.getStage(), engineJobCacheTemplate.getJobInfo(),
                engineJobCacheTemplate.getNodeAddress(), engineJobCacheTemplate.getJobName(),
                engineJobCacheTemplate.getJobPriority(), engineJobCacheTemplate.getJobResource());
        failoverStrategy.dealSubmitFailJob(scheduleJobTemplate.getJobId(), "test !");
        ScheduleJob byJobId = scheduleJobDao.getByJobId(scheduleJobTemplate.getJobId(), null);
        Assert.assertEquals(RdosTaskStatus.SUBMITFAILD.getStatus(),byJobId.getStatus());
    }
}
