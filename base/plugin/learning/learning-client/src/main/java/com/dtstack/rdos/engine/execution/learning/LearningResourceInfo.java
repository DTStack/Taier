package com.dtstack.rdos.engine.execution.learning;

import com.dtstack.learning.client.ClientArguments;
import com.dtstack.rdos.commom.exception.ErrorCode;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;

/**
 * 用于存储从xlearning上获取的资源信息
 * Date: 2018/6/27
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class LearningResourceInfo extends EngineResourceInfo {

    @Override
    public boolean judgeSlots(JobClient jobClient) {
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

        int psCores = clientArguments.getPsVCores();
        int psMem = clientArguments.getPsMemory();
        int psNum = clientArguments.getPsNum();

        return this.judgeResource(workerNum, workerCores, workerMem, psNum, psCores, psMem);
    }

    public boolean judgeResource(int workerNum, int workerCores, int workerMem, int psNum, int psCores, int psMem) {
        if (workerNum == 0 || workerMem == 0 || workerCores == 0) {
            throw new RdosException(LIMIT_ERROR + "Yarn任务资源配置错误，instance：" + workerNum + ", coresPerInstance：" + workerCores + ", memPerInstance：" + workerMem);
        }
        calc();
        if (totalFreeCore == 0 || totalFreeMem == 0) {
            return false;
        }
        int instanceTotalCore = workerNum * workerCores + psNum * psCores;
        if (!judgeCores(1, instanceTotalCore, totalFreeCore, totalCore)) {
            return false;
        }
        if (!judgeMem(workerNum, workerMem, totalFreeMem, totalMem)) {
            return false;
        }
        if (!judgeMem(psNum, psMem, totalFreeMem, totalMem)) {
            return false;
        }
        return true;
    }
}
