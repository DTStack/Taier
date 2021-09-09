package com.dtstack.engine.rdbs.common.executor;

import com.dtstack.engine.pluginapi.JobClient;
import com.google.common.collect.Queues;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.powermock.api.mockito.PowerMockito.when;


public class RdbsExeQueueTest {

    @InjectMocks
    RdbsExeQueue rdbsExeQueue;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Exception {
        BlockingQueue<JobClient> waitQueue = Queues.newLinkedBlockingQueue();
        JobClient jobClient = new JobClient();
        jobClient.setJobName("test");
        jobClient.setTaskId("test");
        jobClient.setSql("select * from tableTest;");
        jobClient.setTaskParams("{\"task\":\"test\"}");
        waitQueue.add(jobClient);
        MemberModifier.field(RdbsExeQueue.class, "waitQueue").set(rdbsExeQueue, waitQueue);
        MemberModifier.field(RdbsExeQueue.class, "minSize").set(rdbsExeQueue, 1);
        MemberModifier.field(RdbsExeQueue.class, "maxSize").set(rdbsExeQueue, 1);
        rdbsExeQueue.init();
    }

    @Test
    public void testSubmit() {
        JobClient jobClient = new JobClient();
        jobClient.setTaskId("test");
        String submit = rdbsExeQueue.submit(jobClient);
        Assert.assertNotNull(submit);
    }


    @Test
    public void testRdbsExeRun() throws Exception {

        RdbsExeQueue.RdbsExe rdbsExe = PowerMockito.mock(RdbsExeQueue.RdbsExe.class);
        List<String> sqlList = new ArrayList<>();
        sqlList.add("select * from testTables");
        MemberModifier.field(RdbsExeQueue.RdbsExe.class, "sqlList").set(rdbsExe, sqlList);

        rdbsExe.run();
        System.out.println("test");
    }
}
