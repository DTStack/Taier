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

package com.dtstack.taier.rdbs.common.executor;

import com.dtstack.taier.base.BaseConfig;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.rdbs.common.constant.ConfigConstant;
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

    protected BaseConfig baseConfig = new BaseConfig();

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

        try {
            String propStr = PublicUtil.objToString(properties);
            baseConfig = PublicUtil.jsonStrToObject(propStr, BaseConfig.class);
            //非kerberos 不进行yarnConf初始化
            if (baseConfig.isOpenKerberos() && null != properties.get("yarnConf")) {
                Map<String, Object> yarnMap = (Map<String, Object>) properties.get("yarnConf");
                yarnConf = KerberosUtils.convertMapConfToConfiguration(yarnMap);
            }
            testConn();
        } catch (Exception e) {
            throw new PluginDefineException("get conn exception:" + e.toString());
        }
    }

    public void testConn() {
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = getConn();
            stmt = conn.createStatement();
            stmt.execute(testSql);
        }catch (Exception e){
            throw new PluginDefineException("get conn exception:" + e.toString());
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
                throw new PluginDefineException("get conn exception:" + e.toString());
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
