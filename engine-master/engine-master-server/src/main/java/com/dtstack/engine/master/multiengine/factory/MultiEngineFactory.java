package com.dtstack.engine.master.multiengine.factory;

import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.multiengine.JobStartTriggerBase;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yuebai
 * @date 2020-04-26
 */
@Component
public class MultiEngineFactory {

    @Resource
    private JobStartTriggerBase kylinJobStartTrigger;

    @Resource
    private JobStartTriggerBase hadoopJobStartTrigger;

    @Resource
    private JobStartTriggerBase jobStartTriggerBase;

    public JobStartTriggerBase getJobTriggerService(int type) {
        MultiEngineType multiEngineType = MultiEngineType.getByType(type);
        if (MultiEngineType.HADOOP == multiEngineType) {
            return hadoopJobStartTrigger;
        } else if (MultiEngineType.KYLIN == multiEngineType) {
            return kylinJobStartTrigger;
        }
        return jobStartTriggerBase;
    }
}
