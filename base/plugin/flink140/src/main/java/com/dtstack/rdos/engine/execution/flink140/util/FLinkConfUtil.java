package com.dtstack.rdos.engine.execution.flink140.util;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.flink140.FlinkClient;
import com.dtstack.rdos.engine.execution.flink140.FlinkPerJobResourceInfo;
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
public class FLinkConfUtil {

    public static ClusterSpecification createClusterSpecification(Configuration configuration, int priority) {
        Properties confProperties = FlinkClient.jobClientThreadLocal.get().getConfProperties();
        if (confProperties != null
                && confProperties.containsKey(FlinkPerJobResourceInfo.JOBMANAGER_MEMORY_MB)
                && confProperties.containsKey(FlinkPerJobResourceInfo.TASKMANAGER_MEMORY_MB)
                && confProperties.containsKey(FlinkPerJobResourceInfo.CONTAINER)
                && confProperties.containsKey(FlinkPerJobResourceInfo.SLOTS)) {
            int jobmanagerMemoryMb = MathUtil.getIntegerVal(confProperties.get(FlinkPerJobResourceInfo.JOBMANAGER_MEMORY_MB));
            if (jobmanagerMemoryMb < FlinkPerJobResourceInfo.MIN_JM_MEMORY) {
                jobmanagerMemoryMb = FlinkPerJobResourceInfo.MIN_JM_MEMORY;
            }
            int taskmanagerMemoryMb = MathUtil.getIntegerVal(confProperties.get(FlinkPerJobResourceInfo.TASKMANAGER_MEMORY_MB));
            if (taskmanagerMemoryMb < FlinkPerJobResourceInfo.MIN_TM_MEMORY) {
                taskmanagerMemoryMb = FlinkPerJobResourceInfo.MIN_TM_MEMORY;
            }
            int numberTaskManagers = MathUtil.getIntegerVal(confProperties.get(FlinkPerJobResourceInfo.CONTAINER));
            int slotsPerTaskManager = MathUtil.getIntegerVal(confProperties.get(FlinkPerJobResourceInfo.SLOTS));
            return new ClusterSpecification.ClusterSpecificationBuilder()
                    .setMasterMemoryMB(jobmanagerMemoryMb)
                    .setTaskManagerMemoryMB(taskmanagerMemoryMb)
                    .setNumberTaskManagers(numberTaskManagers)
                    .setSlotsPerTaskManager(slotsPerTaskManager)
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

        return new ClusterSpecification.ClusterSpecificationBuilder()
                .setMasterMemoryMB(jobManagerMemoryMB)
                .setTaskManagerMemoryMB(taskManagerMemoryMB)
                .setNumberTaskManagers(numberTaskManagers)
                .setSlotsPerTaskManager(slotsPerTaskManager)
                .createClusterSpecification();
    }

}
