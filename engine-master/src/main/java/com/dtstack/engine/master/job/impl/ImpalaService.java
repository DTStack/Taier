package com.dtstack.engine.master.job.impl;


import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.engine.JdbcQuery;
import com.dtstack.dtcenter.common.engine.JdbcUrlPropertiesValue;
import com.dtstack.dtcenter.common.enums.DataBaseType;
import com.dtstack.dtcenter.common.util.DBUtil;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ClusterService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yuemo
 * @company www.dtstack.com
 * @Date 2020-03-06
 */
@Service
public class ImpalaService {
    private static final Logger logger = LoggerFactory.getLogger(ImpalaService.class);

    @Autowired
    ClusterService clusterService;

    public String getTableLocation(Long dtuicTenantId, String dbName, String tableName) throws Exception {
        String location = null;
        List<List<Object>> result = executeQuery(dtuicTenantId, dbName, String.format("DESCRIBE formatted %s", tableName));
        Iterator var6 = result.iterator();

        while(var6.hasNext()) {
            List<Object> objects = (List)var6.next();
            if (objects.get(0).toString().contains("Location:")) {
                location = objects.get(1).toString();
            }
        }

        return location;
    }


    public List<List<Object>> executeQuery(Long dtuicTenantId, String dbName, String sql) throws Exception {
        return this.executeQuery(dtuicTenantId, dbName, sql, true);
    }

    public List<List<Object>> executeQuery(Long dtuicTenantId, String dbName, String sql, Boolean isEnd) throws Exception {
        return this.executeQuery(dtuicTenantId, null, null, dbName, sql, isEnd);
    }

    public List<List<Object>> executeQuery(Long dtuicTenantId, String userName, String password, String dbName, String sql, Boolean isEnd) throws Exception {
        JdbcInfo jdbcInfo = this.getJdbcInfo(dtuicTenantId);
        Connection connection = this.getConnection(jdbcInfo, userName, password, dbName);
        JdbcQuery jdbcQuery = (new JdbcQuery(connection, dbName, dtuicTenantId, sql, org.apache.commons.lang3.BooleanUtils.isFalse(isEnd))).maxRows(jdbcInfo.getMaxRows());
        return this.executeBaseQuery(jdbcQuery.done());
    }

    public JdbcInfo getJdbcInfo(Long dtuicTenantId) {
        JdbcInfo jdbcInfo = null;
        if (dtuicTenantId != null) {
            jdbcInfo = impalaInfo(dtuicTenantId);
        }

        if (jdbcInfo == null) {
            throw new RdosDefineException("can't get impala jdbc conf from clusterService");
        } else {
            JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
            return jdbcInfo;
        }
    }

    public JdbcInfo impalaInfo(Long dtuicTenantId) {
        Object data = clusterService.impalaInfo(dtuicTenantId);
        JdbcInfo JDBCInfo = null;
        if (data != null) {
            try {
                JDBCInfo = (JdbcInfo) PublicUtil.strToObject(data.toString(), JdbcInfo.class);
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        return JDBCInfo;
    }

    public Connection getConnection(JdbcInfo jdbcInfo, String userName, String password, String dbName) {
        password = StringUtils.isBlank(userName) ? jdbcInfo.getPassword() : password;
        userName = StringUtils.isBlank(userName) ? jdbcInfo.getUsername() : userName;
        return DBUtil.getConnection(DataBaseType.Impala, String.format(jdbcInfo.getJdbcUrl(), dbName), userName, password, (Map)null);
    }

    public List<List<Object>> executeBaseQuery(JdbcQuery jdbcQuery) throws Exception {
        List<List<Object>> result = Lists.newArrayList();
        Statement stmt = null;
        ResultSet res = null;

        try {
            stmt = jdbcQuery.getConnection().createStatement();
            stmt.setQueryTimeout(jdbcQuery.getQueryTimeout());
            stmt.setMaxRows(jdbcQuery.getMaxRows());
            logger.info("impala query:{}", jdbcQuery.getSql());
            if (StringUtils.isNotEmpty(jdbcQuery.getDatabase())) {
                stmt.execute("use " + jdbcQuery.getDatabase());
            }

            if (stmt.execute(jdbcQuery.getSql())) {
                res = stmt.getResultSet();
                int columns = res.getMetaData().getColumnCount();
                List<Object> cloumnName = Lists.newArrayList();

                for(int i = 1; i <= columns; ++i) {
                    String name = res.getMetaData().getColumnName(i);
                    if (name.contains(".")) {
                        name = name.split("\\.")[1];
                    }

                    cloumnName.add(name);
                }

                result.add(cloumnName);

                while(res.next()) {
                    List<Object> objects = Lists.newArrayList();

                    for(int i = 1; i <= columns; ++i) {
                        objects.add(res.getObject(i));
                    }

                    result.add(objects);
                }
            }
        } catch (Throwable var12) {
            if (var12.getMessage() != null && var12.getMessage().contains("AuthorizationException")) {
                throw new RdosDefineException("未授权", ErrorCode.SERVER_EXCEPTION,var12.getCause());
            }

            throw var12;
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            if (res != null) {
                res.close();
            }

            if (!jdbcQuery.getMultiplex() && jdbcQuery.getConnection() != null) {
                jdbcQuery.getConnection().close();
                logger.info("success close impala connection");
            }

        }

        return result;
    }
}
