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


package com.dtstack.taier.flink.standalone.client;

import com.dtstack.taier.flink.client.AbstractClientManager;
import com.dtstack.taier.flink.config.FlinkConfig;
import com.dtstack.taier.flink.config.HadoopConfig;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.deployment.StandaloneClientFactory;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public class StandaloneClientManager extends AbstractClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(StandaloneClientManager.class);

    private volatile ClusterClient<StandaloneClusterId> clusterClient = null;

    public StandaloneClientManager(FlinkConfig flinkConfig, HadoopConfig hadoopConf) {
        super(flinkConfig, hadoopConf);
    }

    @Override
    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        if (clusterClient == null) {
            clusterClient = createClusterClient();
        } else {
            String webInterface = clusterClient.getWebInterfaceURL();
            if ("Unknown address.".equals(webInterface)) {
                clusterClient = createClusterClient();
            }
        }
        return clusterClient;
    }

    private ClusterClient<StandaloneClusterId> createClusterClient() {
        ClusterClientFactory<StandaloneClusterId> standaloneClientFactory = new StandaloneClientFactory();
        final StandaloneClusterId clusterId = standaloneClientFactory.getClusterId(flinkConfiguration);
        if (clusterId == null) {
            throw new PluginDefineException("No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        try (ClusterDescriptor<StandaloneClusterId> clusterDescriptor = standaloneClientFactory.createClusterDescriptor(flinkConfiguration)) {
            ClusterClientProvider<StandaloneClusterId> clientProvider = clusterDescriptor.retrieve(clusterId);
            return clientProvider.getClusterClient();
        } catch (ClusterRetrieveException e) {
            LOG.error("No standalone session, Couldn't retrieve cluster Client.", e);
            throw new PluginDefineException("No standalone session, Couldn't retrieve cluster Client.", e);
        }
    }

}
