package com.dtstack.rdos.engine.entrance.zk.task;

import java.util.Map;
import java.util.Set;

import com.dtstack.rdos.engine.db.dao.RdosBatchServerLogDao;
import com.dtstack.rdos.engine.db.dao.RdosStreamServerLogDao;
import com.dtstack.rdos.engine.db.dataobject.RdosBatchServerLog;
import com.dtstack.rdos.engine.db.dataobject.RdosStreamServerLog;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosStreamTaskDAO;
import com.dtstack.rdos.engine.db.dataobject.RdosBatchJob;
import com.dtstack.rdos.engine.db.dataobject.RdosStreamTask;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.util.TaskIdUtil;

/**
 * 
 * @author sishu.yss
 *
 */
public class TaskStatusListener implements Runnable{
	
	private static Logger logger = LoggerFactory.getLogger(TaskListener.class);

    public final static String JOBEXCEPTION = "jobs/%s/exceptions";

    private static long listener = 2000;
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	private Map<String,BrokerDataNode> brokerDatas = zkDistributed.getMemTaskStatus();
	
	private RdosStreamTaskDAO rdosStreamTaskDAO = new RdosStreamTaskDAO();
	
	private RdosBatchJobDAO rdosbatchJobDAO = new RdosBatchJobDAO();

	private RdosStreamServerLogDao rdosStreamServerLogDao = new RdosStreamServerLogDao();

	private RdosBatchServerLogDao rdosBatchServerLogDao = new RdosBatchServerLogDao();

	
	@Override
	public void run() {
	  	int index = 0;
	  	while(true){
	  		try{
		  		++index;
		  		Thread.sleep(listener);
		  		if(PublicUtil.count(index, 5))logger.warn("TaskStatusListener start again...");
		  		updateTaskStatus();
			}catch(Throwable e){
				logger.error("TaskStatusTaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
			}
	    }
	}
	
	private void updateTaskStatus(){
		Set<Map.Entry<String,Byte>> entrys = brokerDatas.get(zkDistributed.getLocalAddress()).getMetas().entrySet();
      	for(Map.Entry<String,Byte> entry:entrys){

            try{
                if(!RdosTaskStatus.needClean(entry.getValue())){
                    String zkTaskId = entry.getKey();
                    int computeType = TaskIdUtil.getComputeType(zkTaskId);
                    String engineTypeName = TaskIdUtil.getEngineType(zkTaskId);
                    String taskId  = TaskIdUtil.getTaskId(zkTaskId);
                    if(computeType == ComputeType.STREAM.getComputeType()){
                        RdosStreamTask rdosTask = rdosStreamTaskDAO.getRdosTaskByTaskId(taskId);
                        if(rdosTask!=null){
                            String engineTaskid = rdosTask.getEngineTaskId();
                            if(StringUtils.isNotBlank(engineTaskid)){
                                RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTypeName, engineTaskid);
                                if(rdosTaskStatus != null){
                                    Integer status = rdosTaskStatus.getStatus();
                                    zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
									rdosStreamTaskDAO.updateTaskEngineIdAndStatus(taskId,engineTaskid,status);
                                    updateJobEngineLog(status, taskId, engineTaskid, engineTypeName, computeType);
								}
                            }
                        }
                    }else if(computeType == ComputeType.BATCH.getComputeType()){
                        RdosBatchJob rdosBatchJob  = rdosbatchJobDAO.getRdosTaskByTaskId(taskId);
						if(rdosBatchJob!=null){
							String engineTaskid = rdosBatchJob.getEngineJobId();
							if(StringUtils.isNotBlank(engineTaskid)){
								if(EngineType.isDataX(engineTypeName)){
									zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId,rdosBatchJob.getStatus());
								}else{
									RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTypeName, engineTaskid);
									if(rdosTaskStatus!=null){
										Integer status = rdosTaskStatus.getStatus();
										zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId,status);
										rdosbatchJobDAO.updateTaskEngineIdAndStatus(taskId,engineTaskid,status);
                                        updateJobEngineLog(status, taskId, engineTaskid, engineTypeName, computeType);
                                    }
								}
							}
						}
                    }
                }
            }catch (Throwable e){
                logger.error("", e);
            }
      	}
	}

	private void updateJobEngineLog(Integer status, String jobId, String engineJobId,
                                    String engineType, int computeType){
        if(!RdosTaskStatus.needClean(status.byteValue())){
            return;
        }

        //从engine获取log
        String jobLog = JobClient.getEngineLog(engineType, engineJobId);

        //写入db
        if(computeType == ComputeType.STREAM.getComputeType()){
            rdosStreamServerLogDao.updateEngineLog(jobId, jobLog);
        }else if(computeType == ComputeType.BATCH.getComputeType()){
            rdosBatchServerLogDao.updateEngineLog(jobId, jobLog);
        }else{
            logger.info("----- not support compute type {}.", computeType);
        }
    }
}