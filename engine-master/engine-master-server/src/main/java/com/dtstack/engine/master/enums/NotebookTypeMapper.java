package com.dtstack.engine.master.enums;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.ScheduleEngineType;

import java.util.HashMap;
import java.util.Map;

/**
 * company: www.dtstack.com
 *
 * author: toutian
 * create: 2019/10/22
 */
public class NotebookTypeMapper {
    public static final Map<Integer, Integer> map = new HashMap<>();

    static {
        map.put(EScheduleJobType.PYTHON.getVal(), ScheduleEngineType.DtScript.getVal());
        map.put(EScheduleJobType.SPARK_PYTHON.getVal(), ScheduleEngineType.Spark.getVal());
    }

    public static Integer getTaskType(Integer engineType) {
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue().equals(engineType)) {
                return entry.getKey();
            }
        }
        throw new RdosDefineException("notebook任务engType类型错误");
    }

    public static Integer getEngineType(Integer taskType) {
        return map.getOrDefault(taskType, ScheduleEngineType.DtScript.getVal());
    }

}
