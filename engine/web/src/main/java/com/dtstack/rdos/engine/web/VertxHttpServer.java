package com.dtstack.rdos.engine.web;

import java.util.Map;
import io.vertx.core.DeploymentOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.engine.web.vertx.ServerVerticle;
import io.vertx.core.Vertx;


/**
 * 
 * @author sishu.yss
 *
 */
public class VertxHttpServer {
	
	private static final Logger logger = LoggerFactory.getLogger(VertxHttpServer.class);

	private Vertx vertx;
	
	private Map<String,Object> nodeConfig;
	
	public VertxHttpServer(Map<String,Object> nodeConfig){
		this.nodeConfig = nodeConfig;
		init();
	}
	
	
	private void init(){
		logger.info("VertxHttpServer start...");
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setWorker(true);
        vertx = Vertx.vertx();
        vertx.deployVerticle(new ServerVerticle(this.nodeConfig), deploymentOptions);
	}


	public void release() {
		// TODO Auto-generated method stub
		this.vertx.close();
	}

}
