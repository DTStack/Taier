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
import org.apache.calcite.sql.SqlNodeList;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/27 11:22
 * @Description:
 */
public class NodeList extends Node {
    private List<Node> list;

    public NodeList(String defaultDb, Map<String, List<Column>> tableColumnsMap){
        super(defaultDb,tableColumnsMap);
    }

    public List<Node> getList() {
        return list;
    }

    public void setList(List<Node> list) {
        this.list = list;
    }

    @Override
    public Node parseSql(SqlNode node) {
        SqlNodeList sqlNodes = checkNode(node);
        for (SqlNode item : sqlNodes.getList()) {
            //TODO
        }
        return null;
    }

    private SqlNodeList checkNode(SqlNode node){
        if (!(node instanceof SqlNodeList)){
            throw new IllegalStateException("sqlNode类型不匹配");
        }
        return (SqlNodeList) node;
    }
}
