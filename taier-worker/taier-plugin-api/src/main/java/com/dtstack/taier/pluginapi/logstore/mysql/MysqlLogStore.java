/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.pluginapi.logstore.mysql;

import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.logstore.AbstractLogStore;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * 操作
 * Date: 2018/1/30
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class MysqlLogStore extends AbstractLogStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlLogStore.class);

    private static final String REPLACE_INTO_SQL = "replace into schedule_plugin_job_info(job_id, job_info, status, log_info, gmt_create, gmt_modified) values(?, ?, ?, ?, NOW(), NOW())";

    private static final String UPDATE_STATUS_SQL = "update schedule_plugin_job_info set status = ?,  gmt_modified = NOW() where job_id = ?";

    private static final String UPDATE_MODIFY_TIME_SQL = "update schedule_plugin_job_info set gmt_modified = NOW() where job_id = ?";

    private static final String UPDATE_JOB_ERRINFO_SQL = "update schedule_plugin_job_info set log_info = ?, status = ?, gmt_modified = NOW() where job_id = ?";

    private static final String GET_STATUS_BY_JOB_ID = "select status,id,gmt_modified from schedule_plugin_job_info where job_id = ?";

    private static final String GET_LOG_BY_JOB_ID = "select log_info from schedule_plugin_job_info where job_id = ?";

    private static final String TIME_OUT_ERR_INFO = "task lose connect(maybe: engine shutdown)";



    private static final String SELECT_MIN_ID_SQL = " select min(id) as id from schedule_plugin_job_info";


    //timeOutDeal
    private final static List<Integer> JOB_EXECUTE_STATUS = Lists.newArrayList(
            TaskStatus.SCHEDULED.getStatus(),
            TaskStatus.RUNNING.getStatus()
    );
    private static final String SELECT_JOB_EXECUTE_STATUS_TEMPLATE = " select id from schedule_plugin_job_info " +
            " where id > ? and id <= ? and status in (" + StringUtils.join(JOB_EXECUTE_STATUS, ",") + ") and gmt_modified < ? order by id asc limit ?";

    private static final String UPDATE_TIME_OUT_TO_FAIL_SQL = String.format("update schedule_plugin_job_info set status = 8, log_info = '%s', gmt_modified = NOW() " +
            " where id in ", TIME_OUT_ERR_INFO);

    //clearJob
    private final static List<Integer> JOB_FINISHED_STATUS = Lists.newArrayList(
            TaskStatus.CANCELED.getStatus(),
            TaskStatus.FINISHED.getStatus(),
            TaskStatus.FAILED.getStatus()
    );
    private static final String SELECT_JOB_FINISHED_STATUS_TEMPLATE = " select id from schedule_plugin_job_info " +
            " where id > ? and id <= ? and status in (" + StringUtils.join(JOB_FINISHED_STATUS, ",") + ") and gmt_modified < ? order by id asc limit ?";


    private static final String DELETE_RETAIN_CLEAR_SQL = "delete from schedule_plugin_job_info where id in ";

    /**
     * 500行为1个批次
     */
    private static final int BATCH_SIZE = 500;
    /**
     * 100秒
     */
    private static final long TIMEOUT = 100000;


    /**
     * 清理数据库中更新时间超过7天的记录
     */
    private static final long RETAIN_TIME = 604800000;

    private static MysqlDataConnPool dataConnPool;

    private static volatile MysqlLogStore mysqlLogStore = null;

    private MysqlLogStore() {
    }

    public static MysqlLogStore getInstance(Map<String, String> dbConfig) {
        if (dbConfig == null) {
            return null;
        }
        if (mysqlLogStore == null) {
            synchronized (MysqlLogStore.class) {
                if (mysqlLogStore == null) {
                    dataConnPool = MysqlDataConnPool.getInstance(dbConfig);
                    mysqlLogStore = new MysqlLogStore();
                }
            }
        }
        return mysqlLogStore;
    }

    @Override
    public int insert(String jobId, String jobInfo, int status) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = dataConnPool.getConn();
            pstmt = connection.prepareStatement(REPLACE_INTO_SQL);
            pstmt.setString(1, jobId);
            pstmt.setString(2, jobInfo);
            pstmt.setInt(3, status);
            pstmt.setString(4, "");

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("", e);
            return 0;
        } finally {
            closeDBResources(null, pstmt, null, connection);
        }
    }

    @Override
    public int updateStatus(String jobId, int status) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = dataConnPool.getConn();
            pstmt = connection.prepareStatement(UPDATE_STATUS_SQL);
            pstmt.setInt(1, status);
            pstmt.setString(2, jobId);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("", e);
            return 0;
        } finally {
            closeDBResources(null, pstmt, null, connection);
        }
    }

    @Override
    public void updateModifyTime(Collection<String> jobIds) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = dataConnPool.getConn();
            pstmt = connection.prepareStatement(UPDATE_MODIFY_TIME_SQL);
            for (String jobId : jobIds) {
                pstmt.setString(1, jobId);
                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            closeDBResources(null, pstmt, null, connection);
        }
    }

    @Override
    public void updateErrorLog(String jobId, String errorLog) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = dataConnPool.getConn();
            pstmt = connection.prepareStatement(UPDATE_JOB_ERRINFO_SQL);
            pstmt.setString(1, errorLog);
            pstmt.setInt(2, TaskStatus.FAILED.getStatus());
            pstmt.setString(3, jobId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            closeDBResources(null, pstmt, null, connection);
        }
    }

    @Override
    public Integer getStatusByJobId(String jobId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataConnPool.getConn();
            preparedStatement = connection.prepareStatement(GET_STATUS_BY_JOB_ID);
            preparedStatement.setString(1, jobId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Timestamp gmtModified = resultSet.getTimestamp("gmt_modified");
                if (gmtModified.getTime() < System.currentTimeMillis() - TIMEOUT){
                    batchExecuteJobTimeOutById(UPDATE_TIME_OUT_TO_FAIL_SQL, Collections.singletonList(resultSet.getLong("id")),connection);
                }
                return resultSet.getInt("status");
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            closeDBResources(resultSet, preparedStatement, null, connection);
        }
        //默认失败
        return TaskStatus.FAILED.getStatus();
    }

    @Override
    public String getLogByJobId(String jobId) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            connection = dataConnPool.getConn();
            pstmt = connection.prepareStatement(GET_LOG_BY_JOB_ID);
            pstmt.setString(1, jobId);

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            closeDBResources(resultSet, pstmt, null, connection);
        }
        return "";
    }

    @Override
    public void timeOutDeal() {
        dealBatchDataTimeout(SELECT_JOB_EXECUTE_STATUS_TEMPLATE, UPDATE_TIME_OUT_TO_FAIL_SQL, TIMEOUT);
    }

    @Override
    public void clearJob() {
        dealBatchDataTimeout(SELECT_JOB_FINISHED_STATUS_TEMPLATE, DELETE_RETAIN_CLEAR_SQL, RETAIN_TIME);
    }

    private void dealBatchDataTimeout(String selectSqlTemplate, String dealSql, long timeout) {
        Connection connection = null;

        long startTime = System.currentTimeMillis() - timeout;
        Timestamp timestamp = new Timestamp(startTime);
        try {
            connection = dataConnPool.getConn();

            long startId = getMinId(connection);

            while (true) {
                PreparedStatement stmt = null;
                PreparedStatement updateStmt = null;
                ResultSet resultSet = null;
                try {
                    stmt = connection.prepareStatement(selectSqlTemplate);
                    stmt.setLong(1, startId);
                    stmt.setLong(2, startId + (BATCH_SIZE * 10));
                    stmt.setTimestamp(3, timestamp);
                    stmt.setInt(4, BATCH_SIZE);


                    resultSet = stmt.executeQuery();
                    List<Long> ids = new ArrayList<>(BATCH_SIZE);
                    while (resultSet.next()) {
                        Long id = resultSet.getLong("id");
                        ids.add(id);
                        startId = id;
                    }
                    if (ids.isEmpty()) {
                        break;
                    }

                    updateStmt= batchExecuteJobTimeOutById(dealSql, ids, connection);
                    LOGGER.info("deal SQL:{} affect ids:{}", dealSql, ids);
                } catch (SQLException e) {
                    LOGGER.error("", e);
                    break;
                } finally {
                    closeDBResources(resultSet, stmt, updateStmt, null);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            closeDBResources(null, null, null, connection);
        }
    }

    private void closeDBResources(ResultSet rs, Statement stmt, Statement stmt2, Connection conn) {
        try {
            if (null != rs) {
                rs.close();
            }

            if (null != stmt) {
                stmt.close();
            }

            if (null != stmt2) {
                stmt2.close();
            }

            if (null != conn) {
                conn.close();
            }
        } catch (Throwable t) {
            LOGGER.error("", t);
        }
    }

    private long getMinId(Connection connection) {
        PreparedStatement minIdPs = null;
        ResultSet minIdRs = null;
        try {
            minIdPs = connection.prepareStatement(SELECT_MIN_ID_SQL);
            minIdRs = minIdPs.executeQuery();
            while (minIdRs.next()) {
                Long minId = minIdRs.getLong("id");
                return minId;
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            closeDBResources(minIdRs, minIdPs,null,null);
        }
        return 0L;
    }

    private PreparedStatement batchExecuteJobTimeOutById(String sql, List<Long> ids, Connection connection)
            throws SQLException {
        StringBuilder prepareStatementSql = new StringBuilder(sql);
        prepareStatementSql.append(" (");
        for (int i = 0; i < ids.size(); i++) {
            prepareStatementSql.append("?,");
        }
        prepareStatementSql.deleteCharAt(prepareStatementSql.length() - 1).append(") ");
        try(PreparedStatement updateStmt = connection.prepareStatement(prepareStatementSql.toString())){
            int parameterIndex = 1;
            for (long id : ids) {
                updateStmt.setLong(parameterIndex++, id);
            }
            updateStmt.executeUpdate();
            return updateStmt;
        }catch (Exception e){
            LOGGER.error("executeUpdate error:",e);
        }
        return null;
    }

}
