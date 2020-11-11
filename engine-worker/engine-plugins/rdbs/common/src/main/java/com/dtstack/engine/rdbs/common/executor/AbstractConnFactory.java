package com.dtstack.engine.rdbs.common.executor;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.rdbs.common.constant.ConfigConstant;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reason:
 * Date: 2018/2/7
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class AbstractConnFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConnFactory.class);

    private AtomicBoolean isFirstLoaded = new AtomicBoolean(true);

    protected String jdbcUrl;

    private String username;

    private String password;

    protected String driverName = null;

    protected String testSql = null;

    protected Configuration yarnConf = null;

    protected BaseConfig baseConfig = null;

    public void init(Properties properties) throws ClassNotFoundException {
        synchronized (AbstractConnFactory.class) {
            if (isFirstLoaded.get()) {
                Class.forName(driverName);
                isFirstLoaded.set(false);
            }

        }
        jdbcUrl = MathUtil.getString(properties.get(ConfigConstant.JDBCURL));
        username = MathUtil.getString(properties.get(ConfigConstant.USERNAME));
        password = MathUtil.getString(properties.get(ConfigConstant.PASSWORD));

        Preconditions.checkNotNull(jdbcUrl, "db url can't be null");
        Map config = (Map) properties.get("config");

        try {
            if (Objects.nonNull(config)) {
                baseConfig = PublicUtil.mapToObject(config, BaseConfig.class);
                if (Objects.nonNull(config.get("yarnConf"))) {
                    Map<String, Object> yarnMap = (Map<String, Object>) config.get("yarnConf");
                    yarnConf = KerberosUtils.convertMapConfToConfiguration(yarnMap);
                }
                if (Objects.nonNull(properties.get("sftpConf"))) {
                    SftpConfig sftpConfig = PublicUtil.mapToObject((Map<String, Object>) properties.get("sftpConf"), SftpConfig.class);
                    baseConfig.setSftpConf(sftpConfig);
//                    baseConfig.setSftpConf((Map<String, String>) properties.get("sftpConf"));
                }
            } else {
                baseConfig = new BaseConfig();
            }
            testConn();
        } catch (Exception e) {
            throw new RdosDefineException("get conn exception:" + e.toString());
        }
    }

    protected List<String> splitSql(String sql) {
        if(StringUtils.isBlank(sql)) {
            return Collections.emptyList();
        }
        return Arrays.asList(sql.split(";"));
    }

    public void testConn() {
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = getConn();
            stmt = conn.createStatement();
            stmt.execute(testSql);
        }catch (Exception e){
            throw new RdosDefineException("get conn exception:" + e.toString());
        }finally {

            try{
                if(stmt != null){
                    stmt.close();
                }

                if(conn != null){
                    conn.close();
                }
            }catch (Exception e){
                LOG.error("", e);
            }
        }

    }

    public Connection getConn() throws Exception {
        return KerberosUtils.login(baseConfig, () -> {
            Connection conn = null;
            try {
                if (username == null) {
                    conn = DriverManager.getConnection(jdbcUrl);
                } else {
                    conn = DriverManager.getConnection(jdbcUrl, username, password);
                }
            } catch (SQLException e) {
                throw new RdosDefineException("get conn exception:" + e.toString());
            }
            return conn;
        }, yarnConf);
    }

    public Connection getConnByTaskParams(String taskParams, String jobName) throws Exception {
        return getConn();
    }

    public boolean supportTransaction() {
        return true;
    }

    public boolean supportProcedure(String sql) {
        if (dealWithProcedure(sql)) {
            return true;
        }
        return false;
    }

    public boolean dealWithProcedure(String sql) {
        if (StringUtils.isBlank(sql)) {
            return true;
        }
        String[] sqls = sql.trim().split("\\s+", 2);
        if (sqls.length >= 2){
            if ("BEGIN".equalsIgnoreCase(sqls[0])) {
                return true;
            }
        }
        return false;
    }

    public List<String> buildSqlList(String sql) {
        throw new UnsupportedOperationException();
    }

    public abstract String getCreateProcedureHeader(String procName);

    public String getCreateProcedureTailer() { return ""; }

    public String getCallProc(String procName) {
        return String.format("call \"%s\"()", procName);
    }

    public String getDropProc(String procName) {
        return String.format("DROP PROCEDURE \"%s\"", procName);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
