package com.dtstack.rdos.engine.execution.base.components;

import java.util.concurrent.locks.ReentrantLock;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author sishu.yss
 *
 */
public class SlotNoAvailableJobClient {
	
	private Logger logger =LoggerFactory.getLogger(SlotNoAvailableJobClient.class);
	
	private ReentrantLock reentrantLock = new ReentrantLock();
	
	private volatile Map<String,JobClient> slotNoAvailableJobClients = Maps.newLinkedHashMap();
	
	public void noAvailSlotsJobaddExecutionQueue(OrderLinkedBlockingQueue<OrderObject> orderLinkedBlockingQueue){
		try{
			reentrantLock.lock();
			Iterator<String> iterator =  slotNoAvailableJobClients.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				JobClient job = slotNoAvailableJobClients.get(key);
				if(StringUtils.isNoneBlank(job.getEngineTaskId())){
					orderLinkedBlockingQueue.add(job);
					slotNoAvailableJobClients.remove(key);
				}else {
					if(JobSubmitExecutor.getInstance().judgeSlostsAndAgainExecute(job)){
						orderLinkedBlockingQueue.add(job);
						slotNoAvailableJobClients.remove(key);
					}else{
						if(job.getAgain() > 2){
							slotNoAvailableJobClients.remove(key);
						}else{
							job.setAgain(job.getAgain()+1);
						}
					}
				}
			}
            
		}catch(Throwable e){
			logger.error("",e);
		}finally{
			reentrantLock.unlock();
		}
	}
	
	public void put(JobClient jobClient){
		try{
			reentrantLock.lock();
			this.slotNoAvailableJobClients.put(jobClient.getTaskId(), jobClient);
		}catch(Throwable e){
			logger.error("",e);
		}finally{
			reentrantLock.unlock();
		}
	}
	
	public boolean remove(String jobId){
		try{
			reentrantLock.lock();
			return slotNoAvailableJobClients.remove(jobId,slotNoAvailableJobClients.get(jobId));
		}catch(Throwable e){
			logger.error("",e);
		}finally{
			reentrantLock.unlock();
		}
		return false;
	}
}
