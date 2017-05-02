package com.dtstack.rdos.engine.entrance.zk.task;

import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;



/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月07日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class MasterListener implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(MasterListener.class);

    private AtomicBoolean isMaster = new AtomicBoolean(false);
    
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	private final static int MASTERCHECK = 500;

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			int index=0;
			while(true){
				++index;
				isMaster.getAndSet(zkDistributed.setMaster());
				if(PublicUtil.count(index,15)){
					logger.warn("MasterListener start again...");
					if(isMaster()){
						logger.warn("i am is master...");
					}
				}
				Thread.sleep(MASTERCHECK);
			}
		}catch(Exception e){
			logger.error("MasterCheck error:{}",ExceptionUtil.getErrorMessage(e));
		}
	}

	public boolean isMaster() {
		return isMaster.get();
	}
}
