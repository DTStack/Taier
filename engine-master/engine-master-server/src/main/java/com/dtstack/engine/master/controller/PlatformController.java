package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.impl.PlatformService;
import com.dtstack.engine.master.router.DtRequestParam;
import com.dtstack.engine.master.vo.PlatformEventVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuebai
 * @date 2020-08-13
 */
@RestController
@RequestMapping("/node/platform")
@Api(value = "/node/platform", tags = {"uic回调接口"})
public class PlatformController {

    @Autowired
    private PlatformService platformService;

    @RequestMapping(value = "/callBack", method = {RequestMethod.POST})
    public void callBack(@DtRequestParam("eventCode") String eventCode, @DtRequestParam("tenantId") Long tenantId) {
        PlatformEventVO vo = new PlatformEventVO();
        vo.setEventCode(eventCode);
        vo.setTenantId(tenantId);
        platformService.callback(vo);
    }

}
