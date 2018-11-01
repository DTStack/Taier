package com.dtstack.rdos.engine.execution.learning;

import com.dtstack.learning.client.ClientArguments;
import com.dtstack.rdos.commom.exception.ErrorCode;
import com.dtstack.rdos.commom.exception.RdosException;
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

    private float capacity = 1;

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        int totalFreeCore = 0;
        int totalFreeMem = 0;
        int totalCore = 0;
        int totalMem = 0;

        int[] nmFree = new int[nodeResourceMap.size()];
        int index = 0;
        for (EngineResourceInfo.NodeResourceInfo tmpMap : nodeResourceMap.values()) {
            int nodeFreeMem = MathUtil.getIntegerVal(tmpMap.getProp(MEMORY_FREE_KEY));
            int nodeFreeCores = MathUtil.getIntegerVal(tmpMap.getProp(CORE_FREE_KEY));
            int nodeCores = MathUtil.getIntegerVal(tmpMap.getProp(CORE_TOTAL_KEY));
            int nodeMem = MathUtil.getIntegerVal(tmpMap.getProp(MEMORY_TOTAL_KEY));

            totalFreeMem += nodeFreeMem;
            totalFreeCore += nodeFreeCores;
            totalCore += nodeCores;
            totalMem += nodeMem;

            nmFree[index++] = nodeFreeMem;
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
            throw new RdosException(ErrorCode.INVALID_PARAMETERS, e);
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

        if(neededCores > (totalCore * capacity)){
            throw new RdosException("任务设置的core 大于 分配的最大的core");
        }

        if (neededCores > (totalFreeCore * capacity)){
            return false;
        }

        if(neededMem > (totalMem * capacity)){
            throw new RdosException("任务设置的MEM 大于 集群最大的MEM");
        }
        if (neededMem > (totalFreeMem * capacity)){
            return false;
        }

        for (int i = 1; i <= workerNum; i++) {
            if (!allocateResource(nmFree, workerMem)) {
                return false;
            }
        }

        for (int i = 1; i <= psNum; i++) {
            if (!allocateResource(nmFree, psMem)) {
                return false;
            }
        }

        return true;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

}
