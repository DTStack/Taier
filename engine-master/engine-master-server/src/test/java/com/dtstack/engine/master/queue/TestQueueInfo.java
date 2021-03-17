package com.dtstack.engine.master.queue;

import com.dtstack.engine.master.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: newman
 * Date: 2020/12/28 10:48 上午
 * Description: 测试QueueInfo
 * @since 1.0.0
 */
public class TestQueueInfo extends AbstractTest {

    @Test
    public void testGetSet(){

        QueueInfo queueInfo = new QueueInfo();
        queueInfo.setSize(10);
        int size = queueInfo.getSize();
        Assert.assertEquals(10,size);
    }
}
