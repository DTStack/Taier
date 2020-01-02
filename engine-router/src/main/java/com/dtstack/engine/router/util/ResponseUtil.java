package com.dtstack.engine.router.util;


import com.dtstack.engine.router.callback.ApiResult;
import com.dtstack.engine.router.enums.Code;
import io.vertx.ext.web.RoutingContext;


/**
 * 
 * @author sishu.yss
 *
 */
public class ResponseUtil {
	
	public static void redirect(RoutingContext routingContext){
		routingContext.response().putHeader("content-type", "application/json;charset=utf-8").setStatusCode(302).end(ApiResult.getApiResult(Code.REDIRECT.getType()));
	}
	
	public static void res500(RoutingContext routingContext, String message){
		routingContext.response().putHeader("content-type", "application/json;charset=utf-8").setStatusCode(500).end(message);
	}
	
	public static void  res200(RoutingContext routingContext,String message){
		routingContext.response().putHeader("content-type", "application/json;charset=utf-8").setStatusCode(200).end(message);
	}

}
