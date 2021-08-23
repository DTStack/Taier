package com.dtstack.engine.master.schedule;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: newman
 * Date: 2021/1/11 3:53 下午
 * Description: 单测
 * @since 1.0.0
 */
public class TestJobRichOperator extends AbstractTest {

    @Autowired
    private JobRichOperator operator;

    @Test
    public void testGetCycTimeLimitEndNow(){
        Pair<String, String> cycTime = operator.getCycTimeLimitEndNow(false);
        Pair<String, String> minCycTime = operator.getCycTimeLimitEndNow(true);

        Assert.assertEquals(cycTime.getRight(),minCycTime.getRight());
        Assert.assertNotEquals(cycTime.getLeft(),minCycTime.getLeft());
    }

    @Test
    public void testGetCycTimeLimit(){
        Pair<String, String> cycTimeLimit = operator.getCycTimeLimit();
        System.out.println("周期实例,startTime:"+ cycTimeLimit.getLeft()+":"+
                cycTimeLimit.getRight());
    }

}
