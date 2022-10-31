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

package com.dtstack.taier.datasource.plugin.oceanbase;

import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OceanBaseSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.Statement;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 14:18 2021/4/21
 */
public class OceanBaseConnFactory extends ConnFactory {
    public OceanBaseConnFactory() {
        driverName = DataBaseType.OceanBase.getDriverClassName();
        this.errorPattern = new OceanBaseErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        Connection connection = super.getConn(sourceDTO);
        OceanBaseSourceDTO source = (OceanBaseSourceDTO) sourceDTO;
        String schema = source.getSchema();
        if (StringUtils.isNotEmpty(schema)) {
            try (Statement statement = connection.createStatement()) {
                //选择schema
                String useSchema = String.format("USE %s", schema);
                statement.execute(useSchema);
            } catch (Exception e) {
                throw new SourceException(e.getMessage(), e);
            }
        }
        return connection;
    }

    @Override
    protected String getCreateProcHeader(String procName) {
        return String.format("create procedure %s() as \n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("EXEC SQL CALL %s()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("DROP PROCEDURE %s", procName);
    }
}
