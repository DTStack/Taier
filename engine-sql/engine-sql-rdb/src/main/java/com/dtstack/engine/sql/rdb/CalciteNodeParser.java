
package com.dtstack.engine.sql.rdb;

import com.dtstack.engine.sql.AlterResult;
import com.dtstack.engine.sql.BaseSqlParser;
import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ColumnLineage;
import com.dtstack.engine.sql.KeywordsHelper;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.TableOperateEnum;
import com.dtstack.engine.sql.calcite.LineageParser;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.node.CreateNode;
import com.dtstack.engine.sql.node.DeleteNode;
import com.dtstack.engine.sql.node.Identifier;
import com.dtstack.engine.sql.node.InsertNode;
import com.dtstack.engine.sql.node.Node;
import com.dtstack.engine.sql.rdb.node.AlterSqlNodeParser;
import com.dtstack.engine.sql.utils.SqlFormatUtil;
import com.dtstack.engine.sql.utils.SqlRegexUtil;
import com.dtstack.engine.sql.utils.SqlTypeRegexUtil;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDelete;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOrderBy;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlWith;
import org.apache.calcite.sql.SqlWithItem;
import org.apache.calcite.sql.ddl.SqlColumnDeclaration;
import org.apache.calcite.sql.ddl.SqlCreateTable;
import org.apache.calcite.sql.ddl.SqlCreateView;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 针对calcite语法结构的解析器实现。暂未更新解析表依赖
 */
public class CalciteNodeParser extends BaseSqlParser {

    public static Logger LOG = LoggerFactory.getLogger(CalciteNodeParser.class);

    private static final String SPLIT_DOT = ".";

    private String currentDb;
    private Set<Table> tables = new HashSet<>();

    public CalciteNodeParser(IUglySqlHandler uglySqlHandler) {
        super(uglySqlHandler);
    }

    @Override
    public ParseResult parseSql(String originSql, String currentDb, Map<String, List<Column>> tableColumnsMap) throws Exception {

        ParseResult parseResult = new ParseResult();
        parseResult.setOriginSql(originSql);
        parseResult.setCurrentDb(currentDb);
        parseResult.setMainTable(new Table());
        this.tables = new HashSet<>();

        originSql = SqlFormatUtil.formatSql(originSql);
        //需要去除limit 否则拿不到血缘
        String standardSql = SqlFormatUtil.getStandardSql(originSql);
        SqlFormatUtil.checkSql(standardSql);
        parseResult.setStandardSql(SqlFormatUtil.removeLimit(originSql));
        if (SqlRegexUtil.isDescSql(standardSql)) {
            parseResult.setSqlType(SqlType.DESC_TABLE);
            return parseResult;
        }
        if (SqlRegexUtil.notCheckSql(standardSql)) {
            return parseResult;
        }
        // 解析和去掉sql中的生命周期和类目信息
        parseLifecycleAndCatalogue(parseResult);
        SqlNode sqlNode = null;

        if (SqlRegexUtil.isCreateTemp(standardSql)) {
            parseResult.setStandardSql(SqlRegexUtil.removeTempKey(parseResult.getStandardSql()));
            parseResult.setExtraSqlType(SqlType.CREATE_TEMP);
        }

        if (isNotParseSql(standardSql,parseResult)){
            return parseResult;
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
            if (Objects.nonNull(parseResult.getMainTable()) && Objects.nonNull(parseResultAnother.getMainTable())) {
                parseResultAnother.getMainTable().setLifecycle(parseResult.getMainTable().getLifecycle());
                parseResultAnother.getMainTable().setOperate(parseResult.getMainTable().getOperate());
            }
            parseResultAnother.setOriginSql(originSql);
            parseResultAnother.setStandardSql(standardSql);
            return parseResultAnother;
        }
        LineageParser lineageParser = LineageParser.ParserProxy.getParser(sqlNode, currentDb, tableColumnsMap);
        getMainTableAndColumnLineage(currentDb, tableColumnsMap, parseResult, sqlNode, lineageParser);

        return parseResult;
    }

