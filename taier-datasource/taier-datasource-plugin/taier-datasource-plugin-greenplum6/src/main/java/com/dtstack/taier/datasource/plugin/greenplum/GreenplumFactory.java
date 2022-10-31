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

package com.dtstack.taier.datasource.plugin.greenplum;

import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.source.Greenplum6SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:13 2020/4/10
 * @Description：Greenplum 工厂
 */
public class GreenplumFactory extends ConnFactory {

    private static final String SCHEMA_SET = "SET search_path TO %s";

    public GreenplumFactory() {
        driverName = DataBaseType.Greenplum6.getDriverClassName();
        errorPattern = new GreenplumErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        init();
        Greenplum6SourceDTO greenplum6SourceDTO = (Greenplum6SourceDTO) sourceDTO;
        Connection connection = super.getConn(greenplum6SourceDTO);
        if (!StringUtils.isBlank(greenplum6SourceDTO.getSchema())) {
            DBUtil.executeSql(connection, String.format(SCHEMA_SET, greenplum6SourceDTO.getSchema()));
        }
        return connection;
    }

    @Override
    protected String getCreateProcHeader(String procName) {
        return String.format("CREATE FUNCTION \"%s\"() RETURNS void AS $body$ ", procName);
    }

    @Override
    protected String getCreateProcTail() {
        return " $body$ LANGUAGE PLPGSQL; ";
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("select \"%s\"()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("drop function \"%s\"()", procName);
    }
}
