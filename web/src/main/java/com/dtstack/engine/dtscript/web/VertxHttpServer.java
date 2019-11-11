package com.dtstack.engine.dtscript.web;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import com.dtstack.engine.common.config.ConfigParse;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.engine.dtscript.web.vertx.ServerVerticle;
import io.vertx.core.Vertx;


/**
 * 
 * @author sishu.yss
 *
 */
public class VertxHttpServer implements Closeable{
	
	private static final Logger logger = LoggerFactory.getLogger(VertxHttpServer.class);

	private Vertx vertx;
	
	private Map<String,Object> nodeConfig;
	
	public VertxHttpServer(Map<String,Object> nodeConfig){
		this.nodeConfig = nodeConfig;
		init();
	}
	
	
	private void init(){
		VertxOptions vo = new VertxOptions();
		vo.setEventLoopPoolSize(ConfigParse.getEventLoopPoolSize());
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(ConfigParse.getInstances());
        deploymentOptions.setWorker(true);
        deploymentOptions.setWorkerPoolSize(ConfigParse.getWorkerPoolSize());
		vertx = Vertx.vertx(vo);
		ServerVerticle.setHostPort(this.nodeConfig);
		vertx.deployVerticle(ServerVerticle.class.getName(), deploymentOptions);
		logger.warn("init http server success...");
	}

	@Override
	public void close() throws IOException {
		this.vertx.close();
	}
}
