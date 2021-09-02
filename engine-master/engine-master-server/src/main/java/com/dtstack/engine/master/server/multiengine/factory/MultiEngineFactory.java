package com.dtstack.engine.master.server.multiengine.factory;

import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.master.server.multiengine.JobStartTriggerBase;
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

    public JobStartTriggerBase getJobTriggerService(Integer type) {
        if(null == type){
            return jobStartTriggerBase;
        }
        MultiEngineType multiEngineType = MultiEngineType.getByType(type);
        if (MultiEngineType.HADOOP == multiEngineType) {
            return hadoopJobStartTrigger;
        } else if (MultiEngineType.KYLIN == multiEngineType) {
            return kylinJobStartTrigger;
        }
        return jobStartTriggerBase;
    }
}
