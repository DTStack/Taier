package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.domain.NodeMachine;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NodeMachineService {

    @Deprecated
    public List<NodeMachine> listByAppType(@Param("appType") String appType){
        return Collections.emptyList();
    }

    @Deprecated
    public NodeMachine getByAppTypeAndMachineType(@Param("appType") String appType, @Param("machineType") int machineType){
        return new NodeMachine();
    }
}
