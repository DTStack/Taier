package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.NodeMachine;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NodeMachineService {

    @Deprecated
    public List<NodeMachine> listByAppType( String appType){
        return Collections.emptyList();
    }

    @Deprecated
    public NodeMachine getByAppTypeAndMachineType( String appType,  int machineType){
        return new NodeMachine();
    }
}
