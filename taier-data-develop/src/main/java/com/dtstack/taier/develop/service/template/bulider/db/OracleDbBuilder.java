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

package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OracleSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class OracleDbBuilder extends AbsRdbmsDbBuilder {

    //过滤表
    private static Pattern sys = Pattern.compile("^SYS$|^SYSTEM$|^APEX_");
    //过滤列
    private static Pattern pollColumn = Pattern.compile("^DATE.*$|^VARCHAR.*$|^TIMESTAMP.*$|^NUMBER.*$");


    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.Oracle;
    }



    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        List<JSONObject> columns = super.listPollTableColumn(sourceDTO, tableName);
        return getByColumn(columns, pollColumn);
    }


    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) sourceDTO;
        // 设置 pdb
        oracleSourceDTO.setPdb(db);
        List<String> schemaList = getClient().getAllDatabases(oracleSourceDTO, SqlQueryDTO.builder().build());
        return getSchemaList(schemaList, sys);
    }

    @Override
    public List<String> listTablesBySchema(String schema, String tableNamePattern, ISourceDTO sourceDTO, String db) {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) sourceDTO;
        // 设置 pdb
        oracleSourceDTO.setPdb(db);
        return getClient().getTableListBySchema(oracleSourceDTO, SqlQueryDTO.builder().schema(schema).tableNamePattern(tableNamePattern).limit(LIMIT_COUNT).build());
    }
}
