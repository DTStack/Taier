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

package com.dtstack.engine.rdbs.adb.postgresql;

import com.dtstack.engine.pluginapi.util.DtStringUtil;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;

import java.util.List;

/**
 * @author tiezhu
 * @since 2021/5/18 6:59 下午
 */
public class AdbPostgresqlFactory extends AbstractConnFactory {

    public AdbPostgresqlFactory() {
        driverName = "org.postgresql.Driver";
        testSql = "select 1";
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        return String.format("CREATE FUNCTION \"%s\"() RETURNS void AS $body$", procName);
    }

    @Override
    public String getCreateProcedureTailer() {
        return " $body$ LANGUAGE PLPGSQL; ";
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("SELECT \"%s\"()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("DROP FUNCTION \"%s\"()", procName);
    }

    @Override
    public List<String> buildSqlList(String sql) {
        return DtStringUtil.splitIgnoreQuota(sql, ';');
    }

}
