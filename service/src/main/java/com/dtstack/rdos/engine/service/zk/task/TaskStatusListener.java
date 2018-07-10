package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosPluginInfoDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EngineType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.task.RestartDealer;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author sishu.yss
 *
 */
public class TaskStatusListener implements Runnable{
	
	private static Logger logger = LoggerFactory.getLogger(TaskListener.class);

	/**最大允许查询不到任务信息的次数--超过这个次数任务会被设置为CANCELED*/
    public final static int NOT_FOUND_LIMIT_TIMES = 300;

    public final static String SYS_CANCLED_LOG = "系统查询不到任务状态,主动设置为取消状态";

    public final static String FLINK_CP_URL_FORMAT = "/jobs/%s/checkpoints";

    public final static String FLINK_CP_HISTORY_KEY = "history";

    public final static String TRIGGER_TIMESTAMP_KEY = "trigger_timestamp";

    private static final long LISTENER_INTERVAL = 2000;

    /**初始启动的时候需要对获取的任务做重启操作*/
    private boolean isFirst = true;
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	private Map<String,BrokerDataNode> brokerDatas = zkDistributed.getMemTaskStatus();

	/**记录job 连续某个状态的频次*/
	private Map<String, Pair<Integer, Integer>> jobStatusFrequency = Maps.newConcurrentMap();

    private RdosEngineStreamJobDAO rdosStreamTaskDAO = new RdosEngineStreamJobDAO();
	
	private RdosEngineBatchJobDAO rdosBatchEngineJobDAO = new RdosEngineBatchJobDAO();

	private RdosEngineJobCacheDAO rdosEngineJobCacheDao = new RdosEngineJobCacheDAO();

	private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

	private RdosPluginInfoDAO pluginInfoDao = new RdosPluginInfoDAO();

	/**失败任务的额外处理：当前只是对失败任务继续更新日志*/
    private Map<String, FailedTaskInfo> failedJobCache = Maps.newConcurrentMap();

