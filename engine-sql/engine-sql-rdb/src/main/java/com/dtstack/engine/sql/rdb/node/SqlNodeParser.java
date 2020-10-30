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


package com.dtstack.engine.sql.rdb.node;

import com.dtstack.engine.sql.BaseSqlParser;
import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.KeywordsHelper;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.rdb.SqlNodeParserFactory;
import com.dtstack.engine.sql.rdb.SqlNodeParserImpl;
import com.dtstack.engine.sql.utils.SqlFormatUtil;
import com.dtstack.engine.sql.utils.SqlRegexUtil;
import com.google.common.collect.Lists;
import org.dtstack.apache.calcite.avatica.util.Casing;
import org.dtstack.apache.calcite.avatica.util.Quoting;
import org.dtstack.apache.calcite.sql.SqlBasicCall;
import org.dtstack.apache.calcite.sql.SqlDataTypeSpec;
import org.dtstack.apache.calcite.sql.SqlIdentifier;
import org.dtstack.apache.calcite.sql.SqlInsert;
import org.dtstack.apache.calcite.sql.SqlJoin;
import org.dtstack.apache.calcite.sql.SqlKind;
import org.dtstack.apache.calcite.sql.SqlNode;
import org.dtstack.apache.calcite.sql.SqlNodeList;
import org.dtstack.apache.calcite.sql.SqlOrderBy;
import org.dtstack.apache.calcite.sql.SqlSelect;
import org.dtstack.apache.calcite.sql.SqlWith;
import org.dtstack.apache.calcite.sql.SqlWithItem;
import org.dtstack.apache.calcite.sql.ddl.SqlColumnDeclaration;
import org.dtstack.apache.calcite.sql.ddl.SqlCreateTable;
import org.dtstack.apache.calcite.sql.parser.SqlParseException;
import org.dtstack.apache.calcite.sql.parser.SqlParser;
import org.dtstack.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jiangbo
 * @date 2019/5/18
 */
public class SqlNodeParser extends BaseSqlParser {

    public static Logger LOG = LoggerFactory.getLogger(SqlNodeParser.class);

    private static final String SPLIT_DOT = ".";

    private String currentDb;

    private Set<Table> tables = new HashSet<>();

    public SqlNodeParser(IUglySqlHandler uglySqlHandler) {
        super(uglySqlHandler);
    }

