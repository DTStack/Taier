/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.container.ContainerEntity;
import com.dtstack.engine.dtscript.container.DtContainerId;
import org.apache.commons.math3.analysis.function.Pow;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.ServiceAuthorizationManager;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RegisterApplicationMasterResponsePBImpl;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.client.api.async.impl.NMClientAsyncImpl;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.security.auth.Subject;
import java.net.InetSocketAddress;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/26 10:24
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AppArguments.class, AMRMClientAsync.class,
        NMClientAsync.class, ApplicationMaster.class, ApplicationMessageService.class,
        System.class,
        NetUtils.class, ApplicationContainerListener.class})
@PowerMockIgnore({"javax.net.ssl.*"})
public class ApplicationMasterTest {

    private static String testFileDir;

    @Mock
    AMRMClientAsync<AMRMClient.ContainerRequest> amrmAsync;

    @Mock
    NMClientAsyncImpl nmAsync;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
    public void testApplicationMaster() throws Exception {
        System.setProperty("HADOOP_USER_NAME", "admin");
        Map<String, String> systemEnvs = new HashMap<>();

        String workDir = temporaryFolder.newFolder("worker").getAbsolutePath();
        temporaryFolder.newFile("worker/dtscript-core-1.0.0.jar");
        temporaryFolder.newFile("worker/dterror.log");
        temporaryFolder.newFile("worker/dtstdout.log");

        systemEnvs.putAll(System.getenv());
        systemEnvs.put(ApplicationConstants.Environment.CONTAINER_ID.toString(), "container_e19_1533108188813_12125_01_000002");
        systemEnvs.put(DtYarnConstants.Environment.XLEARNING_JOB_CONF_LOCATION.toString(), workDir);
        systemEnvs.put("hadoop.job.ugi", "admin,admin");

        systemEnvs.put(DtYarnConstants.Environment.APP_JAR_LOCATION.toString(), workDir + "/" + "dtscript-core-1.0.0.jar");

        DtYarnConfiguration configuration = new DtYarnConfiguration();
        configuration.set("hadoop.job.ugi", "admin,admin");
        DtYarnConfiguration mockConfiguration = Mockito.spy(configuration);
        Mockito.when(mockConfiguration.get(DtYarnConfiguration.DTSCRIPT_APPMASTERJAR_PATH,
                DtYarnConfiguration.DEFAULT_DTSCRIPT_APPMASTERJAR_PATH)).thenReturn(workDir + "/" + "dtscript-core-1.0.0.jar");

        PowerMockito.whenNew(DtYarnConfiguration.class).withAnyArguments().thenReturn(mockConfiguration);

        ApplicationContainerListener containerListener = Mockito.mock(ApplicationContainerListener.class);
        ContainerLostDetector containerLostDetector =  Mockito.mock(ContainerLostDetector.class);
        PowerMockito.whenNew(ContainerLostDetector.class).withArguments(any(ApplicationContainerListener.class)).thenReturn(containerLostDetector);
        PowerMockito.whenNew(ApplicationContainerListener.class).withArguments(any(RunningAppContext.class), any(Configuration.class)).thenReturn(containerListener);

        mockStatic(System.class);
        when(System.getenv()).thenReturn(systemEnvs);

        mockStatic(AMRMClientAsync.class);
        mockStatic(NMClientAsync.class);

        when(AMRMClientAsync.createAMRMClientAsync(anyInt(),
                Mockito.any(RMCallbackHandler.class))).thenReturn(amrmAsync);

        when(NMClientAsync.createNMClientAsync(Mockito.any(NMCallbackHandler.class))).thenReturn(nmAsync);

        RPC.Builder builder = Mockito.mock(RPC.Builder.class);
        PowerMockito.whenNew(RPC.Builder.class).withAnyArguments().thenReturn(builder);
        Mockito.when(builder.setProtocol(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.setInstance(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.setBindAddress(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.setPort(Mockito.anyInt())).thenReturn(builder);

        RPC.Server server = Mockito.mock(RPC.Server.class);
        Mockito.when(server.getPort()).thenReturn(8080);
        ServiceAuthorizationManager manager = Mockito.mock(ServiceAuthorizationManager.class);
        Mockito.when(server.getServiceAuthorizationManager()).thenReturn(manager);

        Mockito.when(builder.build()).thenReturn(server);

        InetSocketAddress address = new InetSocketAddress(0);
        PowerMockito.mockStatic(NetUtils.class);
        Mockito.when(NetUtils.getConnectAddress(server)).thenReturn(address);

        RegisterApplicationMasterResponsePBImpl response = Mockito.mock(RegisterApplicationMasterResponsePBImpl.class);
        Mockito.when(amrmAsync.registerApplicationMaster(Mockito.any(),
                Mockito.anyInt(), Mockito.any())).thenReturn(response);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getVirtualCores()).thenReturn(1);
        Mockito.when(resource.getMemory()).thenReturn(512);
        Mockito.when(response.getMaximumResourceCapability()).thenReturn(resource);


        Container container = Mockito.mock(Container.class);
        ContainerId containerId = Mockito.mock(ContainerId.class);
        RMCallbackHandler rmCallback = Mockito.mock(RMCallbackHandler.class);
        Mockito.when(rmCallback.getAllocatedWorkerContainerNumber()).thenReturn(1);
        PowerMockito.when(container.getId()).thenReturn(containerId);
        Mockito.when(rmCallback.getReleaseContainers()).thenReturn(Arrays.asList(container));
        Mockito.when(rmCallback.getAcquiredWorkerContainer()).thenReturn(Arrays.asList(container, container));
        PowerMockito.whenNew(Container.class).withAnyArguments().thenReturn(container);
        PowerMockito.whenNew(RMCallbackHandler.class).withAnyArguments().thenReturn(rmCallback);
        List<Container> containers = Mockito.mock(List.class);
        Mockito.when(rmCallback.getReleaseContainers()).thenReturn(containers);

        NMCallbackHandler nmCallback = Mockito.mock(NMCallbackHandler.class);
        PowerMockito.whenNew(NMCallbackHandler.class).withArguments(any(ApplicationMaster.class)).thenReturn(nmCallback);

        NodeId nodeId = Mockito.mock(NodeId.class);
        Mockito.when(container.getId()).thenReturn(containerId);
        Mockito.when(container.getNodeId()).thenReturn(nodeId);
        Mockito.when(nodeId.getHost()).thenReturn("127.0.0.1");
        Mockito.when(nodeId.getPort()).thenReturn(8980);

        PowerMockito.when(containerListener.isTrainCompleted()).thenReturn(true);
        PowerMockito.when(containerListener.isAllWorkerContainersSucceeded()).thenReturn(true);


        NMClient nmClient = Mockito.mock(NMClient.class);
        Mockito.when(nmAsync.getClient()).thenReturn(nmClient);

        ApplicationMaster.main(null);

    }

}
