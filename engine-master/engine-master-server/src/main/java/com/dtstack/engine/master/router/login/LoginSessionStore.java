package com.dtstack.engine.master.router.login;


import com.dtstack.engine.master.router.login.domain.DtUicUser;

import java.util.function.Consumer;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/18
 */
public class LoginSessionStore<T> {

    private static String uicURL;

    public static <T> void createSession(String token, Class<T> clazz, Consumer<DtUicUser> dtUicUserHandler) {
        T session = SessionUtil.getUser(token, clazz);
        if (session == null) {
            token = token.intern();
            synchronized (token) {
                session = SessionUtil.getUser(token, clazz);
                if (session == null) {
                    DtUicUserConnect.getInfo(token, uicURL, dtUicUserHandler);
                }
            }
        }
    }

    public static void removeSession(String token) {
        if (DtUicUserConnect.removeUicInfo(token, uicURL)) {
            SessionUtil.pulish(token);
        }
    }

    public static void removeSession(String token, boolean uicLogout) {
        if (uicLogout) {
            if (DtUicUserConnect.removeUicInfo(token, uicURL)) {
                SessionUtil.pulish(token);
            }
        } else {
            SessionUtil.pulish(token);
        }
    }

    public static void setUrl(String url) {
        uicURL = url;
    }
}
