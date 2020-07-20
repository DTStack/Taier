package com.dtstack.engine.master.impl;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JobGraphBuilderTest extends AbstractTest {


    @Autowired
    public JobGraphBuilder jobGraphBuilder;



    @Test
    public void test() {
        jobGraphBuilder.buildTaskJobGraph("2020-07-05");
    }
}
