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

package com.dtstack.taier.script.api;


import com.dtstack.taier.script.common.ContainerStatus;
import com.dtstack.taier.script.common.LocalRemotePath;
import com.dtstack.taier.script.common.Message;
import com.dtstack.taier.script.container.ScriptContainerId;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public interface ApplicationContext {

  ApplicationId getApplicationId();

  int getWorkerNum();

  int getPsNum();

  List<Container> getWorkerContainers();

  List<Container> getPsContainers();

  ContainerStatus getContainerStatus(ScriptContainerId containerId);

  List<LocalRemotePath> getInputs(ScriptContainerId containerId);

  List<InputSplit> getStreamInputs(ScriptContainerId containerId);

  List<LocalRemotePath> getOutputs();

  LinkedBlockingQueue<Message> getMessageQueue();

  String getTensorBoardUrl();

  Map<ScriptContainerId, String> getReporterProgress();

  Map<ScriptContainerId, String> getContainersAppStartTime();

  Map<ScriptContainerId, String> getContainersAppFinishTime();

  Map<ScriptContainerId, String> getMapedTaskId();

  Map<ScriptContainerId, ConcurrentHashMap<String, LinkedBlockingDeque<Object>>> getContainersCpuMetrics();

  int getSavingModelStatus();

  int getSavingModelTotalNum();

  Boolean getStartSavingStatus();

  void startSavingModelStatus(Boolean flag);

  Boolean getLastSavingStatus();

  List<Long> getModelSavingList();

}
