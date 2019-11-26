package com.dtstack.engine.dtscript.api;

import com.dtstack.engine.dtscript.common.DTScriptConstant;
import com.dtstack.engine.dtscript.container.DtContainerId;
import com.dtstack.engine.dtscript.common.HeartbeatRequest;
import com.dtstack.engine.dtscript.common.HeartbeatResponse;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.security.KerberosInfo;

@KerberosInfo(serverPrincipal= DTScriptConstant.RPC_SERVER_PRINCIPAL)
public interface ApplicationContainerProtocol extends VersionedProtocol {

  long versionID = 1090L;

  HeartbeatResponse heartbeat(DtContainerId containerId, HeartbeatRequest heartbeatRequest);

  LocalRemotePath[] getOutputLocation();

  LocalRemotePath[] getInputSplit(DtContainerId containerId);

}
