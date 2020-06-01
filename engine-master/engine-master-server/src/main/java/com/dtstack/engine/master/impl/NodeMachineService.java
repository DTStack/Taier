package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.NodeMachine;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NodeMachineService implements com.dtstack.engine.api.service.NodeMachineService {

    @Deprecated
    public List<NodeMachine> listByAppType(@Param("appType") String appType){
        return Collections.emptyList();
    }

    @Deprecated
    public NodeMachine getByAppTypeAndMachineType(@Param("appType") String appType, @Param("machineType") int machineType){
        return new NodeMachine();
    }
}
