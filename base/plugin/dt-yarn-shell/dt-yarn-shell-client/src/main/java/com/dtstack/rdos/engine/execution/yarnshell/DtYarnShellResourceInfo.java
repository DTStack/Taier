package com.dtstack.rdos.engine.execution.yarnshell;


import com.dtstack.rdos.commom.exception.ErrorCode;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.resource.AbstractYarnResourceInfo;
import com.dtstack.yarn.client.ClientArguments;


/**
 * 用于存储从dt-yarn-shell上获取的资源信息
 * Date: 2018/9/14
 * Company: www.dtstack.com
 * @author jingzhen
 */
public class DtYarnShellResourceInfo extends AbstractYarnResourceInfo {


    @Override
    public boolean judgeSlots(JobClient jobClient) {
        ClientArguments clientArguments;
        try {
            String[] args = DtYarnShellUtil.buildPythonArgs(jobClient);
            clientArguments = new ClientArguments(args);
        } catch (Exception e) {
            throw new RdosException(ErrorCode.INVALID_PARAMETERS, e);
        }

        int amCores = clientArguments.getAmCores();
        int amMem = clientArguments.getAmMem();

        int workerCores = clientArguments.getWorkerVCores();
        int workerMem = clientArguments.getWorkerMemory();
        int workerNum = clientArguments.getWorkerNum();

        return this.judgeResource(amCores, amMem, workerNum, workerCores, workerMem);
    }

    private boolean judgeResource(int amCores, int amMem, int workerNum, int workerCores, int workerMem) {
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

        return true;
    }
}
