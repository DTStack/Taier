package com.dtstack.rdos.engine.execution.flink180;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rods.engine.execution.base.resource.AbstractYarnResourceInfo;

import java.util.Properties;

import static com.dtstack.rdos.engine.execution.flink180.constrant.ConfigConstrant.*;

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
        if (properties != null && properties.containsKey(CONTAINER)) {
            numberTaskManagers = MathUtil.getIntegerVal(properties.get(CONTAINER));
        }

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

        //作为启动 am 和 jobmanager
        if (!judgeYarnResource(1, 1, jobmanagerMemoryMb)) {
            return false;
        }

        if (!judgeYarnResource(numberTaskManagers, slotsPerTaskManager, taskmanagerMemoryMb)) {
            return false;
        }

        return true;
    }

}
