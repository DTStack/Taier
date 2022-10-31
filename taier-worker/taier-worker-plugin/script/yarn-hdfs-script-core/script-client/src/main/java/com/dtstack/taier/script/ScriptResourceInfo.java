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

package com.dtstack.taier.script;

import com.dtstack.taier.base.resource.AbstractYarnResourceInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.google.common.collect.Lists;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.util.List;


/**
 * 用于存储从dt-yarn-shell上获取的资源信息
 */
public class ScriptResourceInfo extends AbstractYarnResourceInfo {

    private YarnClient yarnClient;
    private String queueName;
    private Integer yarnAccepterTaskNumber;
    private ScriptConfiguration scriptConf;

    public ScriptResourceInfo(YarnClient yarnClient, String queueName, Integer yarnAccepterTaskNumber, ScriptConfiguration scriptConf) {
        this.yarnClient = yarnClient;
        this.queueName = queueName;
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
        this.scriptConf = scriptConf;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        JudgeResult jr = getYarnSlots(yarnClient, queueName, yarnAccepterTaskNumber);
        if (!jr.available()) {
            return jr;
        }

        int amCores = scriptConf.getInt(ScriptConfiguration.SCRIPT_AM_CORES, ScriptConfiguration.DEFAULT_SCRIPT_AM_CORES);
        int amMem = scriptConf.getInt(ScriptConfiguration.SCRIPT_AM_MEMORY, ScriptConfiguration.DEFAULT_SCRIPT_AM_MEMORY);

        int workerCores = scriptConf.getInt(ScriptConfiguration.SCRIPT_WORKER_CORES, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_CORES);
        int workerMem = scriptConf.getInt(ScriptConfiguration.SCRIPT_WORKER_MEMORY, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_MEMORY);
        int workerNum = scriptConf.getInt(ScriptConfiguration.SCRIPT_WORKER_NUM, ScriptConfiguration.DEFAULT_SCRIPT_WORKER_NUM);

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

    public static ScriptResourceInfoBuilder scriptResourceInfoBuilder() {
        return new ScriptResourceInfoBuilder();
    }

    public static class ScriptResourceInfoBuilder {
        private YarnClient yarnClient;
        private String queueName;
        private Integer yarnAccepterTaskNumber;
        private ScriptConfiguration scriptConf;

        public ScriptResourceInfoBuilder withYarnClient(YarnClient yarnClient) {
            this.yarnClient = yarnClient;
            return this;
        }

        public ScriptResourceInfoBuilder withQueueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public ScriptResourceInfoBuilder withYarnAccepterTaskNumber(Integer yarnAccepterTaskNumber) {
            this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
            return this;
        }

        public ScriptResourceInfoBuilder withScriptConf(ScriptConfiguration scriptConf){
            this.scriptConf = scriptConf;
            return this;
        }

        public ScriptResourceInfo build() {
            return new ScriptResourceInfo(yarnClient, queueName, yarnAccepterTaskNumber, scriptConf);
        }
    }
}
