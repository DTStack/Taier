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

package com.dtstack.taier.develop.sql.node.hive;

import com.dtstack.taier.develop.sql.Column;
import com.dtstack.taier.develop.sql.node.Identifier;
import com.dtstack.taier.develop.sql.node.Node;
import org.apache.calcite.sql.SqlNode;

import java.util.List;
import java.util.Map;

/**
 * 用于hive侧视图的节点
 */
public class LateralViewNode extends Node {

    /**
     * 侧视图来源字段
     */
    private List<Identifier> comboList;

    public LateralViewNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }


    @Override
    public Node parseSql(SqlNode node) {
        return null;
    }


    public List<Identifier> getComboList() {
        return comboList;
    }

    public void setComboList(List<Identifier> comboList) {
        this.comboList = comboList;
    }
}
