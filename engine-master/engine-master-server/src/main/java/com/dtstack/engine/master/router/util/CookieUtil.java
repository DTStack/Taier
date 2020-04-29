package com.dtstack.engine.master.router.util;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author toutian
 */
public class CookieUtil {

    public static String projectId = "project_id";
    public static String productCode = "dt_product_code";

    public static String dtToken = "dt_token";
    public static String dtUserId = "dt_user_id";
    public static String dtUsername = "dt_username";
    public static String dtSource = "req_source";

    public static String getDtUicToken(RoutingContext routingContext) {
        Cookie cookie = routingContext.getCookie(dtToken);
        return cookie == null ? "" : cookie.getValue();
    }

    public static long getProject(RoutingContext routingContext) {
        Cookie cookie = routingContext.getCookie(projectId);
        return cookie == null ? -1 : Long.parseLong(cookie.getValue());
    }

    public static String getProductCode(RoutingContext routingContext) {
        Cookie cookie = routingContext.getCookie(productCode);
        return cookie == null ? "" : cookie.getValue();
    }

    public static String getUserId(RoutingContext routingContext) {
        Cookie cookie = routingContext.getCookie(dtUserId);
        return cookie == null ? "" : cookie.getValue();
    }


    /**
     * 用户信息丢失 会导致页面显示不全 也需要重新登陆获取
     *
     * @param routingContext
     * @return
     */
    public static boolean isNeedToLogin(RoutingContext routingContext) {
        Predicate<RoutingContext> validCookieInfo = context -> Objects.isNull(context)
                || Objects.isNull(context.getCookie(dtToken));
        return validCookieInfo.test(routingContext);
    }
}
