package com.dtstack.engine.dtscript.api;

import com.dtstack.engine.dtscript.common.HeartbeatRequest;
import com.dtstack.engine.dtscript.common.HeartbeatResponse;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import com.dtstack.engine.dtscript.container.DtContainerId;
import org.apache.hadoop.ipc.VersionedProtocol;

public interface ApplicationContainerProtocol extends VersionedProtocol {

  long versionID = 1090L;

  HeartbeatResponse heartbeat(DtContainerId containerId, HeartbeatRequest heartbeatRequest);

  LocalRemotePath[] getOutputLocation();

  LocalRemotePath[] getInputSplit(DtContainerId containerId);

}
