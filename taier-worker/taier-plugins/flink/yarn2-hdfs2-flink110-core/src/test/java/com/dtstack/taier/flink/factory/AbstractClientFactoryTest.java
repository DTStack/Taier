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

package com.dtstack.taier.flink.factory;

import com.dtstack.taier.flink.FlinkClientBuilder;
import com.dtstack.taier.flink.FlinkConfig;
import org.apache.flink.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;

/**
 * Date: 2021/03/12
 * Company: www.dtstack.com
 *
 * @author tudou
 */
public class AbstractClientFactoryTest {

    AbstractClientFactory abstractClientFactory = new PerJobClientFactory(mock(FlinkClientBuilder.class));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetHdfsFlinkJarPath() {
        FlinkConfig flinkConfig = new FlinkConfig();
        flinkConfig.setRemotePluginRootDir("/data/insight_plugin/flinkplugin");
        Configuration flinkConfiguration = abstractClientFactory.setHdfsFlinkJarPath(flinkConfig, new Configuration());
        Assert.assertFalse(flinkConfiguration.containsKey("remoteFlinkJarPath"));
        Assert.assertFalse(flinkConfiguration.containsKey("remotePluginRootDir"));
        Assert.assertFalse(flinkConfiguration.containsKey("flinkJarPath"));
        Assert.assertFalse(flinkConfiguration.containsKey("flinkPluginRoot"));

        flinkConfig.setRemoteFlinkJarPath("hdfs://ns/110_flinkplugin/");
        flinkConfiguration = abstractClientFactory.setHdfsFlinkJarPath(flinkConfig, new Configuration());
        Assert.assertFalse(flinkConfiguration.containsKey("remoteFlinkJarPath"));
        Assert.assertFalse(flinkConfiguration.containsKey("remotePluginRootDir"));
        Assert.assertFalse(flinkConfiguration.containsKey("flinkJarPath"));
        Assert.assertFalse(flinkConfiguration.containsKey("flinkPluginRoot"));


        flinkConfig.setRemotePluginRootDir("hdfs://ns/data/insight_plugin/flinkplugin");
        flinkConfig.setFlinkJarPath("/data/dtInsight/flink110/flink-1.10.1");
        flinkConfig.setFlinkPluginRoot("/data/insight_plugin/flinkplugin");
        flinkConfiguration = abstractClientFactory.setHdfsFlinkJarPath(flinkConfig, new Configuration());
        Assert.assertTrue(flinkConfiguration.containsKey("remoteFlinkJarPath"));
        Assert.assertTrue(flinkConfiguration.containsKey("remotePluginRootDir"));
        Assert.assertTrue(flinkConfiguration.containsKey("flinkJarPath"));
        Assert.assertTrue(flinkConfiguration.containsKey("flinkPluginRoot"));
    }
}
