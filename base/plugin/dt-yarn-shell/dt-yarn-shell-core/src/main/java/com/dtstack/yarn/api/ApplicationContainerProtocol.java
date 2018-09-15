package com.dtstack.yarn.api;

import com.dtstack.yarn.common.HeartbeatRequest;
import com.dtstack.yarn.common.HeartbeatResponse;
import com.dtstack.yarn.common.LocalRemotePath;
import com.dtstack.yarn.container.DtContainerId;
import org.apache.hadoop.ipc.VersionedProtocol;

public interface ApplicationContainerProtocol extends VersionedProtocol {

  long versionID = 1L;

  HeartbeatResponse heartbeat(DtContainerId containerId, HeartbeatRequest heartbeatRequest);

  LocalRemotePath[] getOutputLocation();

  LocalRemotePath[] getInputSplit(DtContainerId containerId);

}
