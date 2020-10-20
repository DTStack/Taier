package com.dtstack.engine.master.controller;

import com.dtstack.engine.alert.client.AlertGateApiFacade;
import com.dtstack.engine.alert.domian.AlertEvent;
import com.dtstack.engine.alert.enums.AGgateType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.lang.data.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private AlertGateApiFacade alertGateApiFacade;

    @ApiOperation("同步发送告警")
    @PostMapping("/{gateType}/sync")
    public void sendSync(@RequestBody AlertEvent alertEvent, @PathVariable("gateType") String gateType) {
        AGgateType parse = AGgateType.parse(gateType);
        R r = alertGateApiFacade.sendSync(alertEvent, parse);
        if (r.isSuccess()) {
            return;
        }
        throw new RdosDefineException(r.getMessage());
    }

    @ApiOperation("异步发送告警")
    @PostMapping("/{gateType}/async")
    public void sendAsync(@RequestBody AlertEvent alertEvent, @PathVariable("gateType") String gateType) {
        AGgateType parse = AGgateType.parse(gateType);
        alertGateApiFacade.sendAsync(alertEvent, parse);
    }
}
