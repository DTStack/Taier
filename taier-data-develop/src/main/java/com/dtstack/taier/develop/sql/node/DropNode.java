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

import com.dtstack.taier.develop.sql.Column;
import org.apache.calcite.sql.SqlNode;

import java.util.List;
import java.util.Map;

public class DropNode extends Node {
    /**
     * 被删除的表
     */
    private Identifier targetTable;

    public DropNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
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
}
