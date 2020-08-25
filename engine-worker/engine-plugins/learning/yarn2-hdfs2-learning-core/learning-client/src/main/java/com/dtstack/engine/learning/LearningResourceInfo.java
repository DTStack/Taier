package com.dtstack.engine.learning;

import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.learning.client.ClientArguments;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.AbstractYarnResourceInfo;
import com.google.common.collect.Lists;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.util.List;

/**
 * 用于存储从xlearning上获取的资源信息
 * Date: 2018/6/27
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class LearningResourceInfo extends AbstractYarnResourceInfo {

    public static final String DT_APP_YARN_ACCEPTER_TASK_NUMBER = "yarnAccepterTaskNumber";

    private YarnClient yarnClient;
    private String queueName;
    private Integer yarnAccepterTaskNumber;

    public LearningResourceInfo(YarnClient yarnClient, String queueName, Integer yarnAccepterTaskNumber) {
        this.yarnClient = yarnClient;
        this.queueName = queueName;
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        JudgeResult jr = getYarnSlots(yarnClient, queueName, yarnAccepterTaskNumber);
        if (!jr.getResult()) {
            return jr;
        }

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

    private JudgeResult judgeResource(int amCores, int amMem, int workerNum, int workerCores, int workerMem, int psNum, int psCores, int psMem) {
        if (workerNum == 0 || workerMem == 0 || workerCores == 0) {
            throw new LimitResourceException("Yarn task resource configuration error，" +
                    "instance：" + workerNum + ", coresPerInstance：" + workerCores + ", memPerInstance：" + workerMem);
        }

        List<InstanceInfo> instanceInfos = Lists.newArrayList(
                InstanceInfo.newRecord(1, amCores, amMem),
                InstanceInfo.newRecord(workerNum, workerCores, workerMem));
        if (psNum > 0) {
            instanceInfos.add(InstanceInfo.newRecord(psNum, psCores, psMem));
        }
        return judgeYarnResource(instanceInfos);
    }

    public static LearningResourceInfoBuilder LearningResourceInfoBuilder() {
        return new LearningResourceInfoBuilder();
    }

    public static class LearningResourceInfoBuilder {
        private YarnClient yarnClient;
        private String queueName;
        private Integer yarnAccepterTaskNumber;

        public LearningResourceInfoBuilder withYarnClient(YarnClient yarnClient) {
            this.yarnClient = yarnClient;
            return this;
        }

        public LearningResourceInfoBuilder withQueueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public LearningResourceInfoBuilder withYarnAccepterTaskNumber(Integer yarnAccepterTaskNumber) {
            this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
            return this;
        }

        public LearningResourceInfo build() {
            return new LearningResourceInfo(yarnClient, queueName, yarnAccepterTaskNumber);
        }
    }
}
