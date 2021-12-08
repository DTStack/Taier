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

package com.dtstack.engine.rdbs.impala;

import com.dtstack.engine.pluginapi.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Properties;

public class ImpalaConnFactory extends AbstractConnFactory {

    public static String AUTHMECH = "AuthMech";

    public ImpalaConnFactory() {
        driverName = "com.cloudera.impala.jdbc.Driver";
        testSql = "show tables";
    }

    @Override
    public void init(Properties props) throws ClassNotFoundException {
        super.init(props);
        if (StringUtils.isNotBlank(getUsername()) && !jdbcUrl.contains(AUTHMECH) && StringUtils.isNotBlank(getPassword())){
            jdbcUrl = jdbcUrl + ";AuthMech=3";
        } else if (StringUtils.isNotBlank(getUsername()) && !jdbcUrl.contains(AUTHMECH) && StringUtils.isBlank(getPassword())){
//            jdbcUrl = jdbcUrl + ";AuthMech=2";
            jdbcUrl = jdbcUrl;
        }
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
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
