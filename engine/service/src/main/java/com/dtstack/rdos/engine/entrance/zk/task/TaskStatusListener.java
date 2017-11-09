package com.dtstack.rdos.engine.entrance.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosBatchServerLogDao;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.db.dao.RdosStreamServerLogDao;
import com.dtstack.rdos.engine.db.dao.RdosStreamTaskDAO;
import com.dtstack.rdos.engine.db.dataobject.RdosBatchJob;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.db.dataobject.RdosStreamTask;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.util.TaskIdUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @author sishu.yss
 *
 */
public class TaskStatusListener implements Runnable{
	
	private static Logger logger = LoggerFactory.getLogger(TaskListener.class);

    public final static String JOBEXCEPTION = "jobs/%s/exceptions";

    public final static int FLINK_NOT_FOUND_LIMIT_TIMES = 10;

    private static long listener = 2000;

    /**初始启动的时候需要对获取的任务做重启操作*/
    private boolean isFirst = true;
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	private Map<String,BrokerDataNode> brokerDatas = zkDistributed.getMemTaskStatus();

	/**记录job 连续某个状态的频次*/
	private Map<String, Pair<Integer, Integer>> jobStatusFrequency = Maps.newConcurrentMap();

    private RdosStreamTaskDAO rdosStreamTaskDAO = new RdosStreamTaskDAO();
	
	private RdosBatchJobDAO rdosbatchJobDAO = new RdosBatchJobDAO();

	private RdosStreamServerLogDao rdosStreamServerLogDao = new RdosStreamServerLogDao();

	private RdosBatchServerLogDao rdosBatchServerLogDao = new RdosBatchServerLogDao();

