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
import com.dtstack.taier.develop.sql.Pair;
import com.google.common.collect.Lists;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/28 16:52
 * @Description: union查询要将其子结点转化为兄弟结点。
 * <p>
 * union的operands(操作数)只有两个
 */
public class UnionCall extends BasicCall {

    /**
     * 将union树的子结点转化为兄弟结点，存入
     */
    private List<SelectNode> comboFromList;

    public UnionCall(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb,tableColumnsMap);
    }

    public List<SelectNode> getComboFromList() {
        return comboFromList;
    }

    public void setComboFromList(List<SelectNode> comboFromList) {
        this.comboFromList = comboFromList;
    }

    @Override
    public Node parseSql(SqlNode node) {
        SqlBasicCall sqlBasicCall = unionCallCheckNode(node);
        List<SqlNode> operandList = sqlBasicCall.getOperandList();
        comboFromList = Lists.newArrayList();
        SqlNode sqlNode0 = operandList.get(0);
        handleOperand(sqlNode0);
        SqlNode sqlNode1 = operandList.get(1);
        handleOperand(sqlNode1);
        return null;
    }

    /**
     * @param node
     */
    private void handleOperand(SqlNode node) {
        Pair<String, SqlNode> sqlNodePair = removeAs(node);
        String alias = null;
        SqlNode handledNode = node;
        if (sqlNodePair != null) {
            alias = sqlNodePair.getKey();
            handledNode = sqlNodePair.getValue();
        }

        List<Node> operands = getOperands();
        if (operands == null){
            operands = Lists.newArrayList();
            setOperands(operands);
        }
        //union select
        if (handledNode instanceof SqlSelect) {
            SelectNode selectNode = new SelectNode(getDefaultDb(),getTableColumnMap());
            selectNode.setAlias(alias);
            selectNode.setContext(Context.INSERT_FROM_UNION);
            selectNode.parseSql(handledNode);
            operands.add(selectNode);
            comboFromList.add(selectNode);
        }
        //多union
        else if (handledNode instanceof SqlBasicCall && SqlKind.UNION == ((SqlBasicCall) handledNode).getOperator().getKind()) {
            UnionCall unionCall = new UnionCall(getDefaultDb(),getTableColumnMap());
            unionCall.setAlias(alias);
            unionCall.parseSql(handledNode);
            operands.add(unionCall);
            comboFromList.addAll(unionCall.getComboFromList());
        }
    }

    private SqlBasicCall unionCallCheckNode(SqlNode node) {
        if (!(node instanceof SqlBasicCall) && SqlKind.UNION != node.getKind()) {
            throw new IllegalArgumentException("不匹配的sqlNode类型");
        }
        return  (SqlBasicCall)node;
    }
}
