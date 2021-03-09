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


import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.base.enums.ClassLoaderType;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.enums.FlinkYarnMode;
import com.dtstack.engine.flink.util.FileUtil;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.*;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.util.Preconditions;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Date: 2020/5/13
 * Company: www.dtstack.com
 * @author maqi
 */
public class PerJobClientFactory extends AbstractClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PerJobClientFactory.class);

    private FlinkConfig flinkConfig;
    private Configuration flinkConfiguration;

    /**
     * 用于缓存连接perjob对应application的ClusterClient
     */
    private Cache<String, ClusterClient> perJobClientCache = CacheBuilder.newBuilder()
            .removalListener(new ClusterClientRemovalListener())
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    public PerJobClientFactory(FlinkClientBuilder flinkClientBuilder) {
        super(flinkClientBuilder);
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
    }

    @Override
    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {

        String applicationId = jobIdentifier.getApplicationId();
        String taskId = jobIdentifier.getTaskId();

        ClusterClient clusterClient = null;
        try {
            clusterClient = KerberosUtils.login(flinkConfig, () -> {
                try {
                    return perJobClientCache.get(applicationId, () -> {
                        ParamAction action = new ParamAction();
                        action.setTaskId(taskId);
                        action.setName("taskId-" + taskId);
                        action.setTaskType(EJobType.SQL.getType());
                        action.setComputeType(ComputeType.STREAM.getType());
                        action.setTenantId(-1L);
                        String taskParams = "flinkTaskRunMode=per_job";
                        action.setTaskParams(taskParams);
                        JobClient jobClient = new JobClient(action);
                        try (
                                AbstractYarnClusterDescriptor perJobYarnClusterDescriptor = this.createPerJobClusterDescriptor(jobClient);
                        ) {
                            return perJobYarnClusterDescriptor.retrieve(ConverterUtils.toApplicationId(applicationId));
                        }
                    });
                } catch (ExecutionException e) {
                    throw new RdosDefineException(e);
                }
            }, flinkClientBuilder.getYarnConf());
        } catch (Exception e) {
            LOG.error("job[{}] get perJobClient exception:{}", taskId, e.getMessage());
            throw new RdosDefineException(e);
        }

        return clusterClient;
    }

    public void dealWithDeployCluster(String applicationId, ClusterClient<ApplicationId> clusterClient) {
        perJobClientCache.put(applicationId, clusterClient);
    }

    public AbstractYarnClusterDescriptor createPerJobClusterDescriptor(JobClient jobClient) throws Exception {
        String flinkJarPath = flinkConfig.getFlinkJarPath();
        FileUtil.checkFileExist(flinkJarPath);

        Configuration newConf = new Configuration(flinkConfiguration);
        newConf = appendConfigAndInitFs(jobClient, newConf);

        List<File> keytabFiles = getKeytabFilesAndSetSecurityConfig(jobClient, newConf);

        AbstractYarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, flinkClientBuilder.getYarnConf(), ".");

        List<URL> classpaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);

        if (CollectionUtils.isNotEmpty(jobClient.getAttachJarInfos())) {
            for (JarFileInfo jarFileInfo : jobClient.getAttachJarInfos()) {
                classpaths.add(new File(jarFileInfo.getJarPath()).toURI().toURL());
            }
        }

        if (CollectionUtils.isNotEmpty(keytabFiles)) {
            clusterDescriptor.addShipFiles(keytabFiles);
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
                    .filter(key -> key.toString().contains(".") || key.toString().equalsIgnoreCase(ConfigConstrant.LOG_LEVEL_KEY))
                    .forEach(key -> configuration.setString(key.toString(), properties.getProperty(key)));
        }

        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(), jobClient.getComputeType());
        Boolean isPerjob = FlinkYarnMode.isPerJob(taskRunMode);
        if (!flinkConfig.getFlinkHighAvailability() && !isPerjob) {
            setNoneHaModeConfig(configuration);
        } else {
            configuration.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
            configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, jobClient.getTaskId());
        }

        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode()) && ConfigConstrant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            configuration.setString(ConfigConstrant.FLINK_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
        }

        String classloaderCache = configuration.getString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_TRUE);
        configuration.setString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, classloaderCache);
        String append = configuration.getString(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL);
        if (jobClient.getJobType() == EJobType.SQL || jobClient.getJobType() == EJobType.SYNC) {
            String dtstackAppend = "com.fasterxml.jackson.";
            if (StringUtils.isNotEmpty(append)) {
                dtstackAppend = dtstackAppend + ";" + append;
            }
            configuration.setString(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL, dtstackAppend);
        }

        try {
            FileSystem.initialize(configuration);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
        return configuration;
    }

    private List<File> getKeytabFilesAndSetSecurityConfig(JobClient jobClient, Configuration config) throws IOException {
        Map<String, File> keytabs = new HashMap<>();
        String remoteDir = flinkConfig.getRemoteDir();

        // 数据源keytab
        String taskKeytabDirPath = ConfigConstant.LOCAL_KEYTAB_DIR_PARENT + ConfigConstrant.SP + jobClient.getTaskId();
        File taskKeytabDir = new File(taskKeytabDirPath);
        File[] taskKeytabFiles = taskKeytabDir.listFiles();
        if (taskKeytabFiles != null && taskKeytabFiles.length > 0) {
            for (File file : taskKeytabFiles) {
                String fileName = file.getName();
                keytabs.put(fileName, file);
            }
        }

        // 任务提交keytab
        String clusterKeytabDirPath = ConfigConstant.LOCAL_KEYTAB_DIR_PARENT + remoteDir;
        File clusterKeytabDir = new File(clusterKeytabDirPath);
        File[] clusterKeytabFiles = clusterKeytabDir.listFiles();

        if (clusterKeytabFiles != null && clusterKeytabFiles.length > 0) {
            for (File file : clusterKeytabFiles) {
                String fileName = file.getName();
                String keytabPath = file.getAbsolutePath();
                String keytabFileName = flinkConfig.getPrincipalFile();

                if (keytabs.containsKey(fileName) && StringUtils.endsWith(fileName, "keytab")) {
                    String newFileName = String.format("%s-%s", RandomStringUtils.randomAlphanumeric(4), fileName);
                    keytabPath = String.format("%s/%s", taskKeytabDirPath, newFileName);
                    FileUtils.copyFile(file, new File(keytabPath));
                }

                if (StringUtils.equals(fileName, keytabFileName)) {
                    String principal = KerberosUtils.getPrincipal(keytabPath);
                    config.setString(SecurityOptions.KERBEROS_LOGIN_KEYTAB, keytabPath);
                    config.setString(SecurityOptions.KERBEROS_LOGIN_PRINCIPAL, principal);
                    continue;
                }
                File newKeytabFile = new File(keytabPath);
                keytabs.put(newKeytabFile.getName(), newKeytabFile);
            }
        }

        return keytabs.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }


    /**
     * 创建一个监听器，在缓存被移除的时候，得到这个通知
     */
    private class ClusterClientRemovalListener implements RemovalListener<String, ClusterClient> {

        @Override
        public void onRemoval(RemovalNotification<String, ClusterClient> notification) {
            LOG.info("key={},value={},reason={}", notification.getKey(), notification.getValue(), notification.getCause());
            if (notification.getValue() != null) {
                try {
                    notification.getValue().shutdown();
                } catch (Exception ex) {
                    LOG.info("[ClusterClientCache] Could not properly shutdown cluster client.", ex);
                }
            }
        }
    }
}
