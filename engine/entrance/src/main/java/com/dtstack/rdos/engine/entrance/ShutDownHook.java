package com.dtstack.rdos.engine.entrance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.engine.entrance.http.EHttpServer;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;

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
	
	private EHttpServer eHttpServer;
	
	private  ZkDistributed zkDistributed;

	public ShutDownHook(EHttpServer eHttpServer, ZkDistributed zkDistributed) {
		// TODO Auto-generated constructor stub
		this.eHttpServer = eHttpServer;
		this.zkDistributed = zkDistributed;
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
			// TODO Auto-generated method stub
			if(eHttpServer!=null){
				eHttpServer.release();
			}
			if(zkDistributed!=null){
				zkDistributed.release();
			}
		}
	}
}
