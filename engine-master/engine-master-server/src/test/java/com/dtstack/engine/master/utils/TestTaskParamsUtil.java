package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.master.AbstractTest;
import javafx.concurrent.Task;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: newman
 * Date: 2020/12/31 4:39 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestTaskParamsUtil extends AbstractTest {

    @Test
    public void testParseDeployTypeByTaskParams(){

        EDeployMode flink = TaskParamsUtil.parseDeployTypeByTaskParams("flinkTaskRunMode=session",
                0, "flink");
        Assert.assertEquals("session",flink.getMode());
    }



}
