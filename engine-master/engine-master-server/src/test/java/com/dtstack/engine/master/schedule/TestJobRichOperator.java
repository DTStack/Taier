package com.dtstack.engine.master.schedule;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import org.apache.commons.lang3.tuple.Pair;
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

        //测试周期实例的cycTime pare
        Pair<String, String> cycTimeLimitEndNow = operator.getCycTimeLimitEndNow(true,false);
        System.out.println("周期实例,startTime:"+cycTimeLimitEndNow.getLeft()+":"+
                cycTimeLimitEndNow.getRight());
        System.out.println("======");
        //测试周期实例的cycTime pare
        Pair<String, String> fillTime = operator.getCycTimeLimitEndNow(false,false);
        System.out.println("补数据或重跑,startTime:"+fillTime.getLeft()+":"+
                fillTime.getRight());


    }

}
