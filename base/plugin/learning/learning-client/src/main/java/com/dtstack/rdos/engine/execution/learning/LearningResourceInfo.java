package com.dtstack.rdos.engine.execution.learning;

import com.dtstack.learning.client.ClientArguments;
import com.dtstack.rdos.commom.exception.ErrorCode;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.resource.AbstractYarnResourceInfo;

/**
 * 用于存储从xlearning上获取的资源信息
 * Date: 2018/6/27
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class LearningResourceInfo extends AbstractYarnResourceInfo {

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        ClientArguments clientArguments;
        try {
            String[] args = LearningUtil.buildPythonArgs(jobClient);
            clientArguments = new ClientArguments(args);
        } catch (Exception e) {
            throw new RdosException(ErrorCode.INVALID_PARAMETERS, e);
        }

        int amCores = clientArguments.getAmCores();
        int amMem = clientArguments.getAmMem();

        int workerCores = clientArguments.getWorkerVCores();
        int workerMem = clientArguments.getWorkerMemory();
        int workerNum = clientArguments.getWorkerNum();

        int psCores = clientArguments.getPsVCores();
        int psMem = clientArguments.getPsMemory();
        int psNum = clientArguments.getPsNum();

        return this.judgeResource(amCores, amMem, workerNum, workerCores, workerMem, psNum, psCores, psMem);
    }

    private boolean judgeResource(int amCores, int amMem, int workerNum, int workerCores, int workerMem, int psNum, int psCores, int psMem) {
        if (workerNum == 0 || workerMem == 0 || workerCores == 0) {
            throw new RdosException(LIMIT_RESOURCE_ERROR + "Yarn task resource configuration error，" +
                    "instance：" + workerNum + ", coresPerInstance：" + workerCores + ", memPerInstance：" + workerMem);
        }
        if (totalFreeCore == 0 || totalFreeMem == 0) {
            return false;
        }

        //am
        if (!judgeYarnResource(1, amCores, amMem)) {
            return false;
        }
        //work
        if (!judgeYarnResource(workerNum, workerCores, workerMem)) {
            return false;
        }
        //ps
        if (!judgeYarnResource(psNum, psCores, psMem)) {
            return false;
        }
        return true;
    }
}
