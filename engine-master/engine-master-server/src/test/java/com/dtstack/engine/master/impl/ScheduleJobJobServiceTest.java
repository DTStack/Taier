package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.master.vo.ScheduleJobVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dtstack.engine.master.AbstractTest;

import java.util.List;

/**
 * @author basion
 * @Classname ScheduleJobJobServiceTest
 * @Description unit test for ScheduleJobJobService
 * @Date 2020-11-26 17:28:38
 * @Created basion
 */
@PrepareForTest()
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
    public void testDisplayOffSpring() throws Exception {
        ScheduleJobVO displayOffSpring = scheduleJobJobService.displayOffSpring(0L, 0L, 0);
        //TODO
    }

    @Test
    public void testGetOffSpring() {
        ScheduleJobVO getOffSpring = scheduleJobJobService.getOffSpring(null, null, null, false);
        //TODO
    }

    @Test
    public void testDisplayOffSpringWorkFlow() throws Exception {
        ScheduleJobVO displayOffSpringWorkFlow = scheduleJobJobService.displayOffSpringWorkFlow(0L, 0);
        //TODO
    }

    @Test
    public void testDisplayForefathers() throws Exception {
        ScheduleJobVO displayForefathers = scheduleJobJobService.displayForefathers(0L, 0);
        //TODO
    }

    @Test
    public void testGetJobChild() {
        List<ScheduleJobJob> getJobChild = scheduleJobJobService.getJobChild("");
        //TODO
    }

    @Test
    public void testBatchInsert() {
        int batchInsert = scheduleJobJobService.batchInsert(null);
        //TODO
    }
}
