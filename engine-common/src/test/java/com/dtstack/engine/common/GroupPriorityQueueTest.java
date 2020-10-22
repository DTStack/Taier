package com.dtstack.engine.common;

import com.dtstack.engine.common.queue.comparator.JobClientComparator;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.PriorityBlockingQueue;

public class GroupPriorityQueueTest {

    @Test
    public void testPriorityQueueJob() {
        PriorityBlockingQueue<JobClient> queue = new PriorityBlockingQueue<JobClient>(10, new JobClientComparator());
        JobClient job3= new JobClient();job3.setPriority(3);
        JobClient job4= new JobClient();job4.setPriority(4);
        JobClient job1= new JobClient();job1.setPriority(1);
        JobClient job2= new JobClient();job2.setPriority(2);
        JobClient job5= new JobClient();job5.setPriority(5);
        queue.put(job1);
        queue.put(job2);
        queue.put(job3);
        queue.put(job4);
        queue.put(job5);
        JobClient jjj = queue.poll();
        Assert.assertEquals(jjj.getPriority(), job1.getPriority());

    }
}
