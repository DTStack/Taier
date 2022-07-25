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

package com.dtstack.taier.rdbs.oreanbase;

import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.util.DtStringUtil;
import com.dtstack.taier.rdbs.common.executor.AbstractConnFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class OceanBaseConnFactory extends AbstractConnFactory {


    public OceanBaseConnFactory() {
        driverName = "com.alipay.oceanbase.jdbc.Driver";
        testSql = "select 111";
    }

    @Override
    public void init(Properties props) throws ClassNotFoundException {
        super.init(props);
    }

    @Override
    public Connection getConnByTaskParams(String taskParams, String jobName)
            throws ClassNotFoundException, SQLException, IOException {
        Connection connection;
        try {
            connection = DriverManager.getConnection(jdbcUrl, getUsername(), getPassword());
        } catch (Exception e) {
            throw new PluginDefineException(e);
        }
        return connection;
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
        return DtStringUtil.splitIgnoreQuota(sql, ';');
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
