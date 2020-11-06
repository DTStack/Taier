package com.dtstack.engine.rdbs.hive;


import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.rdbs.common.constant.ConfigConstant;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HiveConnFactory extends AbstractConnFactory {

    private static final Logger LOG = LoggerFactory.getLogger(HiveConnFactory.class);

    private String hiveSubType;

    private String queue;

    private static final String HIVE_SUB_TYPE = "hiveSubType";

    private static final String PARAMS_BEGIN = "?";
    private static final String PARAMS_AND = "&";

    private static final String HIVE_CONF_PREFIX = "hiveconf:";
    private static final String HIVE_JOBNAME_PROPERTY = "hiveconf:mapreduce.job.name";

    private static final String MAPREDUCE_JOB_QUEUENAME = "mapreduce.job.queuename=";

    private static final String SUB_TYPE_INCEPTOR = "INCEPTOR";

    public static final String HIVE_USER = "user";
    public static final String HIVE_PASSWORD = "password";

    public HiveConnFactory() {
        driverName = "org.apache.hive.jdbc.HiveDriver";
        testSql = "show tables";
    }

    @Override
    public void init(Properties props) throws ClassNotFoundException {
        fillDefaultDatabase(props);
        super.init(props);
        hiveSubType = props.getProperty(HIVE_SUB_TYPE);
        queue = MathUtil.getString(props.get(ConfigConstant.QUEUE));
        if (StringUtils.isNotBlank(queue)) {
            if (super.jdbcUrl.contains(PARAMS_BEGIN)) {
                super.jdbcUrl += (PARAMS_AND + MAPREDUCE_JOB_QUEUENAME + queue);
            } else {
                super.jdbcUrl += (PARAMS_BEGIN + MAPREDUCE_JOB_QUEUENAME + queue);
            }
        }
    }

    private void fillDefaultDatabase(Properties props) {
        String oldJdbcUrl = MathUtil.getString(props.get(ConfigConstant.JDBCURL));
        String[] jdbcContents = StringUtils.split(oldJdbcUrl, ";");
        if (jdbcContents.length > 0) {
            jdbcContents[0] = StringUtils.endsWith(jdbcContents[0], "/")? jdbcContents[0] + "/default" : jdbcContents[0] + "default";
            String newJdbcUrl = StringUtils.join(jdbcContents, ";");
            props.setProperty(ConfigConstant.JDBCURL, newJdbcUrl);
        }
    }

    @Override
    public Connection getConnByTaskParams(String taskParams, String jobName) throws ClassNotFoundException, SQLException, IOException {
        try {
            return KerberosUtils.login(baseConfig,()->{
                Properties properties =  new Properties();
                Connection conn;

                properties.setProperty(HIVE_JOBNAME_PROPERTY , jobName);

                if (StringUtils.isNotEmpty(taskParams)) {
                    for (String str : taskParams.split("\n")) {
                        if (str.startsWith("#")) {
                            continue;
                        }
                        String[] keyAndVal = str.split("=");
                        if (keyAndVal.length > 1) {
                            String newKey = keyAndVal[0].startsWith(HIVE_CONF_PREFIX) ? keyAndVal[0] : HIVE_CONF_PREFIX + keyAndVal[0];
                            properties.setProperty(newKey, keyAndVal[1]);
                        }
                    }
                }

                try {
                    if (getUsername() == null) {
                        conn = DriverManager.getConnection(jdbcUrl, properties);
                    } else {
                        properties.setProperty(HIVE_USER, getUsername());
                        properties.setProperty(HIVE_PASSWORD, getPassword());
                        conn = DriverManager.getConnection(jdbcUrl, properties);
                    }
                } catch (SQLException e) {
                    throw new RdosDefineException(e);
                }
                return conn;
            },yarnConf);
        } catch (Exception e) {
            LOG.error("getConnByTaskParams error {}",jobName,e);
            throw new RdosDefineException(e);
        }
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }

    @Override
    public boolean supportProcedure(String sql) {
        return false;
    }

    @Override
    public List<String> buildSqlList(String sql) {
        if (hiveSubType != null && hiveSubType.equalsIgnoreCase(SUB_TYPE_INCEPTOR)) {
            sql = "BEGIN\n" + sql + "\nEND;\n";
            List<String> sqlList = new ArrayList<>();
            sqlList.add(sql);
            return sqlList;
        } else {
            return DtStringUtil.splitIgnoreQuota(sql, ';');
        }
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCallProc(String procName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDropProc(String procName) {
        throw new UnsupportedOperationException();
    }
}
