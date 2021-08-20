package com.dtstack.engine.master.router.login;


import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.common.constrant.Cookies;
import com.dtstack.engine.master.router.login.domain.DTToken;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author toutian
 */
@Service
public class LoginService {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginService.class);


    @Autowired
    private CookieService cookieService;

    @Autowired
    private TokenService tokenService;


    public void login(DtUicUser dtUicUser, String token, Consumer<UserDTO> resultHandler) {
        try {
            if (dtUicUser == null) {
                resultHandler.accept(null);
                return;
            }
            boolean isRootUser = Optional.ofNullable(dtUicUser.getRootOnly()).orElse(false);
            LOGGER.info("dtUic userId [{}] userName {} tenantId {} is Root {} login", dtUicUser.getUserId(), dtUicUser.getUserName(), dtUicUser.getTenantId(), isRootUser);
            UserDTO userDTO = new UserDTO();
            userDTO.setDtuicUserId(dtUicUser.getUserId());
            userDTO.setTenantId(dtUicUser.getTenantId());
            userDTO.setUserName(dtUicUser.getUserName());
            userDTO.setRootUser(isRootUser ? 1 : 0);
            resultHandler.accept(userDTO);
        } catch (Throwable e) {
            LOGGER.error("login fail:", e);
            throw e;
        }
    }


    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, DtUicUser dtUicUser) {
        String dtToken = cookieService.token(request);

        //若Token不存在,则生成Token
        if (Objects.isNull(dtToken)
        ) {
            cookie(request, response, dtUicUser);
        } else {
            DTToken token = tokenService.decryption(dtToken);
            boolean equalsUserId = dtUicUser.getUserId().equals(token.getUserId());
            boolean nonNullTenantId = Objects.nonNull(dtUicUser.getTenantId());
            if (nonNullTenantId && !dtUicUser.getTenantId().equals(token.getTenantId())) {
                cookie(request, response, dtUicUser);
            } else if (!equalsUserId) {
                cookie(request, response, dtUicUser);
            }
        }
    }

    private void cookie(HttpServletRequest request, HttpServletResponse response, DtUicUser user) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);
        Objects.requireNonNull(user);

        String token = tokenService.encryption(user.getUserId(), user.getUserName(), user.getTenantId());
        cookieService.addCookie(request, response, Cookies.DT_USER_ID, user.getUserId());
        cookieService.addCookie(request, response, Cookies.DT_USER_NAME, user.getUserName());
        cookieService.addCookie(request, response, Cookies.DT_TOKEN, token);
        cookieService.addCookie(request, response, Cookies.DT_PROJECT_ID, 1);
        cookieService.addCookie(request, response, Cookies.DT_PROJECT_NAME, "DAGScheduleX");
        cookieService.addCookie(request, response, Cookies.DT_APPTYPE_ID, 1);

        cookieService.addCookie(request, response, Cookies.TOKEN, token);
        cookieService.addCookie(request, response, Cookies.USER_ID, user.getUserId());
        cookieService.addCookie(request, response, Cookies.PROJECT_ID, 1);
        cookieService.addCookie(request, response, Cookies.TENANT_ID, user.getTenantId());

        Set<String> clearCookies = new HashSet<>();
        clearCookies.add(Cookies.DT_TENANT_IS_CREATOR);
        clearCookies.add(Cookies.DT_TENANT_IS_ADMIN);

        if (null != user.getTenantId()) {
            cookieService.addCookie(request, response, Cookies.DT_TENANT_ID, user.getTenantId());
            cookieService.addCookie(request, response, Cookies.DT_TENANT_NAME, user.getTenantName());
            cookieService.addCookie(request, response, Cookies.TENANT_ID, 1);
        } else {
            clearCookies.add(Cookies.DT_TENANT_ID);
            clearCookies.add(Cookies.DT_TENANT_NAME);
            clearCookies.add(Cookies.TENANT_ID);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (clearCookies.contains(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }

}
