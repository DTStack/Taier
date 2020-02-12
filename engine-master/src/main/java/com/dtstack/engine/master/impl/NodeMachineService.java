package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.dao.NodeMachineDao;
import com.dtstack.engine.domain.NodeMachine;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeMachineService {

    private NodeMachineDao nodeMachineDao = new NodeMachineDao();

    public List<NodeMachine> listByAppType(@Param("appType") String appType){
        return nodeMachineDao.listByAppType(appType);
    }

    public NodeMachine getByAppTypeAndMachineType(@Param("appType") String appType, @Param("machineType") int machineType){
        return nodeMachineDao.getByAppTypeAndMachineType(appType,machineType);
    }
}
