package com.dtstack.engine.master.router.login;

import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/8/3
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            LOGGER.debug("{}:{}", request.getRequestURI(), request.getParameterMap());
            String token = CookieUtil.getDtUicToken(request.getCookies());
            if (StringUtils.isBlank(token)) {
                throw new RdosDefineException(ErrorCode.NOT_LOGIN);
            }
            LoginSessionStore.createSession(token, UserDTO.class, dtUicUser -> {
                //获取到dtuic的数据后的处理方式
                loginService.login(dtUicUser, token, userVO -> {
                    if (userVO == null) {
                        throw new RdosDefineException(ErrorCode.USER_IS_NULL);
                    }
                    SessionUtil.setUser(token, userVO);
                });
            });
            return true;
        } catch (Throwable t) {
            LOGGER.error("handleLogin error:", t);
        }
        return false;
    }
}