	private RdosEngineJobCacheDao rdosEngineJobCacheDao = new RdosEngineJobCacheDao();

	
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
			}finally {
			    isFirst = false;
            }
        }
	}
	
	private void updateTaskStatus(){
		Set<Map.Entry<String,Byte>> entrys = brokerDatas.get(zkDistributed.getLocalAddress()).getMetas().entrySet();

      	for(Map.Entry<String,Byte> entry : entrys){

            try{
                Integer oldStatus = Integer.valueOf(entry.getValue());

                if(!RdosTaskStatus.needClean(entry.getValue())){
                    String zkTaskId = entry.getKey();
                    int computeType = TaskIdUtil.getComputeType(zkTaskId);
                    String engineTypeName = TaskIdUtil.getEngineType(zkTaskId);
                    String taskId  = TaskIdUtil.getTaskId(zkTaskId);

                    if(computeType == ComputeType.STREAM.getComputeType()){
                        dealStreamJob(taskId, engineTypeName, zkTaskId, computeType, oldStatus);
                    }else if(computeType == ComputeType.BATCH.getComputeType()){
                        dealBatchJob(taskId, engineTypeName, zkTaskId, computeType, oldStatus);
                    }
                }
            }catch (Throwable e){
                logger.error("", e);
            }
      	}
	}

	private void dealStreamJob(String taskId, String engineTypeName, String zkTaskId, int computeType, Integer oldStatus) throws Exception {
        RdosStreamTask rdosTask = rdosStreamTaskDAO.getRdosTaskByTaskId(taskId);

        if(rdosTask != null){
            String engineTaskId = rdosTask.getEngineTaskId();

            if(StringUtils.isNotBlank(engineTaskId)){
                RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTypeName, engineTaskId);

                if(rdosTaskStatus != null){
                    Integer status = rdosTaskStatus.getStatus();
                    zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
                    rdosStreamTaskDAO.updateTaskEngineIdAndStatus(taskId, engineTaskId, status);
                    updateJobEngineLog(status, taskId, engineTaskId, engineTypeName, computeType);
                    dealFlinkAfterGetStatus(status, taskId, engineTypeName, zkTaskId, computeType, engineTaskId);
                }
            }else{
                dealWaitingJobForMigrationJob(zkTaskId, oldStatus);
            }
        }
    }

    private void dealBatchJob(String taskId, String engineTypeName, String zkTaskId, int computeType, Integer oldStatus) throws Exception {
        RdosBatchJob rdosBatchJob  = rdosbatchJobDAO.getRdosTaskByTaskId(taskId);

        if(rdosBatchJob != null){
            String engineTaskId = rdosBatchJob.getEngineJobId();
            if(StringUtils.isNotBlank(engineTaskId)){

                if(EngineType.isDataX(engineTypeName)){
                    zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId, rdosBatchJob.getStatus());
                }else{
                    RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTypeName, engineTaskId);

                    if(rdosTaskStatus != null){
                        Integer status = rdosTaskStatus.getStatus();
                        zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
                        rdosbatchJobDAO.updateTaskEngineIdAndStatus(taskId, engineTaskId, status);
                        updateJobEngineLog(status, taskId, engineTaskId, engineTypeName, computeType);
                        dealSparkAfterGetStatus(status, taskId, zkTaskId, engineTaskId, engineTypeName, computeType);
                    }
                }
            }else{
                dealWaitingJobForMigrationJob(zkTaskId, oldStatus);
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

    private void dealWaitingJobForMigrationJob(String zkJobId, Integer status) throws Exception {
        if(status != RdosTaskStatus.WAITENGINE.getStatus() && status != RdosTaskStatus.WAITCOMPUTE.getStatus()){
            return;
        }

        if(!isFirst && !TaskIdUtil.isMigrationJob(zkJobId)){
            return;
        }

        logger.info("recover job:{} from migration.", zkJobId);
        //从数据库读取出任务信息--并提交
        String jobId = TaskIdUtil.getTaskId(zkJobId);
        RdosEngineJobCache rdosEngineJobCache = rdosEngineJobCacheDao.getJobById(jobId);
        if(rdosEngineJobCache == null){
            logger.error("can't not get engineJobCache from db by jobId:{}.", jobId);
            return;
        }

        String jobInfo = rdosEngineJobCache.getJobInfo();
        ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);

        //send job and change job status
        start(paramAction, zkJobId);
    }

    /**
     * FIXME 未测试
     * flink 重启状态/获取不任务状态--的处理
     * @param status
     * @param jobId
     */
    private void dealFlinkAfterGetStatus(Integer status, String jobId, String engineTypeName, String zkTaskId,
                                         int computeType, String engineTaskId){

        if(RdosTaskStatus.needClean(status.byteValue())){
            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);
            return;
        }

        Pair<Integer, Integer> statusPair = updateJobStatusFrequency(jobId, status);

        if(statusPair.getLeft() == RdosTaskStatus.NOTFOUND.getStatus() && statusPair.getRight() >= FLINK_NOT_FOUND_LIMIT_TIMES){

            status = RdosTaskStatus.CANCELED.getStatus();
            zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
            rdosStreamTaskDAO.updateTaskEngineIdAndStatus(jobId, engineTaskId, status);
            updateJobEngineLog(status, jobId, engineTaskId, engineTypeName, computeType);

            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);
            return;
        }
    }

    private void dealSparkAfterGetStatus(Integer status, String jobId, String zkTaskId, String engineTaskId,
                                         String engineTypeName, int computeType){
        if(RdosTaskStatus.needClean(status.byteValue())){
            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);
            return;
        }

        Pair<Integer, Integer> statusPair = updateJobStatusFrequency(jobId, status);

        if(statusPair.getLeft() == RdosTaskStatus.NOTFOUND.getStatus() && statusPair.getRight() >= FLINK_NOT_FOUND_LIMIT_TIMES){

            status = RdosTaskStatus.CANCELED.getStatus();
            zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
            rdosbatchJobDAO.updateTaskEngineIdAndStatus(jobId, engineTaskId, status);
            updateJobEngineLog(status, jobId, engineTaskId, engineTypeName, computeType);

            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);
           return;
        }
    }

    /**
     * 更新任务状态频次
     * @param jobId
     * @param status
     * @return
     */
    public Pair<Integer, Integer> updateJobStatusFrequency(String jobId, Integer status){

        Pair<Integer, Integer> statusPair = jobStatusFrequency.get(jobId);
        statusPair = statusPair == null ? new MutablePair<>(status, 0) : statusPair;
        if(statusPair.getLeft() == status){
            statusPair.setValue(statusPair.getRight() + 1);
        }else{
            statusPair = new MutablePair<>(status, 1);
        }

        jobStatusFrequency.put(jobId, statusPair);
        return statusPair;
    }

    public void updateJobZookStatus(String zkTaskId, Integer status){
        BrokerDataNode brokerDataNode = BrokerDataNode.initBrokerDataNode();
        brokerDataNode.getMetas().put(zkTaskId, status.byteValue());
        zkDistributed.updateSynchronizedBrokerData(zkDistributed.getLocalAddress(), brokerDataNode, false);
        zkDistributed.updateLocalMemTaskStatus(brokerDataNode);

    }

    public void updateJobStatus(String jobId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getComputeType().equals(computeType)) {
            rdosStreamTaskDAO.updateTaskStatus(jobId, status);
        } else {
            rdosbatchJobDAO.updateJobStatus(jobId, status);
        }
    }

    public void start(ParamAction paramAction, String zkTaskId) throws Exception {

        JobClient jobClient = new JobClient(paramAction);
        String noMigrationJobId = TaskIdUtil.convertToNoMigrationJob(zkTaskId);

        jobClient.setJobClientCallBack(new JobClientCallBack() {

            @Override
            public void execute(Map<String, ? extends Object> params) {

                if(!params.containsKey(JOB_STATUS)){
                    return;
                }

                int jobStatus = MathUtil.getIntegerVal(params.get(JOB_STATUS));
                updateJobZookStatus(noMigrationJobId, jobStatus);
                updateJobStatus(paramAction.getTaskId(), paramAction.getComputeType(), jobStatus);
            }

        });

        updateJobZookStatus(noMigrationJobId, RdosTaskStatus.WAITENGINE.getStatus());
        updateJobStatus(paramAction.getTaskId(), paramAction.getComputeType(), RdosTaskStatus.WAITENGINE.getStatus());
        jobClient.submitJob();
    }

}