/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.vo.QueueVO;
import com.dtstack.engine.master.vo.engine.EngineSupportVO;
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
    public List<QueueVO> getQueue(@RequestParam("engineId") Long engineId) {
        return engineService.getQueue(engineId);
    }

    @RequestMapping(value="/listSupportEngine", method = {RequestMethod.POST})
    public List<EngineSupportVO> listSupportEngine(@RequestParam("tenantId") Long dtUicTenantId) {
        return engineService.listSupportEngine(dtUicTenantId,false);
    }

    @RequestMapping(value="/listSupportEngineWithCommon", method = {RequestMethod.POST})
    public List<EngineSupportVO> listSupportEngineWithCommon(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("needCommon")Boolean needCommon) {
        return engineService.listSupportEngine(dtUicTenantId, Boolean.TRUE.equals(needCommon));
    }

}