    private ParseResult parseAlterSql(ParseResult parseResult, String standardSql, SqlNode sqlNode) throws SQLException {
        SqlNodeParserImpl nodeParser = new AlterSqlNodeParser();
        ((AlterSqlNodeParser) nodeParser).setSql(parseResult.getStandardSql());
        nodeParser.parseSqlNode(sqlNode, parseResult);
        parseResult.setStandardSql(standardSql);
        parseResult.setSqlType(SqlType.ALTER);
        if (Objects.isNull(parseResult.getTables())) {
            parseResult.setTables(new ArrayList<>());
        }
        parseResult.getTables().add(parseResult.getMainTable());
        //alter 需要填写alterResult
        AlterResult alterResult = new AlterResult();
        alterResult.setAlterType(SqlRegexUtil.getAlterEnum(standardSql));
        if (TableOperateEnum.ALTERTABLE_RENAME.equals(alterResult.getAlterType())) {
            Pattern pattern = Pattern.compile(SqlRegexUtil.ALTER_RENAME_REGEX);
            Matcher matcher = pattern.matcher(standardSql);
            if (matcher.find()) {
                alterResult.setOldTableName(matcher.group("oldTable"));
                alterResult.setNewTableName(matcher.group("newTable"));
            }
        }
        parseResult.setAlterResult(alterResult);
        return parseResult;
    }

    /**
     * 把获取maintable 和 血缘的封装起来
     *
     * @param currentDb
     * @param tableColumnsMap
     * @param parseResult
     * @param sqlNode
     * @param lineageParser
     */
    private void getMainTableAndColumnLineage(String currentDb, Map<String, List<Column>> tableColumnsMap, ParseResult parseResult, SqlNode sqlNode, LineageParser lineageParser) throws Exception {
        if (lineageParser != null) {
            Node node = lineageParser.parseSql(sqlNode, currentDb, tableColumnsMap);
            //TODO 主表可通过node的target获取
            List<ColumnLineage> columnLineages = getColumnLineages(node, lineageParser);
            columnLineages = columnLineages.stream().distinct().collect(Collectors.toList());
            Table mainTable = this.getMainTable(node);
            if (Objects.nonNull(parseResult.getMainTable())) {
                mainTable.setLifecycle(parseResult.getMainTable().getLifecycle());
                mainTable.setOperate(parseResult.getMainTable().getOperate());
            }
            parseResult.setMainTable(mainTable);
            parseResult.setColumnLineages(columnLineages);
        }
        if (SqlKind.CREATE_VIEW == sqlNode.getKind()){
            parseResult.getMainTable().setView(true);
        }
        parseResult.setSqlType(SqlType.getCalciteType(sqlNode.getKind()));
        parseResult.setTables(this.parseTables(currentDb, parseResult.getOriginSql()));
        if (SqlType.DROP.equals(parseResult.getSqlType())) {
            this.parseDropTable(parseResult);
        }
        if (SqlType.CREATE.equals(parseResult.getSqlType()) && !SqlType.CREATE_LIKE.equals(parseResult.getSqlType()) && !SqlType.CREATE_TEMP.equals(parseResult.getSqlType())) {
            if (CollectionUtils.isNotEmpty(parseResult.getTables()) && parseResult.getTables().size() > 1) {
                parseResult.setSqlType(SqlType.CREATE_AS);
            }
        }
    }

    private List<ColumnLineage> getColumnLineages(Node sqlNode, LineageParser lineageParser) {
        List<ColumnLineage> resList = Lists.newArrayList();
        List<Pair<Identifier, Identifier>> pairs = lineageParser.parseColumnLineage(sqlNode);
        if (CollectionUtils.isNotEmpty(pairs)) {
            for (Pair<Identifier, Identifier> pair : pairs) {
                Identifier key = pair.getKey();
                Identifier value = pair.getValue();
                ColumnLineage columnLineage = new ColumnLineage();
                columnLineage.setFromDb(value.getDb());
                columnLineage.setFromTable(value.getTable());
                columnLineage.setFromColumn(value.getColumn());
                columnLineage.setToDb(key.getDb());
                columnLineage.setToTable(key.getTable());
                columnLineage.setToColumn(key.getColumn());
                resList.add(columnLineage);
            }
        }
        return resList;
    }

