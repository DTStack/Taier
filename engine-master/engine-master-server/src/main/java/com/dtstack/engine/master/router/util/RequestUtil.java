//package com.dtstack.engine.master.router.util;
//
//import com.google.common.collect.Maps;
//import io.vertx.ext.web.RoutingContext;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.context.ApplicationContext;
//
//import java.util.Map;
//
///**
// * @author sishu.yss
// */
//public class RequestUtil {
//
//    /**
//     * 对user参数解析，可以考虑在login中处理，并直接放入context
//     */
//    public static Map<String, Object> getRequestParams(RoutingContext routingContext) throws Exception {
//        String body = routingContext.getBodyAsString();
//        Map<String, Object> params = getCommonAttr(routingContext);
//        if (StringUtils.isNotBlank(body)) {
//            params.putAll(routingContext.getBodyAsJson().getMap());
//        }
//        //set params,异常时打印方便排查问题
//        routingContext.put("params", params);
//        return params;
//    }
//
//    public static Map<String, Object> getRequestParams(ApplicationContext context, RoutingContext routingContext) throws Exception {
//        String body = routingContext.getBodyAsString();
//        Map<String, Object> params = getCommonAttr(routingContext);
//        if (StringUtils.isNotBlank(body)) {
//            addBodyParam(context, routingContext, null, params);
//        }
//        //set params,异常时打印方便排查问题
//        routingContext.put("params", params);
//        return params;
//    }
//
//    public static Map<String, Object> getRequestParams(Map<String, Object> params, RoutingContext routingContext) throws Exception {
//        if (params == null) {
//            params = Maps.newHashMap();
//        }
//        params.putAll(getCommonAttr(routingContext));
//        return params;
//    }
//
//    public static void addBodyParam(ApplicationContext context, RoutingContext routingContext, Map<String, Object> bodyParam, Map<String, Object> params) {
//        if (bodyParam == null) {
//            bodyParam = routingContext.getBodyAsJson().getMap();
//        }
//        params.putAll(bodyParam);
//        if (bodyParam.containsKey("dtUicUserId") && bodyParam.containsKey("dtUicTenantId")) {
//            Long dtUicTenantId = Long.valueOf(String.valueOf(bodyParam.get("dtUicTenantId")));
//            Long dtUicUserId = Long.valueOf(String.valueOf(bodyParam.get("dtUicUserId")));
//            params.put("userId", dtUicUserId);
//            params.put("createUserId", dtUicUserId);
//            params.put("modifyUserId", dtUicUserId);
//            params.put("dtuicTenantId", dtUicTenantId);
//        }
//    }
//
//    public static Map<String, Object> getRequestParams(Map<String, Object> params, RoutingContext routingContext, ApplicationContext context)
//            throws Exception {
//        if (params == null) {
//            params = Maps.newHashMap();
//        }
//        //以body中传的参数为最终参数
//        Map<String, Object> commonAttr = getCommonAttr(routingContext);
//        if (params.size() > 0) {
//            addBodyParam(context, routingContext, params, commonAttr);
//        }
//        return commonAttr;
//    }
//
//    private static Map<String, Object> getCommonAttr(RoutingContext routingContext) {
//        String dtToken = CookieUtil.getDtUicToken(routingContext);
//        Map<String, Object> params = Maps.newHashMap();
//        params.put("dtToken", dtToken);
//        addCookieInfo(routingContext, params);
//        return params;
//    }
//
//    private static void addCookieInfo(RoutingContext routingContext, Map<String, Object> params) {
//        params.put("projectId", CookieUtil.getProject(routingContext));
//        params.put("productCode", CookieUtil.getProductCode(routingContext));
//        params.put("userId", CookieUtil.getUserId(routingContext));
//    }
//
//}
