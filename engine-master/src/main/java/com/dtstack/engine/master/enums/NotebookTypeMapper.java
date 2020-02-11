package com.dtstack.engine.master.enums;

import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.engine.common.exception.RdosDefineException;

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
        map.put(EJobType.PYTHON.getVal(), EngineType.DtScript.getVal());
        map.put(EJobType.SPARK_PYTHON.getVal(), EngineType.Spark.getVal());
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
        return map.getOrDefault(taskType, EngineType.DtScript.getVal());
    }

}
