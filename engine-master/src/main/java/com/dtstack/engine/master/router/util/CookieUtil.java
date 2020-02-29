package com.dtstack.engine.master.router.util;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author sishu.yss
 */
public class CookieUtil {

    public static String dtToken = "dt_token";

    public static String projectId = "project_id";

    public static String productCode = "dt_product_code";
    
    private static String reqSource = "req_source";

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
    
}
