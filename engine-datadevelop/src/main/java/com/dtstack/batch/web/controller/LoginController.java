package com.dtstack.batch.web.controller;

import com.dtstack.batch.domain.User;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.vo.UserVO;
import com.dtstack.dtcenter.common.enums.ActionType;
import com.dtstack.dtcenter.common.enums.DataInsightAppType;
import com.dtstack.dtcenter.common.login.LoginSessionStore;
import com.dtstack.dtcenter.common.login.SessionUtil;
import com.dtstack.engine.api.vo.security.SecurityLogVO;
import com.dtstack.engine.master.impl.SecurityAuditService;
import com.dtstack.engine.master.router.util.CookieUtil;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(value = "登陆管理", tags = {"登陆管理"})
@RestController
@RequestMapping(value = "/api/rdos/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityAuditService securityAuditService;

    @PostMapping("out")
    @ApiOperation(value = "退出登陆")
    public R<Void> loginOut(HttpServletRequest request) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                String token = CookieUtil.getDtUicToken(request.getCookies());
                if (StringUtils.isBlank(token)) {
                    return null;
                }
                UserVO user = SessionUtil.getUser(token, UserVO.class);
                User user1 = userService.getUser(user.getId());
                SecurityLogVO logVO = new SecurityLogVO();
                logVO.setTenantId(user.getTenantId());
                logVO.setOperatorId(user1.getId());
                logVO.setOperator(user1.getUserName());
                logVO.setAppTag(DataInsightAppType.BATCH.name());
                logVO.setAction(ActionType.LOG_OUT.getTemplate());
                logVO.setOperation(String.valueOf(ActionType.LOG_OUT.getCode()));
                securityAuditService.addSecurityLog(logVO);
                LoginSessionStore.removeSession(token);
                return null;
            }
        }.execute();
    }
}
