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

package com.dtstack.engine.dtscript.container;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.am.AppArguments;
import com.dtstack.engine.dtscript.am.ApplicationContainerListener;
import com.dtstack.engine.dtscript.am.ApplicationMaster;
import com.dtstack.engine.dtscript.am.ApplicationMessageService;
import com.dtstack.engine.dtscript.api.ApplicationContainerProtocol;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.HeartbeatRequest;
import com.dtstack.engine.dtscript.common.HeartbeatResponse;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/26 19:31
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AppArguments.class, AMRMClientAsync.class,
        NMClientAsync.class, ApplicationMaster.class, ApplicationMessageService.class,
        System.class,
        NetUtils.class, ApplicationContainerListener.class})
@PowerMockIgnore({"javax.net.ssl.*"})
public class ContainerStatusNotifilerTest {

    private static DtYarnConfiguration conf = new DtYarnConfiguration();


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeClass
    public static void initPrepare() {
        String rootDir = System.getProperty("user.dir");
        String testFileDir;
        if (rootDir.contains("plugins")) {
            testFileDir = rootDir.substring(0, rootDir.indexOf("plugins")) + "testFile";
        } else {
            testFileDir = rootDir+ "testFile";
        }

        System.setProperty("HADOOP_USER_NAME", "admin");
        System.setProperty("hadoop.job.ugi", "admin,admin");
        Map<String, String> systemEnvs = new HashMap<>();
        systemEnvs.putAll(System.getenv());

        conf.set("ipc.client.fallback-to-simple-auth-allowed", "true");

        String containerId = "container_e19_1533108188813_12125_01_000002";
        systemEnvs.put(ApplicationConstants.Environment.CONTAINER_ID.toString(),
                containerId);
        systemEnvs.put(ApplicationConstants.Environment.LOG_DIRS.name(), testFileDir);
        mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(systemEnvs);
        Mockito.when(System.getenv(ApplicationConstants.Environment.CONTAINER_ID.name())).thenReturn(containerId);
        Mockito.when(System.getenv(DtYarnConstants.Environment.APPMASTER_HOST.toString())).thenReturn("127.0.0.1");
        Mockito.when(System.getenv(DtYarnConstants.Environment.APPMASTER_PORT.toString())).thenReturn("8080");

    }



    @Test
    public void testInit() throws Exception{

        ApplicationContainerProtocol protocol = PowerMockito.mock(ApplicationContainerProtocol.class);
        DtContainerId dtContainerId = PowerMockito.mock(DtContainerId.class);

        HeartbeatRequest heartbeatRequest = PowerMockito.mock(HeartbeatRequest.class);
        PowerMockito.whenNew(HeartbeatRequest.class).withNoArguments().thenReturn(heartbeatRequest);
        HeartbeatResponse heartbeatResponse = PowerMockito.mock(HeartbeatResponse.class);
        PowerMockito.whenNew(HeartbeatResponse.class).withNoArguments().thenReturn(heartbeatResponse);

        ContainerStatusNotifier containerStatusNotifier = new ContainerStatusNotifier(protocol, conf, dtContainerId);

        PowerMockito.when(protocol.heartbeat(Mockito.any(DtContainerId.class), Mockito.any(HeartbeatRequest.class)))
                .thenReturn(heartbeatResponse);
        Assert.assertEquals(heartbeatResponse, containerStatusNotifier.heartbeatWithRetry());

    }

    @Test
    public void testHeartBeatResponseHandler() throws Exception {

        ApplicationContainerProtocol protocol = PowerMockito.mock(ApplicationContainerProtocol.class);
        DtContainerId dtContainerId = PowerMockito.mock(DtContainerId.class);

        HeartbeatRequest heartbeatRequest = PowerMockito.mock(HeartbeatRequest.class);
        PowerMockito.whenNew(HeartbeatRequest.class).withNoArguments().thenReturn(heartbeatRequest);

        ContainerStatusNotifier containerStatusNotifier = new ContainerStatusNotifier(protocol, conf, dtContainerId);

        HeartbeatResponse heartbeatResponse = PowerMockito.mock(HeartbeatResponse.class);

        PowerMockito.when(heartbeatResponse.getIsCompleted()).thenReturn(true);

        Long insertResultTimeStamp = 1000L;

        PowerMockito.when(heartbeatResponse.getInterResultTimeStamp()).thenReturn(insertResultTimeStamp);

        containerStatusNotifier.heartbeatResponseHandle(heartbeatResponse);
    }

}
