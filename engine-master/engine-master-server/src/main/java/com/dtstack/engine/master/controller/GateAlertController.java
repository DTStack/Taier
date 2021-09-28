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

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date: 2020/8/9
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Api(tags = "发送告警")
@RestController
@RequestMapping("/node/gate/alert")
public class GateAlertController {
//    @Autowired
//    private AlertGateApiFacade alertGateApiFacade;
//
//    @ApiOperation("同步发送告警")
//    @PostMapping("/{gateType}/sync")
//    public void sendSync(@RequestBody AlertEvent alertEvent, @PathVariable("gateType") String gateType) {
//        AGgateType parse = AGgateType.parse(gateType);
//        R r = alertGateApiFacade.sendSync(alertEvent, parse);
//        if (r.isSuccess()) {
//            return;
//        }
//        throw new RdosDefineException(r.getMessage());
//    }
//
//    @ApiOperation("异步发送告警")
//    @PostMapping("/{gateType}/async")
//    public void sendAsync(@RequestBody AlertEvent alertEvent, @PathVariable("gateType") String gateType) {
//        AGgateType parse = AGgateType.parse(gateType);
//        alertGateApiFacade.sendAsync(alertEvent, parse);
//    }
}
