package com.dtstack.task.server.job;

import com.dtstack.task.domain.BatchJob;
import com.dtstack.task.domain.BatchTaskShade;

import java.util.Map;

/**
 * @author yuebai
 * @date 2019-11-05
 */
public interface IJobStartTrigger {
     void readyForTaskStartTrigger(Map<String, Object> actionParam, BatchTaskShade taskShade, BatchJob batchJob) throws Exception;
}
