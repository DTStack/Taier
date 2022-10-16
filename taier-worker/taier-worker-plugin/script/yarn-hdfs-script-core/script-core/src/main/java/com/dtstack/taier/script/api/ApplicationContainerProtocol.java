package com.dtstack.taier.script.api;

import com.dtstack.taier.script.common.HeartbeatRequest;
import com.dtstack.taier.script.common.HeartbeatResponse;
import com.dtstack.taier.script.common.LocalRemotePath;
import com.dtstack.taier.script.container.ScriptContainerId;
import org.apache.hadoop.ipc.VersionedProtocol;

public interface ApplicationContainerProtocol extends VersionedProtocol {

  long versionID = 1090L;

  HeartbeatResponse heartbeat(ScriptContainerId containerId, HeartbeatRequest heartbeatRequest);

  LocalRemotePath[] getOutputLocation();

  LocalRemotePath[] getInputSplit(ScriptContainerId containerId);

}
