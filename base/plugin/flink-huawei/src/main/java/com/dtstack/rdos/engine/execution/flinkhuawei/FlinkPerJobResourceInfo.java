package com.dtstack.rdos.engine.execution.flinkhuawei;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.flinkhuawei.util.FlinkUtil;
import com.dtstack.rods.engine.execution.base.resource.AbstractYarnResourceInfo;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Properties;

import static com.dtstack.rdos.engine.execution.flinkhuawei.constrant.ConfigConstrant.*;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkPerJobResourceInfo extends AbstractYarnResourceInfo {

    public int jobmanagerMemoryMb = MIN_JM_MEMORY;
    public int taskmanagerMemoryMb = MIN_JM_MEMORY;
    public int numberTaskManagers = 1;
    public int slotsPerTaskManager = 1;

    public FlinkPerJobResourceInfo() {
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return judgePerjobResource(jobClient);
    }

    private boolean judgePerjobResource(JobClient jobClient) {
        if (totalFreeCore == 0 || totalFreeMem == 0) {
            return false;
        }

        Properties properties = jobClient.getConfProperties();

        if (properties != null && properties.containsKey(SLOTS)) {
            slotsPerTaskManager = MathUtil.getIntegerVal(properties.get(SLOTS));
        }

        Integer sqlParallelism = FlinkUtil.getEnvParallelism(jobClient.getConfProperties());
        Integer jobParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
        int parallelism = Math.max(sqlParallelism, jobParallelism);
        if (properties != null && properties.containsKey(CONTAINER)) {
            numberTaskManagers = MathUtil.getIntegerVal(properties.get(CONTAINER));
        }
        numberTaskManagers = Math.max(numberTaskManagers, parallelism);

        if (properties != null && properties.containsKey(JOBMANAGER_MEMORY_MB)) {
            jobmanagerMemoryMb = MathUtil.getIntegerVal(properties.get(JOBMANAGER_MEMORY_MB));
        }
        if (jobmanagerMemoryMb < MIN_JM_MEMORY) {
            jobmanagerMemoryMb = MIN_JM_MEMORY;
        }

        if (properties != null && properties.containsKey(TASKMANAGER_MEMORY_MB)) {
            taskmanagerMemoryMb = MathUtil.getIntegerVal(properties.get(TASKMANAGER_MEMORY_MB));
        }
        if (taskmanagerMemoryMb < MIN_TM_MEMORY) {
            taskmanagerMemoryMb = MIN_TM_MEMORY;
        }

        List<InstanceInfo> instanceInfos = Lists.newArrayList(
                //作为启动 am 和 jobmanager
                InstanceInfo.newRecord(1, 1, jobmanagerMemoryMb),
                InstanceInfo.newRecord(numberTaskManagers, slotsPerTaskManager, taskmanagerMemoryMb));
        return judgeYarnResource(instanceInfos);
    }

}
