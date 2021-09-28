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

package com.dtstack.engine.lineage.vo;

import com.dtstack.engine.pluginapi.pojo.Column;

import java.util.List;

/**
 * @author chener
 * @Classname SelectSqlParseInfo
 * @Description 针对数据api解析sql出入参的封装
 * @Date 2020/10/15 14:14
 * @Created chener@dtstack.com
 */
public class SelectSqlParseInfo extends BaseParseResult{
    /**
     * 出参，查询结果集
     */
    private List<Column> selectList;
    /**
     * where条件中的字段
     */
    private List<Column> whereParams;

    /**
     * sql中的入参
     */
    private List<SqlParam> sqlParams;

    public List<SqlParam> getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(List<SqlParam> sqlParams) {
        this.sqlParams = sqlParams;
    }

    public List<Column> getSelectList() {
        return selectList;
    }

    public void setSelectList(List<Column> selectList) {
        this.selectList = selectList;
    }

    public List<Column> getWhereParams() {
        return whereParams;
    }

    public void setWhereParams(List<Column> whereParams) {
        this.whereParams = whereParams;
    }

    public static class SqlParam{
        /**
         * 参数名
         */
        private String paramName;
        /**
         * 运算法
         */
        private String operator;
        /**
         * 表达式
         */
        private String expression;

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }
}