    @Override
	public void run() {
	  	int index = 0;
	  	while(true){
	  		try{
		  		++index;
                Thread.sleep(LISTENER_INTERVAL);
		  		if(PublicUtil.count(index, 5)){
		  		    logger.warn("\n\t\t  \t\tThread.sleep({});TaskStatusListener start again...", LISTENER_INTERVAL);
		  		}
		  		updateTaskStatus();
                dealFailedJob();
			}catch(Throwable e){
				logger.error("TaskStatusTaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
			}finally {
			    isFirst = false;
            }
        }
	}

	public void dealFailedJob(){
        try{
            for(Map.Entry<String, FailedTaskInfo> failedTaskEntry : failedJobCache.entrySet()){
                FailedTaskInfo failedTaskInfo = failedTaskEntry.getValue();
                String key = failedTaskEntry.getKey();
                updateJobEngineLog(failedTaskInfo.getJobId(), failedTaskInfo.getEngineJobId(),
                        failedTaskInfo.getEngineType(), failedTaskInfo.getComputeType() , failedTaskInfo.getPluginInfo());
                failedTaskInfo.tryLog();

                if(!failedTaskInfo.canTryLogAgain()){
                    failedJobCache.remove(key);
                }
            }
        }catch (Exception e){
            logger.error("dealFailed job run error:{}",ExceptionUtil.getErrorMessage(e));
        }
    }

    public void addFailedJob(FailedTaskInfo failedTaskInfo){
        if(!failedJobCache.containsKey(failedTaskInfo.getJobId())){
            failedJobCache.put(failedTaskInfo.getJobId(), failedTaskInfo);
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

                    if(computeType == ComputeType.STREAM.getType()){
                        dealStreamJob(taskId, engineTypeName, zkTaskId, computeType, oldStatus);
                    }else if(computeType == ComputeType.BATCH.getType()){
                        dealBatchJob(taskId, engineTypeName, zkTaskId, computeType, oldStatus);
                    }
                }
            }catch (Throwable e){
                logger.error("", e);
            }
      	}
	}

	private void dealStreamJob(String taskId, String engineTypeName, String zkTaskId, int computeType, Integer oldStatus) throws Exception {
        RdosEngineStreamJob rdosTask = rdosStreamTaskDAO.getRdosTaskByTaskId(taskId);

        if(rdosTask != null){
            String engineTaskId = rdosTask.getEngineTaskId();

            if(StringUtils.isNotBlank(engineTaskId)){
                String pluginInfoStr = "";
                if(rdosTask.getPluginInfoId() > 0 ){
                    pluginInfoStr = pluginInfoDao.getPluginInfo(rdosTask.getPluginInfoId());
                }

                RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTypeName, pluginInfoStr, engineTaskId);

                if(rdosTaskStatus != null){
                    Integer status = rdosTaskStatus.getStatus();
                    zkDistributed.updateSyncLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
                    rdosStreamTaskDAO.updateTaskEngineIdAndStatus(taskId, engineTaskId, status);
                    updateJobEngineLog(taskId, engineTaskId, engineTypeName, computeType, pluginInfoStr);

                    boolean isRestart = RestartDealer.getInstance().checkAndRestart(status, taskId, engineTaskId, engineTypeName, computeType, pluginInfoStr);
                    if(isRestart){
                        return;
                    }

                    dealStreamAfterGetStatus(status, taskId, engineTypeName, zkTaskId, computeType, engineTaskId, pluginInfoStr);
                }

                if(rdosTaskStatus != null && RdosTaskStatus.FAILED.equals(rdosTaskStatus)){
                    FailedTaskInfo failedTaskInfo = new FailedTaskInfo(taskId, engineTaskId,
                            engineTypeName, computeType, pluginInfoStr);
                    addFailedJob(failedTaskInfo);
                }
            }else{
                dealWaitingJobForMigrationJob(zkTaskId, oldStatus);
            }
        }
    }

    private void dealBatchJob(String taskId, String engineTypeName, String zkTaskId, int computeType, Integer oldStatus) throws Exception {
        RdosEngineBatchJob rdosBatchJob  = rdosBatchEngineJobDAO.getRdosTaskByTaskId(taskId);

        if(rdosBatchJob != null){
            String engineTaskId = rdosBatchJob.getEngineJobId();
            if(StringUtils.isNotBlank(engineTaskId)){

                if(EngineType.isDataX(engineTypeName)){
                    zkDistributed.updateSyncLocalBrokerDataAndCleanNoNeedTask(zkTaskId, rdosBatchJob.getStatus().intValue());
                }else {
                    String pluginInfoStr = "";
                    if(rdosBatchJob.getPluginInfoId() > 0 ){
                        pluginInfoStr = pluginInfoDao.getPluginInfo(rdosBatchJob.getPluginInfoId());
                    }

                    RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTypeName, pluginInfoStr, engineTaskId);

                    if(rdosTaskStatus != null){
                        Integer status = rdosTaskStatus.getStatus();
                        zkDistributed.updateSyncLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
                        rdosBatchEngineJobDAO.updateJobStatusAndExecTime(taskId, status);
                        updateJobEngineLog(taskId, engineTaskId, engineTypeName, computeType, pluginInfoStr);

                        boolean isRestart = RestartDealer.getInstance().checkAndRestart(status, taskId, engineTaskId, engineTypeName, computeType, pluginInfoStr);
                        if(isRestart){
                            return;
                        }

                        dealBatchJobAfterGetStatus(status, taskId, zkTaskId, computeType);
                    }

                    if(rdosTaskStatus != null && RdosTaskStatus.FAILED.equals(rdosTaskStatus)){
                        FailedTaskInfo failedTaskInfo = new FailedTaskInfo(rdosBatchJob.getJobId(), rdosBatchJob.getEngineJobId(),
                                engineTypeName, computeType, pluginInfoStr);
                        addFailedJob(failedTaskInfo);
                    }
                }
            }else{
                dealWaitingJobForMigrationJob(zkTaskId, oldStatus);
            }
        }
    }

	private void updateJobEngineLog(String jobId, String engineJobId, String engineType, int computeType, String pluginInfo){

        //从engine获取log
        String jobLog = JobClient.getEngineLog(engineType, pluginInfo, engineJobId);

        updateJobEngineLog(jobId, jobLog, computeType);
    }

    private void updateJobEngineLog(String jobId, String jobLog, Integer computeType){

        //写入db
        if(ComputeType.STREAM.getType().equals(computeType)){
            rdosStreamTaskDAO.updateEngineLog(jobId, jobLog);
        }else if(ComputeType.BATCH.getType().equals(computeType)){
            rdosBatchEngineJobDAO.updateEngineLog(jobId, jobLog);
        }else{
            logger.info("----- not support compute type {}.", computeType);
        }
    }

    private void dealWaitingJobForMigrationJob(String zkJobId, Integer status) throws Exception {
        if(status != RdosTaskStatus.WAITENGINE.getStatus().intValue()
                && status != RdosTaskStatus.WAITCOMPUTE.getStatus().intValue()
                && status != RdosTaskStatus.SUBMITTED.getStatus().intValue()
                && status != RdosTaskStatus.ENGINEDISTRIBUTE.getStatus().intValue()){
            return;
        }

        /**第一次启动或者数据是迁移过来的*/
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

        //add job to exeQueue and change job status
        start(paramAction, zkJobId);
    }

    /**
     * stream 获取任务状态--的处理
     * @param status
     * @param jobId
     */
    private void dealStreamAfterGetStatus(Integer status, String jobId, String engineTypeName, String zkTaskId,
                                          int computeType, String engineTaskId, String pluginInfo){


        Pair<Integer, Integer> statusPair = updateJobStatusFrequency(jobId, status);

        if(statusPair.getLeft() == RdosTaskStatus.NOTFOUND.getStatus().intValue() && statusPair.getRight() >= NOT_FOUND_LIMIT_TIMES){

            status = RdosTaskStatus.CANCELED.getStatus();
            zkDistributed.updateSyncLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
            rdosStreamTaskDAO.updateTaskEngineIdAndStatus(jobId, engineTaskId, status);
            updateJobEngineLog(jobId, SYS_CANCLED_LOG, computeType);
        }

        if(RdosTaskStatus.needClean(status)){
            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);

            if(Strings.isNullOrEmpty(engineTaskId)){
                return;
            }

            //TODO 各个插件的操作移动到插件本身
            String checkPath = String.format(FLINK_CP_URL_FORMAT, engineTaskId);
            String checkPointJsonStr = JobClient.getInfoByHttp(engineTypeName, checkPath, pluginInfo);
            updateStreamJobCheckPoint(jobId, engineTaskId, checkPointJsonStr);
        }
    }

    private void updateStreamJobCheckPoint(String taskId, String engineTaskId, String checkpointJsonStr){

        if(Strings.isNullOrEmpty(checkpointJsonStr)){
            logger.info(String.format("taskId %s engineTaskId %s can't get checkpoint info.", taskId, engineTaskId));
            return;
        }

        //获取checkpointJsonStr 的第一个存储的触发时间,最后一个存储的触发时间
        try {
            Map<String, Object> cpJson = PublicUtil.jsonStrToObject(checkpointJsonStr, Map.class);
            if(!cpJson.containsKey(FLINK_CP_HISTORY_KEY)){
                return;
            }

            List<Map<String, Object>> cpList = (List<Map<String, Object>>) cpJson.get(FLINK_CP_HISTORY_KEY);
            if(CollectionUtils.isEmpty(cpList)){
                return;
            }

            Map<String, Object> startNode = cpList.get(0);
            Map<String, Object> endNode = cpList.get(cpList.size() - 1);

            Long startTime = MathUtil.getLongVal(startNode.get(TRIGGER_TIMESTAMP_KEY));
            Long endTime = MathUtil.getLongVal(endNode.get(TRIGGER_TIMESTAMP_KEY));

            Timestamp startTimestamp = new Timestamp(startTime);
            Timestamp endTimestamp = new Timestamp(endTime);

            rdosStreamTaskCheckpointDAO.insert(taskId, engineTaskId, checkpointJsonStr, startTimestamp, endTimestamp);
        } catch (IOException e) {
            logger.error("", e);
        }


    }

    private void dealBatchJobAfterGetStatus(Integer status, String jobId, String zkTaskId, int computeType){

        Pair<Integer, Integer> statusPair = updateJobStatusFrequency(jobId, status);
        if(statusPair.getLeft() == RdosTaskStatus.NOTFOUND.getStatus().intValue() && statusPair.getRight() >= NOT_FOUND_LIMIT_TIMES){

            status = RdosTaskStatus.CANCELED.getStatus();
            zkDistributed.updateSyncLocalBrokerDataAndCleanNoNeedTask(zkTaskId, status);
            rdosBatchEngineJobDAO.updateJobStatusAndExecTime(jobId, status);
            updateJobEngineLog(jobId, SYS_CANCLED_LOG, computeType);
        }

        if(RdosTaskStatus.needClean(status)){
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
    private Pair<Integer, Integer> updateJobStatusFrequency(String jobId, Integer status){

        Pair<Integer, Integer> statusPair = jobStatusFrequency.get(jobId);
        statusPair = statusPair == null ? new MutablePair<>(status, 0) : statusPair;
        if(statusPair.getLeft() == status.intValue()){
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
        if (ComputeType.STREAM.getType().equals(computeType)) {
            rdosStreamTaskDAO.updateTaskStatus(jobId, status);
        } else {
            rdosBatchEngineJobDAO.updateJobStatus(jobId, status);
        }
    }

    /**
     * 从其他节点恢复的任务直接恢复到执行队列
     * @param paramAction
     * @param zkTaskId
     * @throws Exception
     */
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