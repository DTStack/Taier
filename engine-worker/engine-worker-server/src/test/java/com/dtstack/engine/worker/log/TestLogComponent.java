package com.dtstack.engine.worker.log;

import com.dtstack.engine.worker.CommonUtils;
import org.junit.Test;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/11/26
 */
public class TestLogComponent {

    @Test
    public void testLogInit() throws Exception{
        CommonUtils.setUserDirToTest();
        LogbackComponent.setupLogger();
    }
}
