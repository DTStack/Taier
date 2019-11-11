package com.dtstack.engine.flink150.util;

import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.flink150.constrant.ConfigConstrant;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;

import java.util.Properties;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/5
 */
public class FlinkConfUtil {

    public static ClusterSpecification createClusterSpecification(Configuration configuration, int priority, Properties confProperties) {
        int jobmanagerMemoryMb = 1024;
        int taskmanagerMemoryMb = 1024;
        int numberTaskManagers = 1;
        int slotsPerTaskManager = 1;
        if (confProperties != null) {
            if (confProperties.containsKey(ConfigConstrant.JOBMANAGER_MEMORY_MB)){
                jobmanagerMemoryMb = MathUtil.getIntegerVal(confProperties.get(ConfigConstrant.JOBMANAGER_MEMORY_MB));
                if (jobmanagerMemoryMb < ConfigConstrant.MIN_JM_MEMORY) {
                    jobmanagerMemoryMb = ConfigConstrant.MIN_JM_MEMORY;
                }
            }
            if (confProperties.containsKey(ConfigConstrant.TASKMANAGER_MEMORY_MB)){
                taskmanagerMemoryMb = MathUtil.getIntegerVal(confProperties.get(ConfigConstrant.TASKMANAGER_MEMORY_MB));
                if (taskmanagerMemoryMb < ConfigConstrant.MIN_TM_MEMORY) {
                    taskmanagerMemoryMb = ConfigConstrant.MIN_TM_MEMORY;
                }
            }
            if (confProperties.containsKey(ConfigConstrant.SLOTS)){
                slotsPerTaskManager = MathUtil.getIntegerVal(confProperties.get(ConfigConstrant.SLOTS));
            }

            Integer sqlParallelism = FlinkUtil.getEnvParallelism(confProperties);
            Integer jobParallelism = FlinkUtil.getJobParallelism(confProperties);
            int parallelism = Math.max(sqlParallelism, jobParallelism);

            if (confProperties.containsKey(ConfigConstrant.CONTAINER)){
                numberTaskManagers = MathUtil.getIntegerVal(confProperties.get(ConfigConstrant.CONTAINER));
            }
            numberTaskManagers = Math.max(numberTaskManagers, parallelism);

            return new ClusterSpecification.ClusterSpecificationBuilder()
                    .setMasterMemoryMB(jobmanagerMemoryMb)
                    .setTaskManagerMemoryMB(taskmanagerMemoryMb)
                    .setNumberTaskManagers(numberTaskManagers)
                    .setSlotsPerTaskManager(slotsPerTaskManager)
                    .setParallelism(parallelism)
                    .setPriority(priority)
                    .createClusterSpecification();
        }
        return createDefaultClusterSpecification(configuration);
    }

    public static ClusterSpecification createDefaultClusterSpecification(Configuration configuration) {
        final int numberTaskManagers = 1;

        // JobManager Memory
        final int jobManagerMemoryMB = configuration.getInteger(JobManagerOptions.JOB_MANAGER_HEAP_MEMORY);

        // Task Managers memory
        final int taskManagerMemoryMB = configuration.getInteger(TaskManagerOptions.TASK_MANAGER_HEAP_MEMORY);

        int slotsPerTaskManager = configuration.getInteger(ConfigConstants.TASK_MANAGER_NUM_TASK_SLOTS, 1);

        int defaultParallelism = configuration.getInteger(ConfigConstants.DEFAULT_PARALLELISM_KEY, 1);

        return new ClusterSpecification.ClusterSpecificationBuilder()
                .setMasterMemoryMB(jobManagerMemoryMB)
                .setTaskManagerMemoryMB(taskManagerMemoryMB)
                .setNumberTaskManagers(numberTaskManagers)
                .setSlotsPerTaskManager(slotsPerTaskManager)
                .setParallelism(defaultParallelism)
                .createClusterSpecification();
    }

    public static ClusterSpecification createYarnSessionSpecification(Configuration configuration) {
        // JobManager Memory
        final int jobManagerMemoryMB = configuration.getInteger("yarn.jobmanager.heap.mb", 1024);

        // Task Managers memory
        final int taskManagerMemoryMB = configuration.getInteger("yarn.taskmanager.heap.mb", 1024);

        int slotsPerTaskManager = configuration.getInteger("yarn.taskmanager.numberOfTaskSlots", 2);
        int numberOfTaskManager = configuration.getInteger("yarn.taskmanager.numberOfTaskManager", 2);

        return new ClusterSpecification.ClusterSpecificationBuilder()
                .setMasterMemoryMB(jobManagerMemoryMB)
                .setTaskManagerMemoryMB(taskManagerMemoryMB)
                .setNumberTaskManagers(numberOfTaskManager)
                .setSlotsPerTaskManager(slotsPerTaskManager)
                .createClusterSpecification();
    }

}
