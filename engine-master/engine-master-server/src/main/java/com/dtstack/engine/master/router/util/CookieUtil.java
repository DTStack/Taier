package com.dtstack.engine.master.router.util;


import com.google.common.base.Charsets;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author toutian
 */
public class CookieUtil {

    public static final String PROJECT_ID = "project_id";
    public static final String PROJECT_CODE = "dt_product_code";

    public static final String DT_TOKEN = "dt_token";
    public static final String DT_USERID = "dt_user_id";
    public static final String dtUsername = "dt_username";
    public static final String dtSource = "req_source";

    public static String getDtUicToken(Cookie[] cookies) {
        Object value = getCookieValue(cookies, DT_TOKEN);
        return value == null ? "" : value.toString();
    }

    public static long getProject(Cookie[] cookies) {
        Object value = getCookieValue(cookies, PROJECT_ID);
        return value == null ? -1 : Long.parseLong(value.toString());
    }

    public static String getProductCode(Cookie[] cookies) {
        Object value = getCookieValue(cookies, PROJECT_CODE);
        return value == null ? "" : value.toString();
    }

    public static long getUserId(Cookie[] cookies) {
        Object value = getCookieValue(cookies, DT_USERID);
        return value == null ? -1 : Long.parseLong(value.toString());
    }

    public static String getDtUserName(Cookie[] cookies) {
        Object value = getCookieValue(cookies, dtUsername);
        try {
            return value == null ? "" : URLDecoder.decode(value.toString(), Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        return "";
    }

    private static Object getCookieValue(Cookie[] cookies, String key) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
