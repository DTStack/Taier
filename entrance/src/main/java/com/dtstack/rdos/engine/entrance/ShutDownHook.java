package com.dtstack.rdos.engine.entrance;

import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.web.VertxHttpServer;

import java.io.Closeable;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ShutDownHook {
	
	private static final Logger logger = LoggerFactory.getLogger(ShutDownHook.class);

	private Closeable[] closeables = null;

	public ShutDownHook(Closeable ... closeables) {
		// TODO Auto-generated constructor stub
		this.closeables = closeables;

	}

	public void addShutDownHook(){
		   Thread shut =new Thread(new ShutDownHookThread());
		   shut.setDaemon(true);
		   Runtime.getRuntime().addShutdownHook(shut);
		   logger.warn("addShutDownHook success ...");
		}
	
	private class ShutDownHookThread implements Runnable{

		@Override
		public void run() {
			if(closeables != null){
				for(Closeable closeable:closeables){
					try{
						closeable.close();
					}catch (Exception e){
						logger.error("",e);
					}
				}
			}
		}
	}
}
