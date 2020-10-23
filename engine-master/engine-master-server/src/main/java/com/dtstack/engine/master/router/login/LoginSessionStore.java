package com.dtstack.engine.master.router.login;


import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/18
 */
@Component
public class LoginSessionStore {
    
    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private SessionUtil sessionUtil;

    public  <T> void createSession(String token, Class<T> clazz, Consumer<DtUicUser> dtUicUserHandler) {
        T session = sessionUtil.getUser(token, clazz);
        if (session == null) {
            token = token.intern();
            synchronized (token) {
                session = sessionUtil.getUser(token, clazz);
                if (session == null) {
                    DtUicUserConnect.getInfo(token, environmentContext.getDtUicUrl(), dtUicUserHandler);
                }
            }
        }
    }

    public void removeSession(String token) {
        if (DtUicUserConnect.removeUicInfo(token, environmentContext.getDtUicUrl())) {
            sessionUtil.pulish(token);
        }
    }

    public void removeSession(String token, boolean uicLogout) {
        if (uicLogout) {
            if (DtUicUserConnect.removeUicInfo(token, environmentContext.getDtUicUrl())) {
                sessionUtil.pulish(token);
            }
        } else {
            sessionUtil.pulish(token);
        }
    }
}
