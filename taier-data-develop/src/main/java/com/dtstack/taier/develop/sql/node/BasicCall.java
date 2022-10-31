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
import com.dtstack.taier.develop.sql.calcite.Operator;
import com.google.common.collect.Lists;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlCase;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/26 10:45
 * @Description:对应于 {@link SqlBasicCall}常规的sqlCall，将操作数保存在列表中
 * <p>
 * operands属性: 操作数的列表。
 * operator属性: 操作类型
 * <p>
 * 在查询selectList中的函数，用于复合的identifier。将所有对字段的函数操作结点都定义为该类型。注意，这不是常规操作。
 */
public class BasicCall extends BaseCall {

    public static Logger LOG = LoggerFactory.getLogger(BasicCall.class);

    private String name;

    private Operator operator;

    /**
     * 在from join和from union用于解析表
     * join:
     * union:
     */
    private List<Node> operands;

    public BasicCall(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<Node> getOperands() {
        return operands;
    }

    public void setOperands(List<Node> operands) {
        this.operands = operands;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 在context是CALL_IN_COLUMN时使用
     * 涉及到的字段
     */
    private List<Identifier> comboList;

    public List<Identifier> getComboList() {
        return comboList;
    }

    public void setComboList(List<Identifier> comboList) {
        this.comboList = comboList;
    }

    @Override
    public Node parseSql(SqlNode node) {
        SqlCall sqlCall = (SqlCall) checkNode(node);
        //用在对字段处理时。字段血缘解析时仅在selectList中的字段。
        if (Context.CALL_IN_COLUMN == getContext() || Context.CASE_IN_COLUMN == getContext()) {
            List<Identifier> allColumnInFunc = findAllColumnInFunc(node);
            this.comboList = allColumnInFunc;
        }
        this.name = node.toString();
        return this;
    }

    public SqlNode checkNode(SqlNode node) {
        if (!(node instanceof SqlCall)) {
            throw new IllegalArgumentException("sqlNode类型不匹配");
        }
        return node;
    }

    private List<Identifier> findAllColumnInFunc(SqlNode sn) {
        List<Identifier> comboIdentifiers = Lists.newArrayList();
        List<SqlNode> operandList = null;
        if (sn instanceof SqlBasicCall) {
            operandList = ((SqlCall) sn).getOperandList();
        } else if (sn instanceof SqlNodeList) {
            operandList = ((SqlNodeList) sn).getList();
        }
        //case when只需要then和else中的字段。条件字段when中的不要
        else if (sn instanceof SqlCase) {
            operandList = new ArrayList<>();
            operandList.addAll(((SqlCase) sn).getThenOperands().getList());
            operandList.add(((SqlCase) sn).getElseOperand());
        } else if (sn instanceof SqlLiteral) {
            //常量丢弃
        } else if (sn instanceof SqlSelect) {
            //字段里的子查询
            operandList = (((SqlSelect) sn).getSelectList().getList());
        } else {
            LOG.warn("未处理的函数内sql类型：{}", sn.getKind());
        }
        if (CollectionUtils.isNotEmpty(operandList)) {
            for (SqlNode node : operandList) {
                if (node != null) {

                    if (node instanceof SqlIdentifier) {
                        Identifier identifier = new Identifier(getDefaultDb(), getTableColumnMap());
                        identifier.setContext(Context.IDENTIFIER_COLUMN);
                        identifier.parseSql(node);
                        comboIdentifiers.add(identifier);
                    } else {
                        comboIdentifiers.addAll(findAllColumnInFunc(node));
                    }
                }
            }
        }

        return comboIdentifiers.stream().distinct().collect(Collectors.toList());
    }

}
