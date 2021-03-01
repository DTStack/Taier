package com.dtstack.engine.master.queue;

import com.dtstack.engine.master.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: newman
 * Date: 2020/12/26 5:12 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestGroupInfo extends AbstractTest {

    public GroupInfo groupInfo = new GroupInfo();

    @Test
    public void testSetGetSize(){
        groupInfo.setSize(10);
        int size = groupInfo.getSize();
        Assert.assertEquals(10,size);
    }

    @Test
    public void testSetGetPriority(){

        groupInfo.setPriority(10L);
        long priority = groupInfo.getPriority();
        Assert.assertEquals(priority,10L);
    }




}
