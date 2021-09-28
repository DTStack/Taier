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

package com.dtstack.engine.flink;

import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Date: 2020/7/10
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public class FlinkConfUtilTest {

    @Test
    public void testCreateClusterSpecification() {
        Properties props = new Properties();
        props.setProperty(ConfigConstrant.JOBMANAGER_MEMORY_MB, "2048");
        props.setProperty(ConfigConstrant.TASKMANAGER_MEMORY_MB, "2048");
        props.setProperty(ConfigConstrant.SLOTS, "1");

        ClusterSpecification clusterSpecification =
                FlinkConfUtil.createClusterSpecification(null, 0, props);
        Assert.assertNotNull(clusterSpecification);

        Configuration configuration = new Configuration();
        configuration.setInteger(JobManagerOptions.JOB_MANAGER_HEAP_MEMORY_MB, 1024);
        configuration.setInteger(TaskManagerOptions.TASK_MANAGER_HEAP_MEMORY_MB, 1024);
        configuration.setInteger(ConfigConstants.TASK_MANAGER_NUM_TASK_SLOTS, 1);
        configuration.setInteger(ConfigConstants.DEFAULT_PARALLELISM_KEY, 1);
        ClusterSpecification defaultClusterSpecification =
                FlinkConfUtil.createClusterSpecification(configuration, 0, null);
        Assert.assertNotNull(defaultClusterSpecification);
    }
}
