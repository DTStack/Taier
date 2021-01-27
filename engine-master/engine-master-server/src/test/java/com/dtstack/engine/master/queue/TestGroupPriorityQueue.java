package com.dtstack.engine.master.queue;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Author: newman
 * Date: 2020/12/26 5:17 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestGroupPriorityQueue extends AbstractTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JobDealer jobDealer;


    private GroupPriorityQueue build(){
        GroupPriorityQueue priorityQueue =  GroupPriorityQueue.builder();
        priorityQueue.setApplicationContext(applicationContext);
        priorityQueue.setJobDealer(jobDealer);
        priorityQueue.setJobResource("falfjaljfa");
        return priorityQueue.build();
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAdd(){

        JobClient jobClient = null;
        try {
            jobClient = CommonUtils.getJobClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean add = build().add(jobClient, true, true);
        Assert.assertTrue(add);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    @Test
    public void testAddRestartJob() throws Exception {
        JobClient jobClient = CommonUtils.getJobClient();
        boolean b = build().addRestartJob(jobClient);
        Assert.assertTrue(b);
    }

    @Test
    public void testGetQueue(){

        PriorityBlockingQueue<JobClient> queue = build().getQueue();
        Assert.assertNotNull(queue);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testRemoveQueue() throws Exception {

        JobClient jobClient = CommonUtils.getJobClient();
        boolean remove = build().remove(jobClient);
        Assert.assertFalse(remove);
        boolean add = build().add(jobClient, true, true);
        Assert.assertTrue(add);
    }


}
