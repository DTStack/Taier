package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.NodeMachine;

import java.util.List;

public interface NodeMachineService {
    @Deprecated
    public List<NodeMachine> listByAppType( String appType);

    @Deprecated
    public NodeMachine getByAppTypeAndMachineType( String appType,  int machineType);
}
