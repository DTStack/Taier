package com.dtstack.engine.master.restartStrategy;

import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.restart.RestartStrategyType;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.restartStrategy.flink.FlinkAddMemoryRestartStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/27
 */
@Component
public class JobRestartStrategyPlain {

    @Autowired
    private WorkerOperator workerOperator;

    private FlinkAddMemoryRestartStrategy flinkAddMemoryRestartStrategy = new FlinkAddMemoryRestartStrategy();

    public JobRestartStrategy getRestartStrategyByEngineType(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        RestartStrategyType restartStrategyType = workerOperator.getRestartStrategyType(engineType, pluginInfo, jobIdentifier);

        if (restartStrategyType == null || RestartStrategyType.NONE == restartStrategyType) {
            return null;
        }

        if (EngineType.Flink == EngineType.getEngineType(engineType)) {
            return flinkAddMemoryRestartStrategy;
        } else {
            return null;
        }
    }
}
