package com.dtstack.engine.learning;

import com.dtstack.learning.client.ClientArguments;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.AbstractYarnResourceInfo;
import com.google.common.collect.Lists;

import java.util.List;

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
            throw new ClientArgumentException(e);
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
            throw new LimitResourceException("Yarn task resource configuration error，" +
                    "instance：" + workerNum + ", coresPerInstance：" + workerCores + ", memPerInstance：" + workerMem);
        }
        if (totalFreeCore == 0 || totalFreeMem == 0) {
            return false;
        }

        List<InstanceInfo> instanceInfos = Lists.newArrayList(
                InstanceInfo.newRecord(1, amCores, amMem),
                InstanceInfo.newRecord(workerNum, workerCores, workerMem));
        if (psNum > 0) {
            instanceInfos.add(InstanceInfo.newRecord(psNum, psCores, psMem));
        }
        return judgeYarnResource(instanceInfos);
    }
}
