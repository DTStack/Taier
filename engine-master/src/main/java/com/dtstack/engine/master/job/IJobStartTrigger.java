package com.dtstack.engine.master.job;

import com.dtstack.engine.api.domain.BatchJob;
import com.dtstack.engine.api.domain.BatchTaskShade;

import java.util.Map;

/**
 * @author yuebai
 * @date 2019-11-05
 */
public interface IJobStartTrigger {
     void readyForTaskStartTrigger(Map<String, Object> actionParam, BatchTaskShade taskShade, BatchJob batchJob) throws Exception;
}
