package com.dtstack.rdos.engine.execution.rdbs.executor;

import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pluginlog.PluginJobInfoComponent;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * mysql 执行队列
 * engineJobId === jobId
 * 1:执行实体
 * 2:监控类
 * //FIXME 不考虑机器挂掉任务恢复的问题，只需要在机器挂掉之后任务设置为失败即可
 * Date: 2018/1/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdbsExeQueue {
    
    private static final Logger LOG = LoggerFactory.getLogger(RdbsExeQueue.class);

    private int minSize = 1;

    /**最大允许同时执行的sql任务长度*/
    private int maxSize = 20;

    private BlockingQueue<Runnable> queue;

    private ExecutorService jobExecutor;

    private ExecutorService monitorExecutor;

    /**优先执行*/
    private BlockingQueue<JobClient> waitQueue = Queues.newLinkedBlockingQueue();

    private Map<String, RdbsExe> threadCache = Maps.newHashMap();

    /**缓存所有进入执行引擎的任务---在执行完成删除*/
    private Map<String, JobClient> jobCache = Maps.newConcurrentMap();

    private PluginJobInfoComponent jobInfoComponent = PluginJobInfoComponent.getPluginJobInfoComponent();

    private ConnFactory connFactory;

    public RdbsExeQueue(ConnFactory connFactory){
        this.connFactory = connFactory;
    }

    public void init(){
        queue = new ArrayBlockingQueue<>(1);
        jobExecutor = new ThreadPoolExecutor(minSize, maxSize, 0, TimeUnit.MILLISECONDS, queue,
                new CustomThreadFactory("mysql-job-exe"));

        monitorExecutor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), new CustomThreadFactory("monitor-exe"));

        monitorExecutor.submit(new WaitQueueDealer());
        monitorExecutor.submit(new StatusUpdateDealer(jobCache));
    }

    /**
     * 提交成功返回engine_id
     */
    public String submit(JobClient jobClient){

        try {
            waitQueue.put(jobClient);
            jobCache.put(jobClient.getTaskId(), jobClient);
            jobInfoComponent.insert(jobClient.getTaskId(), jobClient.getParamAction().toString(), RdosTaskStatus.SCHEDULED.getStatus());
        } catch (InterruptedException e) {
            LOG.error("", e);
            return null;
        }

        return jobClient.getTaskId();
    }

    public boolean checkCanSubmit(){
        if(queue.size() > 0 ){
            return false;
        }else if(waitQueue.size() >= maxSize){
            return false;
        }

        return true;
    }

    public boolean cancelJob(String jobId){
        RdbsExe rdbsExe = threadCache.get(jobId);
        if(rdbsExe == null){
            return false;
        }

        rdbsExe.cancelJob();
        return true;
    }


    public RdosTaskStatus getJobStatus(String jobId){
        Integer status = jobInfoComponent.getStatusByJobId(jobId);
        if(status == null){
            return null;
        }

        return RdosTaskStatus.getTaskStatus(status);
    }

    public String getJobLog(String jobId){
        String logInfo = jobInfoComponent.getLogByJobId(jobId);
        return logInfo == null ? "" : logInfo;
    }


    class RdbsExe implements Runnable {

        private static final String NAME_SPLIT = "_";

        private static final String SEMICOLON = ";";

        private String jobName;

        private String engineJobId;

        private String jobSqlProc;

        private CallableStatement stmt;

        private String procedureName;

        private AtomicBoolean isCancel = new AtomicBoolean(false);

        public RdbsExe(String jobName, String sql, String jobId){
            this.jobName = jobName;
            this.jobSqlProc = createSqlProc(sql, jobName, jobId);
            this.engineJobId = jobId;
        }

        /**
         * 1:需要在存储过程加上事物
         * 2:存储过程在执行完成的时候删除--如何处理机器挂了导致存储过程未被删除
         * @param exeSql
         * @return
         */
        private String createSqlProc(String exeSql, String jobName, String jobId){
            procedureName = jobName + NAME_SPLIT +jobId;
            StringBuilder sb = new StringBuilder(connFactory.getCreateProcedureHeader(procedureName));

            sb.append(exeSql);

            String sql = sb.toString();
            if(!sql.endsWith(SEMICOLON)) {
                sql += SEMICOLON;
            }

            return sql;
        }

        public void cancelJob(){
            isCancel.set(true);
            if(stmt != null){
                try {
                    stmt.cancel();
                } catch (SQLException e) {
                    LOG.error("", e);
                }finally {
                    //更新任务状态
                    jobInfoComponent.updateStatus(engineJobId, RdosTaskStatus.CANCELED.getStatus());
                    jobCache.remove(engineJobId);
                }
            }
        }


        /**
         * TODO 是否可以复用statement?
         */
        @Override
        public void run() {
            if(Strings.isNullOrEmpty(jobSqlProc)){
                return;
            }

            Connection conn = null;
            boolean exeResult = false;

            try{
                conn = connFactory.getConn();
                if(isCancel.get()){
                    LOG.info("job:{} is canceled", jobName);
                    return;
                }

                //创建存储过程
                stmt = conn.prepareCall(jobSqlProc);
                stmt.execute();
                //调用存储过程
                String procCall = connFactory.getCallProc(procedureName);
                stmt = conn.prepareCall(procCall);
                stmt.execute();

                stmt = null;
                exeResult = true;
            }catch (Exception e){
                LOG.error("", e);
                //错误信息更新到日志里面
                jobInfoComponent.updateErrorLog(engineJobId, e.toString());
            }finally {

                try {
                    if(conn != null){
                        //删除存储过程
                        String dropSql = connFactory.getDropProc(procedureName);
                        Statement dropStmt = conn.createStatement();
                        dropStmt.execute(dropSql);
                        dropStmt.close();

                        if(stmt != null && !stmt.isClosed()){
                            stmt.close();
                        }

                        conn.close();
                    }

                } catch (SQLException e) {
                    LOG.error("", e);
                }

                LOG.info("job:{} exe end...", jobName, exeResult);
                //修改指定任务的状态--成功或者失败
                //TODO 处理cancel job 情况
                jobInfoComponent.updateStatus(engineJobId, exeResult ? RdosTaskStatus.FINISHED.getStatus() : RdosTaskStatus.FAILED.getStatus());
                jobCache.remove(engineJobId);
            }
        }
    }

    class WaitQueueDealer implements Runnable{

        private boolean isRun = true;

        @Override
        public void run() {

            LOG.warn("---mysql WaitQueueDealer is start----");
            while (isRun){
                try{
                    JobClient jobClient = waitQueue.take();
                    String taskName = jobClient.getJobName();
                    String sql = jobClient.getSql();
                    String jobId = jobClient.getTaskId();

                    RdbsExe rdbsExe = new RdbsExe(taskName, sql, jobId);
                    try{
                        jobExecutor.submit(rdbsExe);
                    }catch (RejectedExecutionException e){
                        //等待继续执行---说明当时执行队列处于满状态-->先等2s
                        waitQueue.add(jobClient);
                        Thread.sleep(2 * 1000);
                    }
                }catch (Throwable t){
                    LOG.error("", t);
                }
            }

            LOG.warn("---mysql WaitQueueDealer is stop----");
        }

        public void stop(){
            this.isRun = false;
        }
    }

}
