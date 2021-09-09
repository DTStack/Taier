package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.pluginapi.util.RetryUtil;
import com.dtstack.engine.dtscript.api.ApplicationContext;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.client.ClientTest;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.HeartbeatRequest;
import com.dtstack.engine.dtscript.common.HeartbeatResponse;
import com.dtstack.engine.dtscript.container.ContainerEntity;
import com.dtstack.engine.dtscript.container.DtContainerId;
import com.google.gson.Gson;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import java.nio.file.FileSystem;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/26 19:13
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobConf.class, ClientTest.class, RetryUtil.class,
        ConverterUtils.class,
        Gson.class, UserGroupInformation.class, YarnClient.class,
        FileSystem.class, DtYarnConstants.class, ApplicationConstants.class})
@PowerMockIgnore("javax.net.ssl.*")
public class ApplicationContainerListenerTest {


    private static String testFileDir;

    @BeforeClass
    public static void beforeClassRun() {
        String rootDir = System.getProperty("user.dir");
        if (rootDir.contains("plugins")) {
            testFileDir = rootDir.substring(0, rootDir.indexOf("plugins")) + "testFile";
        } else {
            testFileDir = rootDir+ "testFile";
        }
    }

    @Test
    public void tesGetProtocolSignature() throws Exception {
        ApplicationContainerListener applicationContainerListener = PowerMockito.mock(ApplicationContainerListener.class);
        PowerMockito.when(applicationContainerListener.getProtocolSignature(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
        Assert.assertNull(applicationContainerListener.getProtocolSignature("http://", 2L, 90324));
    }

    @Test
    public void testGetProtocolVersion() throws Exception {
        ApplicationContainerListener applicationContainerListener = PowerMockito.mock(ApplicationContainerListener.class);
        PowerMockito.when(applicationContainerListener.getProtocolVersion(Mockito.anyString(), Mockito.anyLong())).thenReturn(0L);
        Assert.assertEquals(0L, applicationContainerListener.getProtocolVersion("http://", 2L));
    }

    @Test
    public void testHeartBeat() throws Exception {

        ApplicationContext context = PowerMockito.mock(ApplicationContext.class);
        Configuration configuration = new Configuration();
        ApplicationContainerListener listener = new ApplicationContainerListener(context, configuration);

        HeartbeatRequest heartbeatRequest = PowerMockito.mock(HeartbeatRequest.class);
        HeartbeatResponse heartbeatResponse = PowerMockito.mock(HeartbeatResponse.class);
        DtContainerId dtContainerId = PowerMockito.mock(DtContainerId.class);
        DtContainerStatus currentContainerStatus = DtContainerStatus.RUNNING;

        ConcurrentHashMap<DtContainerId, ContainerEntity>  allContainers = PowerMockito.mock(ConcurrentHashMap.class);

        PowerMockito.when(heartbeatRequest.getXlearningContainerStatus()).thenReturn(currentContainerStatus);

        ContainerEntity containerEntity = PowerMockito.mock(ContainerEntity.class);

        PowerMockito.when(allContainers.get(dtContainerId)).thenReturn(containerEntity);

        PowerMockito.when(containerEntity.getLastBeatTime()).thenReturn(131455520L);

        PowerMockito.when(containerEntity.getDtContainerStatus()).thenReturn(currentContainerStatus);

        ApplicationContainerListener applicationContainerListener = PowerMockito.mock(ApplicationContainerListener.class);

        PowerMockito.when(applicationContainerListener.heartbeat(dtContainerId, heartbeatRequest)).thenReturn(heartbeatResponse);

        Assert.assertNotNull(listener.heartbeat(dtContainerId, heartbeatRequest));


    }


}
