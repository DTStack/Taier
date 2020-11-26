package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;


/**
 * Date: 2020/6/5
 * Company: www.dtstack.com
 *
 * @author xiuzhu
 */

public class StreamTaskServiceTest extends AbstractTest {

    @Autowired
    StreamTaskService streamTaskService;

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetCheckPoint() {
        EngineJobCheckpoint engineJobCheckpoint = DataCollection.getData().getEngineJobCheckpoint();

        Long triggerStart = engineJobCheckpoint.getCheckpointTrigger().getTime() - 1;
        Long triggerEnd = engineJobCheckpoint.getCheckpointTrigger().getTime() + 1;
        List<EngineJobCheckpoint> engineJobCheckpoints = streamTaskService.getCheckPoint(
                engineJobCheckpoint.getTaskId(), triggerStart, triggerEnd);
        Assert.assertNotNull(engineJobCheckpoints);
        Assert.assertTrue(engineJobCheckpoints.size() > 0);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetByTaskIdAndEngineTaskId() {
        EngineJobCheckpoint engineJobCheckpoint = DataCollection.getData().getEngineJobCheckpoint();

        EngineJobCheckpoint resJobCheckpoint = streamTaskService.getByTaskIdAndEngineTaskId(
                engineJobCheckpoint.getTaskId(), engineJobCheckpoint.getTaskEngineId());
        Assert.assertNotNull(resJobCheckpoint);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetEngineStreamJob() {
        ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

        List<String> taskIds = Arrays.asList(new String[]{streamJob.getJobId()});
        List<ScheduleJob> jobs = streamTaskService.getEngineStreamJob(taskIds);
        Assert.assertNotNull(jobs);
        Assert.assertTrue(jobs.size() > 0);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetTaskIdsByStatus() {
        ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

        Integer status = streamJob.getStatus();
        List<String> taskIds = streamTaskService.getTaskIdsByStatus(status);
        Assert.assertNotNull(taskIds);
        Assert.assertTrue(taskIds.contains(streamJob.getJobId()));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetTaskStatus() {
        ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

        Integer taskStatus = streamTaskService.getTaskStatus(streamJob.getJobId());

        Assert.assertEquals(RdosTaskStatus.FINISHED.getStatus(), taskStatus);
    }

}