    @Override
    public ParseResult parseSql(String originSql, String currentDb, Map<String, List<Column>> tableColumnsMap) throws Exception {
        ParseResult parseResult = new ParseResult();
        parseResult.setOriginSql(originSql);
        parseResult.setCurrentDb(currentDb);
        parseResult.setMainTable(new Table());

        originSql = SqlFormatUtil.formatSql(originSql);
        String standardSql = SqlFormatUtil.getStandardSql(originSql);
        SqlFormatUtil.checkSql(standardSql);
        parseResult.setStandardSql(standardSql);

        // 解析和去掉sql中的生命周期和类目信息
        parseLifecycleAndCatalogue(parseResult);

        SqlNodeParserImpl nodeParser;
        SqlNode sqlNode = null;

        // calcite不支持 create table like语句，这里做单独处理
        if (SqlRegexUtil.isCreateLike(standardSql)) {
            nodeParser = new CreateTableSqlNodeParser();
            ((CreateTableSqlNodeParser) nodeParser).setSql(parseResult.getStandardSql());
        } else if (SqlRegexUtil.isAlterSql(standardSql)) {
            nodeParser = new AlterSqlNodeParser();
            ((AlterSqlNodeParser) nodeParser).setSql(parseResult.getStandardSql());
        } else {
            if (SqlRegexUtil.isCreateTemp(standardSql)) {
                parseResult.setStandardSql(SqlRegexUtil.removeTempKey(parseResult.getStandardSql()));
                parseResult.setExtraSqlType(SqlType.CREATE_TEMP);
            }

            SqlParser.ConfigBuilder builder = SqlParser.configBuilder();
            builder.setParserFactory(SqlDdlParserImpl.FACTORY);
            builder.setCaseSensitive(true);
            builder.setQuoting(Quoting.DOUBLE_QUOTE);
            builder.setQuotedCasing(Casing.UNCHANGED);
            builder.setUnquotedCasing(Casing.UNCHANGED);

            SqlParser parser = SqlParser.create(parseResult.getStandardSql(), builder.build());
            try {
                sqlNode = parser.parseQuery();
            } catch (SqlParseException e) {
                String formattedSql = this.uglySqlHandler.parseUglySql(originSql);
                if (StringUtils.isEmpty(formattedSql)) {
                    return parseResult;
                }
                ParseResult parseResultAnother = anotherParse(formattedSql, currentDb, tableColumnsMap);
                if (this.uglySqlHandler.isTemp()) {
                    //临时表也要存储表结构
                    parseResultAnother.setExtraSqlType(SqlType.CREATE_TEMP);
                }
                parseResultAnother.setOriginSql(originSql);
                return parseResultAnother;
            }

            SqlNodeParserFactory factory = SqlNodeParserFactory.getInstance();
            nodeParser = factory.getNodeParser(sqlNode.getClass());
        }

        if (nodeParser instanceof SelectSqlNodeParser) {
            ((SelectSqlNodeParser) nodeParser).setTableColumnMap(tableColumnsMap);
        }

        nodeParser.parseSqlNode(sqlNode, parseResult);

        if (StringUtils.isNotEmpty(parseResult.getMainTable().getName())) {
            tables.add(parseResult.getMainTable());
        }

        parseResult.setTables(this.parseTables(currentDb,parseResult.getOriginSql()));
        return parseResult;
    }

    private ParseResult anotherParse(String originSql, String currentDb, Map<String, List<Column>> tableColumnsMap) throws Exception {
        ParseResult parseResult = new ParseResult();
        parseResult.setOriginSql(originSql);
        parseResult.setCurrentDb(currentDb);
        parseResult.setMainTable(new Table());

        originSql = SqlFormatUtil.formatSql(originSql);
        String standardSql = SqlFormatUtil.getStandardSql(originSql);
        SqlFormatUtil.checkSql(standardSql);
        parseResult.setStandardSql(standardSql);

        // 解析和去掉sql中的生命周期和类目信息
        parseLifecycleAndCatalogue(parseResult);

        SqlNodeParserImpl nodeParser;
        SqlNode sqlNode = null;

        // calcite不支持 create table like语句，这里做单独处理
        if (SqlRegexUtil.isCreateLike(standardSql)) {
            nodeParser = new CreateTableSqlNodeParser();
            ((CreateTableSqlNodeParser) nodeParser).setSql(parseResult.getStandardSql());
        } else if (SqlRegexUtil.isAlterSql(standardSql)) {
            nodeParser = new AlterSqlNodeParser();
            ((AlterSqlNodeParser) nodeParser).setSql(parseResult.getStandardSql());
        } else {
            SqlParser.ConfigBuilder builder = SqlParser.configBuilder();
            builder.setParserFactory(SqlDdlParserImpl.FACTORY);
            builder.setCaseSensitive(true);
            builder.setQuoting(Quoting.DOUBLE_QUOTE);
            builder.setQuotedCasing(Casing.UNCHANGED);
            builder.setUnquotedCasing(Casing.UNCHANGED);

//            SqlParser parser = SqlParser.create(parseResult.getStandardSql(), builder.build());
            try {
//                sqlNode = parser.parseQuery();
                sqlNode = parse(parseResult.getStandardSql(),builder.build());
            } catch (SqlParseException e) {
                LOG.error("sql解析异常========>>>>:{}", e.getMessage());
                throw e;
            }
            SqlNodeParserFactory factory = SqlNodeParserFactory.getInstance();
            nodeParser = factory.getNodeParser(sqlNode.getClass());
        }

        if (nodeParser instanceof SelectSqlNodeParser) {
            ((SelectSqlNodeParser) nodeParser).setTableColumnMap(tableColumnsMap);
        }

        nodeParser.parseSqlNode(sqlNode, parseResult);

        if (StringUtils.isNotEmpty(parseResult.getMainTable().getName())) {
            tables.add(parseResult.getMainTable());
        }

        parseResult.setTables(new ArrayList<>(tables));
        return parseResult;
    }

