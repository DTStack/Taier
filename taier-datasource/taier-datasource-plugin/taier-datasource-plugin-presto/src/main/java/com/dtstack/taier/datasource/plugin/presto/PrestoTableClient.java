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

package com.dtstack.taier.datasource.plugin.presto;

import com.dtstack.taier.datasource.plugin.rdbms.AbsTableClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.UpsertColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author ：qianyi
 * company: www.dtstack.com
 */
@Slf4j
public class PrestoTableClient extends AbsTableClient {

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.Presto;
    }

    private static final String ADD_COLUMN_SQL = "alter table %s add column %s %s comment '%s'";

    @Override
    public List<String> showPartitions(ISourceDTO source, String tableName) {
        throw new SourceException("The method is not supported");
    }

    @Override
    public Boolean alterTableParams(ISourceDTO source, String tableName, Map<String, String> params) {
        throw new SourceException("The method is not supported");
    }

    @Override
    public Boolean renameTable(ISourceDTO source, String oldTableName, String newTableName) {
        throw new SourceException("The method is not supported");
    }

    @Override
    public Boolean dropTable(ISourceDTO source, String tableName) {
        throw new SourceException("The method is not supported");
    }


    /**
     * 添加表字段
     *
     * @param source
     * @param columnMetaDTO
     * @return
     */
    protected Boolean addTableColumn(ISourceDTO source, UpsertColumnMetaDTO columnMetaDTO) {
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) source;
        String schema = StringUtils.isNotBlank(columnMetaDTO.getSchema()) ? columnMetaDTO.getSchema() : rdbmsSourceDTO.getSchema();
        String comment = StringUtils.isNotBlank(columnMetaDTO.getColumnComment()) ? columnMetaDTO.getColumnComment() : "";
        String sql = String.format(ADD_COLUMN_SQL, transferSchemaAndTableName(schema, columnMetaDTO.getTableName()), columnMetaDTO.getColumnName(), columnMetaDTO.getColumnType(), comment);
        return executeSqlWithoutResultSet(source, sql);
    }
}
