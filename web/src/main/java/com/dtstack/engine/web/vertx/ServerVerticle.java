package com.dtstack.engine.web.vertx;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.service.send.Urls;
import com.dtstack.engine.web.HttpCommon;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author sishu.yss
 *
 */
public class ServerVerticle extends AbstractVerticle{
	
	private static String host = "0.0.0.0";
	
	private static int port = 8090;
	
	public static void setHostPort(Map<String, Object> nodeConfig){
		String localAddress = ConfigParse.getLocalAddress();
        if(StringUtils.isNotBlank(localAddress)){
            port  = (Integer) HttpCommon.getUrlPort(localAddress)[1];
        }
	}
	
	

	@Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHearders())
                .allowedMethods(allowMethods()));
        router.post(Urls.ROOT+"/*").handler(new BaseVerticle()::request);
        vertx.createHttpServer()
        .requestHandler(router::accept)
        .listen(config().getInteger("http.port", port),
                config().getString("http.address",host), result -> {
                    if (result.succeeded()){
                        future.complete();
                    }else{
                        future.fail(result.cause());
                    }
                });
    }
	
	private Set<String> allowHearders(){
        Set<String> allowHeaders = new HashSet<String>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        return allowHeaders;
	}

	private Set<HttpMethod> allowMethods(){
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.POST);
        return allowMethods;
	}
}
