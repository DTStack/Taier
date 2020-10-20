package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.SecurityLogParam;
import com.dtstack.engine.api.vo.security.ApiOperateTypeVO;
import com.dtstack.engine.api.vo.security.SecurityLogVO;
import com.dtstack.engine.master.impl.SecurityAuditService;
import com.dtstack.engine.master.utils.CacheUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/10/9 9:52 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Api(tags = "安全审计")
@RestController
@RequestMapping("/node/securityAudit")
public class SecurityAuditController {

    @Autowired
    private SecurityAuditService securityAuditService;


    @ApiOperation("添加安全日志接口免登陆，需将参数加密传输,用于替换console: /api/console/service/securityAudit/addSecurityLog")
    @PostMapping("/addSecurityLog")
    public void addSecurityLog(@RequestBody SecurityLogParam securityLogParam) {
        securityAuditService.addSecurityLog(securityLogParam.getSign());
    }


    @ApiOperation("getOperationList 获得列表 用于替换console: /api/console/service/securityAudit/getOperationList")
    @PostMapping("/getOperationList")
    public List<ApiOperateTypeVO> getOperationList(@RequestBody SecurityLogParam securityLogParam) {
        return securityAuditService.getOperationList(securityLogParam.getAppTag());
    }

    @ApiOperation("pageQuery 分页查询安全日志 用于替换console: /api/console/service/securityAudit/pageQuery")
    @PostMapping("/pageQuery")
    public PageResult<List<SecurityLogVO>> pageQuery(@RequestBody SecurityLogParam param, @CookieValue("dt_token") String token) {
        UserDTO user = CacheUtils.getUser(token);
        param.setTenantId(user.getTenantId());
        param.setRoot(user.getRootUser() == 1);

        return securityAuditService.pageQuery(param.getAppTag(), param.getTenantId(), param.getStartTime(),
                param.getEndTime(), param.getOperator(), param.getCurrentPage(), param.getPageSize(), param.getOprtation(), param.getOperationObject(),
                param.getRoot());
    }


}
