package com.dtstack.task.web.util;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.task.web.callback.ApiResult;
import io.vertx.ext.web.RoutingContext;


/**
 * @author sishu.yss
 */
public class ResponseUtil {

    public static void redirect(RoutingContext routingContext) {
        String msg = ApiResult.createErrorResultJsonStr(ErrorCode.NOT_LOGIN.getCode(), ErrorCode.NOT_LOGIN.getDescription());
        routingContext.response().putHeader("content-type", "application/json;charset=utf-8").setStatusCode(200).end(msg);
    }

    public static void res200(RoutingContext routingContext, String message) {
        routingContext.response().putHeader("content-type", "application/json;charset=utf-8").setStatusCode(200).end(message);
    }

    public static void res402(RoutingContext routingContext, String message) {
        routingContext.response().putHeader("content-type", "application/json;charset=utf-8").setStatusCode(402).end(message);
    }

}
