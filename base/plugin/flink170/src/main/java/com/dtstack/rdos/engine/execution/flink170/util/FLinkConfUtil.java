package com.dtstack.rdos.engine.execution.flink170.util;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.flink170.FlinkClient;
import com.dtstack.rdos.engine.execution.flink170.FlinkPerJobResourceInfo;
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
        int jobmanagerMemoryMb = 1024;
        int taskmanagerMemoryMb = 1024;
        int numberTaskManagers = 1;
        int slotsPerTaskManager = 1;
        if (confProperties != null) {
            if (confProperties.containsKey(FlinkPerJobResourceInfo.JOBMANAGER_MEMORY_MB)){
                jobmanagerMemoryMb = MathUtil.getIntegerVal(confProperties.get(FlinkPerJobResourceInfo.JOBMANAGER_MEMORY_MB));
                if (jobmanagerMemoryMb < FlinkPerJobResourceInfo.MIN_JM_MEMORY) {
                    jobmanagerMemoryMb = FlinkPerJobResourceInfo.MIN_JM_MEMORY;
                }
            }
            if (confProperties.containsKey(FlinkPerJobResourceInfo.TASKMANAGER_MEMORY_MB)){
                taskmanagerMemoryMb = MathUtil.getIntegerVal(confProperties.get(FlinkPerJobResourceInfo.TASKMANAGER_MEMORY_MB));
                if (taskmanagerMemoryMb < FlinkPerJobResourceInfo.MIN_TM_MEMORY) {
                    taskmanagerMemoryMb = FlinkPerJobResourceInfo.MIN_TM_MEMORY;
                }
            }
            if (confProperties.containsKey(FlinkPerJobResourceInfo.CONTAINER)){
                numberTaskManagers = MathUtil.getIntegerVal(confProperties.get(FlinkPerJobResourceInfo.CONTAINER));
            }
            if (confProperties.containsKey(FlinkPerJobResourceInfo.SLOTS)){
                slotsPerTaskManager = MathUtil.getIntegerVal(confProperties.get(FlinkPerJobResourceInfo.SLOTS));
            }
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
        final int jobManagerMemoryMB = configuration.getInteger(JobManagerOptions.JOB_MANAGER_HEAP_MEMORY_MB);

        // Task Managers memory
        final int taskManagerMemoryMB = configuration.getInteger(TaskManagerOptions.TASK_MANAGER_HEAP_MEMORY_MB);

        int slotsPerTaskManager = configuration.getInteger(ConfigConstants.TASK_MANAGER_NUM_TASK_SLOTS, 1);

        return new ClusterSpecification.ClusterSpecificationBuilder()
                .setMasterMemoryMB(jobManagerMemoryMB)
                .setTaskManagerMemoryMB(taskManagerMemoryMB)
                .setNumberTaskManagers(numberTaskManagers)
                .setSlotsPerTaskManager(slotsPerTaskManager)
                .createClusterSpecification();
    }

}
