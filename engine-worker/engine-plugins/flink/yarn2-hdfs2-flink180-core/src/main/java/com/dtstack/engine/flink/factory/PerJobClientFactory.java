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
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.util.FileUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Date: 2020/5/13
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
    public static volatile PerJobClientFactory perJobClientFactory;

    private PerJobClientFactory(FlinkClientBuilder flinkClientBuilder) {
        super(flinkClientBuilder);
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
    }

    @Override
    public ClusterClient getClusterClient() {
        // perjob by deployJobCluster return clusterClient
        return null;
    }

    public AbstractYarnClusterDescriptor createPerJobClusterDescriptor(JobClient jobClient) throws MalformedURLException {
        String flinkJarPath = flinkConfig.getFlinkJarPath();
        FileUtil.checkFileExist(flinkJarPath);

        Configuration newConf = new Configuration(flinkConfiguration);
        newConf = appendConfigAndInitFs(jobClient, newConf);

        AbstractYarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, flinkClientBuilder.getYarnConf(), ".");

        List<URL> classpaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);

        if (CollectionUtils.isNotEmpty(jobClient.getAttachJarInfos())) {
            for (JarFileInfo jarFileInfo : jobClient.getAttachJarInfos()) {
                classpaths.add(new File(jarFileInfo.getJarPath()).toURI().toURL());
            }
        }

        if (flinkConfig.isOpenKerberos()) {
            List<File> keytabFilePath = getKeytabFilePath();
            clusterDescriptor.addShipFiles(keytabFilePath);
        }

        clusterDescriptor.setName(jobClient.getJobName());
        clusterDescriptor.setProvidedUserJarFiles(classpaths);
        clusterDescriptor.setQueue(flinkConfig.getQueue());
        return clusterDescriptor;
    }

    public void deleteTaskIfExist(JobClient jobClient) {
        try {
            String taskName = jobClient.getJobName();
            String queueName = flinkConfig.getQueue();
            YarnClient yarnClient = flinkClientBuilder.getYarnClient();

            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.ACCEPTED);
            enumSet.add(YarnApplicationState.RUNNING);

            List<ApplicationReport> existApps = yarnClient.getApplications(enumSet).stream().
                    filter(report -> report.getQueue().endsWith(queueName))
                    .filter(report -> report.getName().equals(taskName))
                    .collect(Collectors.toList());

            for (ApplicationReport report : existApps) {
                ApplicationId appId = report.getApplicationId();
                yarnClient.killApplication(appId);
            }
        } catch (Exception e) {
            LOG.error("Delete task error " + e.getMessage());
            throw new RdosDefineException("Delete task error");
        }
    }

    private Configuration appendConfigAndInitFs(JobClient jobClient, Configuration configuration) {
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
            String haClusterId = String.format("%s-%s", jobClient.getTaskId(), RandomStringUtils.randomAlphanumeric(8));
            configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, haClusterId);
        }

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

    private List<File> getKeytabFilePath() {
        List<File> keytabs = Lists.newLinkedList();
        String remoteDir = flinkConfig.getRemoteDir();
        String keytabDir = USER_DIR + KERBEROS_DIR + remoteDir;
        File keytabDirName = new File(keytabDir);
        File[] files = keytabDirName.listFiles();

        if (files == null || files.length == 0) {
            throw new RdosDefineException("not find keytab file from " + keytabDir);
        }
        for (File file : files) {
            keytabs.add(file);
        }
        return keytabs;
    }


    public static PerJobClientFactory createPerJobClientFactory(FlinkClientBuilder flinkClientBuilder) {
        PerJobClientFactory perJobClientFactory = new PerJobClientFactory(flinkClientBuilder);
        return perJobClientFactory;
    }

    public static PerJobClientFactory getPerJobClientFactory() {
        return perJobClientFactory;
    }

}
