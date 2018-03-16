package com.dtstack.rdos.engine.execution.sparkyarn;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.UnitConvertUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;

import java.util.Properties;

/**
 * spark yarn 资源相关
 * Date: 2017/11/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SparkYarnResourceInfo extends EngineResourceInfo {

    public final static String CORE_TOTAL_KEY = "cores.total";

    public final static String CORE_USED_KEY = "cores.used";

    public final static String CORE_FREE_KEY = "cores.free";

    public final static String MEMORY_TOTAL_KEY = "memory.total";

    public final static String MEMORY_USED_KEY = "memory.used";

    public final static String MEMORY_FREE_KEY = "memory.free";

    private final static String EXECUTOR_INSTANCES_KEY = "executor.instances";

    private final static String EXECUTOR_MEM_KEY = "executor.memory";

    private final static String EXECUTOR_CORES_KEY = "executor.cores";

    private final static String EXECUTOR_MEM_OVERHEAD_KEY = "yarn.executor.memoryOverhead";

    public final static int DEFAULT_CORES = 1;

    public final static int DEFAULT_INSTANCES = 1;

    public final static int DEFAULT_MEM = 512;

    public final static int DEFAULT_MEM_OVERHEAD = 384;


    @Override
    public boolean judgeSlots(JobClient jobClient) {
        int totalFreeCore = 0;
        int totalFreeMem = 0;
        for(NodeResourceInfo tmpMap : nodeResourceMap.values()){
            int nodeFreeMem = MathUtil.getIntegerVal(tmpMap.getProp(MEMORY_FREE_KEY));
            int nodeFreeCores = MathUtil.getIntegerVal(tmpMap.getProp(CORE_FREE_KEY));
            totalFreeMem += nodeFreeMem;
            totalFreeCore += nodeFreeCores;
        }

        if(totalFreeCore == 0 || totalFreeMem == 0){
            return false;
        }

        Properties properties = jobClient.getConfProperties();
        int instances = DEFAULT_INSTANCES;
        if(properties != null){
            instances = properties.containsKey(EXECUTOR_INSTANCES_KEY) ? MathUtil.getIntegerVal(properties.get(EXECUTOR_INSTANCES_KEY)) : DEFAULT_INSTANCES;
        }

        return judgeCores(jobClient, instances, totalFreeCore) && judgeMem(jobClient, instances, totalFreeMem);
    }

    private boolean judgeCores(JobClient jobClient, int instances, int freeCore){

        Properties properties = jobClient.getConfProperties();
        int executorCores = properties.containsKey(EXECUTOR_CORES_KEY) ?
                MathUtil.getIntegerVal(properties.get(EXECUTOR_CORES_KEY)) : DEFAULT_CORES;

        int needCores = instances * executorCores;
        return needCores <= freeCore;
    }

    public boolean judgeMem(JobClient jobClient, int instances, int freeMem){
        Properties properties = jobClient.getConfProperties();

        int oneNeedMem = DEFAULT_MEM;
        if(properties.containsKey(EXECUTOR_MEM_KEY)){
            String setMemStr = properties.getProperty(EXECUTOR_MEM_KEY);
            oneNeedMem = UnitConvertUtil.convert2MB(setMemStr);
        }

        int executorJvmMem = DEFAULT_MEM_OVERHEAD;
        if(properties.containsKey(EXECUTOR_MEM_OVERHEAD_KEY)){
            String setMemStr = properties.getProperty(EXECUTOR_MEM_OVERHEAD_KEY);
            executorJvmMem = UnitConvertUtil.convert2MB(setMemStr);
        }

        oneNeedMem += executorJvmMem;

        int needTotal = instances * oneNeedMem;

        return needTotal <= freeMem;
    }
}
