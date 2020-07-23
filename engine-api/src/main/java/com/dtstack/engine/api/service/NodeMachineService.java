package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.NodeMachine;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface NodeMachineService extends DtInsightServer {

    @Deprecated
    @RequestLine("POST /node/nodeMachine/listByAppType")
    List<NodeMachine> listByAppType(String appType);

    @Deprecated
    @RequestLine("POST /node/nodeMachine/getByAppTypeAndMachineType")
    NodeMachine getByAppTypeAndMachineType(String appType, int machineType);
}
