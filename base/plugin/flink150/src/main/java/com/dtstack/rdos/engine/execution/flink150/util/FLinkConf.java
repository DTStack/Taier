package com.dtstack.rdos.engine.execution.flink150.util;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;

import java.io.File;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/5
 */
public class FLinkConf {

    public static Configuration getConfiguration(String dir) {
        String configurationDirectory = getConfigurationDirectoryFromEnv(dir);
        Configuration configuration = GlobalConfiguration.loadConfiguration(configurationDirectory);
        return configuration;
    }

    private static String getConfigurationDirectoryFromEnv(String dir) {
        String location = System.getenv(ConfigConstants.ENV_FLINK_CONF_DIR);

        if (location != null) {
            if (new File(location).exists()) {
                return location;
            } else {
                throw new RuntimeException("The configuration directory '" + location + "', specified in the '" +
                        ConfigConstants.ENV_FLINK_CONF_DIR + "' environment variable, does not exist.");
            }
        } else if (dir != null && new File(dir).exists()) {
            location = dir;
        } else {
            throw new RuntimeException("The configuration directory was not specified. " +
                    "Please specify the directory containing the configuration file through the '" +
                    ConfigConstants.ENV_FLINK_CONF_DIR + "' environment variable.");
        }

        return location;
    }

    public static ClusterSpecification createClusterSpecification(Configuration configuration) {
        final int numberTaskManagers = 1;

        // JobManager Memory
        final int jobManagerMemoryMB = configuration.getInteger(JobManagerOptions.JOB_MANAGER_HEAP_MEMORY);

        // Task Managers memory
        final int taskManagerMemoryMB = configuration.getInteger(TaskManagerOptions.TASK_MANAGER_HEAP_MEMORY);

        int slotsPerTaskManager = configuration.getInteger(TaskManagerOptions.NUM_TASK_SLOTS);

        return new ClusterSpecification.ClusterSpecificationBuilder()
                .setMasterMemoryMB(jobManagerMemoryMB)
                .setTaskManagerMemoryMB(taskManagerMemoryMB)
                .setNumberTaskManagers(numberTaskManagers)
                .setSlotsPerTaskManager(slotsPerTaskManager)
                .createClusterSpecification();
    }

}
