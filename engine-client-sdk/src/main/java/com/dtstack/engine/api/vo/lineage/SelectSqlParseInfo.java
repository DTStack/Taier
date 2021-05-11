package com.dtstack.engine.api.vo.lineage;

import com.dtstack.engine.api.pojo.lineage.Column;

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
