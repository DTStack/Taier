package com.dtstack.rdos.engine.service.zk.task;

import java.util.concurrent.atomic.AtomicBoolean;

import com.dtstack.rdos.engine.service.node.MasterNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;



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

		int index=0;
		while(true){
			try{
                ++index;
                isMaster.getAndSet(zkDistributed.setMaster());
                MasterNode.getInstance().setIsMaster(isMaster.get());

                if(PublicUtil.count(index,15)){
                    logger.warn("MasterListener start again...");
                    if(isMaster()){
                        logger.warn("i am is master...");
                    }
                }
			}catch(Throwable e){
				logger.error("MasterCheck error:{}",ExceptionUtil.getErrorMessage(e));
			}finally {
                try {
                    Thread.sleep(MASTERCHECK);
                } catch (InterruptedException e1) {
                    logger.error("", e1);
                }
            }
        }

	}

	public boolean isMaster() {
		return isMaster.get();
	}
}
