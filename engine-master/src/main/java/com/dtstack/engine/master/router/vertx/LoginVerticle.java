package com.dtstack.engine.master.router.vertx;


import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dto.UserDTO;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.callback.ApiCallback;
import com.dtstack.engine.master.router.callback.ApiCallbackMethod;
import com.dtstack.engine.master.router.callback.ApiResult;
import com.dtstack.engine.master.router.login.LoginService;
import com.dtstack.engine.master.router.login.LoginSessionStore;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.router.util.CookieUtil;
import com.dtstack.engine.master.router.util.ResponseUtil;
import com.google.common.collect.Lists;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.regex.Pattern;


/**
 * @author toutian
 */
public class LoginVerticle {

    private static Logger logger = LoggerFactory.getLogger(LoginVerticle.class);

    private LoginService loginService;

    private ApplicationContext context;

    private static List<String> freeLoginPathList = Lists.newArrayList("clusterInfo","clusterExtInfo", "pluginInfo", "hiveInfo","hiveServerInfo","hadoopInfo",
            "carbonInfo","addSecurityLog", "engine/listSupportEngine", "component/listConfigOfComponents", "taskParam", "clusterSftpDir", "impalaInfo", "sftpInfo" +
                    "migrate", "listByAppType", "getByAppTypeAndMachineType", "action/start", "action/stop", "action/entitys", "action/log");
    private static List<Pattern> freeLoginRegex = Lists.newArrayList(Pattern.compile("node/streamTask/.+"),Pattern.compile("node/action/.+"));

    public LoginVerticle() {
    }

    public LoginVerticle(ApplicationContext context) {
        if (context != null) {
            this.context = context;
            EnvironmentContext env = this.context.getBean(EnvironmentContext.class);
            if (StringUtils.isBlank(env.getDtUicUrl())) {
                throw new RdosDefineException("DtUicUrl 为空", ErrorCode.CONF_ERROR);
            }
            LoginSessionStore.setUrl(env.getDtUicUrl());
            loginService = (LoginService) this.context.getBean("loginService");
        }
    }

    public void handleLogin(RoutingContext routingContext) {
        try {
            logger.debug("{}:{}", routingContext.request().path(), routingContext.getBodyAsString());
            if (!freeLogin(routingContext.request().path())) {
                String token = CookieUtil.getDtUicToken(routingContext);
                if (StringUtils.isBlank(token)) {
                    throw new RdosDefineException(ErrorCode.TOKEN_IS_NULL);
                }
                if(CookieUtil.isNeedToLogin(routingContext)){
                    throw new RdosDefineException(ErrorCode.NOT_LOGIN);
                }
                LoginSessionStore.createSession(token, UserDTO.class, dtUicUser -> {
                    //获取到dtuic的数据后的处理方式
                    loginService.login(dtUicUser, token, userVO -> {
                        if (userVO == null) {
                            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
                        }
                        if (userVO.getRootUser() != 1) {
                            throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
                        }
                        SessionUtil.setUser(token, userVO);
                    });
                });
            }
            routingContext.next();
        } catch (Throwable t) {
            logger.error("handleLogin error:", t);
            if (t instanceof RdosDefineException) {
                if (((RdosDefineException) t).getErrorCode().getCode() == ErrorCode.PERMISSION_LIMIT.getCode()) {
                    String msg = ApiResult.createErrorResultJsonStr(ErrorCode.PERMISSION_LIMIT.getCode(), "", ErrorCode.PERMISSION_LIMIT.getDescription());
                    ResponseUtil.res200(routingContext, msg);
                    return;
                }
            }
            ResponseUtil.redirect(routingContext);
        }
    }

    public void loginOut(RoutingContext routingContext) {
        ApiCallbackMethod.doCallback(new ApiCallback() {
            @Override
            public Object execute() throws Exception {
                try {
                    String token = CookieUtil.getDtUicToken(routingContext);
                    LoginSessionStore.removeSession(token);
                    logger.info("loginOut session remove");
                } catch (Exception e) {
                    logger.error("loginOut fail:", e);
                }
                return null;
            }
        }, routingContext);
    }

    /**
     * 判断免登陆接口
     *
     * @param path
     * @return
     */
    public boolean freeLogin(String path) {
        for (String freePath : freeLoginPathList) {
            if (path.endsWith(freePath)) {
                return true;
            }
        }

        for (Pattern freePathPattern : freeLoginRegex) {
            if (freePathPattern.matcher(path).find()){
                return true;
            }
        }

        return false;
    }

}
