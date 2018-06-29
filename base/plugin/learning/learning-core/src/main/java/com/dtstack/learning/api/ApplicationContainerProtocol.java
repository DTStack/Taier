package com.dtstack.learning.api;

import com.dtstack.learning.common.InputInfo;
import com.dtstack.learning.container.LearningContainerId;
import com.dtstack.learning.common.HeartbeatRequest;
import com.dtstack.learning.common.HeartbeatResponse;
import com.dtstack.learning.common.OutputInfo;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.mapred.InputSplit;

public interface ApplicationContainerProtocol extends VersionedProtocol {

  public static final long versionID = 1L;

  void reportReservedPort(String host, int port, String role, int index);

  void reportLightGbmIpPort(LearningContainerId containerId, String lightGbmIpPort);

  String getLightGbmIpPortStr();

  String getClusterDef();

  HeartbeatResponse heartbeat(LearningContainerId containerId, HeartbeatRequest heartbeatRequest);

  InputInfo[] getInputSplit(LearningContainerId containerId);

  InputSplit[] getStreamInputSplit(LearningContainerId containerId);

  OutputInfo[] getOutputLocation();

  void reportTensorBoardURL(String url);

  void reportMapedTaskID(LearningContainerId containerId, String taskId);

  void reportCpuMetrics(LearningContainerId containerId, String cpuMetrics);

  Long interResultTimeStamp();

}
