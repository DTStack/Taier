package com.dtstack.rdos.engine.task;

import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.ClientCache;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.restart.RestartStrategyUtil;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.queue.ExeQueueMgr;
import com.dtstack.rdos.engine.util.TaskIdUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 注意如果是由于资源不足导致的任务失败应该减慢发送速度
 * Date: 2018/3/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RestartDealer {

    private static final Logger LOG = LoggerFactory.getLogger(RestartDealer.class);

    private static final Integer SUBMIT_INTERVAL = 5 * 60 * 1000;

    private static final Integer CHECK_INTERVAL = 10 * 1000;

    private RdosEngineJobCacheDAO engineJobCacheDAO = new RdosEngineJobCacheDAO();

    private RdosEngineBatchJobDAO engineBatchJobDAO = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO engineStreamJobDAO = new RdosEngineStreamJobDAO();

    private ClientCache clientCache = ClientCache.getInstance();

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private Map<String, ResubmitQueue> resubmitQueueMap = Maps.newConcurrentMap();

    private ExecutorService es =  new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), new CustomThreadFactory("restartDealer"));

    private static RestartDealer sigleton = new RestartDealer();

    private RestartDealer(){
        startDealer();
    }

    public static RestartDealer getInstance(){
        return sigleton;
    }


    /**
     * 对提交结果判定是否重试
     * 不限制重试次数
     * @param jobClient
     * @return
     */
    public boolean checkAndRestartForSubmitResult(JobClient jobClient){
        if(!checkNeedReSubmitForSubmitResult(jobClient)){
            return false;
        }

        resetStatus(jobClient.getTaskId(), jobClient.getComputeType().getType(), jobClient.getEngineType());
        addToRestart(jobClient);
        LOG.info("------ job: {} add into orderLinkedBlockingQueue again.", jobClient.getTaskId());
        return true;
    }

    /***
     * 对任务状态判断是否需要重试
     * @param status
     * @param jobId
     * @param engineJobId
     * @param engineType
     * @param computeType
     * @param pluginInfo
     * @return
     */
    public boolean checkAndRestart(Integer status, String jobId, String engineJobId, String engineType,
                                          Integer computeType, String pluginInfo){

        if(!RdosTaskStatus.FAILED.getStatus().equals(status) && !RdosTaskStatus.SUBMITFAILD.getStatus().equals(status)){
            return false;
        }

        try {
            if(!checkNeedResubmit(engineJobId, engineType, pluginInfo)){
                return false;
            }

            resetStatus(jobId, computeType, engineType);
            RdosEngineJobCache jobCache = engineJobCacheDAO.getJobById(jobId);
            if(jobCache == null){
                LOG.error("can't get record from rdos_engine_job_cache by jobId:{}", jobId);
                return false;
            }

            String jobInfo = jobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);
            addToRestart(jobClient);
            LOG.info("------ job: {} add into orderLinkedBlockingQueue again.", jobClient.getTaskId());
            return true;
        } catch (Exception e) {
            LOG.error("", e);
            return false;
        }
    }

    private boolean checkNeedResubmit(String engineJobId, String engineType, String pluginInfo) throws Exception {
        if(Strings.isNullOrEmpty(engineJobId)){
            return false;
        }

        IClient client = clientCache.getClient(engineType, pluginInfo);
        if(client == null){
            LOG.error("can't get client by engineType:{}", engineJobId);
            return false;
        }

        return RestartStrategyUtil.getInstance().checkCanRestart(engineJobId, engineType, client);
    }


    private boolean checkNeedReSubmitForSubmitResult(JobClient jobClient){

        if(jobClient.getJobResult() == null){
            //未提交过
            return true;
        } else if(isSubmitFailOfEngineDown(jobClient)){
            //引擎挂了,需要不断重试
            return true;
        }

        return false;
    }

    private boolean isSubmitFail(JobClient jobClient){

        if(jobClient.getJobResult() != null && jobClient.getJobResult().isErr()){
            return true;
        }

        return false;
    }

    private boolean isSubmitFailOfEngineDown(JobClient jobClient){

        if(!isSubmitFail(jobClient)){
            return false;
        }

        try{
            String engineType = jobClient.getEngineType();
            String resultMsg = jobClient.getJobResult().getMsgInfo();
            return RestartStrategyUtil.getInstance().checkFailureForEngineDown(engineType, resultMsg);

        }catch (Exception e){
            LOG.error("", e);
        }

        return false;

    }

    private void resetStatus(String jobId, Integer computeType, String engineType){
        //更新rdos_engine_batch_task/rdos_engine_stream_task 状态
        //清理engineJobId , 更新db/zk状态为waitCompute
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, engineType, jobId);
        zkDistributed.updateJobZKStatus(zkTaskId, RdosTaskStatus.WAITCOMPUTE.getStatus());
        if(ComputeType.STREAM.getType().equals(computeType)){
            engineStreamJobDAO.updateTaskEngineIdAndStatus(jobId, null, RdosTaskStatus.WAITCOMPUTE.getStatus());
        }else if(ComputeType.BATCH.getType().equals(computeType)){
            engineBatchJobDAO.updateJobEngineIdAndStatus(jobId, null, RdosTaskStatus.WAITCOMPUTE.getStatus());
        }else{
            LOG.error("not support for computeType:{}", computeType);
        }
    }

    private void addToRestart(JobClient jobClient){
        String engineTypeName = jobClient.getEngineType();
        ResubmitQueue resubmitQueue = resubmitQueueMap.computeIfAbsent(engineTypeName, key -> new ResubmitQueue(engineTypeName));
        resubmitQueue.addJobClient(jobClient);
    }

    private void startDealer(){
        es.submit(() -> {
            LOG.warn("------restartDealer thread start------");
            while (true){
                try {
                    Long currentTime = System.currentTimeMillis();
                    for(ResubmitQueue queue : resubmitQueueMap.values()){
                        Long lastExeTime = queue.getLastSubmitTime();
                        if(lastExeTime + SUBMIT_INTERVAL <= currentTime){
                            JobClient jobClient = queue.getJobClient();
                            if(jobClient != null){
                                ExeQueueMgr.getInstance().add(jobClient);
                            }
                        }
                    }
                }catch (Exception e){
                    LOG.error("", e);
                }finally {
                    Thread.sleep(CHECK_INTERVAL);
                }

            }
        });
    }


    class ResubmitQueue{

        private String engineTypeName;

        private Long lastExeTime = -1L;

        private BlockingQueue<JobClient> queue = Queues.newLinkedBlockingQueue();

        public ResubmitQueue(String engineTypeName){
            this.engineTypeName = engineTypeName;
        }

        public JobClient getJobClient(){
            try {
                JobClient jobClient = queue.poll(2, TimeUnit.SECONDS);
                if(jobClient != null){
                    this.lastExeTime = System.currentTimeMillis();
                }

                return jobClient;
            } catch (InterruptedException e) {
                return null;
            }
        }

        public void addJobClient(JobClient jobClient){
            queue.add(jobClient);
        }

        public Long getLastSubmitTime(){
            return lastExeTime;
        }
    }
}
