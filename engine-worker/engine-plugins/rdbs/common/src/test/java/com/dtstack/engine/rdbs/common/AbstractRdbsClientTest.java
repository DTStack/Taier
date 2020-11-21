package com.dtstack.engine.rdbs.common;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.rdbs.common.executor.RdbsExeQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.support.membermodification.MemberModifier;


public class AbstractRdbsClientTest {

    @InjectMocks
    AbstractRdbsClient abstractRdbsClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessSubmitJobWithType() throws Exception {

        JobClient jobClient = new JobClient();
        jobClient.setJobType(EJobType.MR);

        boolean isMr = false;
        try {
            abstractRdbsClient.processSubmitJobWithType(jobClient);
            isMr = true;
        } catch (RdosDefineException e) {
        }
        Assert.assertTrue(isMr);

        RdbsExeQueue exeQueue = new RdbsExeQueue(null, 2, 2);
        exeQueue.init();
        MemberModifier.field(AbstractRdbsClient.class, "exeQueue").set(abstractRdbsClient, exeQueue);

        jobClient.setTaskId("test");
        jobClient.setJobType(EJobType.SQL);
        JobResult jobResult = abstractRdbsClient.processSubmitJobWithType(jobClient);


    }

}
