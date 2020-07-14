package com.dtstack.engine.master.router.login;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.callback.ApiResult;
import com.dtstack.engine.master.router.util.CookieUtil;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @Autowired
    private LoginSessionStore loginSessionStore;

    @Autowired
    private SessionUtil sessionUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        LOGGER.debug("{}:{}", request.getRequestURI(), request.getParameterMap());
        String token = CookieUtil.getDtUicToken(request.getCookies());
        if (StringUtils.isBlank(token)) {
            throw new RdosDefineException(ErrorCode.NOT_LOGIN);
        }
        loginSessionStore.createSession(token, UserDTO.class, dtUicUser -> {
            //获取到dtuic的数据后的处理方式
            loginService.login(dtUicUser, token, userVO -> {
                if (userVO == null) {
                    throw new RdosDefineException(ErrorCode.USER_IS_NULL);
                }
                sessionUtil.setUser(token, userVO);
            });
        });

        return true;
    }
}
