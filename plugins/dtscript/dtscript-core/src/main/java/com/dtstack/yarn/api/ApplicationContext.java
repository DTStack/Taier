package com.dtstack.yarn.api;


import com.dtstack.yarn.common.DtContainerStatus;
import com.dtstack.yarn.common.LocalRemotePath;
import com.dtstack.yarn.common.Message;
import com.dtstack.yarn.container.DtContainerId;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public interface ApplicationContext {

  ApplicationId getApplicationID();

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

  Map<DtContainerId, String> getMapedTaskID();

  Map<DtContainerId, ConcurrentHashMap<String, LinkedBlockingDeque<Object>>> getContainersCpuMetrics();

  int getSavingModelStatus();

  int getSavingModelTotalNum();

  Boolean getStartSavingStatus();

  void startSavingModelStatus(Boolean flag);

  Boolean getLastSavingStatus();

  List<Long> getModelSavingList();

}
