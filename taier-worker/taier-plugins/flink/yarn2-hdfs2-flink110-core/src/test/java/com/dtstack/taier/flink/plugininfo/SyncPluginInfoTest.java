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
package com.dtstack.taier.flink.plugininfo;

import com.dtstack.taier.flink.FlinkClient;
import com.dtstack.taier.flink.FlinkConfig;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.Properties;

/**
 * Date: 2021/05/31
 * Company: www.dtstack.com
 *
 * @author tudou
 */
@RunWith(PowerMockRunner.class)
public class SyncPluginInfoTest {

    @Test
    public void testCreateSyncPluginArgs() {
        JobClient jobClient = new JobClient();
        jobClient.setClassArgs("-jobid flink_test_stream");
        jobClient.setComputeType(ComputeType.STREAM);
        Whitebox.setInternalState(jobClient, "confProperties", new Properties());

        FlinkConfig flinkConfig = new FlinkConfig();
        flinkConfig.setRemoteFlinkJarPath("/opt/dtstack/110_flinkplugin/");
        flinkConfig.setFlinkPluginRoot("/opt/dtstack/110_flinkplugin/");
        flinkConfig.setMonitorAddress("http://localhost:8088");
        flinkConfig.setPluginLoadMode("shipfile");
        SyncPluginInfo syncPluginInfo = SyncPluginInfo.create(flinkConfig);
        List<String> args = syncPluginInfo.createSyncPluginArgs(jobClient, new FlinkClient());
        String result = new Gson().toJson(args);
        String expectStr = "[\"-jobid\",\"flink_test_stream\",\"-monitor\",\"http://localhost:8088\",\"-pluginLoadMode\",\"shipfile\",\"-mode\",\"yarnPer\"]";
        Assert.assertEquals(expectStr, result);
    }
}
