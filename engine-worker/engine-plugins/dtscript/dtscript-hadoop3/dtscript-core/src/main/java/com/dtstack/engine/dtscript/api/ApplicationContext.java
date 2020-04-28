package com.dtstack.engine.dtscript.api;


import com.dtstack.engine.dtscript.container.DtContainerId;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import com.dtstack.engine.dtscript.common.Message;
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

  DtContainerStatus getContainerStatus(DtContainerId containerId);

  List<LocalRemotePath> getInputs(DtContainerId containerId);

  List<InputSplit> getStreamInputs(DtContainerId containerId);

  List<LocalRemotePath> getOutputs();

  LinkedBlockingQueue<Message> getMessageQueue();

  String getTensorBoardUrl();

  Map<DtContainerId, String> getReporterProgress();

  Map<DtContainerId, String> getContainersAppStartTime();

  Map<DtContainerId, String> getContainersAppFinishTime();

  Map<DtContainerId, String> getMapedTaskId();

  Map<DtContainerId, ConcurrentHashMap<String, LinkedBlockingDeque<Object>>> getContainersCpuMetrics();

  int getSavingModelStatus();

  int getSavingModelTotalNum();

  Boolean getStartSavingStatus();

  void startSavingModelStatus(Boolean flag);

  Boolean getLastSavingStatus();

  List<Long> getModelSavingList();

}
