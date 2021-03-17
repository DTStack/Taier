package com.dtstack.engine.dtscript.common.type;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dtscript.DataUtil;
import com.dtstack.engine.dtscript.DtscriptUtils;
import com.dtstack.engine.dtscript.client.ClientArguments;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/28 10:15
 */
public class JLogstashTypeTest {

    @Test
    public void testBuildCmd() throws Exception {
        JobClient jobClient = DataUtil.getJobClient2();
        String[] args = DtscriptUtils.buildPythonArgs(jobClient);
        ClientArguments clientArguments = new ClientArguments(args);
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.set("java.home", "/mowen/java/jdk");
        yarnConfiguration.set("jlogstash.root", "/user/mowen");

        JLogstashType jLogstashType = new JLogstashType();
        String cmd = jLogstashType.buildCmd(clientArguments, yarnConfiguration);
        Assert.assertNotNull(cmd);

    }

}
