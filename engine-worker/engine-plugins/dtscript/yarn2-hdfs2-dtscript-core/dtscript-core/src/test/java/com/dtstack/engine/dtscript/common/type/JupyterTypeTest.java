package com.dtstack.engine.dtscript.common.type;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.dtscript.DataUtil;
import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.DtscriptUtils;
import com.dtstack.engine.dtscript.client.ClientArguments;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/28 10:40
 */
public class JupyterTypeTest {


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testBuildCmd() throws Exception{

        String projdectDir = temporaryFolder.newFolder("jupter1").getAbsolutePath();

        JobClient jobClient = DataUtil.getJobClient2();
        String[] args = DtscriptUtils.buildPythonArgs(jobClient);
        ClientArguments clientArguments = new ClientArguments(args);
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.set("java.home", "/mowen/java/jdk");
        yarnConfiguration.set("jlogstash.root", "/user/mowen");
        yarnConfiguration.set("jupyter.path", "/user/mowen/jupyter");
        yarnConfiguration.set("jupyter.project.root", "/user/mowen/workspace/jupyter1");

        DtYarnConfiguration dtYarnConfiguration = new DtYarnConfiguration();
        dtYarnConfiguration.set("jupyter.project.dir", projdectDir);

        Map<String, Object> containerInfo = new HashMap<>();

        JupyterType jupyterType = new JupyterType();
        String cmd = jupyterType.buildCmd(clientArguments, yarnConfiguration);
        System.out.println(cmd);
        Assert.assertNotNull(cmd);

        String extraCmd = jupyterType.cmdContainerExtra(cmd, dtYarnConfiguration, containerInfo);
        Assert.assertNotNull(extraCmd);

    }

}
