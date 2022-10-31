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

package com.dtstack.taier.develop.sql.hive;

import com.dtstack.taier.develop.sql.AlterResult;
import com.dtstack.taier.develop.sql.BaseSqlParser;
import com.dtstack.taier.develop.sql.Column;
import com.dtstack.taier.develop.sql.ColumnLineage;
import com.dtstack.taier.develop.sql.Pair;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.sql.SqlType;
import com.dtstack.taier.develop.sql.Table;
import com.dtstack.taier.develop.sql.TableLineage;
import com.dtstack.taier.develop.sql.TableOperateEnum;
import com.dtstack.taier.develop.sql.calcite.LineageParser;
import com.dtstack.taier.develop.sql.handler.IUglySqlHandler;
import com.dtstack.taier.develop.sql.hive.node.NodeParser;
import com.dtstack.taier.develop.sql.hive.node.OtherNodeParser;
import com.dtstack.taier.develop.sql.node.AlterNode;
import com.dtstack.taier.develop.sql.node.CreateNode;
import com.dtstack.taier.develop.sql.node.DropNode;
import com.dtstack.taier.develop.sql.node.Identifier;
import com.dtstack.taier.develop.sql.node.InsertNode;
import com.dtstack.taier.develop.sql.node.SelectNode;
import com.dtstack.taier.develop.sql.utils.SqlFormatUtil;
import com.dtstack.taier.develop.sql.utils.SqlRegexUtil;
import com.dtstack.taier.develop.sql.utils.SqlTypeRegexUtil;
import com.google.common.collect.Lists;
import org.apache.calcite.sql.SqlKind;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * hive sql解析实现类
 *
 * @author jiangbo
 */
public class AstNodeParser extends BaseSqlParser {

    public static Logger LOG = LoggerFactory.getLogger(AstNodeParser.class);

    private static ParseDriver parseDriver = new ParseDriver();

    public AstNodeParser() {
    }

    public AstNodeParser(IUglySqlHandler uglySqlHandler) {
        super(uglySqlHandler);
    }

    /**
     * 解析sql
     */
    @Override
    public ParseResult parseSql(String originSql, String currentDb, Map<String, List<Column>> tableColumnsMap) throws Exception {
        ParseResult parseResult = new ParseResult();
        parseResult.setCurrentDb(currentDb);
        parseResult.setOriginSql(originSql);
        parseResult.setMainTable(new Table());
        ASTNode root;
        originSql = SqlFormatUtil.formatSql(originSql);

        // 解析和去掉sql中的生命周期和类目信息
        parseLifecycleAndCatalogue(parseResult);
        SqlFormatUtil.checkSql(parseResult.getStandardSql());
        if (isNotParse(originSql, parseResult)) {
            return parseResult;
        }
        if (SqlRegexUtil.isCreateTemp(parseResult.getStandardSql())) {
            parseResult.setStandardSql(SqlRegexUtil.removeTempKey(parseResult.getStandardSql()));
            parseResult.setExtraSqlType(SqlType.CREATE_TEMP);
        }
        String formattedSql = this.uglySqlHandler.parseUglySql(originSql);
        if (StringUtils.isEmpty(formattedSql)) {
            return parseResult;
        }
        try {
            root = parseDriver.parse(formattedSql);
        } catch (ParseException e) {
            LOG.error("sql解析异常========>>>>:{}", e.getMessage());
            //用正则匹配一下
            ParseResult p = SqlTypeRegexUtil.getParseResultByRegexSql(parseResult);
            return p;
        }
        return getParseResult(originSql, currentDb, tableColumnsMap, parseResult, root);
    }

