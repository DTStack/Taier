package com.dtstack.rdos.engine.test;

import com.dtstack.rdos.engine.execution.base.ClientType;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import org.junit.Test;

import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/22
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class SubmitTest {

    @Test
    public void submitJar(){

        Properties properties = new Properties();
        properties.setProperty("host", "172.16.1.151");
        properties.setProperty("port", "6123");

        JobSubmitExecutor.getInstance().init(ClientType.Flink, properties);
    }
}