    /**
     * 解析带有查询语句sql里的表名
     *
     * @param currentDb 当前的数据库
     * @param sql       sq语句
     * @return tables
     * @throws Exception
     */
    @Override
    public List<Table> parseTables(String currentDb, String sql) throws Exception {
        this.currentDb = currentDb;

        if (SqlRegexUtil.isCreateLike(sql)) {
            parseSqlCreateLike(sql, tables);
        } else {
            if (SqlRegexUtil.isCreateTemp(sql)) {
                sql = SqlRegexUtil.removeTempKey(sql);
            }

            try {
                SqlParser.ConfigBuilder builder = SqlParser.configBuilder();
                builder.setParserFactory(SqlDdlParserImpl.FACTORY);
                builder.setCaseSensitive(true);
                builder.setQuoting(Quoting.DOUBLE_QUOTE);
                builder.setQuotedCasing(Casing.UNCHANGED);
                builder.setUnquotedCasing(Casing.UNCHANGED);

                SqlParser parser = SqlParser.create(sql, builder.build());
                SqlNode sqlNode = parser.parseQuery();

                parseSqlNode(sqlNode, tables);
            } catch (Exception e) {
                String formattedSql =   this.uglySqlHandler.parseUglySql(sql);
                if (StringUtils.isEmpty(formattedSql)) {
                    return Lists.newArrayList();
                }
                return anotherParseTable(currentDb, formattedSql);
            }
        }

        return new ArrayList<>(tables);
    }

    @Override
    public ParseResult parseTableLineage(String originSql, String currentDb) throws Exception {
        return null;
    }

