package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.master.AbstractTest;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2020/11/26 5:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobCheckRunInfoTest extends AbstractTest {


    @Test
    public void testStopGraphBuildIsMaster() throws Exception {
        JobCheckRunInfo.createCheckInfo(JobCheckStatus.CAN_EXE);
        JobCheckRunInfo.createCheckInfo(JobCheckStatus.CAN_EXE,"");
    }
}
