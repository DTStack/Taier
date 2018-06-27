package com.dtstack.learning.api;

import com.dtstack.learning.common.InputInfo;
import com.dtstack.learning.common.Message;
import com.dtstack.learning.common.OutputInfo;
import com.dtstack.learning.common.XLearningContainerStatus;
import com.dtstack.learning.container.XLearningContainerId;
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

  XLearningContainerStatus getContainerStatus(XLearningContainerId containerId);

  List<InputInfo> getInputs(XLearningContainerId containerId);

  List<InputSplit> getStreamInputs(XLearningContainerId containerId);

  List<OutputInfo> getOutputs();

  LinkedBlockingQueue<Message> getMessageQueue();

  String getTensorBoardUrl();

  Map<XLearningContainerId, String> getReporterProgress();

  Map<XLearningContainerId, String> getContainersAppStartTime();

  Map<XLearningContainerId, String> getContainersAppFinishTime();

  Map<XLearningContainerId, String> getMapedTaskID();

  Map<XLearningContainerId, ConcurrentHashMap<String, LinkedBlockingDeque<Object>>> getContainersCpuMetrics();

  int getSavingModelStatus();

  int getSavingModelTotalNum();

  Boolean getStartSavingStatus();

  void startSavingModelStatus(Boolean flag);

  Boolean getLastSavingStatus();

  List<Long> getModelSavingList();

}
