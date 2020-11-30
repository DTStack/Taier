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

import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkClientBuilder;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 2020/5/29
 * Company: www.dtstack.com
 * @author maqi
 */
public class StandaloneClientFactory implements IClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(StandaloneClientFactory.class);

    private Configuration flinkConfiguration;

    public StandaloneClientFactory(FlinkClientBuilder flinkClientBuilder) {
        this.flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
    }

    @Override
    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        ClusterClientFactory<StandaloneClusterId> standaloneClientFactory = new org.apache.flink.client.deployment.StandaloneClientFactory();
        final StandaloneClusterId clusterId = standaloneClientFactory.getClusterId(flinkConfiguration);
        if (clusterId == null) {
            throw new RdosDefineException("No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        try {
            final ClusterDescriptor<StandaloneClusterId> clusterDescriptor = standaloneClientFactory.createClusterDescriptor(flinkConfiguration);
            ClusterClient<StandaloneClusterId> clusterClient = clusterDescriptor.retrieve(clusterId).getClusterClient();
            return clusterClient;
        } catch (Exception e) {
            LOG.info("No standalone session, Couldn't retrieve cluster Client.", e);
            throw new RdosDefineException("No standalone session, Couldn't retrieve cluster Client.");
        }
    }
}
