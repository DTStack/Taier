package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.NodeMachine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/nodeMachine")
@Api(value = "/node/nodeMachine", tags = {"节点接口"})
@Deprecated
public class NodeMachineController {

    @RequestMapping(value="/listByAppType", method = {RequestMethod.POST})
    @Deprecated
    public List<NodeMachine> listByAppType(@RequestParam("appType") String appType) {
        return null;
    }

    @RequestMapping(value="/getByAppTypeAndMachineType", method = {RequestMethod.POST})
    @Deprecated
    public NodeMachine getByAppTypeAndMachineType(@RequestParam("appType") String appType, @RequestParam("machineType") int machineType) {
        return null;
    }
}
