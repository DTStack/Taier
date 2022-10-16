package com.dtstack.taier.flink.perjob.client;

import com.dtstack.taier.base.enums.ClassLoaderType;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.flink.client.AbstractClientManager;
import com.dtstack.taier.flink.config.FlinkConfig;
import com.dtstack.taier.flink.config.HadoopConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.flink.util.FileUtil;
import com.dtstack.taier.flink.util.FlinkUtil;
import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
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
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.yarn.Utils;
import org.apache.flink.yarn.YarnClusterDescriptor;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public class PerJobClientManager extends AbstractClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(PerJobClientManager.class);

    /**
     * used to cache ClusterClient of application.
     * ClusterClients are different when their pluginInfo are different.
     */
    private final Cache<String, ClusterClient> perJobClientCache = CacheBuilder.newBuilder()
            .removalListener(new ClusterClientRemovalListener())
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    public PerJobClientManager(FlinkConfig flinkConfig, HadoopConfig hadoopConf, Configuration flinkGlobalConfiguration) {
        super(flinkConfig, hadoopConf);
        addFlinkConfiguration(flinkGlobalConfiguration);
    }

    @Override
    public ClusterClient getClusterClient(JobIdentifier jobIdentifier) {
        String applicationId = jobIdentifier.getApplicationId();
        String taskId = jobIdentifier.getJobId();

        ClusterClient<ApplicationId> clusterClient;

        try {
            clusterClient = KerberosUtils.login(flinkConfig, () -> {
                try {

                    if (StringUtils.isBlank(applicationId)) {
                        throw new IllegalArgumentException("applicationId must not be empty! ");
                    }

                    return perJobClientCache.get(applicationId, () -> {
                        ParamAction action = new ParamAction();
                        action.setJobId(taskId);
                        action.setName("taskId-" + taskId);
                        action.setTaskType(EJobType.SQL.getType());
                        action.setComputeType(ComputeType.STREAM.getType());
                        action.setTenantId(-1L);
                        String taskParams = "high-availability.cluster-id=" + applicationId;
                        action.setTaskParams(taskParams);
                        JobClient jobClient = new JobClient(action);
                        try (YarnClusterDescriptor perJobYarnClusterDescriptor = createPerJobClusterDescriptor(jobClient)) {
                            return perJobYarnClusterDescriptor.retrieve(ConverterUtils.toApplicationId(applicationId)).getClusterClient();
                        }
                    });
                } catch (ExecutionException e) {
                    throw new PluginDefineException(e);
                }
            }, hadoopConfig.getYarnConfiguration());
        } catch (Exception e) {
            throw new PluginDefineException(e);
        }

        return clusterClient;
    }

    public YarnClusterDescriptor createPerJobClusterDescriptor(JobClient jobClient) throws Exception {
        String flinkJarPath = flinkConfig.getFlinkLibDir();
        FileUtil.checkFileExist(flinkJarPath);

        Configuration newConf = applyToConfiguration(jobClient, new Configuration(flinkConfiguration));

        newConf = setHdfsFlinkJarPath(flinkConfig, newConf);

        List<File> resourceFiles = getResourceFilesAndSetSecurityConfig(jobClient, newConf);

        YarnClusterDescriptor clusterDescriptor = getClusterDescriptor(newConf, hadoopConfig.getYarnConfiguration());
        List<URL> classPaths = getFlinkJarFile(flinkJarPath, clusterDescriptor);

        if (CollectionUtils.isNotEmpty(jobClient.getAttachJarInfos())) {
            for (JarFileInfo jarFileInfo : jobClient.getAttachJarInfos()) {
                classPaths.add(new File(jarFileInfo.getJarPath()).toURI().toURL());
            }
        }

        if (CollectionUtils.isNotEmpty(resourceFiles)) {
            clusterDescriptor.addShipFiles(resourceFiles);
        }

        clusterDescriptor.setProvidedUserJarFiles(classPaths);
        return clusterDescriptor;
    }

    private Configuration applyToConfiguration(JobClient jobClient, Configuration configuration) {
        Properties properties = jobClient.getConfProperties();
        if (properties != null) {
            for (Object key : properties.keySet()) {
                String keyStr = key.toString();
                if (!StringUtils.contains(keyStr, ".")
                        && !StringUtils.equalsIgnoreCase(keyStr, ConfigConstant.LOG_LEVEL_KEY)) {
                    continue;
                }
                String value = properties.getProperty(keyStr);
                if (StringUtils.equalsIgnoreCase(keyStr, SecurityOptions.KERBEROS_LOGIN_CONTEXTS.key())
                        && StringUtils.isNotEmpty(value)) {

                    value = StringUtils.replacePattern(value, "\\s*", "");

                    String contexts = configuration.get(SecurityOptions.KERBEROS_LOGIN_CONTEXTS);
                    contexts = StringUtils.replacePattern(contexts, "\\s*", "");
                    contexts = StringUtils.isNotEmpty(contexts) ? String.format("%s,%s", value, contexts) : value;
                    List<String> contextsTmp = Arrays.asList(StringUtils.split(contexts, ","));
                    Set<String> contextsSet = new HashSet<>(contextsTmp);
                    value = StringUtils.join(contextsSet, ",");
                }
                configuration.setString(keyStr, value);
            }
        }

        configuration.setString(ConfigConstant.KEY_PROMGATEWAY_JOBNAME, jobClient.getJobId());

        if (flinkConfig.getFlinkHighAvailability()) {
            configuration.setString(HighAvailabilityOptions.HA_MODE, HighAvailabilityMode.ZOOKEEPER.toString());
            configuration.setString(HighAvailabilityOptions.HA_CLUSTER_ID, jobClient.getJobId());
        }

        configuration.setString(YarnConfigOptions.APPLICATION_NAME, jobClient.getJobName());

        if (StringUtils.isNotBlank(flinkConfig.getPluginLoadMode())
                && ConfigConstant.FLINK_PLUGIN_SHIPFILE_LOAD.equalsIgnoreCase(flinkConfig.getPluginLoadMode())) {
            configuration.setString(ConfigConstant.FLINKX_PLUGIN_LOAD_MODE, flinkConfig.getPluginLoadMode());
        }

        // class load
        String classloaderCache = configuration.getString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_TRUE);
        configuration.setString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, classloaderCache);
        String append = configuration.getString(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL);
        if (jobClient.getJobType() == EJobType.SQL || jobClient.getJobType() == EJobType.SYNC) {
            String dtstackAppend = ConfigConstant.PARENT_FIRST_LOADER_PATTERNS_DEFAULT;
            if (StringUtils.isNotEmpty(append)) {
                dtstackAppend = dtstackAppend + ";" + append;
            }
            configuration.setString(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL, dtstackAppend);
        }

        return configuration;
    }

    private List<File> getResourceFilesAndSetSecurityConfig(JobClient jobClient, Configuration config) throws IOException {
        Map<String, File> resources = new HashMap<>();
        String remoteDir = flinkConfig.getRemoteDir();

        // resource files
        String taskWorkspace = FlinkUtil.getTaskWorkspace(jobClient.getJobId());
        String taskResourceDirPath = taskWorkspace + ConfigConstant.SP + "resource";
        File taskResourceDir = new File(taskResourceDirPath);
        File[] taskResourceDirFiles = taskResourceDir.listFiles();
        if (taskResourceDirFiles != null && taskResourceDirFiles.length > 0) {
            for (File file : taskResourceDirFiles) {
                String fileName = file.getName();
                resources.put(fileName, file);
            }
        }

        // 任务提交keytab
        String clusterKeytabDirPath = com.dtstack.taier.pluginapi.constrant.ConfigConstant.LOCAL_KEYTAB_DIR_PARENT + remoteDir;
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
            }

            String[] kerberosFiles = KerberosUtils.getKerberosFile(flinkConfig, null);
            String krb5Path = kerberosFiles[1];
            String newKrb5Path = taskWorkspace + ConfigConstant.SP + Utils.KRB5_FILE_NAME;
            FileUtils.copyFile(new File(krb5Path), new File(newKrb5Path));
            resources.put(Utils.KRB5_FILE_NAME, new File(newKrb5Path));
        }

        // set krb5.conf
        if (resources.containsKey(Utils.KRB5_FILE_NAME)) {
            config.setString(SecurityOptions.KERBEROS_KRB5_PATH, resources.get(Utils.KRB5_FILE_NAME).getAbsolutePath());
            resources.remove(Utils.KRB5_FILE_NAME);
        }

        return new ArrayList<>(resources.values());
    }


    /**
     * flink rest client listener
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

    /**
     * 在任务执行成功后放入缓存
     */
    public void dealWithDeployCluster(String applicationId, ClusterClient<ApplicationId> clusterClient) {
        perJobClientCache.put(applicationId, clusterClient);
    }

    /**
     * kill application which name and queue are the same as this jobClient
     * when run in stream-computing mode
     */
    public void deleteTaskIfExist(JobClient jobClient) {
        if (ComputeType.BATCH.equals(jobClient.getComputeType())) {
            return;
        }
        try {
            String taskName = jobClient.getJobName();
            String queueName = flinkConfig.getQueue();
            YarnClient yarnClient = getYarnClient();

            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.NEW);
            enumSet.add(YarnApplicationState.NEW_SAVING);
            enumSet.add(YarnApplicationState.SUBMITTED);
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
            throw new PluginDefineException("Delete task error");
        }
    }

}
