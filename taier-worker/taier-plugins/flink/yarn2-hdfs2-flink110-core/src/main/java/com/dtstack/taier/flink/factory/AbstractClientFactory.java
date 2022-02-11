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

package com.dtstack.taier.flink.factory;

import com.dtstack.taier.flink.FlinkClientBuilder;
import com.dtstack.taier.flink.FlinkConfig;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.flink.base.enums.ClusterMode;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/5/29
 * Company: www.dtstack.com
 * @author maqi
 */
public abstract class AbstractClientFactory implements IClientFactory {

    public FlinkClientBuilder flinkClientBuilder;

    public static IClientFactory createClientFactory(FlinkClientBuilder flinkClientBuilder) {
        FlinkConfig flinkConfig = flinkClientBuilder.getFlinkConfig();
        ClusterMode clusterMode = ClusterMode.getClusteMode(flinkConfig.getClusterMode());
        IClientFactory clientFactory;
        switch (clusterMode) {
            case PER_JOB:
                clientFactory = new PerJobClientFactory(flinkClientBuilder);
                break;
            case SESSION:
                clientFactory = new SessionClientFactory(flinkClientBuilder);
                break;
            case STANDALONE:
                clientFactory = new StandaloneClientFactory(flinkClientBuilder);
                break;
            default:
                throw new PluginDefineException("not support clusterMode: " + clusterMode);
        }
        return clientFactory;
    }

    public YarnClusterDescriptor getClusterDescriptor(Configuration configuration, YarnConfiguration yarnConfiguration) {
        ClusterClientFactory<ApplicationId> clusterClientFactory = new YarnClusterClientFactory();
        YarnClusterClientFactory yarnClusterClientFactory = (YarnClusterClientFactory) clusterClientFactory;

        YarnConfiguration newYarnConfig = new YarnConfiguration();
        Iterator<Map.Entry<String, String>> iterator = yarnConfiguration.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            newYarnConfig.set(next.getKey(), next.getValue());
        }
        return yarnClusterClientFactory.createClusterDescriptor(configuration, newYarnConfig);
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
            throw new PluginDefineException("The Flink jar path is null");
        }
        return classpaths;
    }

    /**
     * 插件包及Lib包提前上传至HDFS，设置远程HDFS路径参数
     * @param flinkConfig 控制台flink配置
     * @param conf   YarnClusterDescriptor flinkConfiguration
     * @return  YarnClusterDescriptor flinkConfiguration
     */
    public Configuration setHdfsFlinkJarPath(FlinkConfig flinkConfig, Configuration flinkConfiguration){
        //检查HDFS上是否已经上传插件包及Lib包
        String remoteFlinkJarPath = flinkConfig.getRemoteFlinkJarPath();
        //remotePluginRootDir默认为/data/insight_plugin/flinkplugin, 不可能为空
        String remotePluginRootDir = flinkConfig.getRemotePluginRootDir();
        //不考虑二者只有其一上传到了hdfs上的情况
        if(StringUtils.startsWith(remoteFlinkJarPath, "hdfs://") && StringUtils.startsWith(remotePluginRootDir, "hdfs://")){
            flinkConfiguration.setString("remoteFlinkJarPath", remoteFlinkJarPath);
            flinkConfiguration.setString("remotePluginRootDir", remotePluginRootDir);
            flinkConfiguration.setString("flinkJarPath", flinkConfig.getFlinkJarPath());
            flinkConfiguration.setString("flinkPluginRoot", flinkConfig.getFlinkPluginRoot());
        }
        return flinkConfiguration;
    }
}