    /**
     * 进行语法树转换 和 血缘解析的具体类
     *
     * @param originSql
     * @param currentDb
     * @param tableColumnsMap
     * @param parseResult
     * @param root
     * @return
     */
    private ParseResult getParseResult(String originSql, String currentDb, Map<String, List<Column>> tableColumnsMap, ParseResult parseResult, ASTNode root) {
        NodeParser nodeParser = ASTNodeLineageParser.ParserProxy.getParser((ASTNode) root.getChild(0), originSql);
        if (Objects.isNull(nodeParser)) {
            return parseResult;
        }
        if (nodeParser instanceof OtherNodeParser) {
            ((OtherNodeParser) nodeParser).getSqlType((ASTNode) root.getChild(0), parseResult);
            return parseResult;
        }
        com.dtstack.taier.develop.sql.node.Node node = nodeParser.parseSql((ASTNode) root.getChild(0), currentDb, tableColumnsMap, new HashMap<>());
        if (Objects.isNull(node)) {
            return parseResult;
        }
        LineageParser lineageParser = LineageParser.HiveParserProxy.getParser(node, currentDb, tableColumnsMap);
        if (lineageParser != null) {
            List<ColumnLineage> columnLineages = getColumnLineages(node, lineageParser);
            parseResult.setColumnLineages(columnLineages);
        }
        getMainTable(node, parseResult, root);
        try {
            Set<Table> tables = new HashSet<>();
            nodeParser.parseSqlTable(node, tables);
            parseResult.setTables(new ArrayList<>(tables));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parseResult;
    }

    @Override
    public List<Table> parseTables(String currentDb, String sql) throws Exception {
        Set<Table> tables = new HashSet<>();
        ASTNode root;
        sql = SqlFormatUtil.formatSql(sql);
        String standardSql = SqlFormatUtil.getStandardSql(sql);
        SqlFormatUtil.checkSql(standardSql);
        String formattedSql = null;
        formattedSql = this.uglySqlHandler.parseUglySql(standardSql);
        if (StringUtils.isEmpty(formattedSql)) {
            return Lists.newArrayList();
        }
        try {
            root = parseDriver.parse(formattedSql);
        } catch (ParseException e) {
            LOG.error("sql解析异常========>>>>:{}", e.getMessage());
            return new ArrayList<>();
        }
        if (Objects.isNull(root)) {
            return new ArrayList<>();
        }
        NodeParser nodeParser = ASTNodeLineageParser.ParserProxy.getParser((ASTNode) root.getChild(0), sql);
        if (Objects.isNull(nodeParser)) {
            return new ArrayList<>();
        }
        com.dtstack.taier.develop.sql.node.Node node = nodeParser.parseSql((ASTNode) root.getChild(0), currentDb, new HashMap<>(), new HashMap<>());
        if (Objects.isNull(node)) {
            return new ArrayList<>();
        }
        nodeParser.parseSqlTable(node, tables);
        return new ArrayList<>(tables);
    }

    @Override
    public ParseResult parseTableLineage(String originSql, String currentDb) throws Exception {
        ParseResult parseResult = new ParseResult();
        parseResult.setCurrentDb(currentDb);
        parseResult.setOriginSql(originSql);
        parseResult.setMainTable(new Table());

        ASTNode root;
        originSql = SqlFormatUtil.formatSql(originSql);
        String standardSql = SqlFormatUtil.getStandardSql(originSql);
        SqlFormatUtil.checkSql(standardSql);
        parseResult.setStandardSql(standardSql);

        // 解析和去掉sql中的生命周期和类目信息
        parseLifecycleAndCatalogue(parseResult);
        String formattedSql = this.uglySqlHandler.parseUglySql(originSql);
        if (StringUtils.isEmpty(formattedSql)) {
            return parseResult;
        }
        parseResult.setStandardSql(formattedSql);
        try {
            root = parseDriver.parse(parseResult.getStandardSql());
        } catch (ParseException e) {
            LOG.error("sql解析异常========>>>>:{}", e.getMessage());
            //用正则匹配一下
            ParseResult p = SqlTypeRegexUtil.getParseResultByRegexSql(parseResult);
            return p;
        }
        NodeParser nodeParser = ASTNodeLineageParser.ParserProxy.getParser((ASTNode) root.getChild(0), originSql);
        com.dtstack.taier.develop.sql.node.Node node = nodeParser.parseSql((ASTNode) root.getChild(0), currentDb, new HashMap<>(), new HashMap<>());
        LineageParser lineageParser = LineageParser.HiveParserProxy.getParser(node, currentDb, new HashMap<>());
        if (lineageParser != null) {
            List<TableLineage> tableLineages = getTableLineages(node, lineageParser);
            parseResult.setTableLineages(tableLineages);
        }
        getMainTable(node, parseResult, root);
        return parseResult;
    }

    public Set<String> parseFunction(String sql) {
        Set<String> backLists = new HashSet<>();
        String standardSql = SqlFormatUtil.getStandardSql(SqlFormatUtil.formatSql(sql));
        String formattedSql = this.uglySqlHandler.parseUglySql(standardSql);
        List<String> formatSqls = SqlFormatUtil.splitSqlWithoutSemi(formattedSql);
        for (String singleSql : formatSqls) {
            ASTNode root = null;
            try {
                singleSql = removeEl(singleSql);
                root = parseDriver.parse(singleSql);
                backLists.addAll(ASTNodeUtil.getFunctionNames(root));
            } catch (ParseException e) {
                LOG.warn("parseFunction error:{}", e);
            }
        }
        return backLists.stream().filter(StringUtils::isNoneBlank).collect(Collectors.toSet());
    }

    private void parseTableFromASTNode(ASTNode root, Set<Table> tables, String defaultDb) {
        if (root.getToken().getType() == HiveParser.TOK_TABNAME) {
            Table table = new Table();
            int type = root.getParent().getType();
            if (type == HiveParser.TOK_TAB) {
                table.setOperate(TableOperateEnum.INSERT);
            } else if (type == HiveParser.TOK_TABREF) {
                table.setOperate(TableOperateEnum.SELECT);
            } else if (type == HiveParser.TOK_ALTERTABLE) {
                if (ASTNodeUtil.contains((ASTNode) root.getParent(), HiveParser.TOK_ALTERTABLE_ADDPARTS)) {
                    table.setOperate(TableOperateEnum.ALTERTABLE_ADDPARTS);
                } else {
                    table.setOperate(TableOperateEnum.ALTER);
                }
            } else {
                table.setOperate(TableOperateEnum.getOperateBySqlType(type));
            }

            Map<String, String> tableDb = ASTNodeUtil.getTableNameAndDbName((ASTNode) root.getParent());
            table.setName(tableDb.get(ASTNodeUtil.TABLE_NAME_KEY));
            table.setDb(tableDb.get(ASTNodeUtil.DB_NAME_KEY) == null ? defaultDb : tableDb.get(ASTNodeUtil.DB_NAME_KEY));

            tables.add(table);
        } else if (root.getChildCount() > 0) {
            for (Node node : root.getChildren()) {
                parseTableFromASTNode((ASTNode) node, tables, defaultDb);
            }
        }
    }

    private List<ColumnLineage> getColumnLineages(com.dtstack.taier.develop.sql.node.Node sqlNode, LineageParser lineageParser) {
        List<ColumnLineage> resList = Lists.newArrayList();
        List<Pair<Identifier, Identifier>> pairs = lineageParser.parseColumnLineage(sqlNode);
        if (CollectionUtils.isNotEmpty(pairs)) {
            for (Pair<Identifier, Identifier> pair : pairs) {
                Identifier key = pair.getKey();
                Identifier value = pair.getValue();
                ColumnLineage columnLineage = new ColumnLineage();
                columnLineage.setFromDb(value.getDb().replace("`",""));
                columnLineage.setFromTable(value.getTable().replace("`",""));
                columnLineage.setFromColumn(value.getColumn().replace("`",""));
                columnLineage.setToDb(key.getDb().replace("`",""));
                columnLineage.setToTable(key.getTable().replace("`",""));
                columnLineage.setToColumn(key.getColumn().replace("`",""));
                resList.add(columnLineage);
            }
        }
        return resList;
    }

    /**
     * 对表级血缘进行组装
     *
     * @param sqlNode
     * @param lineageParser
     * @return
     */
    private List<TableLineage> getTableLineages(com.dtstack.taier.develop.sql.node.Node sqlNode, LineageParser lineageParser) {
        List<TableLineage> resList = Lists.newArrayList();
        List<Pair<Identifier, Identifier>> pairs = lineageParser.parseTableLineage(sqlNode);
        if (CollectionUtils.isNotEmpty(pairs)) {
            for (Pair<Identifier, Identifier> pair : pairs) {
                Identifier key = pair.getKey();
                Identifier value = pair.getValue();
                TableLineage tableLineage = new TableLineage();
                tableLineage.setFromDb(value.getDb());
                tableLineage.setFromTable(value.getTable());
                tableLineage.setToDb(key.getDb());
                tableLineage.setToTable(key.getTable());
                resList.add(tableLineage);
            }
        }
        return resList;
    }

    /*
       获取main主表
    */
    private void getMainTable(com.dtstack.taier.develop.sql.node.Node node, ParseResult parseResult, ASTNode root) {
        Table mainTable = parseResult.getMainTable();
        if (null == mainTable) {
            mainTable = new Table();
        }
        Identifier table = null;
        if (node instanceof InsertNode) {
            table = ((InsertNode) node).getTargetTable();
            parseResult.setSqlType(SqlType.INSERT);
        } else if (node instanceof CreateNode) {
            table = ((CreateNode) node).getName();
            if (((CreateNode) node).getSqlKind().equals(SqlKind.SELECT)) {
                parseResult.setSqlType(SqlType.CREATE_AS);
            } else if (((CreateNode) node).getSqlKind().equals(SqlKind.LIKE)) {
                parseResult.setSqlType(SqlType.CREATE_LIKE);
            } else {
                parseResult.setSqlType(SqlType.CREATE);
            }
        } else if (node instanceof DropNode) {
            table = ((DropNode) node).getTargetTable();
            parseResult.setSqlType(SqlType.DROP);
        } else if (node instanceof AlterNode) {
            AlterNode alterNode = (AlterNode) node;
            table = alterNode.getSourceTable();
            converAlterResultVO(parseResult, alterNode);
            parseResult.setSqlType(SqlType.ALTER);
        } else if (node instanceof SelectNode) {
            parseResult.setLimit(((SelectNode) node).getLimit());
            if (((SelectNode) node).getFromClause() instanceof Identifier) {
                table = (Identifier) ((SelectNode) node).getFromClause();
            }
            if (parseResult.getStandardSql().toLowerCase().startsWith("with")){
                parseResult.setSqlType(SqlType.WITH_QUERY);
            }else {
                parseResult.setSqlType(SqlType.QUERY);
            }
        } else {
            parseResult.setSqlType(SqlType.OTHER);
        }
        if (table != null) {
            mainTable.setName(table.getTable());
            mainTable.setDb(StringUtils.isNotEmpty(table.getDb()) ? table.getDb() : table.getDefaultDb());
        }
        boolean isIgnore = ASTNodeUtil.contains((ASTNode) root.getChild(0), HiveParser.TOK_IFNOTEXISTS) || ASTNodeUtil.contains((ASTNode) root.getChild(0), HiveParser.TOK_IFEXISTS);
        if (root.getChild(0).getType() == HiveParser.TOK_CREATEVIEW) {
            mainTable.setView(true);
        }
        mainTable.setIgnore(isIgnore);
        parseResult.setMainTable(mainTable);
    }

    /**
     * alter语句结果转换
     *
     * @param parseResult
     * @param alterNode
     */
    private void converAlterResultVO(ParseResult parseResult, AlterNode alterNode) {
        AlterResult alterResult = new AlterResult();
        alterResult.setAlterType(alterNode.getAlterType());
        alterResult.setOldTableName(alterNode.getSourceTable().getTable());
        alterResult.setNewDB(alterNode.getTargetTable().getDb());
        alterResult.setOldDB(alterNode.getSourceTable().getDb());
        alterResult.setNewTableName(alterNode.getTargetTable().getTable());
        alterResult.setAlterColumnResult(alterNode.getAlterColumnMap());
        alterResult.setNewColumns(alterNode.getNewColumns());
        alterResult.setDropParts(alterNode.getDropParts());
        alterResult.setNewPartitions(alterNode.getNewPartitions());
        alterResult.setTableProperties(alterNode.getTableProperties());
        alterResult.setRenamePart(alterNode.getRenamePart());
        parseResult.setAlterResult(alterResult);
    }

    /**
     * 是否是数据库操作
     *
     * @param astNode
     * @return
     */
    private Boolean isDataBaseOperate(ASTNode astNode) {
        return HiveParser.TOK_CREATEDATABASE == astNode.getChild(0).getType() || HiveParser.TOK_DROPDATABASE == astNode.getChild(0).getType();
    }

    /**
     * 是否是desc
     *
     * @param astNode
     * @return
     */
    private Boolean isDescOperate(ASTNode astNode) {
        return HiveParser.TOK_SHOWTABLES == astNode.getChild(0).getType();
    }

    /**
     * 是否是showTable
     *
     * @param astNode
     * @return
     */
    private Boolean isShowTable(ASTNode astNode) {
        return HiveParser.TOK_SHOWTABLES == astNode.getChild(0).getType();
    }

    private Boolean isNotParse(String sql, ParseResult parseResult) {

        //需要解析刷新的表
        if (SqlRegexUtil.isInvalidateTableSql(sql)) {
            this.parseInvalidateTable(parseResult);
            return true;
        }
        if (SqlRegexUtil.isRefreshSql(sql)) {
            parseResult.setStandardSql(sql);
            parseResult.setSqlType(SqlType.OTHER);
            return true;
        }
        return false;
    }

    /**
     * 从invalidate 语句中刷新表信息
     */
    private ParseResult parseInvalidateTable(ParseResult result) {
        Map<String, String> dbTable = SqlRegexUtil.parseDbTableFromInvalidateSql(result.getStandardSql());
        Table table = result.getMainTable();
        if (Objects.isNull(table)) {
            table = new Table();
        }
        table.setDb(dbTable.get(SqlRegexUtil.KEY_DB) == null ? result.getCurrentDb() : dbTable.get(SqlRegexUtil.KEY_DB));
        table.setName(dbTable.get(SqlRegexUtil.KEY_TABLE));
        table.setOperate(TableOperateEnum.INVALIDATE_TABLE);
        if (Objects.isNull(result.getTables())) {
            result.setTables(new ArrayList<>());
        }
        result.getTables().add(table);
        result.setMainTable(table);
        result.setSqlType(SqlType.INVALIDATE_TABLE);
        return result;
    }

    private String removeEl(String sql){
        char[] array = sql.toCharArray();
        StringBuilder sb = new StringBuilder(array.length);
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '$' && i+1<array.length && array[i+1]=='{'){
                while (array[i]!='}'){
                    i++;
                }
                sb.append('1');
                continue;
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

}