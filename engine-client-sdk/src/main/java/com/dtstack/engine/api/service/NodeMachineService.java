package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.NodeMachine;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface NodeMachineService extends DtInsightServer {

    @Deprecated
    @RequestLine("POST /node/nodeMachine/listByAppType")
    ApiResponse<List<NodeMachine>> listByAppType(@Param("appType") String appType);

    @Deprecated
    @RequestLine("POST /node/nodeMachine/getByAppTypeAndMachineType")
    ApiResponse<NodeMachine> getByAppTypeAndMachineType(@Param("appType") String appType, @Param("machineType") int machineType);
}
