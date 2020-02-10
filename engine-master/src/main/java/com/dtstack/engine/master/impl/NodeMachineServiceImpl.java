package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.dao.RdosNodeMachineDAO;
import com.dtstack.engine.domain.RdosNodeMachine;

import java.util.List;

public class NodeMachineServiceImpl {

    private RdosNodeMachineDAO rdosNodeMachineDAO = new RdosNodeMachineDAO();

    public List<RdosNodeMachine> listByAppType(@Param("appType") String appType){
        return rdosNodeMachineDAO.listByAppType(appType);
    }

    public RdosNodeMachine getByAppTypeAndMachineType(@Param("appType") String appType, @Param("machineType") int machineType){
        return rdosNodeMachineDAO.getByAppTypeAndMachineType(appType,machineType);
    }
}
