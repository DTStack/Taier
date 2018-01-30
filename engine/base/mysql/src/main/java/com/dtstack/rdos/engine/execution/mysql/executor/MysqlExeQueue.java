package com.dtstack.rdos.engine.execution.mysql.executor;

import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.mysql.ConnPool;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * mysql 执行队列
 * engineJobId === jobId
 * 1:执行实体
 * 2:监控类
 *
 * TODO 如何保证sql都执行完成(机器挂了如何处理) -- 分布式--数据库还是zk? ---暂时不考虑引入分布式处理
 * TODO ---但是需要如何处理在机器挂掉之后如何将状态设置为失败
 * TODO sql执行完成之后如何保证状态可以提供查询？是否像flink一样保存最新n个(同时设定最小保存时间)
 * Date: 2018/1/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MysqlExeQueue {
    
    private static final Logger LOG = LoggerFactory.getLogger(MysqlExeQueue.class);

    private int minSize = 1;

    /**最大允许同时执行的sql任务长度*/
    private int maxSize = 100;

    private BlockingQueue<Runnable> queue;

    private ExecutorService executor;

    private Map<String, MysqlExe> cache = Maps.newHashMap();

    public void init(){
        queue = new ArrayBlockingQueue<>(1);
        executor = new ThreadPoolExecutor(minSize, maxSize, 0, TimeUnit.MILLISECONDS, queue,
                new CustomThreadFactory("mysql-exe-queue"));
    }

    /**
     * 提交成功返回engine_id
     * @param taskName
     * @param sql
     */
    public String submit(String taskName, String sql, String jobId){
        MysqlExe mysqlExe = new MysqlExe(taskName, sql, jobId);
        try{
            executor.submit(mysqlExe);
            //添加到zk上
        }catch (RejectedExecutionException e){
            //TODO 等待继续继续执行
        }

        //TODO
        return "00";
    }

    public boolean checkCanSubmit(){
        if(queue.size() > 0){
            return false;
        }

        return true;
    }

    public boolean cancelJob(String jobId){
        MysqlExe mysqlExe = cache.get(jobId);
        if(mysqlExe == null){
            return false;
        }

        mysqlExe.cancelJob();
        return true;
    }


    public RdosTaskStatus getJobStatus(){
        //TODO 查库

        return RdosTaskStatus.FINISHED;
    }


    class MysqlExe implements Runnable {

        private static final String NAME_SPLIT = "_";

        private String jobName;

        private String engineJobId;

        private String jobSqlProc;

        private CallableStatement stmt;

        private String procedureName;

        private boolean isCancel = false;

        public MysqlExe(String jobName, String sql, String jobId){
            this.jobName = jobName;
            this.jobSqlProc = createSqlProc(sql, jobName, jobId);
            this.engineJobId = jobId;
        }

        /**
         * 1:需要在存储过程加上事物
         * 2:存储过程在执行完成的时候删除
         * @param exeSql
         * @return
         */
        private String createSqlProc(String exeSql, String jobName, String jobId){
            procedureName = jobName + NAME_SPLIT +jobId;
            StringBuilder sb = new StringBuilder(String.format("create procedure %s()", procedureName));
            sb.append("BEGIN")
              .append("START TRANSACTION;")
              .append(exeSql)
              .append("ROLLBACK;")
              .append("END");

            return sb.toString();
        }

        public void cancelJob(){
            isCancel = true;
            if(stmt != null){
                try {
                    stmt.cancel();
                } catch (SQLException e) {
                    LOG.error("", e);
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
            try{
                conn = ConnPool.getInstance().getConn();
                boolean result;
                if(isCancel){
                    LOG.info("job:{} is cancled", jobName);
                    return;
                }

                //创建存储过程
                stmt = conn.prepareCall(jobSqlProc);
                result = stmt.execute();
                if(!result){
                    return;
                }

                //调用存储过程
                String procCall = String.format("{call %s()}", procedureName);
                stmt = conn.prepareCall(procCall);
                result = stmt.execute();
                if(!result){
                    return;
                }

                stmt = null;

                LOG.info("job:{} exe result:{}", jobName, result);
            }catch (Exception e){
                LOG.error("", e);
            }finally {

                try {

                    //删除存储过程
                    String dropSql = String.format("DROP PROCEDURE IF EXISTS `%s`", procedureName);
                    Statement dropStmt = conn.createStatement();
                    dropStmt.execute(dropSql);
                    dropStmt.close();

                    if(stmt != null && !stmt.isClosed()){
                        stmt.close();
                    }

                    conn.close();
                } catch (SQLException e) {
                    LOG.error("", e);
                }

                LOG.info("job:{} exe end...", jobName);
            }
        }
    }

}
