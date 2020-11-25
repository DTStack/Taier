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

package org.apache.flink.kubernetes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.runtime.clusterframework.BootstrapTools;
import org.apache.flink.runtime.clusterframework.ContaineredTaskManagerParameters;
import org.apache.flink.runtime.clusterframework.TaskExecutorProcessSpec;
import org.apache.flink.runtime.clusterframework.TaskExecutorProcessUtils;
import org.apache.flink.util.FlinkRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.flink.configuration.GlobalConfiguration.FLINK_CONF_FILENAME;
import static org.apache.flink.kubernetes.utils.Constants.CONFIG_FILE_LOG4J_NAME;
import static org.apache.flink.kubernetes.utils.Constants.CONFIG_FILE_LOGBACK_NAME;
import static org.apache.flink.kubernetes.utils.Constants.CONFIG_MAP_PREFIX;
import static org.apache.flink.kubernetes.utils.Constants.FLINK_CONF_VOLUME;
import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * Common utils for Kubernetes.
 */
public class KubernetesUtils {

    private static final Logger LOG = LoggerFactory.getLogger(KubernetesUtils.class);

    /**
     * Read file content to string.
     *
     * @param filePath file path
     * @return content
     */
    public static String getContentFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists()) {
            StringBuilder content = new StringBuilder();
            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error read file content.", e);
            }
            return content.toString();
        }
        throw new FileNotFoundException("File " + filePath + " not exists.");
    }

    /**
     * Check whether the port config option is a fixed port. If not, the fallback port will be set to configuration.
     * @param flinkConfig flink configuration
     * @param port config option need to be checked
     * @param fallbackPort the fallback port that will be set to the configuration
     */
    public static void checkAndUpdatePortConfigOption(
        Configuration flinkConfig,
        ConfigOption<String> port,
        int fallbackPort) {
        if (KubernetesUtils.parsePort(flinkConfig, port) == 0) {
            flinkConfig.setString(port, String.valueOf(fallbackPort));
            LOG.info(
                "Kubernetes deployment requires a fixed port. Configuration {} will be set to {}",
                port.key(),
                fallbackPort);
        }
    }

    /**
     * Parse a valid port for the config option. A fixed port is expected, and do not support a range of ports.
     *
     * @param flinkConfig flink config
     * @param port port config option
     * @return valid port
     */
    public static Integer parsePort(Configuration flinkConfig, ConfigOption<String> port) {
        checkNotNull(flinkConfig.get(port), port.key() + " should not be null.");

        try {
            return Integer.parseInt(flinkConfig.get(port));
        } catch (NumberFormatException ex) {
            throw new FlinkRuntimeException(
                port.key() + " should be specified to a fixed port. Do not support a range of ports.",
                ex);
        }
    }

    /**
     * Generates the shell command to start a job manager for kubernetes.
     *
     * @param flinkConfig The Flink configuration.
     * @param jobManagerMemoryMb JobManager heap size.
     * @param configDirectory The configuration directory for the flink-conf.yaml
     * @param logDirectory The log directory.
     * @param hasLogback Uses logback?
     * @param hasLog4j Uses log4j?
     * @param mainClass The main class to start with.
     * @param mainArgs The args for main class.
     * @return A String containing the job manager startup command.
     */
    public static String getJobManagerStartCommand(
        Configuration flinkConfig,
        int jobManagerMemoryMb,
        String configDirectory,
        String logDirectory,
        boolean hasLogback,
        boolean hasLog4j,
        String mainClass,
        @Nullable String mainArgs) {
        final int heapSize = BootstrapTools.calculateHeapSize(jobManagerMemoryMb, flinkConfig);
        final String jvmMemOpts = String.format("-Xms%sm -Xmx%sm", heapSize, heapSize);
        return getCommonStartCommand(
            flinkConfig,
            ClusterComponent.JOB_MANAGER,
            jvmMemOpts,
            configDirectory,
            logDirectory,
            hasLogback,
            hasLog4j,
            mainClass,
            mainArgs
        );
    }

    /**
     * Generates the shell command to start a task manager for kubernetes.
     *
     * @param flinkConfig The Flink configuration.
     * @param tmParams Parameters for the task manager.
     * @param configDirectory The configuration directory for the flink-conf.yaml
     * @param logDirectory The log directory.
     * @param hasLogback Uses logback?
     * @param hasLog4j Uses log4j?
     * @param mainClass The main class to start with.
     * @param mainArgs The args for main class.
     * @return A String containing the task manager startup command.
     */
    public static String getTaskManagerStartCommand(
        Configuration flinkConfig,
        ContaineredTaskManagerParameters tmParams,
        String configDirectory,
        String logDirectory,
        boolean hasLogback,
        boolean hasLog4j,
        String mainClass,
        @Nullable String mainArgs) {
        final TaskExecutorProcessSpec taskExecutorProcessSpec = tmParams.getTaskExecutorProcessSpec();
        final String jvmMemOpts = TaskExecutorProcessUtils.generateJvmParametersStr(taskExecutorProcessSpec);
        String args = TaskExecutorProcessUtils.generateDynamicConfigsStr(taskExecutorProcessSpec);
        if (mainArgs != null) {
            args += " " + mainArgs;
        }
        return getCommonStartCommand(
            flinkConfig,
            ClusterComponent.TASK_MANAGER,
            jvmMemOpts,
            configDirectory,
            logDirectory,
            hasLogback,
            hasLog4j,
            mainClass,
            args
        );
    }

    /**
     * Get config map volume for job manager and task manager pod.
     *
     * @param clusterId Cluster id.
     * @param hasLogback Uses logback?
     * @param hasLog4j Uses log4j?
     * @return Config map volume.
     */
    public static Volume getConfigMapVolume(String clusterId, boolean hasLogback, boolean hasLog4j) {
        final Volume configMapVolume = new Volume();
        configMapVolume.setName(FLINK_CONF_VOLUME);

        final List<KeyToPath> items = new ArrayList<>();
        items.add(new KeyToPath(FLINK_CONF_FILENAME, null, FLINK_CONF_FILENAME));

        if (hasLogback) {
            items.add(new KeyToPath(CONFIG_FILE_LOGBACK_NAME, null, CONFIG_FILE_LOGBACK_NAME));
        }

        if (hasLog4j) {
            items.add(new KeyToPath(CONFIG_FILE_LOG4J_NAME, null, CONFIG_FILE_LOG4J_NAME));
        }

        configMapVolume.setConfigMap(new ConfigMapVolumeSourceBuilder()
            .withName(CONFIG_MAP_PREFIX + clusterId)
            .withItems(items)
            .build());
        return configMapVolume;
    }

    /**
     * Get config map volume for job manager and task manager pod.
     *
     * @return Volume mount list.
     */
    public static Volume getHadoopConfigMapVolume(String clusterId) {
        Volume configMapVolume = new Volume();
        configMapVolume.setName("hadoop-config-volume");
        List<KeyToPath> items = new ArrayList();
        items.add(new KeyToPath("core-site.xml", (Integer) null, "core-site.xml"));
        configMapVolume.setConfigMap(new ConfigMapVolumeSourceBuilder()
            .withName(CONFIG_MAP_PREFIX + clusterId)
            .withItems(items)
            .build());
        return configMapVolume;
    }

    /**
     * Get config map volume for job manager and task manager pod.
     *
     * @param flinkConfDirInPod Flink conf directory that will be mounted in the pod.
     * @param hasLogback Uses logback?
     * @param hasLog4j Uses log4j?
     * @return Volume mount list.
     */
    public static List<VolumeMount> getConfigMapVolumeMount(String flinkConfDirInPod, boolean hasLogback, boolean hasLog4j) {
        final List<VolumeMount> volumeMounts = new ArrayList<>();
        volumeMounts.add(new VolumeMountBuilder()
            .withName(FLINK_CONF_VOLUME)
            .withMountPath(new File(flinkConfDirInPod, FLINK_CONF_FILENAME).getPath())
            .withSubPath(FLINK_CONF_FILENAME).build());

        if (hasLogback) {
            volumeMounts.add(new VolumeMountBuilder()
                .withName(FLINK_CONF_VOLUME)
                .withMountPath(new File(flinkConfDirInPod, CONFIG_FILE_LOGBACK_NAME).getPath())
                .withSubPath(CONFIG_FILE_LOGBACK_NAME)
                .build());
        }

        if (hasLog4j) {
            volumeMounts.add(new VolumeMountBuilder()
                .withName(FLINK_CONF_VOLUME)
                .withMountPath(new File(flinkConfDirInPod, CONFIG_FILE_LOG4J_NAME).getPath())
                .withSubPath(CONFIG_FILE_LOG4J_NAME)
                .build());
        }

        return volumeMounts;
    }

    /**
     * Get config map volume for job manager and task manager pod.
     *
     * @param flinkConfig Flink Configuration.
     * @param hasLogback Uses logback?
     * @param hasLog4j Uses log4j?
     * @return Volume mount list.
     */
    public static List<VolumeMount> getConfigMapVolumeMount(Configuration flinkConfig, boolean hasLogback, boolean hasLog4j) {
        final String flinkConfDirInPod = flinkConfig.getString(KubernetesConfigOptions.FLINK_CONF_DIR);
        final String hadoopConfDirInPod = flinkConfig.getString(KubernetesConfigOptions.HADOOP_CONF_DIR);

        final List<VolumeMount> volumeMounts = new ArrayList<>();
        volumeMounts.add(new VolumeMountBuilder()
            .withName(FLINK_CONF_VOLUME)
            .withMountPath(new File(flinkConfDirInPod, FLINK_CONF_FILENAME).getPath())
            .withSubPath(FLINK_CONF_FILENAME).build());

        if (flinkConfig.contains(KubernetesConfigOptions.HADOOP_CONF_STRING)) {
            volumeMounts.add(new VolumeMountBuilder()
                .withName("hadoop-config-volume")
                .withMountPath(new File(hadoopConfDirInPod, "core-site.xml").getPath())
                .withSubPath("core-site.xml").build());
        }

        if (hasLogback) {
            volumeMounts.add(new VolumeMountBuilder()
                .withName(FLINK_CONF_VOLUME)
                .withMountPath(new File(flinkConfDirInPod, CONFIG_FILE_LOGBACK_NAME).getPath())
                .withSubPath(CONFIG_FILE_LOGBACK_NAME)
                .build());
        }

        if (hasLog4j) {
            volumeMounts.add(new VolumeMountBuilder()
                .withName(FLINK_CONF_VOLUME)
                .withMountPath(new File(flinkConfDirInPod, CONFIG_FILE_LOG4J_NAME).getPath())
                .withSubPath(CONFIG_FILE_LOG4J_NAME)
                .build());
        }

        return volumeMounts;
    }

    /**
     * Get imagePullSecrets for job manager and task manager pod.
     *
     * @param flinkConfig Flink Configuration.
     * @return LocalObjectReference list.
     */
    public static List<LocalObjectReference> getImagePullSecrets(Configuration flinkConfig) {
        List<LocalObjectReference> imagePullSecrets = new ArrayList<>();
        if (flinkConfig.contains(KubernetesConfigOptions.CONTAINER_IMAGE_PULL_SECRETS)) {
            String secrets = flinkConfig.getString(KubernetesConfigOptions.CONTAINER_IMAGE_PULL_SECRETS);

            imagePullSecrets = Arrays.stream(secrets.split(","))
                .map(secret -> new LocalObjectReferenceBuilder().withName(secret.trim()).build())
                .collect(Collectors.toList());
        }
        return imagePullSecrets;
    }

    /**
     * Get resource requirements from memory and cpu.
     *
     * @param mem Memory in mb.
     * @param cpu cpu.
     * @return KubernetesResource requirements.
     */
    public static ResourceRequirements getResourceRequirements(int mem, double cpu) {
        final Quantity cpuQuantity = new Quantity(String.valueOf(cpu));
        final Quantity memQuantity = new Quantity(mem + Constants.RESOURCE_UNIT_MB);

        return new ResourceRequirementsBuilder()
            .addToRequests(Constants.RESOURCE_NAME_MEMORY, memQuantity)
            .addToRequests(Constants.RESOURCE_NAME_CPU, cpuQuantity)
            .addToLimits(Constants.RESOURCE_NAME_MEMORY, memQuantity)
            .addToLimits(Constants.RESOURCE_NAME_CPU, cpuQuantity)
            .build();
    }

    private static String getJavaOpts(Configuration flinkConfig, ConfigOption<String> configOption) {
        String baseJavaOpts = flinkConfig.getString(CoreOptions.FLINK_JVM_OPTIONS);

        if (flinkConfig.getString(configOption).length() > 0) {
            return baseJavaOpts + " " + flinkConfig.getString(configOption);
        } else {
            return baseJavaOpts;
        }
    }

    private static String getLogging(String logFile, String confDir, boolean hasLogback, boolean hasLog4j) {
        StringBuilder logging = new StringBuilder();
        if (hasLogback || hasLog4j) {
            logging.append("-Dlog.file=").append(logFile);
            if (hasLogback) {
                logging.append(" -Dlogback.configurationFile=file:").append(confDir).append("/logback.xml");
            }
            if (hasLog4j) {
                logging.append(" -Dlog4j.configuration=file:").append(confDir).append("/log4j.properties");
            }
        }
        return logging.toString();
    }

    private static String getCommonStartCommand(
        Configuration flinkConfig,
        ClusterComponent mode,
        String jvmMemOpts,
        String configDirectory,
        String logDirectory,
        boolean hasLogback,
        boolean hasLog4j,
        String mainClass,
        @Nullable String mainArgs) {
        final Map<String, String> startCommandValues = new HashMap<>();
        startCommandValues.put("java", "$JAVA_HOME/bin/java");
        startCommandValues.put("classpath", "-classpath " + "$" + Constants.ENV_FLINK_CLASSPATH);

        startCommandValues.put("jvmmem", jvmMemOpts);

        final String opts;
        final String logFileName;
        if (mode == ClusterComponent.JOB_MANAGER) {
            opts = getJavaOpts(flinkConfig, CoreOptions.FLINK_JM_JVM_OPTIONS);
            logFileName = "jobmanager";
        } else {
            opts = getJavaOpts(flinkConfig, CoreOptions.FLINK_TM_JVM_OPTIONS);
            logFileName = "taskmanager";
        }
        startCommandValues.put("jvmopts", opts);

        startCommandValues.put("logging",
            getLogging(logDirectory + "/" + logFileName + ".log", configDirectory, hasLogback, hasLog4j));

        startCommandValues.put("class", mainClass);

        startCommandValues.put("args", mainArgs != null ? mainArgs : "");

        startCommandValues.put("redirects",
            "1> " + logDirectory + "/" + logFileName + ".out " +
                "2> " + logDirectory + "/" + logFileName + ".err");

        final String commandTemplate = flinkConfig.getString(KubernetesConfigOptions.CONTAINER_START_COMMAND_TEMPLATE);
        return BootstrapTools.getStartCommand(commandTemplate, startCommandValues);
    }

    public static List<VolumeMount> parseVolumeMountsWithPrefix(String prefix, Configuration flinkConfig, List<VolumeMount> volumeMounts) {
        HashMap<String, Map<String, String>> volumeMountSources = new HashMap();
        for (String key : flinkConfig.keySet()) {
            if (StringUtils.startsWith(key, prefix) && StringUtils.contains(key, Constants.VOLUME_MOUNT_KEY)) {
                String[] contents = StringUtils.split(key, ".");
                String volumeName = contents[4];
                String optionName = contents[6];
                ConfigOption configOption = ConfigOptions.key(key).stringType().noDefaultValue();
                String optionValue = flinkConfig.getString(configOption);
                Map<String, String> volumeInfo = volumeMountSources.computeIfAbsent(volumeName, k -> new HashMap(){{
                    put("name", volumeName);
                }});
                volumeInfo.put(optionName, optionValue);
            }
        }

        if (volumeMountSources.isEmpty()) {
            return new ArrayList();
        }

        List<VolumeMount> customVolumeMounts = generateVolumeMounts(volumeMountSources);
        return customVolumeMounts;
    }

    private static List<VolumeMount> generateVolumeMounts(HashMap<String, Map<String, String>> volumeMountSources) {
        ObjectMapper objectMapper = new ObjectMapper();
        return volumeMountSources.keySet().stream().map(volumeName -> {
            Map<String, String> volumeMountSource = volumeMountSources.get(volumeName);
            try {
                byte[] objectBytes = objectMapper.writeValueAsBytes(volumeMountSource);
                VolumeMount volumeMount = objectMapper.readValue(objectBytes, VolumeMount.class);
                if (Objects.isNull(volumeMount.getReadOnly())) {
                    volumeMount.setReadOnly(Boolean.valueOf(true));
                }
                return volumeMount;
            } catch (Exception e) {
                LOG.error("Generate VolumeMounts error{}", e.getMessage());
                throw new RuntimeException("Generate VolumeMounts error");
            }
        }).collect(Collectors.toList());
    }

    public static List<Volume> parseVolumesWithPrefix(String prefix, Configuration flinkConfig, List<Volume> volumes) {
        HashMap<String, Map<String, String>> volumeSources = new HashMap();

        for (String key : flinkConfig.keySet()) {
            if (StringUtils.startsWith(key, prefix) && StringUtils.contains(key, Constants.VOLUME_OPTIONS_KEY)) {
                String[] contents = StringUtils.split(key, ".");
                String volumeType = contents[3];
                String volumeName = contents[4];
                String optionName = contents[6];
                ConfigOption configOption = ConfigOptions.key(key).stringType().noDefaultValue();
                String optionValue = flinkConfig.getString(configOption);

                Map<String, String> volumeInfo = volumeSources.computeIfAbsent(volumeName, k -> new HashMap<String, String>(){
                    {put(Constants.VOLUME_TYPE_KEY, volumeType);}
                });
                volumeInfo.put(optionName, optionValue);
            }
        }

        if (volumeSources.isEmpty()) {
            return new ArrayList();
        }

        List<Volume> customVolumes = generateVolumes(volumeSources);
        return customVolumes;
    }

    private static List<Volume> generateVolumes(HashMap<String, Map<String, String>> volumeSources) {
        return volumeSources.keySet().stream().map(volumeName -> {
            Map<String, String> volumeSource = volumeSources.get(volumeName);
            String volumeType = volumeSource.get(Constants.VOLUME_TYPE_KEY);
            try {
                return createVolumeByType(volumeName, volumeType, volumeSource);
            } catch (Exception e) {
                LOG.error("Generate Volumes error{}", e.getMessage());
                throw new RuntimeException("Generate Volumes error");
            }
        }).collect(Collectors.toList());
    }

    private static Volume createVolumeByType(String volumeName, String volumeType, Map<String, String> volumeSourceMap) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        byte[] objectBytes = objectMapper.writeValueAsBytes(volumeSourceMap);
        switch (volumeType) {
            case "nfs":
                NFSVolumeSource NFSvolumeSource = objectMapper.readValue(objectBytes, NFSVolumeSource.class);
                return new VolumeBuilder().withName(volumeName).withNfs(NFSvolumeSource).build();
            case "persistentVolumeClaim":
                PersistentVolumeClaimVolumeSource PVCvolumeSource = objectMapper.readValue(objectBytes, PersistentVolumeClaimVolumeSource.class);
                return new VolumeBuilder().withName(volumeName).withPersistentVolumeClaim(PVCvolumeSource).build();
            case "configMap":
                ConfigMapVolumeSource CMvolumeSource = objectMapper.readValue(objectBytes, ConfigMapVolumeSource.class);
                return new VolumeBuilder().withName(volumeName).withConfigMap(CMvolumeSource).build();
            default:
                throw new UnsupportedOperationException("Not implemented volumeType");
        }
    }

    private enum ClusterComponent {
        JOB_MANAGER,
        TASK_MANAGER
    }

    private KubernetesUtils() {}
}