    public List<Column> parserTempTableColumns(String sql) {
        String handledSql = this.uglySqlHandler.parseUglySql(sql);
        if (this.uglySqlHandler.isTemp()) {
            SqlParser.ConfigBuilder builder = SqlParser.configBuilder();
            builder.setParserFactory(SqlDdlParserImpl.FACTORY);
            builder.setCaseSensitive(true);
            builder.setQuoting(Quoting.DOUBLE_QUOTE);
            builder.setQuotedCasing(Casing.UNCHANGED);
            builder.setUnquotedCasing(Casing.UNCHANGED);

            SqlParser parser = SqlParser.create(handledSql, builder.build());
            try {
                SqlNode sqlNode = parser.parseQuery();
                SqlCreateTable createTable = (SqlCreateTable) sqlNode;
                SqlIdentifier dbTableIdentifier = (SqlIdentifier) createTable.getOperandList().get(0);
                String tableName = getTableNameFromIdentifier(dbTableIdentifier);
                return parseCreateTableColumn(createTable, tableName);
            } catch (SqlParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getTableNameFromIdentifier(SqlIdentifier dbTableIdentifier) {
        List<String> identifierList = dbTableIdentifier.names.asList();
        if (identifierList.size() == 1) {
            return identifierList.get(0);
        } else if (identifierList.size() == 2) {
            return identifierList.get(1);
        }
        return null;
    }

    private List<Column> parseCreateTableColumn(SqlCreateTable sqlNode, String tableName) {
        List<Column> resultList = Lists.newArrayList();
        List<SqlNode> operandList = sqlNode.getOperandList();
        SqlNodeList columnNodeList = (SqlNodeList) operandList.get(1);
        if (columnNodeList != null) {
            List<SqlNode> columnNodes = columnNodeList.getList();
            for (SqlNode node : columnNodes) {
                if (node instanceof SqlColumnDeclaration) {
                    Column column = new Column();
                    column.setTable(tableName);
                    List<SqlNode> sqlNodes = ((SqlColumnDeclaration) node).getOperandList();
                    SqlIdentifier name = (SqlIdentifier) sqlNodes.get(0);
                    SqlDataTypeSpec typeSpec = (SqlDataTypeSpec) sqlNodes.get(1);

                    column.setName(name.names.get(0));
                    column.setType(typeSpec.getTypeName().names.get(0));

                    resultList.add(column);
                }
            }
            return resultList;
        }

        SqlNode query = operandList.get(2);
        if (SqlKind.SELECT.equals(query.getKind())) {
            SqlSelect selectNode = (SqlSelect) query;
            SqlNodeList selectList = selectNode.getSelectList();
            if (selectList != null) {
                List<SqlNode> list = selectList.getList();
                for (SqlNode node : list) {
                    if (node instanceof SqlIdentifier) {
                        SqlIdentifier iNode = (SqlIdentifier) node;
                        String columnName = getTableNameFromIdentifier(iNode);
                        Column column = new Column();
                        column.setTable(tableName);
                        column.setName(columnName);
                        resultList.add(column);
                    } else if (node instanceof SqlBasicCall) {
                        SqlBasicCall bNode = (SqlBasicCall) node;
                        if (SqlKind.AS.equals(bNode.getKind())) {
                            SqlIdentifier asINode = (SqlIdentifier) bNode.getOperandList().get(1);
                            String columnName = getTableNameFromIdentifier(asINode);
                            Column column = new Column();
                            column.setTable(tableName);
                            column.setName(columnName);
                            resultList.add(column);
                        }
                    }
                }
            }

        }
        return resultList;
    }

    private List<Table> anotherParseTable(String currentDb, String sql) throws Exception {
        this.currentDb = currentDb;

        if (SqlRegexUtil.isCreateLike(sql)) {
            parseSqlCreateLike(sql, tables);
        } else {
            try {
                SqlParser.ConfigBuilder builder = SqlParser.configBuilder();
                builder.setParserFactory(SqlDdlParserImpl.FACTORY);
                builder.setCaseSensitive(true);
                builder.setQuoting(Quoting.DOUBLE_QUOTE);
                builder.setQuotedCasing(Casing.UNCHANGED);
                builder.setUnquotedCasing(Casing.UNCHANGED);

//                SqlParser parser = SqlParser.create(sql, builder.build());
//                SqlNode sqlNode = parser.parseQuery();
                SqlNode sqlNode = parse(sql,builder.build());
                parseSqlNode(sqlNode, tables);
            } catch (Exception e) {
                LOG.warn("解析表失败===============>>>>{}", e.getMessage());
            }
        }

        return new ArrayList<>(tables);
    }

    private SqlNode parse(String sql,SqlParser.Config config) throws SqlParseException {
        SqlParser parser = SqlParser.create(sql, config);
        try {
            return parser.parseQuery();
        } catch (SqlParseException e) {
            String msg = e.getMessage();
            if (KeywordsHelper.exceptionWithKeywords(msg)){
                KeywordsHelper keywordsHelper = new KeywordsHelper(sql);
                if (keywordsHelper.parseErrorInfo(msg)){
                    return parse(keywordsHelper.getSql(),config);
                }
            }
            LOG.error("sql解析异常:{}",e.getMessage());
            throw e;
        }
    }

    private void parseSqlCreateLike(String sql, Set<Table> tables) {
        Map<String, String> dbTable = SqlRegexUtil.parseDbTableFromLikeSql(sql);

        Table table = new Table();
        table.setDb(dbTable.get(SqlRegexUtil.KEY_DB) == null ? currentDb : dbTable.get(SqlRegexUtil.KEY_DB));
        table.setName(dbTable.get(SqlRegexUtil.KEY_TABLE));
        table.setLikeTableDb(dbTable.get(SqlRegexUtil.KEY_LIKE_DB) == null ? currentDb : dbTable.get(SqlRegexUtil.KEY_LIKE_DB));
        table.setLikeTable(dbTable.get(SqlRegexUtil.KEY_LIKE_TABLE));

        tables.add(table);
    }

    private void parseSqlNode(SqlNode sqlNode, Set<Table> tables) {
        if (sqlNode instanceof SqlCreateTable) {
            parseSqlCreate((SqlCreateTable) sqlNode, tables);
        } else if (sqlNode instanceof SqlBasicCall) {
            parseSqlBasicCall((SqlBasicCall) sqlNode, tables);
        } else if (sqlNode instanceof SqlWith) {
            parseSqlWith((SqlWith) sqlNode, tables);
        } else if (sqlNode instanceof SqlInsert) {
            parseSqlInsert((SqlInsert) sqlNode, tables);
        } else if (sqlNode instanceof SqlJoin) {
            parseSqlJoin((SqlJoin) sqlNode, tables);
        } else if (sqlNode instanceof SqlOrderBy){
            parseSqlSelect((SqlSelect) ((SqlOrderBy) sqlNode).query,tables);
        } else {
            parseSqlSelect((SqlSelect) sqlNode, tables);
        }
    }

    private void parseSqlJoin(SqlJoin sqlNode, Set<Table> tables) {
        sqlNode.getJoinType();
        SqlNode left = sqlNode.getLeft();
        SqlNode right = sqlNode.getRight();
        if (left != null) {
            if (left instanceof SqlBasicCall){
                parseSqlBasicCall((SqlBasicCall) left, tables);
            }else {
                parseSqlNode(left,tables);
            }
        }
        if (right != null) {
            if (right instanceof SqlBasicCall){
                parseSqlBasicCall((SqlBasicCall) right, tables);
            }else {
                parseSqlNode(right,tables);
            }
        }
    }

    private void parseSqlInsert(SqlInsert sqlNode, Set<Table> tables) {
        Table table = new Table();
        SqlIdentifier targetTableIdentifier = (SqlIdentifier) sqlNode.getTargetTable();
        List<String> identifierList = targetTableIdentifier.names.asList();
        if (identifierList.size() == 1) {
            table.setName(identifierList.get(0));
            table.setDb(currentDb);
        } else if (identifierList.size() == 2) {
            table.setName(identifierList.get(1));
            table.setDb(identifierList.get(0));
        }
        SqlNode source = sqlNode.getSource();
        if (source != null && source instanceof SqlSelect) {
            parseSqlSelect((SqlSelect) source, tables);
        } else if (source != null && source instanceof SqlBasicCall){
            parseSqlBasicCall((SqlBasicCall) source,tables);
        }
        tables.add(table);
    }

    private void parseSqlCreate(SqlCreateTable createTable, Set<Table> tables) {
        Table table = new Table();
        List<SqlNode> operandList = createTable.getOperandList();
        SqlIdentifier dbTableIdentifier = (SqlIdentifier) operandList.get(0);
        SqlNode query = operandList.get(2);
        if (query != null) {
            parseSqlNode(query, tables);
        }

        List<String> identifierList = dbTableIdentifier.names.asList();
        if (identifierList.size() == 1) {
            table.setName(identifierList.get(0));
            table.setDb(currentDb);
        } else if (identifierList.size() == 2) {
            table.setName(identifierList.get(1));
            table.setDb(identifierList.get(0));
        }

        tables.add(table);
    }

    private void parseSqlBasicCall(SqlBasicCall sqlBasicCall, Set<Table> tables) {
        SqlKind kind = sqlBasicCall.getOperator().kind;
        if (SqlKind.AS == kind) {
            // temp和join
            List<SqlNode> operands = sqlBasicCall.getOperandList();
            if (operands.get(0) instanceof SqlIdentifier) {
                // join tb
                tables.add(getTable(operands.get(0).toString()));
            } else {
                parseSqlNode(operands.get(0), tables);
            }
        } else if (SqlKind.UNION == kind) {
            for (SqlNode sqlNode : sqlBasicCall.getOperandList()) {
                parseSqlNode(sqlNode, tables);
            }
        }
        //添加where 条件中表解析
        else {
            List<SqlNode> operands = sqlBasicCall.getOperandList();
            if (CollectionUtils.isNotEmpty(operands)){
                for (SqlNode node:operands){
                    if (node instanceof SqlCreateTable || node instanceof SqlBasicCall || node instanceof SqlWith
                            || node instanceof SqlInsert || node instanceof SqlJoin){
                        parseSqlNode(node,tables);
                    }else {
                        LOG.info("warning!!未解析的类型：{}",node.getClass().getName());
                    }
                }
            }
        }
    }

    private void parseSqlWith(SqlWith sqlWith, Set<Table> tables) {
        List<SqlNode> operandList = sqlWith.getOperandList();
        SqlNodeList nodesList = (SqlNodeList) operandList.get(0);
        for (SqlNode sqlNode : nodesList.getList()) {
            parseSqlWithItem((SqlWithItem) sqlNode, tables);
        }

        SqlSelect body = (SqlSelect) operandList.get(1);
        parseSqlSelect(body, tables);
    }

    private void parseSqlWithItem(SqlWithItem item, Set<Table> tables) {
        parseSqlSelect((SqlSelect) item.query, tables);
    }

    private void parseSqlSelect(SqlSelect sqlSelect, Set<Table> tables) {
        //deal from
        dealFrom(sqlSelect.getFrom(), tables);
        //解析表依赖关系时，也需要解析where条件中的表
        if (null != sqlSelect.getWhere()){
            dealWhere(sqlSelect.getWhere(),tables);
        }
    }

    private void dealWhere(SqlNode whereNode, Set<Table> tables) {
        if (whereNode instanceof SqlBasicCall){
            parseSqlBasicCall((SqlBasicCall) whereNode, tables);
        }else {
            LOG.info("未处理的where case:{}",whereNode.getKind().sql);
        }
    }

    private void dealFrom(SqlNode tableNode, Set<Table> tables) {
        switch (tableNode.getKind()) {
            case IDENTIFIER:
                tables.add(getTable(tableNode.toString()));
                break;
            case AS:
                // 这里需要单独处理 select * from db.tb alias 这种情况，不用再生成子节点去处理了
                if (tableNode instanceof SqlBasicCall) {
                    List<SqlNode> operandList = ((SqlBasicCall) tableNode).getOperandList();
                    if (operandList.get(0) instanceof SqlIdentifier) {
                        tables.add(getTable(operandList.get(0).toString()));
                        break;
                    }
                }

                parseSqlNode(tableNode, tables);
                break;
            case JOIN:
                SqlJoin joinTab = (SqlJoin) tableNode;

                dealJoinNode(joinTab.getLeft(), tables);
                dealJoinNode(joinTab.getRight(), tables);
                break;
            case UNION:
                if (tableNode instanceof SqlBasicCall) {
                    for (SqlNode sqlNode : ((SqlBasicCall) tableNode).getOperandList()) {
                        parseSqlNode(sqlNode, tables);
                    }
                } else {
                    LOG.debug("warning!!! 未处理的union case");
                }
                break;
            case SELECT:
                if (tableNode instanceof SqlSelect){
                    dealFrom(((SqlSelect) tableNode).getFrom(),tables);
                }else {
                    LOG.debug("warning!!! 未处理的select case");
                }
                break;
            default:
        }
    }

    private void dealJoinNode(SqlNode sqlNode, Set<Table> tables) {
        if (sqlNode instanceof SqlIdentifier) {
            tables.add(getTable(sqlNode.toString()));
        } else {
            parseSqlNode(sqlNode, tables);
        }
    }

    private Table getTable(String dbTable) {
        Table table = new Table();
        if (dbTable.contains(SPLIT_DOT)) {
            String[] splits = dbTable.split("\\.");
            table.setDb(splits[0]);
            table.setName(splits[1]);
        } else {
            table.setDb(currentDb);
            table.setName(dbTable);
        }

        return table;
    }
}
