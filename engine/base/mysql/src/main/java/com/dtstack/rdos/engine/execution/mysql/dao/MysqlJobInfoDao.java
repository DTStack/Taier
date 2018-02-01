package com.dtstack.rdos.engine.execution.mysql.dao;

import com.dtstack.rdos.engine.execution.mysql.executor.ConnPool;
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
 * TODO 建库 id, jobId, jobInfo, status, gmt_create, gmt_modified
 * Date: 2018/1/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MysqlJobInfoDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(MysqlJobInfoDao.class);

    private static final String INSERT_SQL = "insert into mysql_job_info(job_id, job_info, status, gmt_create, gmt_modified) values(?, ?, ?, NOW(), NOW())";

    private static final String UPDATE_STATUS_SQL = "update mysql_job_info set status = ?,  gmt_modified = NOW() where job_id = ?";

    private static final String UPDATE_MODIFY_TIME_SQL = "update mysql_job_info set gmt_modified = NOW() where job_id = ?";

    private static final String GET_STATUS_BY_JOB_ID = "select status from mysql_job_info where job_id = ?";

    /**未完成的任务在30s内没有任务更新操作---认为任务已经挂了*/
    private static final String CLEAR_TIME_OUT_SQL = "update mysql_job_info set  where status not in(5,7,8,9,13,14,15) and (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(gmt_modified)) > 30 ";

    public int insert(String jobId, String jobInfo, int status){
        ConnPool connPool = ConnPool.getInstance();
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = connPool.getConn();
            pstmt = connection.prepareStatement(INSERT_SQL);
            pstmt.setString(1, jobId);
            pstmt.setString(2, jobInfo);
            pstmt.setInt(3, status);

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

    public int updateStatus(String jobId, int status){
        ConnPool connPool = ConnPool.getInstance();
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = connPool.getConn();
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

    public void updateModifyTime(Collection<String> jobIds){
        ConnPool connPool = ConnPool.getInstance();
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = connPool.getConn();
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


    public Integer getStatusByJobId(String jobId){
        ConnPool connPool = ConnPool.getInstance();
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = connPool.getConn();
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

    public void clearInfo(){
        ConnPool connPool = ConnPool.getInstance();
        Connection connection = null;
        Statement stmt = null;

        try {
            connection = connPool.getConn();
            stmt = connection.createStatement();
            stmt.executeUpdate(CLEAR_TIME_OUT_SQL);

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
