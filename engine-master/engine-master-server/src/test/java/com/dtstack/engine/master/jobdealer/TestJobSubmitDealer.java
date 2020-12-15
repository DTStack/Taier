package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @Author: newman
 * Date: 2020-11-30 19:17
 * Description: 测试JobSubmitDealer
 * @since 1.0.0
 */
public class TestJobSubmitDealer extends AbstractTest {


    @Autowired
    private ApplicationContext applicationContext;


    @Test
    public void testJobSubmitDealer1(){

        try {
            JobSubmitDealer jobSubmitDealer = new JobSubmitDealer("127.0.0.1:8099", null, applicationContext);
        } catch (Exception e) {
            Assert.assertEquals("priorityQueue must not null.",e.getMessage());
        }

    }


}
