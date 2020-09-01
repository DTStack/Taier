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

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Date: 2020/6/1
 * Company: www.dtstack.com
 * @author maqi
 */
public class SessionClientFactory extends AbstractClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SessionClientFactory.class);

    private String sessionClusterId;
    private FlinkClientBuilder flinkClientBuilder;
    private FlinkConfig flinkConfig;

    public SessionClientFactory(FlinkClientBuilder flinkClientBuilder) {

        this.flinkClientBuilder = flinkClientBuilder;
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();

        String defaultClusterId = flinkConfig.getFlinkSessionName() + ConfigConstrant.CLUSTER_ID_SPLIT
                + flinkConfig.getCluster() + ConfigConstrant.CLUSTER_ID_SPLIT + flinkConfig.getNamespace();
        sessionClusterId = StringUtils.replaceChars(defaultClusterId, ConfigConstrant.SPLIT, ConfigConstrant.CLUSTER_ID_SPLIT);
    }

    @Override
    public ClusterClient getClusterClient(JobClient jobClient) {
        Configuration flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();

        ClusterDescriptor kubernetesClusterDescriptor = createSessionClusterDescriptor();

        ClusterClient<String> retrieveClusterClient = retrieveClusterClient(sessionClusterId, null);

        if (Objects.nonNull(retrieveClusterClient)) {
            return retrieveClusterClient;
        }

        try {
            ClusterSpecification clusterSpecification = FlinkConfUtil.createClusterSpecification(flinkConfiguration, null);
            ClusterClient<String> clusterClient = kubernetesClusterDescriptor.deploySessionCluster(clusterSpecification).getClusterClient();
            Preconditions.checkNotNull(clusterClient, "clusterClient is null");
            return clusterClient;
        } catch (FlinkException e) {
            LOG.info("Couldn't deploy session on Kubernetes cluster, {}", e);
            throw new RdosDefineException(e);
        }
    }

    @Override
    public ClusterClient retrieveClusterClient(String clusterId, JobClient jobClient) {
        try {
            ClusterDescriptor kubernetesClusterDescriptor = createSessionClusterDescriptor();
            ClusterClientProvider<String> clusterClientProvider = kubernetesClusterDescriptor.retrieve(clusterId);
            if (Objects.isNull(clusterClientProvider)) {
                return null;
            }
            ClusterClient<String> clusterClient = clusterClientProvider.getClusterClient();
            return clusterClient;
        } catch (Exception e) {
            throw new RdosDefineException(e);
        }
    }

    public ClusterDescriptor<String> createSessionClusterDescriptor() {

        Configuration flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        Configuration newConf = new Configuration(flinkConfiguration);


        if (!flinkClientBuilder.getFlinkConfig().getFlinkHighAvailability()) {
            setNoneHaModeConfig(newConf);
        } else {
            //由engine管控的session clusterId不进行设置，默认使用appId作为clusterId
            newConf.removeConfig(HighAvailabilityOptions.HA_CLUSTER_ID);
        }


        // plugin dependent on shipfile
        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode()) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            newConf.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
            newConf.setString("classloader.resolve-order", "parent-first");
        }

        // set resource config
        FlinkConfUtil.setResourceConfig(newConf, null);

        KubernetesClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf);
        return clusterDescriptor;
    }

    public static SessionClientFactory createSessionClientFactory(FlinkClientBuilder flinkClientBuilder) {
        SessionClientFactory sessionClientFactory = new SessionClientFactory(flinkClientBuilder);
        return sessionClientFactory;
    }

    public String getSessionClusterId() {
        return sessionClusterId;
    }
}
