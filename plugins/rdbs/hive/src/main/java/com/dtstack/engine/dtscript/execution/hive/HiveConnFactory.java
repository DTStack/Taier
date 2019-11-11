package com.dtstack.engine.dtscript.execution.hive;


import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.dtscript.execution.rdbs.constant.ConfigConstant;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class HiveConnFactory extends ConnFactory {

    private String hiveSubType;

    private String queue;

    private static final String HIVE_SUB_TYPE = "hiveSubType";

    private static final String PARAMS_BEGIN = "?";
    private static final String PARAMS_AND = "&";

    private static final String HIVE_CONF_PREFIX = "hiveconf:";
    private static final String HIVE_JOBNAME_PROPERTY = "hiveconf:mapreduce.job.name";

    private static final String MAPREDUCE_JOB_QUEUENAME = "mapreduce.job.queuename=";

    private static final String SUB_TYPE_INCEPTOR = "INCEPTOR";

    public HiveConnFactory() {
        driverName = "org.apache.hive.jdbc.HiveDriver";
        testSql = "show tables";
    }

    @Override
    public void init(Properties props) throws ClassNotFoundException {
        super.init(props);
        hiveSubType = props.getProperty(HIVE_SUB_TYPE);
        queue = MathUtil.getString(props.get(ConfigConstant.QUEUE));
        if (StringUtils.isNotBlank(queue)) {
            if (super.dbURL.contains(PARAMS_BEGIN)) {
                super.dbURL += (PARAMS_AND + MAPREDUCE_JOB_QUEUENAME + queue);
            } else {
                super.dbURL += (PARAMS_BEGIN + MAPREDUCE_JOB_QUEUENAME + queue);
            }
        }
    }
    @Override
    public Connection getConnByTaskParams(String taskParams, String jobName) throws ClassNotFoundException, SQLException, IOException {
        Properties properties =  new Properties();
        Connection conn;

        properties.setProperty(HIVE_JOBNAME_PROPERTY , jobName);

        if (StringUtils.isNotEmpty(taskParams)) {
            for (String str : taskParams.split("\n")) {
                String[] keyAndVal = str.split("=");
                if (keyAndVal.length > 1) {
                    properties.setProperty(HIVE_CONF_PREFIX + keyAndVal[0], keyAndVal[1]);
                }
            }
        }

        if (getUserName() == null) {
            conn = DriverManager.getConnection(dbURL, properties);
        } else {
            properties.setProperty(ConfigConstant.JDBC_USER_NAME_KEY, getUserName());
            properties.setProperty(ConfigConstant.JDBC_PASSWORD_KEY, getPwd());
            conn = DriverManager.getConnection(dbURL, properties);
        }
        return conn;
    }




    @Override
    public boolean supportProcedure() {
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
            return Arrays.asList(sql.split(";"));
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
