package com.dtstack.rdos.engine.web;

import java.util.Map;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
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
		VertxOptions vo = new VertxOptions();
		vo.setEventLoopPoolSize(100);
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(100);
        deploymentOptions.setWorker(true);
        deploymentOptions.setWorkerPoolSize(2000);
        vertx = Vertx.vertx(vo);
        ServerVerticle.setHostPort(this.nodeConfig);
        vertx.deployVerticle(ServerVerticle.class.getName(), deploymentOptions);
	}


	public void release() {
		// TODO Auto-generated method stub
		this.vertx.close();
	}

}
