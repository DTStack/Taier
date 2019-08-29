package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.flink150.enums.FlinkYarnMode;
import com.dtstack.rdos.engine.execution.flink150.util.FlinkUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.dtstack.rdos.engine.execution.flink150.constrant.ConfigConstrant.*;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkResourceInfo extends EngineResourceInfo {

    private static final Logger logger = LoggerFactory.getLogger(FlinkResourceInfo.class);

    private final static ObjectMapper objMapper = new ObjectMapper();

    public int jobmanagerMemoryMb = MIN_JM_MEMORY;
    public int taskmanagerMemoryMb = MIN_JM_MEMORY;
    public int numberTaskManagers = 1;
    public int slotsPerTaskManager = 1;

    private boolean isPerJob;

    public FlinkResourceInfo(JobClient jobClient) {
        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(), jobClient.getComputeType());
        isPerJob = ComputeType.STREAM == jobClient.getComputeType() || FlinkYarnMode.isPerJob(taskRunMode);
    }

    public boolean isPerJob() {
        return isPerJob;
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        if (isPerJob) {
            return judgePerjobResource(jobClient);
        } else {
            return judgeYarnSeesionResource(jobClient);
        }
    }

    private boolean judgeYarnSeesionResource(JobClient jobClient) {
        int sqlEnvParallel = 1;
        int mrParallel = 1;

        if (jobClient.getConfProperties().containsKey(SQL_ENV_PARALLELISM)) {
            sqlEnvParallel = MathUtil.getIntegerVal(jobClient.getConfProperties().get(SQL_ENV_PARALLELISM));
        }

        if (jobClient.getConfProperties().containsKey(MR_JOB_PARALLELISM)) {
            mrParallel = MathUtil.getIntegerVal(jobClient.getConfProperties().get(MR_JOB_PARALLELISM));
        }

        return super.judgeFlinkSessionResource(sqlEnvParallel, mrParallel);
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

    public FlinkResourceInfo getFlinkSessionSlots(String message) {
        if (StringUtils.isNotBlank(message)) {
            try {
                Map<String, Object> taskManagerInfo = objMapper.readValue(message, Map.class);
                if (taskManagerInfo.containsKey("taskmanagers")) {
                    List<Map<String, Object>> taskManagerList = (List<Map<String, Object>>) taskManagerInfo.get("taskmanagers");
                    for (Map<String, Object> tmp : taskManagerList) {
                        int freeSlots = MapUtils.getIntValue(tmp, "freeSlots");
                        int slotsNumber = MapUtils.getIntValue(tmp, "slotsNumber");
                        this.addNodeResource(new EngineResourceInfo.NodeResourceDetail((String) tmp.get("id"), freeSlots, slotsNumber));
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return this;
    }

}
