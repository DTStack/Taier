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
import com.dtstack.engine.flink.FlinkConfig;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.MiniClusterClient;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalException;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.minicluster.MiniClusterConfiguration;
import org.apache.flink.runtime.util.LeaderConnectionInfo;

import java.net.InetSocketAddress;

/**
 * Date: 2020/5/12
 * Company: www.dtstack.com
 * @author maqi
 */
public class StandaloneClientFactory implements IClientFactory {

    private boolean isDetached = true;
    private Configuration flinkConfiguration;
    private FlinkConfig flinkConfig;


    public StandaloneClientFactory(Configuration flinkConfiguration, FlinkConfig flinkConfig) {
        this.flinkConfiguration = flinkConfiguration;
        this.flinkConfig = flinkConfig;
    }

    @Override
    public ClusterClient getClusterClient() {
        if (HighAvailabilityMode.ZOOKEEPER == HighAvailabilityMode.valueOf(flinkConfiguration.getValue(HighAvailabilityOptions.HA_MODE))) {
            return initClusterClientByZk();
        } else {
            return initClusterClientByUrl();
        }
    }

    private ClusterClient initClusterClientByZk() {

        MiniClusterConfiguration.Builder configBuilder = new MiniClusterConfiguration.Builder();
        Configuration config = new Configuration(flinkConfiguration);
        configBuilder.setConfiguration(config);
        //初始化的时候需要设置,否则提交job会出错,update config of jobMgrhost, jobMgrprt
        MiniCluster cluster = null;
        MiniClusterClient clusterClient = null;
        try {
            cluster = new MiniCluster(configBuilder.build());
            clusterClient = new MiniClusterClient(config, cluster);
            LeaderConnectionInfo connectionInfo = clusterClient.getClusterConnectionInfo();
            InetSocketAddress address = AkkaUtils.getInetSocketAddressFromAkkaURL(connectionInfo.getAddress());
            config.setString(JobManagerOptions.ADDRESS, address.getAddress().getHostName());
            config.setInteger(JobManagerOptions.PORT, address.getPort());
        } catch (LeaderRetrievalException e) {
            throw new RdosDefineException("Could not retrieve the leader address and leader session ID.");
        } catch (Exception e1) {
            throw new RdosDefineException("Failed to retrieve JobManager address");
        }
        return clusterClient;
    }

    /**
     * 直接指定jobmanager host:port方式
     */
    private ClusterClient initClusterClientByUrl() {

        String[] splitInfo = flinkConfig.getFlinkJobMgrUrl().split(":");
        if (splitInfo.length < 2) {
            throw new RdosDefineException("the config of engineUrl is wrong. " +
                    "setting value is :" + flinkConfig.getFlinkJobMgrUrl() + ", please check it!");
        }

        String jobMgrHost = splitInfo[0].trim();
        Integer jobMgrPort = Integer.parseInt(splitInfo[1].trim());

        Configuration config = new Configuration();
        config.setString(JobManagerOptions.ADDRESS, jobMgrHost);
        config.setInteger(JobManagerOptions.PORT, jobMgrPort);

        StandaloneClusterDescriptor descriptor = new StandaloneClusterDescriptor(config);
        RestClusterClient<StandaloneClusterId> clusterClient = null;
        try {
            clusterClient = descriptor.retrieve(null);
        } catch (ClusterRetrieveException e) {
            throw new RdosDefineException("Couldn't retrieve standalone cluster");
        }
        clusterClient.setDetached(isDetached);
        return clusterClient;
    }

}
