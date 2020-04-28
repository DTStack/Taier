package com.dtstack.engine.rdbs.common.executor;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.logstore.LogStoreFactory;
import com.dtstack.engine.common.util.DateUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mysql 执行队列
 * engineJobId === jobId
 * 1:执行实体
 * 2:监控类
 * //FIXME 不考虑机器挂掉任务恢复的问题，只需要在机器挂掉之后任务设置为失败即可
 * Date: 2018/1/29
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class RdbsExeQueue {

    private static final Logger LOG = LoggerFactory.getLogger(RdbsExeQueue.class);

    private static String ORACLE_STATEMENT_CLASS_NAME = "oracle.jdbc.driver.T4CStatement";

    private static Pattern pattern = Pattern.compile("^select");

    private int minSize = 20;

    /**
     * 最大允许同时执行的sql任务长度
     */
    private int maxSize = 20;

    private BlockingQueue<Runnable> queue;

    private ExecutorService jobExecutor;

    private ExecutorService monitorExecutor;

    /**
     * 优先执行
     */
    private BlockingQueue<JobClient> waitQueue = Queues.newLinkedBlockingQueue();

    private Map<String, RdbsExe> threadCache = Maps.newHashMap();

    /**
     * 缓存所有进入执行引擎的任务---在执行完成删除
     */
    private Map<String, JobClient> jobCache = Maps.newConcurrentMap();

    private AbstractConnFactory connFactory;

    private StatusUpdateDealer statusUpdateDealer;

    public RdbsExeQueue(AbstractConnFactory connFactory, Integer maxPoolSize, Integer minPoolSize) {
        this.connFactory = connFactory;
        if (maxPoolSize != null) {
            this.maxSize = maxPoolSize;
        }
        if (minPoolSize != null) {
            this.minSize = minPoolSize;
        }
    }

    public void init() {
        queue = new ArrayBlockingQueue<>(1);
        jobExecutor = new ThreadPoolExecutor(minSize, maxSize, 0, TimeUnit.MILLISECONDS, queue,
                new CustomThreadFactory("rdb-job-exe"));

        monitorExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), new CustomThreadFactory("monitor-exe"));

        monitorExecutor.submit(new WaitQueueDealer());
        statusUpdateDealer = new StatusUpdateDealer(jobCache);
        statusUpdateDealer.start();
    }

    /**
     * 提交成功返回engine_id
     */
    public String submit(JobClient jobClient) {

        try {
            if (LogStoreFactory.getLogStore() != null) {
                LogStoreFactory.getLogStore().insert(jobClient.getTaskId(), jobClient.getParamAction().toString(), RdosTaskStatus.SCHEDULED.getStatus());
            }
            jobCache.put(jobClient.getTaskId(), jobClient);
            waitQueue.put(jobClient);
        } catch (InterruptedException e) {
            LOG.error("", e);
            return null;
        }

        return jobClient.getTaskId();
    }

    public boolean checkCanSubmit() {
        if (queue.size() > 0) {
            return false;
        } else if (waitQueue.size() >= maxSize) {
            return false;
        }

        return true;
    }

    public boolean cancelJob(String jobId) {
        RdbsExe rdbsExe = threadCache.get(jobId);
        if (rdbsExe == null) {
            return false;
        }

        rdbsExe.cancelJob();
        threadCache.remove(jobId);
        return true;
    }


    public RdosTaskStatus getJobStatus(String jobId) {
        if (LogStoreFactory.getLogStore() != null) {
            Integer status = LogStoreFactory.getLogStore().getStatusByJobId(jobId);
            if (status != null) {
                return RdosTaskStatus.getTaskStatus(status);
            }
        }
        return null;
    }

    public String getJobLog(String jobId) {
        String logInfo = null;
        if (LogStoreFactory.getLogStore() != null) {
            logInfo = LogStoreFactory.getLogStore().getLogByJobId(jobId);
        }
        return logInfo == null ? "" : logInfo;
    }


    class RdbsExe implements Runnable {

        private static final String NAME_SPLIT = "_";

        private static final String PRODUCE_NAME_PREFIX = "prod";

        private static final String SEMICOLON = ";";

        private String jobName;

        private String engineJobId;

        private String jobSqlProc;

        private List<String> sqlList;

        private CallableStatement stmt;

        private Statement simpleStmt;

        private String procedureName;

        private AtomicBoolean isCancel = new AtomicBoolean(false);

        private String taskParams;

        public RdbsExe(String jobName, String sql, String jobId) {
            this(jobName, sql, jobId, null);
        }

        public RdbsExe(String jobName, String sql, String jobId, String taskParams) {
            this.jobName = jobName;
            this.taskParams = taskParams;
            if (connFactory.supportProcedure()) {
                jobSqlProc = createSqlProc(sql, jobName, jobId);
            } else {
                sqlList = connFactory.buildSqlList(sql);
            }
            this.engineJobId = jobId;
        }

        private boolean executeSqlList() {
            Connection conn = null;
            boolean exeResult = false;
            long start = System.currentTimeMillis();
            String currentSql = "";
            try {
                conn = connFactory.getConnByTaskParams(taskParams, jobName);
                if (connFactory.supportTransaction()) {
                    conn.setAutoCommit(false);
                }
                if(isCancel.get()){
                    LOG.info("exe cancel, jobId={}, jobName={} is canceled", engineJobId, jobName);
                    return false;
                }

                simpleStmt = conn.createStatement();
                if (LogStoreFactory.getLogStore() != null) {
                    LogStoreFactory.getLogStore().updateStatus(engineJobId, RdosTaskStatus.RUNNING.getStatus());
                }
                int i = 1;
                for (String sql : sqlList) {
                    currentSql = sql;
                    if (isSelectSql(sql)) {
                        LOG.info("exe {} line skip,jobId={},jobName={},sql={}", i++, engineJobId, jobName, sql);
                        continue;
                    }

                    long sqlStart = System.currentTimeMillis();
                    simpleStmt.execute(currentSql);
                    LOG.info("exe {} line success,jobId={},jobName={},cost={}ms", i++, engineJobId, jobName, (System.currentTimeMillis() - sqlStart));
                    if (isCancel.get()) {
                        LOG.info("exe cancel,jobId={},jobName={}", engineJobId, jobName);
                        return false;
                    }
                }
                if (connFactory.supportTransaction()) {
                    conn.commit();
                }
                exeResult = true;
            } catch (Exception e) {
                LOG.error("exe error,jobId={},jobName={},ex={}",engineJobId, jobName, e);
                if (connFactory.supportTransaction()) {
                    try {
                        conn.rollback();
                    } catch (SQLException e1) {
                        LOG.error("rollback error,jobId={},jobName={},ex={}", engineJobId, jobName, e1);
                    }
                }
                //错误信息更新到日志里面
                if (LogStoreFactory.getLogStore() != null) {
                    LogStoreFactory.getLogStore().updateErrorLog(engineJobId, String.format("startTime=[%s],endTime=[%s],sql=[%s]\n\r error=[%s]", DateUtil.getDate(start, "yyyyMMdd HH:mm:ss"),
                            DateUtil.getDate(new Date(), "yyyyMMdd HH:mm:ss"), currentSql, e.toString()));
                }
            } finally {

                try {
                    if (conn != null) {

                        if (simpleStmt != null && !simpleStmt.isClosed()) {
                            simpleStmt.close();
                        }

                        conn.close();
                    }

                } catch (SQLException e) {
                    LOG.error("", e);
                }

                LOG.info("exe finish, jobId={},jobName={},exeResult={},cost={}ms", engineJobId, jobName, exeResult, (System.currentTimeMillis() - start));
                //修改指定任务的状态--成功或者失败
                //处理cancel job 情况
                if (LogStoreFactory.getLogStore() != null) {
                    LogStoreFactory.getLogStore().updateStatus(engineJobId, exeResult ? RdosTaskStatus.FINISHED.getStatus() : RdosTaskStatus.FAILED.getStatus());
                }
                jobCache.remove(engineJobId);
                threadCache.remove(engineJobId);
            }
            return exeResult;

        }

        private boolean isSelectSql(String sql) {
            Matcher matcher = pattern.matcher(sql.toLowerCase().trim());
            if (matcher.find()) {
                return true;
            }
            return false;
        }

        /**
         * 1:需要在存储过程加上事物
         * 2:存储过程在执行完成的时候删除--如何处理机器挂了导致存储过程未被删除
         * @param exeSql
         * @return
         */
        private String createSqlProc(String exeSql, String jobName, String jobId) {
            procedureName = PRODUCE_NAME_PREFIX + NAME_SPLIT + jobId;
            StringBuilder sb = new StringBuilder(connFactory.getCreateProcedureHeader(procedureName));

            sb.append(exeSql);

            if (!exeSql.trim().endsWith(SEMICOLON)) {
                sb.append(SEMICOLON);
            }

            sb.append(connFactory.getCreateProcedureTailer());

            return sb.toString();
        }

        public void cancelJob() {
            isCancel.set(true);
            if (stmt != null) {
                try {
                    stmt.cancel();
                } catch (SQLException e) {
                    LOG.error("", e);
                } finally {
                    //更新任务状态
                    if (LogStoreFactory.getLogStore() != null) {
                        LogStoreFactory.getLogStore().updateStatus(engineJobId, RdosTaskStatus.CANCELED.getStatus());
                    }
                    jobCache.remove(engineJobId);
                }
            }
        }

        private boolean runProc() {
            Connection conn = null;
            boolean exeResult = false;
            Statement procCreateStmt = null;

            try {
                conn = connFactory.getConn();
                if (isCancel.get()) {
                    LOG.info("job:{} is canceled", jobName);
                    return false;
                }

                //创建存储过程
                procCreateStmt = conn.createStatement();
                procCreateStmt.execute(jobSqlProc);

                //调用存储过程
                String procCall = connFactory.getCallProc(procedureName);
                stmt = conn.prepareCall(procCall);
                stmt.execute();

                exeResult = true;
            } catch (Exception e) {
                LOG.error("", e);
                //错误信息更新到日志里面
                if (LogStoreFactory.getLogStore() != null) {
                    LogStoreFactory.getLogStore().updateErrorLog(engineJobId, e.toString());
                }
            } finally {
                closeDBResources(procCreateStmt, null);

                Statement dropStmt = null;
                try {
                    if (conn != null) {
                        //删除存储过程
                        String dropSql = connFactory.getDropProc(procedureName);
                        dropStmt = conn.createStatement();
                        dropStmt.execute(dropSql);
                    }
                } catch (Exception e) {
                    LOG.error("", e);
                } finally {
                    closeDBResources(dropStmt, null);
                }

                closeDBResources(stmt, conn);

                LOG.info("job:{} exe end...", jobName, exeResult);
                //修改指定任务的状态--成功或者失败
                if (LogStoreFactory.getLogStore() != null) {
                    LogStoreFactory.getLogStore().updateStatus(engineJobId, exeResult ? RdosTaskStatus.FINISHED.getStatus() : RdosTaskStatus.FAILED.getStatus());
                }
                jobCache.remove(engineJobId);
            }
            return exeResult;
        }


        /**
         * TODO 是否可以复用statement?
         */
        @Override
        public void run() {
            if (CollectionUtils.isNotEmpty(sqlList)) {
                executeSqlList();
            } else {
                runProc();
            }
        }

        private void closeDBResources(Statement stmt, Connection conn) {
            try {
                if (null != stmt) {
                    stmt.close();
                }

                if (null != conn) {
                    conn.close();
                }
            } catch (Throwable t) {
                LOG.error("", t);
            }
        }
    }


    class WaitQueueDealer implements Runnable {

        private boolean isRun = true;

        @Override
        public void run() {

            LOG.warn("---mysql WaitQueueDealer is start----");
            while (isRun) {
                try {
                    JobClient jobClient = waitQueue.take();
                    String taskName = jobClient.getJobName();
                    String sql = jobClient.getSql();
                    String jobId = jobClient.getTaskId();

                    RdbsExe rdbsExe = new RdbsExe(taskName, sql, jobId, jobClient.getTaskParams());
                    try {
                        jobExecutor.submit(rdbsExe);
                        threadCache.put(jobId, rdbsExe);
                    } catch (RejectedExecutionException e) {
                        //等待继续执行---说明当时执行队列处于满状态-->先等2s
                        waitQueue.add(jobClient);
                        Thread.sleep(2 * 1000);
                    }
                } catch (Throwable t) {
                    LOG.error("", t);
                }
            }

            LOG.warn("---mysql WaitQueueDealer is stop----");
        }

        public void stop() {
            this.isRun = false;
        }
    }

}