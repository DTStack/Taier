package com.dtstack.engine.master.router.login;

import com.dtstack.dtcenter.common.cache.SessionCache;
import com.dtstack.dtcenter.common.util.MD5Util;
import org.springframework.context.ApplicationContext;

/**
 * @author toutian
 */
public class SessionUtil {

    private final static String USER_KEY = "user";
    private static SessionCache sessionCache;

    public static <T> T getValue(String token, String key, Class<T> clazz) {
        return sessionCache.get(getSessionId(token), key, clazz);
    }

    public static void setValue(String token, String key, Object value) {
        sessionCache.set(getSessionId(token), key, value);
    }

    public static <T> T getUser(String token, Class<T> clazz) {
        return sessionCache.get(getSessionId(token), USER_KEY, clazz);
    }

    public static void setUser(String token, Object value) {
        sessionCache.set(getSessionId(token), USER_KEY, value);
    }

    public static void pulish(String token) {
        sessionCache.publishRemoveMessage(getSessionId(token));
    }

    public static void setContext(ApplicationContext context) {
        sessionCache = context.getBean("sessionCache", SessionCache.class);
    }

    public static String getSessionId(String token) {
        return MD5Util.getMD5String(token);
    }

}
