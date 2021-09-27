package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.pluginapi.util.RetryUtil;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.client.ClientTest;
import com.google.gson.Gson;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import java.nio.file.FileSystem;
import java.util.List;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/27 15:38
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobConf.class, ClientTest.class, RetryUtil.class,
        ConverterUtils.class,
        Gson.class, UserGroupInformation.class, YarnClient.class,
        FileSystem.class, DtYarnConstants.class, ApplicationConstants.class})
@PowerMockIgnore("javax.net.ssl.*")
public class RMCallbackHandlerTest {

    @Test
    public void testOnContainersAllocated() {
        List<Container> containerList = PowerMockito.mock(List.class);

        RMCallbackHandler rmCallbackHandler = PowerMockito.mock(RMCallbackHandler.class);

        Container container = PowerMockito.mock(Container.class);

        ContainerId containerId = PowerMockito.mock(ContainerId.class);

        PowerMockito.when(container.getId()).thenReturn(containerId);
        NodeId nodeId = PowerMockito.mock(NodeId.class);
        PowerMockito.when(container.getNodeId()).thenReturn(nodeId);
        PowerMockito.when(nodeId.getHost()).thenReturn("127.0.0.1");
        PowerMockito.when(nodeId.getPort()).thenReturn(9080);

        Resource resource = PowerMockito.mock(Resource.class);
        PowerMockito.when(container.getResource()).thenReturn(resource);

        rmCallbackHandler.onContainersAllocated(containerList);
    }


}
