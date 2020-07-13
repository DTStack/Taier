package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.master.impl.EngineService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/engine")
@Api(value = "/node/engine", tags = {"引擎接口"})
public class EngineController {

    @Autowired
    private EngineService engineService;

    @RequestMapping(value="/getQueue", method = {RequestMethod.POST})
    List<QueueVO> getQueue(@RequestParam("engineId") Long engineId) {
        return engineService.getQueue(engineId);
    }

    @RequestMapping(value="/listSupportEngine", method = {RequestMethod.POST})
    public String listSupportEngine(@RequestParam("tenantId") Long dtUicTenantId) {
        return engineService.listSupportEngine(dtUicTenantId);
    }
}
