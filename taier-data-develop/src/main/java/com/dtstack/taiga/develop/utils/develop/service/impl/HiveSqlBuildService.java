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

package com.dtstack.taiga.develop.utils.develop.service.impl;

import com.dtstack.taiga.common.exception.DtCenterDefException;
import com.dtstack.taiga.develop.utils.develop.common.dto.ColumnDTO;
import com.dtstack.taiga.develop.utils.develop.common.dto.TableDTO;
import com.dtstack.taiga.develop.utils.develop.common.enums.StoredType;
import com.dtstack.taiga.develop.utils.develop.service.ISqlBuildService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jiangbo
 */
@Service
public class HiveSqlBuildService implements ISqlBuildService {

    public String buildAddColumnSql(String tableName, List<ColumnDTO> columns) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("alter table ").append(tableName).append(" add columns (");
        List<String> strings = buildColumnsSql(columns, true);
        for (int i=0;i<strings.size();i++){
            sqlBuilder.append(strings.get(i));
            if (i<strings.size()-1){
                sqlBuilder.append(",");
            }
        }
        return sqlBuilder.append(")").toString();
    }

    @Override
    public String buildCreateSql(TableDTO createTableDTO){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("create ");

        boolean isExternal = StringUtils.isNotEmpty(createTableDTO.getLocation());
        if(isExternal){
            sqlBuilder.append("external ");
        }

        // 表名
        sqlBuilder.append("table ").append(quote(createTableDTO.getTableName()));

        // 字段
        if(CollectionUtils.isNotEmpty(createTableDTO.getColumns())){
            String tempSql = StringUtils.join(buildColumnsSql(createTableDTO.getColumns(),true),",");
            sqlBuilder.append(String.format("(%s) ",tempSql));
        }

        // 描述
        if(StringUtils.isNotEmpty(createTableDTO.getTableDesc())){
            sqlBuilder.append("comment ").append(String.format("'%s'",createTableDTO.getTableDesc())).append(" ");
        }

        // 分区
        if(CollectionUtils.isNotEmpty(createTableDTO.getPartitionKeys())){
            sqlBuilder.append("partitioned by ");
            String tempSql = StringUtils.join(buildColumnsSql(createTableDTO.getPartitionKeys(),true),",");
            sqlBuilder.append(String.format("(%s)",tempSql));
        }

        // 分隔符
        if(StringUtils.isNotEmpty(createTableDTO.getStoredType())){
            if(StoredType.TEXTFILE.getValue().equalsIgnoreCase(createTableDTO.getStoredType())){
                String delim = StringUtils.isNotEmpty(createTableDTO.getDelim()) ? createTableDTO.getDelim() : ",";
                sqlBuilder.append("row format delimited fields terminated by ")
                        .append(String.format("'%s'",delim))
                        .append(" ");
            }

            //存储格式
            sqlBuilder.append("stored as ").append(createTableDTO.getStoredType()).append(" ");
        }

        // 外部表路径
        if(isExternal){
            sqlBuilder.append("location ").append(String.format("'%s'",createTableDTO.getLocation())).append(" ");
        }

        return sqlBuilder.toString();
    }

    @Override
    public String buildRenameTableSql(String oldTable,String newTable){
        return String.format("alter table %s rename to %s",quote(oldTable),quote(newTable));
    }

    @Override
    public String buildAlterTableSql(String tableName, String comment, Integer lifecycle, Long catalogueId) {
        String lifecycleStr = "";
        if (lifecycle != null) {
            lifecycleStr = String.format("lifecycle %s", lifecycle);
        }

        String catalogueStr = "";
        if (catalogueId != null) {
            catalogueStr = String.format("catalogue %s", catalogueId);
        }

        return String.format("alter table %s set tblproperties ('comment'='%s') %s %s", quote(tableName), comment, lifecycleStr, catalogueStr);
    }

    private List<String> buildColumnsSql(List<ColumnDTO> columns, boolean isAddCol){
        List<String> columnsStr = Lists.newArrayList();
        String colName;
        String colType;
        String colDesc;
        String addColumnFormat = "%s %s comment '%s'";
        String alterColumnFormat = "%s %s %s comment '%s'";
        for (ColumnDTO col : columns) {

            if(StringUtils.isEmpty(col.getColumnName()) || StringUtils.isEmpty(col.getColumnType())){
                throw new DtCenterDefException("column name or type can not be null");
            }

            colName = col.getColumnName();
            colType = col.getColumnType();
            colDesc = col.getComment() == null ? "" : col.getComment();

            if ("DECIMAL".equals(colType)){
                int precision = col.getPrecision() == null ? 10 : col.getPrecision();
                int scale = col.getScale() == null ? 0 : col.getScale();
                colType += String.format("(%s,%s)",precision,scale);
            }

            if(isAddCol){
                columnsStr.add(String.format(addColumnFormat,quote(colName),colType,colDesc));
            } else {
                columnsStr.add(String.format(alterColumnFormat,quote(colName),colName,colType,colDesc));
            }
        }

        return columnsStr;
    }

    @Override
    public String buildAddFuncSql(String funcName,String className,String resource){
        return String.format("create function %s as '%s' using %s",funcName,className,resource);
    }

    @Override
    public String buildDropFuncSql(String funcName){
        return String.format("drop function if exists %s",funcName);
    }

    @Override
    public String quote(String values){
        return String.format("`%s`",values);
    }
}

