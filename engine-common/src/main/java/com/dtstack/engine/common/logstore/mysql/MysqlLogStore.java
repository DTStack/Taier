package com.dtstack.engine.common.logstore.mysql;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.logstore.AbstractLogStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 操作
 * Date: 2018/1/30
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class MysqlLogStore extends AbstractLogStore {

    private static final Logger LOG = LoggerFactory.getLogger(MysqlLogStore.class);

    private static final String REPLACE_INTO_SQL = "replace into schedule_plugin_job_info(job_id, job_info, status, log_info, gmt_create, gmt_modified) values(?, ?, ?, ?, NOW(), NOW())";

    private static final String UPDATE_STATUS_SQL = "update schedule_plugin_job_info set status = ?,  gmt_modified = NOW() where job_id = ?";

    private static final String UPDATE_MODIFY_TIME_SQL = "update schedule_plugin_job_info set gmt_modified = NOW() where job_id = ?";

    private static final String UPDATE_JOB_ERRINFO_SQL = "update schedule_plugin_job_info set log_info = ?, status = ?, gmt_modified = NOW() where job_id = ?";

    private static final String GET_STATUS_BY_JOB_ID = "select status from schedule_plugin_job_info where job_id = ?";

    private static final String GET_LOG_BY_JOB_ID = "select log_info from schedule_plugin_job_info where job_id = ?";

    private static final String TIME_OUT_ERR_INFO = "task lose connect(maybe: engine shutdown)";

    private static final String SELECT_TIME_OUT_TO_FAIL_SQL = " select " +
            " id, job_id, job_info, log_info, status, gmt_create, gmt_modified, is_deleted " +
            " from schedule_plugin_job_info " +
            " where id > ? and status not in (" + StringUtils.join(RdosTaskStatus.getStoppedAndNotFound(), ",") + ") and gmt_modified < ? order by id asc limit ?";

    private static final String UPDATE_TIME_OUT_TO_FAIL_SQL = String.format("update schedule_plugin_job_info set status = 8, log_info = '%s', gmt_modified = NOW() " +
            " where id in ", TIME_OUT_ERR_INFO);
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
    private static int retainTime = 604800;

    private static final String DELETE_RETAIN_CLEAR_SQL = "delete from schedule_plugin_job_info where id in ";

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
            LOG.error("", e);
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
            LOG.error("", e);
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
            LOG.error("", e);
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
            pstmt.setInt(2, RdosTaskStatus.FAILED.getStatus());
            pstmt.setString(3, jobId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOG.error("", e);
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
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOG.error("", e);
        } finally {
            closeDBResources(resultSet, preparedStatement, null, connection);
        }
        return 0;
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
            LOG.error("", e);
        } finally {
            closeDBResources(resultSet, pstmt, null, connection);
        }
        return "";
    }

    @Override
    public void timeOutDeal() {
        dealBatchDataTimeout(UPDATE_TIME_OUT_TO_FAIL_SQL, TIMEOUT);
    }

    @Override
    public void clearJob() {
        dealBatchDataTimeout(DELETE_RETAIN_CLEAR_SQL, retainTime);
    }

    private void dealBatchDataTimeout(String sql, long timeout) {
        Connection connection = null;

        long startId = 0L;
        long startTime = System.currentTimeMillis() - timeout;
        Timestamp timestamp = new Timestamp(startTime);
        try {
            connection = dataConnPool.getConn();

            while (true) {
                PreparedStatement stmt = null;
                PreparedStatement updateStmt = null;
                ResultSet resultSet = null;
                try {
                    stmt = connection.prepareStatement(SELECT_TIME_OUT_TO_FAIL_SQL);
                    stmt.setLong(1, startId);
                    stmt.setTimestamp(2, timestamp);
                    stmt.setInt(3, BATCH_SIZE);


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

                    StringBuilder prepareStatementSql = new StringBuilder(sql);
                    prepareStatementSql.append(" (");
                    for (int i = 0; i < ids.size(); i++) {
                        prepareStatementSql.append("?,");
                    }
                    prepareStatementSql = prepareStatementSql.deleteCharAt(prepareStatementSql.length() - 1);
                    prepareStatementSql.append(") ");

                    updateStmt = connection.prepareStatement(prepareStatementSql.toString());
                    int parameterIndex = 1;
                    for (long id : ids) {
                        updateStmt.setLong(parameterIndex++, id);
                    }
                    updateStmt.executeUpdate();
                } catch (SQLException e) {
                    LOG.error("", e);
                    break;
                } finally {
                    closeDBResources(resultSet, stmt, updateStmt, null);
                }
            }
        } catch (SQLException e) {
            LOG.error("", e);
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
            LOG.error("", t);
        }
    }

}
