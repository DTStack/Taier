/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.flink.factory;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkClientBuilder;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2020/5/29
 * Company: www.dtstack.com
 * @author maqi
 */
public abstract class AbstractClientFactory implements IClientFactory {

    public FlinkClientBuilder flinkClientBuilder;

    public YarnClusterDescriptor getClusterDescriptor(Configuration configuration, YarnConfiguration yarnConfiguration) {
        ClusterClientFactory<ApplicationId> clusterClientFactory = new YarnClusterClientFactory();
        YarnClusterClientFactory yarnClusterClientFactory = (YarnClusterClientFactory) clusterClientFactory;
        return yarnClusterClientFactory.createClusterDescriptor(configuration, yarnConfiguration);
    }

    /**
     * set the copy of configuration
     */
    public void setNoneHaModeConfig(Configuration configuration) {
        configuration.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.NONE.toString());
        configuration.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        configuration.removeConfig(HighAvailabilityOptions.HA_ZOOKEEPER_ROOT);
        configuration.removeConfig(HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM);
    }

    public List<URL> getFlinkJarFile(String flinkJarPath, YarnClusterDescriptor clusterDescriptor) throws MalformedURLException {
        List<URL> classpaths = new ArrayList<>();
        if (flinkJarPath != null) {
            File[] jars = new File(flinkJarPath).listFiles();
            for (File file : jars) {
                if (file.toURI().toURL().toString().contains("flink-dist")) {
                    clusterDescriptor.setLocalJarPath(new Path(file.toURI().toURL().toString()));
                } else {
                    classpaths.add(file.toURI().toURL());
                }
            }
        } else {
            throw new RdosDefineException("The Flink jar path is null");
        }
        return classpaths;
    }

}
