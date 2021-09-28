/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.dtscript;


import com.dtstack.engine.base.resource.AbstractYarnResourceInfo;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.exception.ClientArgumentException;
import com.dtstack.engine.pluginapi.pojo.JudgeResult;
import com.dtstack.engine.dtscript.client.ClientArguments;
import com.google.common.collect.Lists;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.util.List;


/**
 * 用于存储从dt-yarn-shell上获取的资源信息
 * Date: 2018/9/14
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class DtScriptResourceInfo extends AbstractYarnResourceInfo {

    private YarnClient yarnClient;
    private String queueName;
    private Integer yarnAccepterTaskNumber;

    public DtScriptResourceInfo(YarnClient yarnClient, String queueName, Integer yarnAccepterTaskNumber) {
        this.yarnClient = yarnClient;
        this.queueName = queueName;
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        JudgeResult jr = getYarnSlots(yarnClient, queueName, yarnAccepterTaskNumber);
        if (!jr.available()) {
            return jr;
        }

        ClientArguments clientArguments;
        try {
            String[] args = DtScriptUtil.buildPythonArgs(jobClient);
            clientArguments = new ClientArguments(args);
        } catch (Exception e) {
            throw new ClientArgumentException(e);
        }

        int amCores = clientArguments.getAmCores();
        int amMem = clientArguments.getAmMem();

        int workerCores = clientArguments.getWorkerVcores();
        int workerMem = clientArguments.getWorkerMemory();
        int workerNum = clientArguments.getWorkerNum();

        return this.judgeResource(amCores, amMem, workerNum, workerCores, workerMem);
    }

    private JudgeResult judgeResource(int amCores, int amMem, int workerNum, int workerCores, int workerMem) {
        if (workerNum == 0 || workerMem == 0 || workerCores == 0) {
            return JudgeResult.limitError("Yarn task resource configuration error，" +
                    "instance：" + workerNum + ", coresPerInstance：" + workerCores + ", memPerInstance：" + workerMem);
        }

        List<InstanceInfo> instanceInfos = Lists.newArrayList(
                InstanceInfo.newRecord(1, amCores, amMem),
                InstanceInfo.newRecord(workerNum, workerCores, workerMem));
        return judgeYarnResource(instanceInfos);
    }

    public static DtScriptResourceInfoBuilder DtScriptResourceInfoBuilder() {
        return new DtScriptResourceInfoBuilder();
    }

    public static class DtScriptResourceInfoBuilder {
        private YarnClient yarnClient;
        private String queueName;
        private Integer yarnAccepterTaskNumber;

        public DtScriptResourceInfoBuilder withYarnClient(YarnClient yarnClient) {
            this.yarnClient = yarnClient;
            return this;
        }

        public DtScriptResourceInfoBuilder withQueueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public DtScriptResourceInfoBuilder withYarnAccepterTaskNumber(Integer yarnAccepterTaskNumber) {
            this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
            return this;
        }

        public DtScriptResourceInfo build() {
            return new DtScriptResourceInfo(yarnClient, queueName, yarnAccepterTaskNumber);
        }
    }
}
