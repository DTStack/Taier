package com.dtstack.engine.rdbs.common;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.rdbs.common.executor.RdbsExeQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;

import static org.mockito.Mockito.when;

public class RdbsResourceInfoTest {

    @InjectMocks
    RdbsResourceInfo rdbsResourceInfo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testJudgeSlots() throws Exception {

        RdbsExeQueue rdbsExeQueue = PowerMockito.mock(RdbsExeQueue.class);
        when(rdbsExeQueue.checkCanSubmit()).thenReturn(true);
        MemberModifier.field(RdbsResourceInfo.class, "rdbsExeQueue").set(rdbsResourceInfo, rdbsExeQueue);

        JobClient jobClient = new JobClient();
        JudgeResult judgeResult = rdbsResourceInfo.judgeSlots(jobClient);
        Assert.assertTrue(judgeResult.available());
    }

}