    private ParseResult anotherParse(String originSql, String currentDb, Map<String, List<Column>> tableColumnsMap) throws Exception {
        ParseResult parseResult = new ParseResult();
        SqlNode sqlNode;
        parseResult.setOriginSql(originSql);
        parseResult.setCurrentDb(currentDb);
        parseResult.setMainTable(new Table());
        originSql = SqlFormatUtil.formatSql(originSql);
        String standardSql = SqlFormatUtil.getStandardSql(originSql);
        SqlFormatUtil.checkSql(standardSql);

        parseResult.setStandardSql(standardSql);
        parseLifecycleAndCatalogue(parseResult);

        SqlParser.ConfigBuilder builder = SqlParser.configBuilder();
        builder.setParserFactory(SqlDdlParserImpl.FACTORY);
        builder.setCaseSensitive(true);
        builder.setQuoting(Quoting.DOUBLE_QUOTE);
        builder.setQuotedCasing(Casing.UNCHANGED);
        builder.setUnquotedCasing(Casing.UNCHANGED);

        try {
            sqlNode = parse(parseResult.getStandardSql(), builder.build());
        } catch (SqlParseException e) {
            LOG.error("sql解析异常========>>>>:{}", e.getMessage());
            return SqlTypeRegexUtil.getParseResultByRegexSql(parseResult);
        }
        LineageParser lineageParser = LineageParser.ParserProxy.getParser(sqlNode, currentDb, tableColumnsMap);
        getMainTableAndColumnLineage(currentDb, tableColumnsMap, parseResult, sqlNode, lineageParser);
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
                String formattedSql = this.uglySqlHandler.parseUglySql(sql);
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
                SqlNode sqlNode = parse(sql, builder.build());
                parseSqlNode(sqlNode, tables);
            } catch (Exception e) {
                if (SqlRegexUtil.isAlterSql(sql)) {
                    LOG.warn("解析表失败===============>>>>{}", e.getMessage());
                }
            }
        }

        return new ArrayList<>(tables);
    }

    private SqlNode parse(String sql, SqlParser.Config config) throws SqlParseException {
        SqlParser parser = SqlParser.create(sql, config);
        try {
            return parser.parseQuery();
        } catch (SqlParseException e) {
            String msg = e.getMessage();
            if (KeywordsHelper.exceptionWithKeywords(msg)) {
                KeywordsHelper keywordsHelper = new KeywordsHelper(sql);
                if (keywordsHelper.parseErrorInfo(msg)) {
                    return parse(keywordsHelper.getSql(), config);
                }
            }
            LOG.error("sql解析异常:{}", e.getMessage());
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
        table.setOperate(TableOperateEnum.CREATE);

        tables.add(table);
    }

    private void parseSqlNode(SqlNode sqlNode, Set<Table> tables) {
        if (sqlNode instanceof SqlCreateTable) {
            parseSqlCreate((SqlCreateTable) sqlNode, tables);
        } else if (sqlNode instanceof SqlCreateView) {
            this.parseSqlCreateView((SqlCreateView) sqlNode, tables);
        } else if (sqlNode instanceof SqlBasicCall) {
            parseSqlBasicCall((SqlBasicCall) sqlNode, tables);
        } else if (sqlNode instanceof SqlWith) {
            parseSqlWith((SqlWith) sqlNode, tables);
        } else if (sqlNode instanceof SqlInsert) {
            parseSqlInsert((SqlInsert) sqlNode, tables);
        } else if (sqlNode instanceof SqlJoin) {
            parseSqlJoin((SqlJoin) sqlNode, tables);
        } else if (sqlNode instanceof SqlOrderBy) {
            parseSqlSelect((SqlSelect) ((SqlOrderBy) sqlNode).query, tables);
        } else if (sqlNode instanceof SqlDelete) {
            Identifier identifier = new Identifier(this.currentDb,new HashMap<>());
            identifier.setContext(Node.Context.IDENTIFIER_TABLE);
            identifier.parseSql(((SqlDelete) sqlNode).getTargetTable());
            Table table = new Table(identifier.getDb(),identifier.getTable());
            tables.add(table);
        } else {
            parseSqlSelect((SqlSelect) sqlNode, tables);
        }
    }

    private void parseSqlCreateView(SqlCreateView sqlNode, Set<Table> tables) {
        Table table = new Table();
        List<SqlNode> operandList = sqlNode.getOperandList();
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
        table.setView(true);
        table.setOperate(TableOperateEnum.CREATE);
        tables.add(table);

    }

    private void parseSqlJoin(SqlJoin sqlNode, Set<Table> tables) {
        sqlNode.getJoinType();
        SqlNode left = sqlNode.getLeft();
        SqlNode right = sqlNode.getRight();
        if (left != null) {
            if (left instanceof SqlBasicCall) {
                parseSqlBasicCall((SqlBasicCall) left, tables);
            } else {
                parseSqlNode(left, tables);
            }
        }
        if (right != null) {
            if (right instanceof SqlBasicCall) {
                parseSqlBasicCall((SqlBasicCall) right, tables);
            } else {
                parseSqlNode(right, tables);
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
        } else if (source != null && source instanceof SqlOrderBy) {
            parseSqlSelect(((SqlSelect) ((SqlOrderBy) source).getOperandList().get(0)), tables);
        } else if (source != null && source instanceof SqlBasicCall) {
            parseSqlBasicCall((SqlBasicCall) source, tables);
        }
        table.setOperate(TableOperateEnum.INSERT);
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
        table.setOperate(TableOperateEnum.CREATE);

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
            if (CollectionUtils.isNotEmpty(operands)) {
                for (SqlNode node : operands) {
                    if (node instanceof SqlCreateTable || node instanceof SqlBasicCall || node instanceof SqlWith
                            || node instanceof SqlInsert || node instanceof SqlJoin || node instanceof SqlSelect || node instanceof SqlOrderBy) {
                        parseSqlNode(node, tables);
                    } else {
                        LOG.info("warning!!未解析的类型：{}", node.getClass().getName());
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
        dealColumn(sqlSelect.getSelectList(), tables);
        //deal from
        dealFrom(sqlSelect.getFrom(), tables);
        //解析表依赖关系时，也需要解析where条件中的表
        if (null != sqlSelect.getWhere()) {
            dealWhere(sqlSelect.getWhere(), tables);
        }
    }

    /**
     * 查询字段中的 是否有子查询
     *
     * @param column
     * @param tables
     */
    private void dealColumn(SqlNode column, Set<Table> tables) {
        //去掉 as

        if (column instanceof SqlNodeList) {
            SqlNodeList columnList = (SqlNodeList) column;
            for (SqlNode sqlNode : columnList) {
                if (sqlNode instanceof SqlBasicCall) {
                    parseColumnSonSelect((SqlBasicCall) sqlNode, tables);
                }
            }
        }
    }

    private void parseColumnSonSelect(SqlBasicCall sqlNode, Set<Table> tables) {
        for (SqlNode node : sqlNode.getOperandList()) {
            if (node instanceof SqlBasicCall) {
                parseColumnSonSelect((SqlBasicCall) node, tables);
            } else if (node instanceof SqlCreateTable || node instanceof SqlBasicCall || node instanceof SqlWith
                    || node instanceof SqlInsert || node instanceof SqlJoin || node instanceof SqlSelect || node instanceof SqlOrderBy) {
                parseSqlNode(node, tables);
            }
        }
    }

    private void dealWhere(SqlNode whereNode, Set<Table> tables) {
        if (whereNode instanceof SqlBasicCall) {
            parseSqlBasicCall((SqlBasicCall) whereNode, tables);
        } else if (whereNode instanceof SqlSelect) {
            parseSqlNode(whereNode, tables);
        } else {
            LOG.info("未处理的where case:{}", whereNode.getKind().sql);

        }
    }

    private void dealFrom(SqlNode tableNode, Set<Table> tables) {
        if (Objects.isNull(tableNode)) {
            return;
        }
        switch (tableNode.getKind()) {
            case IDENTIFIER:
                Table table = getTable(tableNode.toString());
                table.setOperate(TableOperateEnum.SELECT);
                tables.add(table);
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
                if (tableNode instanceof SqlSelect) {
                    dealFrom(((SqlSelect) tableNode).getFrom(), tables);
                } else {
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

    /*
        获取main主表
     */
    private Table getMainTable(Node node) {
        Table mainTable = new Table();
        Identifier table = null;
        if (node instanceof InsertNode) {
            table = ((InsertNode) node).getTargetTable();
        } else if (node instanceof CreateNode) {
            table = ((CreateNode) node).getName();
        } else if (node instanceof DeleteNode){
            table = ((DeleteNode) node).getTableName();
        }
        if (table != null) {
            mainTable.setName(table.getTable());
            mainTable.setDb(table.getDb());
        }
        return mainTable;
    }

    /**
     * 判断是否不需要解析
     * @param standardSql
     * @param parseResult
     * @return
     */
    private Boolean isNotParseSql(String standardSql,ParseResult parseResult){
        //CREATE LIKE 需要自己处理
        if ((SqlRegexUtil.isCreateLike(standardSql))) {
            try {
                parseResult.setTables(this.parseTables(currentDb, parseResult.getOriginSql()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            parseResult.setSqlType(SqlType.CREATE_LIKE);
            if (CollectionUtils.isNotEmpty(parseResult.getTables())) {
                parseResult.setMainTable(parseResult.getTables().get(0));
            }
            return true;
        }
        //show sql 不解析 179
        if (SqlRegexUtil.isShowSql(standardSql)) {
            parseResult.setStandardSql(standardSql);
            parseResult.setSqlType(SqlType.SHOW);
            return true;
        }
        if(SqlRegexUtil.isDataBaseOperate(standardSql)){
            parseResult.setStandardSql(standardSql);
            parseResult.setSqlType(SqlType.DATABASE_OPERATE);
            return true;
        }
        if (SqlRegexUtil.isAlterSql(standardSql)) {
            try {
                this.parseAlterSql(parseResult, standardSql, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        //需要解析刷新的表
        if (SqlRegexUtil.isInvalidateTableSql(standardSql)) {
             this.parseInvalidateTable(parseResult);
            return true;
        }
        if (SqlRegexUtil.isRefreshSql(standardSql)) {
            parseResult.setStandardSql(standardSql);
            parseResult.setSqlType(SqlType.OTHER);
            return true;
        }
        if (SqlRegexUtil.isDropSql(standardSql)) {
            parseResult.setSqlType(SqlType.DROP);
            this.parseDropTable(parseResult);
            return true;
        }

        return false;
    }


    /**
     * 从drop语句中解析建表信息
     */
    private void parseDropTable(ParseResult result) {
        Map<String, String> dbTable = SqlRegexUtil.parseDbTableFromDropSql(result.getStandardSql());
        Table table = result.getMainTable();
        if (Objects.isNull(table)) {
            table = new Table();
        }
        table.setDb(dbTable.get(SqlRegexUtil.KEY_DB) == null ? result.getCurrentDb() : dbTable.get(SqlRegexUtil.KEY_DB));
        table.setName(dbTable.get(SqlRegexUtil.KEY_TABLE));
        table.setOperate(TableOperateEnum.DROP);
        if (Objects.isNull(result.getTables())) {
            result.setTables(new ArrayList<>());
        }
        result.getTables().add(table);
        result.setMainTable(table);
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
}
