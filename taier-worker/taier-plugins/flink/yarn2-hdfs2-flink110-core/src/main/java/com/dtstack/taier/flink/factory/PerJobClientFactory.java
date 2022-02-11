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
import com.dtstack.taier.flink.constrant.ConfigConstrant;
import com.dtstack.taier.flink.util.FileUtil;
import com.dtstack.taier.flink.util.FlinkUtil;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.base.enums.ClassLoaderType;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.flink.base.enums.ClusterMode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
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
 * Date: 2020/5/29
 * Company: www.dtstack.com
 * @author maqi
 */
public class PerJobClientFactory extends AbstractClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PerJobClientFactory.class);

    private FlinkConfig flinkConfig;
    private Configuration flinkConfiguration;
    private YarnConfiguration yarnConf;

    /**
     * 用于缓存连接perjob对应application的ClusterClient
     */
    private Cache<String, ClusterClient> perJobClientCache = CacheBuilder.newBuilder()
            .removalListener(new ClusterClientRemovalListener())
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    public PerJobClientFactory(FlinkClientBuilder flinkClientBuilder) {
        this.flinkClientBuilder = flinkClientBuilder;
        this.flinkConfig = flinkClientBuilder.getFlinkConfig();
        this.flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
        this.yarnConf = flinkClientBuilder.getYarnConf();
    }

    @Override
    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        String applicationId = jobIdentifier.getApplicationId();
        String jobId = jobIdentifier.getJobId();

        ClusterClient clusterClient = null;

        try {
            clusterClient = KerberosUtils.login(flinkConfig, () -> {
                try {
                    return perJobClientCache.get(applicationId, () -> {
                        ParamAction action = new ParamAction();
                        action.setJobId(jobId);
                        action.setName("jobId-" + jobId);
                        action.setTaskType(EJobType.SQL.getType());
                        action.setComputeType(ComputeType.STREAM.getType());
                        action.setTenantId(-1L);
                        String taskParams = "flinkTaskRunMode=per_job";
                        action.setTaskParams(taskParams);
                        JobClient jobClient = new JobClient(action);
                        try (
                                YarnClusterDescriptor perJobYarnClusterDescriptor = this.createPerJobClusterDescriptor(jobClient);
                        )  {
                            return perJobYarnClusterDescriptor.retrieve(ConverterUtils.toApplicationId(applicationId)).getClusterClient();
                        }
                    });
                } catch (ExecutionException e) {
                    throw new PluginDefineException(e);
                }
            }, flinkClientBuilder.getYarnConf());
        } catch (Exception e) {
            LOG.error("job[{}] get perJobClient exception:{}", jobId, e);
            throw new PluginDefineException(e);
        }

        return clusterClient;
    }

    public void dealWithDeployCluster(String applicationId, ClusterClient<ApplicationId> clusterClient) {
        perJobClientCache.put(applicationId, clusterClient);
    }

    public YarnClusterDescriptor createPerJobClusterDescriptor(JobClient jobClient) throws Exception {
        String flinkJarPath = flinkConfig.getFlinkJarPath();
        FileUtil.checkFileExist(flinkJarPath);

        Configuration newConf = new Configuration(flinkConfiguration);
        newConf = appendJobConfigAndInitFs(jobClient, newConf);

        newConf = setHdfsFlinkJarPath(flinkConfig, newConf);

        List<File> resourceFiles = getResourceFilesAndSetSecurityConfig(jobClient, newConf);

        YarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, yarnConf);
        List<URL> classpaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);

        if (CollectionUtils.isNotEmpty(jobClient.getAttachJarInfos())) {
            for (JarFileInfo jarFileInfo : jobClient.getAttachJarInfos()) {
                classpaths.add(new File(jarFileInfo.getJarPath()).toURI().toURL());
            }
        }

        if (CollectionUtils.isNotEmpty(resourceFiles)) {
            clusterDescriptor.addShipFiles(resourceFiles);
        }

        clusterDescriptor.setProvidedUserJarFiles(classpaths);
        // judge job kind via JobType
        clusterDescriptor.setJobType(jobClient.getJobType());
        return clusterDescriptor;
    }

    /**
     * kill application which name and queue are the same as this jobClient
     * when run in stream-computing mode
     */
    public void deleteTaskIfExist(JobClient jobClient) {
        if(ComputeType.BATCH.equals(jobClient.getComputeType())){
            return;
        }
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
                LOG.info("try to kill application " + appId.toString() + " which name is " + report.getName());
                yarnClient.killApplication(appId);
            }
        } catch (Exception e) {
            LOG.error("Delete task error ", e);
            throw new PluginDefineException("Delete task error");
        }
    }

    private Configuration appendJobConfigAndInitFs(JobClient jobClient, Configuration configuration) {
        Properties properties = jobClient.getConfProperties();
        if (properties != null) {
            for (Object key : properties.keySet()) {
                String keyStr = key.toString();
                if (!StringUtils.contains(keyStr, ".") && !StringUtils.equalsIgnoreCase(keyStr, ConfigConstrant.LOG_LEVEL_KEY)) {
                    continue;
                }
                String value = properties.getProperty(keyStr);
                if (StringUtils.equalsIgnoreCase(keyStr, SecurityOptions.KERBEROS_LOGIN_CONTEXTS.key()) && StringUtils.isNotEmpty(value)) {
                    value = StringUtils.replacePattern(value, "\\s*", "");

                    String contexts = configuration.get(SecurityOptions.KERBEROS_LOGIN_CONTEXTS);
                    contexts = StringUtils.replacePattern(contexts, "\\s*", "");
                    contexts = StringUtils.isNotEmpty(contexts)? String.format("%s,%s", value, contexts) : value;
                    List<String> contextsTmp = Arrays.asList(StringUtils.split(contexts, ","));
                    Set contextsSet = new HashSet(contextsTmp);
                    value = StringUtils.join(contextsSet, ",");
                }
                configuration.setString(keyStr, value);
            }
        }

        String taskId = jobClient.getJobId();
        configuration.setString(ConfigConstrant.KEY_PROMGATEWAY_JOBNAME, taskId);

        ClusterMode clusterMode = ClusterMode.getClusteMode(flinkConfig.getClusterMode());
        Boolean isPerjob = ClusterMode.isPerjob(clusterMode);
        if (!flinkConfig.getFlinkHighAvailability() && !isPerjob) {
            setNoneHaModeConfig(configuration);
        } else {
            configuration.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
            configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, jobClient.getJobId());
        }

        configuration.setString(YarnConfigOptions.APPLICATION_NAME, jobClient.getJobName());

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
            throw new PluginDefineException(e);
        }
        return configuration;
    }

    private List<File> getResourceFilesAndSetSecurityConfig(JobClient jobClient, Configuration config) throws IOException {
        Map<String, File> resources = new HashMap<>();
        String remoteDir = flinkConfig.getRemoteDir();

        // resource files
        String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
        String taskResourceDirPath = taskWorkspace + ConfigConstrant.SP + "resource";
        File taskResourceDir = new File(taskResourceDirPath);
        File[] taskResourceDirFiles = taskResourceDir.listFiles();
        if (taskResourceDirFiles != null && taskResourceDirFiles.length > 0) {
            for (File file : taskResourceDirFiles) {
                String fileName = file.getName();
                resources.put(fileName, file);
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

                if (resources.containsKey(fileName) && StringUtils.endsWith(fileName, "keytab")) {
                    String newFileName = String.format("%s-%s", RandomStringUtils.randomAlphanumeric(4), fileName);
                    keytabPath = String.format("%s/%s", taskResourceDirPath, newFileName);
                    FileUtils.copyFile(file, new File(keytabPath));
                }

                if (StringUtils.equals(fileName, keytabFileName)) {
                    String principal = flinkConfig.getPrincipal();
                    if (StringUtils.isEmpty(principal)) {
                        principal = KerberosUtils.getPrincipal(keytabPath);
                    }
                    config.setString(SecurityOptions.KERBEROS_LOGIN_KEYTAB, keytabPath);
                    config.setString(SecurityOptions.KERBEROS_LOGIN_PRINCIPAL, principal);
                    continue;
                }
                File newKeytabFile = new File(keytabPath);
                resources.put(newKeytabFile.getName(), newKeytabFile);
            }
        }
        return resources.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
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
                    notification.getValue().close();
                } catch (Exception ex) {
                    LOG.info("[ClusterClientCache] Could not properly shutdown cluster client.", ex);
                }
            }
        }
    }

}
