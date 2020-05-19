package com.dtstack.engine.master.factory;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.job.IJobStartTrigger;
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

    @Resource(name = "batchTiDBJobStartTrigger")
    private IJobStartTrigger batchTiDBJobStartTrigger;

    @Resource(name = "batchLibraJobStartTrigger")
    private IJobStartTrigger batchLibraJobStartTrigger;

    @Resource(name = "batchOracleJobStartTrigger")
    private IJobStartTrigger batchOracleJobStartTrigger;

    public IJobStartTrigger getJobTriggerService(int multiEngineType) {
        if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return batchHadoopJobStartTrigger;
        }
        if (MultiEngineType.LIBRA.getType() == multiEngineType) {
            return batchLibraJobStartTrigger;
        }
        if (MultiEngineType.KYLIN.getType() == multiEngineType) {
            return batchKylinJobStartTrigger;
        }
        if (MultiEngineType.TIDB.getType() == multiEngineType) {
            return batchTiDBJobStartTrigger;
        }
        if (MultiEngineType.ORACLE.getType() == multiEngineType) {
            return batchOracleJobStartTrigger;
        }
        throw new RdosDefineException(String.format("not support engine type %d now", multiEngineType));
    }
}
