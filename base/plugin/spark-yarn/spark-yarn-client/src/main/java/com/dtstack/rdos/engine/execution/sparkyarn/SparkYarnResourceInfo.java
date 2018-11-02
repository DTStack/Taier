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

        Properties properties = jobClient.getConfProperties();
        int instances = DEFAULT_INSTANCES;
        if(properties != null && properties.containsKey(EXECUTOR_INSTANCES_KEY)){
            instances = MathUtil.getIntegerVal(properties.get(EXECUTOR_INSTANCES_KEY));
        }

        int executorCores = DEFAULT_CORES;
        if(properties != null && properties.containsKey(EXECUTOR_CORES_KEY)){
            executorCores = MathUtil.getIntegerVal(properties.get(EXECUTOR_CORES_KEY));
        }

        int oneNeedMem = DEFAULT_MEM;
        if(properties != null && properties.containsKey(EXECUTOR_MEM_KEY)){
            String setMemStr = properties.getProperty(EXECUTOR_MEM_KEY);
            oneNeedMem = UnitConvertUtil.convert2MB(setMemStr);
        }
        int executorJvmMem = DEFAULT_MEM_OVERHEAD;
        if(properties != null && properties.containsKey(EXECUTOR_MEM_OVERHEAD_KEY)){
            String setMemStr = properties.getProperty(EXECUTOR_MEM_OVERHEAD_KEY);
            executorJvmMem = UnitConvertUtil.convert2MB(setMemStr);
        }
        oneNeedMem+= executorJvmMem;

        return super.judgeYarnResource(instances,executorCores, oneNeedMem);
    }
}
