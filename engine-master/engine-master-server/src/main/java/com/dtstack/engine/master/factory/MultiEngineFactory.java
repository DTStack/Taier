package com.dtstack.engine.master.factory;

import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.job.IJobStartTrigger;
import com.dtstack.engine.master.job.JobStartTriggerBase;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yuebai
 * @date 2020-04-26
 */
@Component
public class MultiEngineFactory {

    @Resource(name = "batchKylinJobStartTrigger")
    private IJobStartTrigger batchKylinJobStartTrigger;

    @Resource(name = "batchHadoopJobStartTrigger")
    private IJobStartTrigger batchHadoopJobStartTrigger;

    @Resource
    private JobStartTriggerBase jobStartTriggerBase;

    public IJobStartTrigger getJobTriggerService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return batchHadoopJobStartTrigger;
        }

        if (MultiEngineType.KYLIN.getType() == multiEngineType) {
            return batchKylinJobStartTrigger;
        }

        return jobStartTriggerBase;
    }
}
