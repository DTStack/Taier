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

package com.dtstack.taier.script.am;

import com.dtstack.taier.script.api.ApplicationContext;
import com.dtstack.taier.script.common.ContainerStatus;
import com.dtstack.taier.script.common.LocalRemotePath;
import com.dtstack.taier.script.common.Message;
import com.dtstack.taier.script.container.ScriptContainerId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;


public class RunningAppContext implements ApplicationContext {

    private static final Log LOG = LogFactory.getLog(RunningAppContext.class);

    final ApplicationMaster appMaster;

    public RunningAppContext(ApplicationMaster appMaster) {
        this.appMaster = appMaster;
    }

    @Override
    public ApplicationId getApplicationId() {
        return appMaster.applicationAttemptId.getApplicationId();
    }

    @Override
    public int getWorkerNum() {
        return 1;
    }

    @Override
    public int getPsNum() {
        return 0;
    }

    @Override
    public List<Container> getWorkerContainers() {
        return null;
    }

    @Override
    public List<Container> getPsContainers() {
        return null;
    }

    @Override
    public ContainerStatus getContainerStatus(ScriptContainerId containerId) {
        return null;
    }

    @Override
    public LinkedBlockingQueue<Message> getMessageQueue() {
        return null;
    }

    @Override
    public List<LocalRemotePath> getInputs(ScriptContainerId containerId) {
        return null;
    }

    @Override
    public List<InputSplit> getStreamInputs(ScriptContainerId containerId) {
        return null;
    }

    @Override
    public List<LocalRemotePath> getOutputs() {
        return null;
    }

    @Override
    public String getTensorBoardUrl() {
        //return appMaster.containerListener.getTensorboardUrl();
        return null;
    }

    @Override
    public Map<ScriptContainerId, String> getReporterProgress() {
        //return appMaster.containerListener.getReporterProgress();
        return null;
    }

    @Override
    public Map<ScriptContainerId, String> getContainersAppStartTime() {
        //return appMaster.containerListener.getContainersAppStartTime();
        return null;
    }

    @Override
    public Map<ScriptContainerId, String> getContainersAppFinishTime() {
        //return appMaster.containerListener.getContainersAppFinishTime();
        return null;
    }

    @Override
    public Map<ScriptContainerId, String> getMapedTaskId() {
        //return appMaster.containerListener.getMapedTaskID();
        return null;
    }

    @Override
    public Map<ScriptContainerId, ConcurrentHashMap<String, LinkedBlockingDeque<Object>>> getContainersCpuMetrics() {
        //return appMaster.containerListener.getContainersCpuMetrics();
        return null;
    }

    @Override
    public int getSavingModelStatus() {
        return -1;
        //return appMaster.containerListener.interResultCompletedNum(appMaster.containerListener.interResultTimeStamp());
    }

    @Override
    public Boolean getStartSavingStatus() {
        //return appMaster.startSavingModel;
        return null;
    }

    @Override
    public int getSavingModelTotalNum() {
        //return appMaster.containerListener.getInnerSavingContainerNum();
        return -1;
    }

    @Override
    public void startSavingModelStatus(Boolean flag) {
        LOG.info("current savingModelStatus is " + flag);
        //appMaster.startSavingModel = flag;
    }

    @Override
    public Boolean getLastSavingStatus() {
        //return appMaster.lastSavingStatus;
        return false;
    }

    @Override
    public List<Long> getModelSavingList() {
        //return appMaster.savingModelList;
        return null;
    }

}
