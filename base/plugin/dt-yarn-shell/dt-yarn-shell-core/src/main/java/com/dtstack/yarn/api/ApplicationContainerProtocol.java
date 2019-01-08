package com.dtstack.yarn.api;

import com.dtstack.yarn.common.DTYarnShellConstant;
import com.dtstack.yarn.common.HeartbeatRequest;
import com.dtstack.yarn.common.HeartbeatResponse;
import com.dtstack.yarn.common.LocalRemotePath;
import com.dtstack.yarn.container.DtContainerId;
import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.security.KerberosInfo;

@KerberosInfo(serverPrincipal= DTYarnShellConstant.RPC_SERVER_PRINCIPAL)
@ProtocolInfo(protocolName = "com.dtstack.yarn.api.ApplicationContainerProtocol",
        protocolVersion = 1090)
public interface ApplicationContainerProtocol extends VersionedProtocol {

  long versionID = 1090L;

  HeartbeatResponse heartbeat(DtContainerId containerId, HeartbeatRequest heartbeatRequest);

  LocalRemotePath[] getOutputLocation();

  LocalRemotePath[] getInputSplit(DtContainerId containerId);

}
