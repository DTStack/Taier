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

package com.dtstack.taier.develop.sql.node;

import com.dtstack.taier.develop.sql.AlterColumnResult;
import com.dtstack.taier.develop.sql.Column;
import com.dtstack.taier.develop.sql.PartCondition;
import com.dtstack.taier.develop.sql.Partition;
import com.dtstack.taier.develop.sql.TableOperateEnum;
import org.apache.calcite.sql.SqlNode;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Map;

public class AlterNode extends Node {
    /**
     * 被修改的表
     */
    private Identifier sourceTable;
    /**
     * 修改之后的表
     */
    private Identifier targetTable;

    /**
     * 被修改的字段
     */
    private AlterColumnResult alterColumnMap;

    /**
     * 新增字段
     */
    private List<Column> newColumns;

    /**
     * 具体的修改类型
     */
    private TableOperateEnum alterType;

    /**
     * 删除的分区
     */
    private List<PartCondition> dropParts;

    /**
     * 新增加的分区
     */
    private List<Partition> newPartitions;


    /**
     * 表属性
     */
    private List<Pair<String, String>> tableProperties;


    /**
     * 修改的分区
     */
    private List<Pair<String, String>> renamePart;

    public List<Pair<String, String>> getRenamePart() {
        return renamePart;
    }

    public void setRenamePart(List<Pair<String, String>> renamePart) {
        this.renamePart = renamePart;
    }

    public AlterNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }

    @Override
    public Node parseSql(SqlNode node) {
        return null;
    }

    public Identifier getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(Identifier targetTable) {
        this.targetTable = targetTable;
    }

    public Identifier getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(Identifier sourceTable) {
        this.sourceTable = sourceTable;
    }

    public AlterColumnResult getAlterColumnMap() {
        return alterColumnMap;
    }

    public void setAlterColumnMap(AlterColumnResult alterColumnMap) {
        this.alterColumnMap = alterColumnMap;
    }

    public List<Column> getNewColumns() {
        return newColumns;
    }

    public void setNewColumns(List<Column> newColumns) {
        this.newColumns = newColumns;
    }

    public TableOperateEnum getAlterType() {
        return alterType;
    }

    public void setAlterType(TableOperateEnum alterType) {
        this.alterType = alterType;
    }

    public List<PartCondition> getDropParts() {
        return dropParts;
    }

    public void setDropParts(List<PartCondition> dropParts) {
        this.dropParts = dropParts;
    }

    public List<Partition> getNewPartitions() {
        return newPartitions;
    }

    public void setNewPartitions(List<Partition> newPartitions) {
        this.newPartitions = newPartitions;
    }

    public List<Pair<String, String>> getTableProperties() {
        return tableProperties;
    }

    public void setTableProperties(List<Pair<String, String>> tableProperties) {
        this.tableProperties = tableProperties;
    }
}
