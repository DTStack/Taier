package com.dtstack.rdos.engine.web.vertx;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dtstack.rdos.engine.send.Urls;
import com.dtstack.rdos.engine.web.HttpCommon;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * 
 * @author sishu.yss
 *
 */
public class ServerVerticle extends AbstractVerticle{
	
	private String host="0.0.0.0";
	
	private int port;
	
	
	public ServerVerticle(String host, int port) {
		// TODO Auto-generated constructor stub
	}

	public ServerVerticle(Map<String, Object> nodeConfig) {
		String localAddress = (String) nodeConfig.get("localAddress");
		this.host = (String) HttpCommon.getUrlPort(localAddress)[0];
		this.port  = (Integer) HttpCommon.getUrlPort(localAddress)[1];
	}
	

	@Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHearders())
                .allowedMethods(allowMethods()));
        router.post(Urls.ROOT+"/*").handler(new BaseVerticle()::request);
        vertx.createHttpServer()
        .requestHandler(router::accept)
        .listen(config().getInteger("http.port", port),
                config().getString("http.address",host), result -> {
                    if (result.succeeded())
                        future.complete();
                    else
                        future.fail(result.cause());
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
