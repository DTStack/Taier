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
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.util.Strings;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ResourceManagerOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Date: 2020/6/1
 * Company: www.dtstack.com
 * @author maqi
 */
public class PerJobClientFactory extends AbstractClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PerJobClientFactory.class);

    private FlinkClientBuilder flinkClientBuilder;

    private PerJobClientFactory() {
    }

    private PerJobClientFactory(FlinkClientBuilder flinkClientBuilder) {
        this.flinkClientBuilder = flinkClientBuilder;
    }

    public ClusterDescriptor<String> createPerjobClusterDescriptor(JobClient jobClient, String projobClusterId) {

        FlinkConfig flinkConfig = flinkClientBuilder.getFlinkConfig();
        Configuration flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        Configuration newConf = new Configuration(flinkConfiguration);

        // set env
        setContainerEnv(newConf, jobClient);

        // set job config
        newConf = appendJobConfigAndInitFs(jobClient.getConfProperties(), newConf);

        // set cluster id
        newConf.setString(KubernetesConfigOptions.CLUSTER_ID, projobClusterId);

        // set resource config
        FlinkConfUtil.setResourceConfig(newConf, jobClient.getConfProperties());

        if (!flinkConfig.getFlinkHighAvailability() && ComputeType.BATCH == jobClient.getComputeType()) {
            setNoneHaModeConfig(newConf);
        }

        KubernetesClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf);

        // plugin dependent on shipfile
        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode()) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            newConf.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
            newConf.setString("classloader.resolve-order", "parent-first");
        }

        return clusterDescriptor;
    }

    private Configuration setContainerEnv(Configuration config, JobClient jobClient) {
        // set log env
        config.setString(buildMasterEnvKey(ConfigConstrant.TASKID_KEY), jobClient.getTaskId());
        config.setString(buildTaskManagerEnvKey(ConfigConstrant.TASKID_KEY), jobClient.getTaskId());

        config.setString(buildMasterEnvKey(ConfigConstrant.FLINKX_HOSTS_ENV), config.getString(ConfigConstrant.FLINKX_HOSTS_CONFIG_KEY, ""));
        config.setString(buildTaskManagerEnvKey(ConfigConstrant.FLINKX_HOSTS_ENV), config.getString(ConfigConstrant.FLINKX_HOSTS_CONFIG_KEY, ""));

        // set host env
        if (config.contains(KubernetesConfigOptions.KUBERNETES_HOST_ALIASES)) {
            String hostAliases = config.getString(KubernetesConfigOptions.KUBERNETES_HOST_ALIASES);
            hostAliases = hostAliases.replaceAll("[;；]", "\n");

            config.setString(buildMasterEnvKey(ConfigConstrant.KUBERNETES_HOST_ALIASES_ENV), hostAliases);
            config.setString(buildTaskManagerEnvKey(ConfigConstrant.KUBERNETES_HOST_ALIASES_ENV), hostAliases);
        }

        // set sftp env
        FlinkConfig flinkConfig = flinkClientBuilder.getFlinkConfig();
        Map<String, String> sftpConfig = flinkConfig.getSftpConf();
        if (sftpConfig.size() != 0) {
            String host = MapUtils.getString(sftpConfig, ConfigConstrant.KEY_HOST);
            String port = MapUtils.getString(sftpConfig, ConfigConstrant.KEY_PORT, ConfigConstrant.DEFAULT_PORT);
            String username = MapUtils.getString(sftpConfig, ConfigConstrant.KEY_USERNAME);
            String password = MapUtils.getString(sftpConfig, ConfigConstrant.KEY_PASSWORD);

            config.setString(buildMasterEnvKey(ConfigConstrant.SFTP_USERNAME_ENV), username);
            config.setString(buildTaskManagerEnvKey(ConfigConstrant.SFTP_USERNAME_ENV), username);

            config.setString(buildMasterEnvKey(ConfigConstrant.SFTP_PASSWORD_ENV), password);
            config.setString(buildTaskManagerEnvKey(ConfigConstrant.SFTP_PASSWORD_ENV), password);

            config.setString(buildMasterEnvKey(ConfigConstrant.SFTP_HOST_ENV), host);
            config.setString(buildTaskManagerEnvKey(ConfigConstrant.SFTP_HOST_ENV), host);

            config.setString(buildMasterEnvKey(ConfigConstrant.SFTP_PORT_ENV), port);
            config.setString(buildTaskManagerEnvKey(ConfigConstrant.SFTP_PORT_ENV), port);
        }

        // set sftp files path env
        Properties confProps = jobClient.getConfProperties();
        if (confProps != null && confProps.containsKey(ConfigConstrant.KEY_SFTPFILES_PATH)) {
            String sftpFilesPath = confProps.getProperty(ConfigConstrant.KEY_SFTPFILES_PATH);
            config.setString(buildMasterEnvKey(ConfigConstrant.SFTPFILES_PATH_ENV), sftpFilesPath);
            config.setString(buildTaskManagerEnvKey(ConfigConstrant.SFTPFILES_PATH_ENV), sftpFilesPath);
        }
        return config;
    }

    private String buildMasterEnvKey(String env){
        return ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + env;
    }
    private String buildTaskManagerEnvKey(String env){
        return ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + env;
    }

    private Configuration appendJobConfigAndInitFs(Properties properties, Configuration configuration) {
        if (properties != null) {
            properties.forEach((key, value) -> {
                Boolean isLogLevel = key.toString().equalsIgnoreCase(KubernetesConfigOptions.FLINK_LOG_LEVEL.key());
                Boolean isLogFileName = key.toString().equalsIgnoreCase(KubernetesConfigOptions.FLINK_LOG_FILE_NAME.key());
                if (key.toString().contains(".") || isLogLevel || isLogFileName) {
                    configuration.setString(key.toString(), value.toString());
                }
            });
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
    public ClusterClient getClusterClient(JobClient jobClient) {

        String taskName = getEffectiveTaskName(jobClient);
        String projobClusterId = taskName;

        try (
                ClusterDescriptor<String> clusterDescriptor = createPerjobClusterDescriptor(jobClient, projobClusterId);
        ) {

            deleteJobIfExist(flinkClientBuilder.getKubernetesClient(), jobClient, taskName);

            Configuration flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
            ClusterSpecification clusterSpecification = FlinkConfUtil.createClusterSpecification(flinkConfiguration, jobClient.getConfProperties());
            ClusterClient clusterClient = clusterDescriptor.deploySessionCluster(clusterSpecification).getClusterClient();
            return clusterClient;
        } catch (ClusterDeploymentException e) {
            if (flinkClientBuilder.getFlinkKubeClient().getInternalService(projobClusterId) != null) {
                flinkClientBuilder.getFlinkKubeClient().stopAndCleanupCluster(projobClusterId);
            }
            throw new RdosDefineException(e);
        }
    }

    private String getEffectiveTaskName(JobClient jobClient) {
        String taskName = jobClient.getJobName();
        String taskId = jobClient.getTaskId();
        if (Strings.isNotEmpty(taskName)) {
            taskName = StringUtils.lowerCase(taskName);
            taskName = StringUtils.splitByWholeSeparator(taskName, taskId)[0];
            taskName = taskName.replaceAll("\\p{P}", "-");
            taskName = String.format("%s-%s", taskName, taskId);
            Integer taskNameLength = taskName.length();
            if (taskNameLength > ConfigConstrant.TASKNAME_MAX_LENGTH) {
                taskName = taskName.substring(taskNameLength - ConfigConstrant.TASKNAME_MAX_LENGTH, taskNameLength);
            }
        }
        return taskName;
    }

    private void deleteJobIfExist(KubernetesClient kubernetesClient, JobClient jobClient, String effectiveTaskName) {
        String namespace = flinkClientBuilder.getFlinkConfig().getNamespace();
        ServiceList services = kubernetesClient.services().inNamespace(namespace).list();
        for (Service service : services.getItems()) {
            String serviceName = service.getMetadata().getName();
            if (StringUtils.isEmpty(serviceName)) {
                continue;
            }
            String regex = String.format("(%s)-[0-9a-z]{8}", effectiveTaskName);
            Boolean isEffective = serviceName.matches(regex);
            if (StringUtils.startsWith(serviceName, effectiveTaskName) && isEffective) {
                flinkClientBuilder.getFlinkKubeClient().stopAndCleanupCluster(serviceName);
            }
        }
    }

    @Override
    public ClusterClient retrieveClusterClient(String clusterId, JobClient jobClient) {

        try (
                ClusterDescriptor<String> clusterDescriptor = createPerjobClusterDescriptor(jobClient, clusterId);
        ) {
            return clusterDescriptor.retrieve(clusterId).getClusterClient();
        } catch (Exception e) {
            throw new RdosDefineException(e);
        }

    }

    public static PerJobClientFactory createPerJobClientFactory(FlinkClientBuilder flinkClientBuilder) {
        PerJobClientFactory perJobClientFactory = new PerJobClientFactory(flinkClientBuilder);
        return perJobClientFactory;
    }

    public FlinkClientBuilder getFlinkClientBuilder() {
        return flinkClientBuilder;
    }

    public void setFlinkClientBuilder(FlinkClientBuilder flinkClientBuilder) {
        this.flinkClientBuilder = flinkClientBuilder;
    }
}
