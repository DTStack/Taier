package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.impl.vo.ScheduleJobVO;
import com.dtstack.schedule.common.enums.AppType;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.dtstack.engine.master.AbstractTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author basion
 * @Classname ScheduleJobJobServiceTest
 * @Description unit test for ScheduleJobJobService
 * @Date 2020-11-26 17:28:38
 * @Created basion
 */
public class ScheduleJobJobServiceTest extends AbstractTest {

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDisplayOffSpring() throws Exception {
        ScheduleJob defaultJobForSpring1 = DataCollection.getData().getDefaultJobForSpring1();
        ScheduleJobVO displayOffSpring = scheduleJobJobService.displayOffSpring(defaultJobForSpring1.getId(), 2);
        Assert.assertNotNull(displayOffSpring);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDisplayOffSpringWorkFlow() throws Exception {
        ScheduleJob defaultJobForFlowChild = DataCollection.getData().getDefaultJobForFlowParent();
        ScheduleJobVO displayOffSpringWorkFlow = scheduleJobJobService.displayOffSpringWorkFlow(defaultJobForFlowChild.getId(), AppType.RDOS.getType());
        Assert.assertNotNull(displayOffSpringWorkFlow);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDisplayForefathers() throws Exception {
        ScheduleJob defaultJobForFlowChild = DataCollection.getData().getDefaultJobForFlowChild();
        ScheduleJobVO displayForefathers = scheduleJobJobService.displayForefathers(defaultJobForFlowChild.getId(), 4);
        Assert.assertNotNull(displayForefathers);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetJobChild() {
        ScheduleJob defaultJobForFlowChild = DataCollection.getData().getDefaultJobForFlowParent();
        List<ScheduleJobJob> getJobChild = scheduleJobJobService.getJobChild(defaultJobForFlowChild.getJobKey());
        Assert.assertTrue(CollectionUtils.isNotEmpty(getJobChild));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testBatchInsert() {
        ScheduleJobJob jobJob = Template.getDefaultScheduleJobJobFlowTemplate();
        jobJob.setJobKey("aaaaaaa");
        jobJob.setParentJobKey("bbbbbbb");
        jobJob.setParentAppType(jobJob.getAppType());
        List<ScheduleJobJob> jobJobs = new ArrayList<>();
        jobJobs.add(jobJob);
        int batchInsert = scheduleJobJobService.batchInsert(jobJobs);
        Assert.assertEquals(batchInsert,1);
    }
}
