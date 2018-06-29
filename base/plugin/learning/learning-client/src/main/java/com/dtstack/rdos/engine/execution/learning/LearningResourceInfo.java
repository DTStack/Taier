package com.dtstack.rdos.engine.execution.learning;

import com.dtstack.learning.client.ClientArguments;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;

/**
 * 用于存储从xlearning上获取的资源信息
 * Date: 2018/6/27
 * Company: www.dtstack.com
 * @author jingzhen
 */
public class LearningResourceInfo extends EngineResourceInfo {

    public final static String CORE_TOTAL_KEY = "cores.total";

    public final static String CORE_USED_KEY = "cores.used";

    public final static String CORE_FREE_KEY = "cores.free";

    public final static String MEMORY_TOTAL_KEY = "memory.total";

    public final static String MEMORY_USED_KEY = "memory.used";

    public final static String MEMORY_FREE_KEY = "memory.free";


    @Override
    public boolean judgeSlots(JobClient jobClient) {
        int totalFreeCore = 0;
        int totalFreeMem = 0;
        int totalCore = 0;
        int totalMem = 0;

        for (NodeResourceInfo tmpMap : nodeResourceMap.values()) {
            int nodeFreeMem = MathUtil.getIntegerVal(tmpMap.getProp(MEMORY_FREE_KEY));
            int nodeFreeCores = MathUtil.getIntegerVal(tmpMap.getProp(CORE_FREE_KEY));
            int nodeCores = MathUtil.getIntegerVal(tmpMap.getProp(CORE_TOTAL_KEY));
            int nodeMem = MathUtil.getIntegerVal(tmpMap.getProp(MEMORY_TOTAL_KEY));

            totalFreeMem += nodeFreeMem;
            totalFreeCore += nodeFreeCores;
            totalCore += nodeCores;
            totalMem += nodeMem;
        }

        System.out.println("totalFreeCore=" + totalFreeCore + " totalFreeMem=" + totalFreeMem);
        if(totalFreeCore == 0 || totalFreeMem == 0){
            return false;
        }

        ClientArguments clientArguments;
        try {
            String[] args = LearningUtil.buildPythonArgs(jobClient);
            clientArguments = new ClientArguments(args);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
        int workerCores = clientArguments.getWorkerVCores();
        int workerMem = clientArguments.getWorkerMemory();
        int workerNum = clientArguments.getWorkerNum();
        int workerTotalCores = workerCores * workerNum;
        int workerTotalMem = workerMem * workerNum;

        int psCores = clientArguments.getPsVCores();
        int psMem = clientArguments.getPsMemory();
        int psNum = clientArguments.getPsNum();
        int psTotalCores = psCores * psNum;
        int psTotalMem = psMem * psNum;

        int neededCores = workerTotalCores + psTotalCores;
        int neededMem = workerTotalMem + psTotalMem;

        return neededCores <= totalFreeCore && neededMem <= totalFreeMem;
    }

}
