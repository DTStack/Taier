package com.dtstack.rdos.engine.execution.base.components;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.util.EngineRestParseUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author sishu.yss
 *
 */
public class SlotNoAvailableJobClient {
	
	private Logger logger = LoggerFactory.getLogger(SlotNoAvailableJobClient.class);
	
	private ReentrantLock reentrantLock = new ReentrantLock();
	
	private volatile Map<String,JobClient> slotNoAvailableJobClients = Maps.newLinkedHashMap();
	
	public void noAvailSlotsJobaddExecutionQueue(OrderLinkedBlockingQueue<OrderObject> orderLinkedBlockingQueue){
		try{
			reentrantLock.lock();
			Iterator<String> iterator =  slotNoAvailableJobClients.keySet().iterator();

			while(iterator.hasNext()){
				String key = iterator.next();
				JobClient jobClient = slotNoAvailableJobClients.get(key);

				if(checkNeedReSubmit(jobClient)){
                    logger.info("------ job: {} add into orderLinkedBlockingQueue again.", jobClient.getTaskId());
                    orderLinkedBlockingQueue.put(jobClient);
                    iterator.remove();
                }else{
                    iterator.remove();
                    listenerJobStatus(jobClient);
                }
			}
            
		}catch(Throwable e){
			logger.error("", e);
		}finally{
			reentrantLock.unlock();
		}
	}

	public boolean checkNeedReSubmit(JobClient jobClient){

        if(jobClient.getJobResult() == null){
            //未提交过
            return true;
        } else if(isSubmitFailOfEngineDown(jobClient)){
            //因为引擎挂了,需要不断重试
            return true;
        }else if(StringUtils.isBlank(jobClient.getEngineTaskId()) && !isSubmitFail(jobClient)){
            //FIXME
            return true;
        } else if(StringUtils.isNotBlank(jobClient.getEngineTaskId())
                && JobSubmitExecutor.getInstance().judgeSlotsAndAgainExecute(jobClient.getEngineType(), jobClient.getEngineTaskId())){
            //提交成功但是获取到的在服务器上出现资源不足
            return true;
        }

        return false;
    }

	public boolean isSubmitFail(JobClient jobClient){

	    if(jobClient.getJobResult() != null && jobClient.getJobResult().isErr()){
            return true;
        }

        return false;
    }

    public boolean isSubmitFailOfEngineDown(JobClient jobClient){

	    if(!isSubmitFail(jobClient)){
	        return false;
        }

	    try{
	        if(EngineType.isFlink(jobClient.getEngineType())){
                return EngineRestParseUtil.FlinkRestParseUtil.checkFailureForEngineDown(jobClient.getJobResult().getMsgInfo());
            }else if(EngineType.isSpark(jobClient.getEngineType())){
                return EngineRestParseUtil.SparkRestParseUtil.checkFailureForEngineDown(jobClient.getJobResult().getMsgInfo());
            }else{
                return false;
            }

        }catch (Exception e){
	        logger.error("", e);
        }

        return false;

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

	private void listenerJobStatus(JobClient jobClient){
		jobClient.setJobResult(jobClient.getJobResult());
		JobClient.getQueue().offer(jobClient);//添加触发读取任务状态消息
	}
}
