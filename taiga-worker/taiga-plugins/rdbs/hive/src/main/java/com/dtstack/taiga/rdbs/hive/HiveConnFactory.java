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

package com.dtstack.taiga.rdbs.hive;


import com.dtstack.taiga.base.util.KerberosUtils;
import com.dtstack.taiga.pluginapi.exception.PluginDefineException;
import com.dtstack.taiga.pluginapi.util.DtStringUtil;
import com.dtstack.taiga.pluginapi.util.MathUtil;
import com.dtstack.taiga.rdbs.common.constant.ConfigConstant;
import com.dtstack.taiga.rdbs.common.executor.AbstractConnFactory;
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
    private static final String HIVECONF_MAPREDUCE_MAP_JAVA_OPTS = "hiveconf:mapreduce.map.java.opts";
    private static final String HIVECONF_MAPREDUCE_REDUCE_JAVA_OPTS = "hiveconf:mapreduce.reduce.java.opts";

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

    @Override
    public Connection getConnByTaskParams(String taskParams, String jobName) throws ClassNotFoundException, SQLException, IOException {
        Properties properties = new Properties();;
        Connection conn;

        properties.setProperty(HIVE_JOBNAME_PROPERTY , jobName);

        if (StringUtils.isNotEmpty(taskParams)) {
            for (String line : taskParams.split("\n")) {
                line = StringUtils.trim(line);
                if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                    continue;
                }

                String[] keyAndVal = line.split("=");
                if (keyAndVal.length > 1) {
                    String newKey = keyAndVal[0].startsWith(HIVE_CONF_PREFIX) ? keyAndVal[0] : HIVE_CONF_PREFIX + keyAndVal[0];
                    String newValue = keyAndVal[1];
                    properties.setProperty(newKey, newValue);
                }
            }
        }

        try {
            conn = KerberosUtils.login(baseConfig, () -> {
                Connection connection = null;
                try {
                    if (getUsername() == null) {
                        connection = DriverManager.getConnection(jdbcUrl, properties);
                    } else {
                        properties.setProperty(HIVE_USER, getUsername());
                        properties.setProperty(HIVE_PASSWORD, getPassword());
                        connection = DriverManager.getConnection(jdbcUrl, properties);
                    }
                } catch (Exception e) {
                    throw new PluginDefineException(e);
                }
                return connection;
            }, yarnConf);
        } catch (Exception e) {
            throw new PluginDefineException("get connection by taskParams error", e);
        }
        return conn;
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
