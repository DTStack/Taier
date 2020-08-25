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

import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.util.FileUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Date: 2020/5/29
 * Company: www.dtstack.com
 * @author maqi
 */
public class PerJobClientFactory extends AbstractClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PerJobClientFactory.class);

    private static final String KERBEROS_DIR = "/kerberosPath/";

    private static final String LOG_LEVEL_KEY = "logLevel";

    private static final String USER_DIR = System.getProperty("user.dir");

    private FlinkConfig flinkConfig;
    private Configuration flinkConfiguration;
    private YarnConfiguration yarnConf;

    private PerJobClientFactory(FlinkClientBuilder flinkClientBuilder) {
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        this.yarnConf = flinkClientBuilder.getYarnConf();
    }

    public YarnClusterDescriptor createPerJobClusterDescriptor(JobClient jobClient) throws MalformedURLException {
        String flinkJarPath = flinkConfig.getFlinkJarPath();
        FileUtil.checkFileExist(flinkJarPath);

        Configuration newConf = new Configuration(flinkConfiguration);
        newConf = appendJobConfigAndInitFs(jobClient, newConf);

        YarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, yarnConf);
        List<URL> classpaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);

        if (CollectionUtils.isNotEmpty(jobClient.getAttachJarInfos())) {
            for (JarFileInfo jarFileInfo : jobClient.getAttachJarInfos()) {
                classpaths.add(new File(jarFileInfo.getJarPath()).toURI().toURL());
            }
        }

        clusterDescriptor.setProvidedUserJarFiles(classpaths);
        return clusterDescriptor;
    }

    private Configuration appendJobConfigAndInitFs(JobClient jobClient, Configuration configuration) {
        Properties properties = jobClient.getConfProperties();
        if (properties != null) {
            properties.stringPropertyNames()
                    .stream()
                    .filter(key -> key.toString().contains(".") || key.toString().equalsIgnoreCase(LOG_LEVEL_KEY))
                    .forEach(key -> configuration.setString(key.toString(), properties.getProperty(key)));
        }

        if (!flinkConfig.getFlinkHighAvailability() && ComputeType.BATCH == jobClient.getComputeType()) {
            setNoneHaModeConfig(configuration);
        } else {
            configuration.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
            configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, jobClient.getTaskId());
        }

        configuration.setString(YarnConfigOptions.APPLICATION_NAME, jobClient.getJobName());
        configuration.setInteger(YarnConfigOptions.APPLICATION_ATTEMPTS.key(), 0);

        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode()) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            configuration.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
            configuration.setString("classloader.resolve-order", "parent-first");
        }

        try {
            FileSystem.initialize(configuration);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
        return configuration;
    }

    @Override
    public ClusterClient getClusterClient() {
        return null;
    }

    public static PerJobClientFactory createPerJobClientFactory(FlinkClientBuilder flinkClientBuilder) {
        PerJobClientFactory perJobClientFactory = new PerJobClientFactory(flinkClientBuilder);
        return perJobClientFactory;
    }

}
