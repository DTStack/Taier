package com.dtstack.engine.learning;

import com.dtstack.engine.common.JobClient;
import org.junit.Test;

import java.util.Arrays;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2021/04/01 10:36
 */
public class LearningUtilTest {

    @Test
    public void testBuildPythonArgs() throws Exception{
        LearningUtil.buildPythonArgs(DataUtil.getJobClient());
    }
}
