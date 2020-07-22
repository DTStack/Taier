package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.NodeMachine;
import com.dtstack.engine.api.service.NodeMachineService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/nodeMachine")
@Api(value = "/node/nodeMachine", tags = {"节点接口"})
@Deprecated
public class NodeMachineController implements NodeMachineService {

    @RequestMapping(value="/listByAppType", method = {RequestMethod.POST})
    @Deprecated
    public List<NodeMachine> listByAppType(@DtRequestParam("appType") String appType) {
        return null;
    }

    @RequestMapping(value="/getByAppTypeAndMachineType", method = {RequestMethod.POST})
    @Deprecated
    public NodeMachine getByAppTypeAndMachineType(@DtRequestParam("appType") String appType, @DtRequestParam("machineType") int machineType) {
        return null;
    }
}
