package com.dtstack.rdos.engine.service;

import com.dtstack.rdos.common.annotation.Param;
import com.dtstack.rdos.engine.service.db.dao.RdosNodeMachineDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosNodeMachine;

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
