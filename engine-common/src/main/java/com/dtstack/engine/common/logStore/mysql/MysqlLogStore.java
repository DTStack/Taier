package com.dtstack.engine.common.logStore.mysql;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.logStore.LogStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

/**
 * 操作
 * Date: 2018/1/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MysqlLogStore extends AbstractLogStore {
    
    private static final Logger LOG = LoggerFactory.getLogger(MysqlLogStore.class);

    private static final String REPLACE_INTO_SQL = "replace into rdos_plugin_job_info(job_id, job_info, status, log_info, gmt_create, gmt_modified) values(?, ?, ?, ?, NOW(), NOW())";

    private static final String UPDATE_STATUS_SQL = "update rdos_plugin_job_info set status = ?,  gmt_modified = NOW() where job_id = ?";

    private static final String UPDATE_MODIFY_TIME_SQL = "update rdos_plugin_job_info set gmt_modified = NOW() where job_id = ?";

    private static final String UPDATE_JOB_ERRINFO_SQL = "update rdos_plugin_job_info set log_info = ?, status = ?, gmt_modified = NOW() where job_id = ?";

    private static final String GET_STATUS_BY_JOB_ID = "select status from rdos_plugin_job_info where job_id = ?";

    private static final String GET_LOG_BY_JOB_ID = "select log_info from rdos_plugin_job_info where job_id = ?";

    private static final String TIME_OUT_ERR_INFO = "task lose connect(maybe: engine shutdown)";

    /**未完成的任务在60s内没有任务更新操作---认为任务已经挂了*/
    private static final String TIME_OUT_TO_FAIL_SQL = String.format("update rdos_plugin_job_info set status = 8, log_info = '%s', " +
            " gmt_modified = NOW()  where status not in(5,7,8,9,13,14,15) and (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(gmt_modified)) > 60 ", TIME_OUT_ERR_INFO);

    /**清理数据库中更新时间超过7天的记录*/
    private static int retain_time = 604800;

    private static final String RETAIN_CLEAR_SQL = "delete from rdos_plugin_job_info where status in (5,7,8,9,13,14,15) and (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(gmt_modified)) > " + retain_time;

    private MysqlDataConnPool dataConnPool = MysqlDataConnPool.getInstance();

    private static volatile MysqlLogStore mysqlLogStore = null;

    private MysqlLogStore(){

    }

    public static MysqlLogStore getInstance(){
        if(mysqlLogStore==null){
            synchronized (MysqlLogStore.class){
                if(mysqlLogStore==null){
                    mysqlLogStore = new MysqlLogStore();
                }
            }
        }
        return mysqlLogStore;
    }

    @Override
    public int insert(String jobId, String jobInfo, int status){
        MysqlDataConnPool metaDataConnPool = MysqlDataConnPool.getInstance();
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = metaDataConnPool.getConn();
            pstmt = connection.prepareStatement(REPLACE_INTO_SQL);
            pstmt.setString(1, jobId);
            pstmt.setString(2, jobInfo);
            pstmt.setInt(3, status);
            pstmt.setString(4, "");

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            LOG.error("", e);
            return 0;
        }finally {
            try {

                if(pstmt != null){
                    pstmt.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public int updateStatus(String jobId, int status){
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
        }finally {
            try {

                if(pstmt != null){
                    pstmt.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public void updateModifyTime(Collection<String> jobIds){
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = dataConnPool.getConn();
            pstmt = connection.prepareStatement(UPDATE_MODIFY_TIME_SQL);
            for(String jobId : jobIds){
                pstmt.setString(1, jobId);
                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            LOG.error("", e);
        }finally {
            try {

                if(pstmt != null){
                    pstmt.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public void updateErrorLog(String jobId, String errorLog){
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
        }finally {
            try {

                if(pstmt != null){
                    pstmt.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public Integer getStatusByJobId(String jobId){
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = dataConnPool.getConn();
            pstmt = connection.prepareStatement(GET_STATUS_BY_JOB_ID);
            pstmt.setString(1, jobId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                return rs.getInt(1);
            }

            return null;

        } catch (SQLException e) {
            LOG.error("", e);
            return null;
        }finally {
            try {

                if(pstmt != null){
                    pstmt.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public String getLogByJobId(String jobId){
        MysqlDataConnPool metaDataConnPool = MysqlDataConnPool.getInstance();
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = metaDataConnPool.getConn();
            pstmt = connection.prepareStatement(GET_LOG_BY_JOB_ID);
            pstmt.setString(1, jobId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                return rs.getString(1);
            }

            return null;

        } catch (SQLException e) {
            LOG.error("", e);
            return null;
        }finally {
            try {

                if(pstmt != null){
                    pstmt.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public void timeOutDeal(){
        MysqlDataConnPool metaDataConnPool = MysqlDataConnPool.getInstance();
        Connection connection = null;
        Statement stmt = null;

        try {
            connection = metaDataConnPool.getConn();
            stmt = connection.createStatement();
            stmt.executeUpdate(TIME_OUT_TO_FAIL_SQL);

        } catch (SQLException e) {
            LOG.error("", e);
        }finally {
            try {
                if(stmt != null){
                    stmt.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public void clearJob(){
        MysqlDataConnPool metaDataConnPool = MysqlDataConnPool.getInstance();
        Connection connection = null;
        Statement stmt = null;

        try {
            connection = metaDataConnPool.getConn();
            stmt = connection.createStatement();
            stmt.executeUpdate(RETAIN_CLEAR_SQL);

        } catch (SQLException e) {
            LOG.error("", e);
        }finally {
            try {
                if(stmt != null){
                    stmt.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }
    }

}
