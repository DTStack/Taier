package com.dtstack.engine.sql.handler;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.utils.SqlFormatUtil;
import com.dtstack.engine.sql.utils.SqlRegexUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/8/16 23:15
 * @Description:
 */
public class LibraUglySqlHandler implements IUglySqlHandler {

    private String sql;

    private String formattedSql;

    /**
     * create temp table
     */
    public static final int CREATE_TEMP = 0b00000001;

    private static final Pattern CREATE_TEMP_PATTEN = Pattern.compile(SqlRegexUtil.CREATE_TEMP_TABLE);

    /**
     * comment on table
     */
    private static final int COMMENT_ON_TABLE = 0b00000010;
    private static final Pattern COMMENT_ON_TABLE_PATTERN = Pattern.compile(SqlRegexUtil.COMMENT_ON_TABLE);

    /**
     * comment on column
     */
    private static final int COMMENT_ON_COLUMN = 0b00000100;
    private static final Pattern COMMENT_ON_COLUMN_PATTERN = Pattern.compile(SqlRegexUtil.COMMENT_ON_COLUMN);

    /**
     * create table DISTRIBUTE BY
     */
    private static final int DISTRIBUTE_BY = 0b00001000;
    private static Pattern CREATE_DISTRIBUTE_BY_PATTERN = Pattern.compile(SqlRegexUtil.CREATE_DISTRIBUTE_BY);

    /**
     * create table PARTITION BY
     */
    private static final int PARTITION_BY = 0b00010000;
    private static Pattern PARTITION_BY_PATTERN = Pattern.compile(SqlRegexUtil.PARTITION_BY);

    /**
     * truncate table
     */
    private static final int TRUNCATE_TABLE = 0b00100000;
    private static Pattern TRUNCATE_TABLE_PATTERN = Pattern.compile(SqlRegexUtil.TRUNCATE_TABLE);

    /**
     * tablespace
     */
    private static final int TABLE_SPACE = 0b01000000;
    private static Pattern TABLE_SPACE_PATTERN = Pattern.compile(SqlRegexUtil.TABLE_SPACE);

    /**
     * ::强转
     */
    private static final int FORCE_CAST = 0b10000000;
    private static Pattern FORCE_CAST_PATTERN = Pattern.compile(SqlRegexUtil.FORCE_CAST);

    /**
     * create table with
     */
    private static final int CREATE_TABLE_WITH = 0b100000000;
    private static Pattern CREATE_TABLE_WITH_PATTERN = Pattern.compile(SqlRegexUtil.CREATE_TABLE_WITH);

    /**
     * DICTIONARY关键字
     */
    private static final int DICTIONARY = 0b1000000000;
    private static Pattern DICTIONARY_PATTERN = Pattern.compile(SqlRegexUtil.DICTIONARY);

    private static final int EXPLAIN = 0b10000000000;
    private static Pattern EXPLAIN_PATTERN = Pattern.compile(SqlRegexUtil.EXPLAIN);

    private static final int CREATE_COLLATE = 0b100000000000;
    private static Pattern CREATE_COLLATE_PATTERN = Pattern.compile(SqlRegexUtil.COLLATE);

    private static final int UGLY_INTERVAL = 0b1000000000000;
    private static Pattern UGLY_INTERVAL_PATTERN = Pattern.compile(SqlRegexUtil.UGLY_INTERVAL);

    private static final int CREATE_TABLE_COMMENT = 0b10000000000000;
    private static Pattern CREATE_TABLE_COMMENT_PATTERN = Pattern.compile(SqlRegexUtil.CREATE_TABLE_COMMENT);
    private static Pattern TABLE_COMMENT_PATTERN = Pattern.compile("(?i)(?<comment>(comment\\s+('[\\u4e00-\\u9fa5_a-zA-Z0-9,\\s]*'|\"[\\u4e00-\\u9fa5_a-zA-Z0-9,\\s]*\")))");

    private int uglySqlMode = 0b00000000;
    private int sqlMode = 0b00000000;

    public int getSqlMode() {
        return sqlMode;
    }

    public LibraUglySqlHandler() {
    }

    public LibraUglySqlHandler(String originSql) {
        if (StringUtils.isEmpty(originSql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        this.sql = originSql;
        initSql();
        initSqlMode();
    }

    public LibraUglySqlHandler(String originSql, int sqlMode){
        initSql();
        this.uglySqlMode = sqlMode;
    }

    private void initSql(){
        formattedSql = SqlFormatUtil.formatSql(sql);
        formattedSql = formattedSql.replaceAll("`","");
        formattedSql = formattedSql.trim();
        if (formattedSql.endsWith(";")) {
            formattedSql = formattedSql.substring(0, formattedSql.length() - 1);
        }
    }

    private void initSqlMode() {
        this.sqlMode = 0b00000000;
        this.uglySqlMode = 0b00000000;
        if (SqlRegexUtil.isCreateTemp(formattedSql)) {
            uglySqlMode = uglySqlMode + CREATE_TEMP;
        }
        if (formattedSql.matches(SqlRegexUtil.COMMENT_ON_TABLE)) {
            uglySqlMode = uglySqlMode + COMMENT_ON_TABLE;
        }
        if (formattedSql.matches(SqlRegexUtil.COMMENT_ON_COLUMN)) {
            uglySqlMode = uglySqlMode + COMMENT_ON_COLUMN;
        }
        if (formattedSql.matches(SqlRegexUtil.CREATE_DISTRIBUTE_BY)){
            uglySqlMode = uglySqlMode + DISTRIBUTE_BY;
        }
        if (formattedSql.matches(SqlRegexUtil.PARTITION_BY)){
            uglySqlMode = uglySqlMode + PARTITION_BY;
        }
        if (formattedSql.matches(SqlRegexUtil.TRUNCATE_TABLE)){
            uglySqlMode = uglySqlMode + TRUNCATE_TABLE;
        }
        if (formattedSql.matches(SqlRegexUtil.TABLE_SPACE)){
            uglySqlMode = uglySqlMode + TABLE_SPACE;
        }
//        if (formattedSql.matches(SqlRegexUtil.FORCE_CAST)){
//            uglySqlMode = uglySqlMode + FORCE_CAST;
//        }
        Matcher forceCastMatter = FORCE_CAST_PATTERN.matcher(formattedSql);
        if (forceCastMatter.find()){
            uglySqlMode = uglySqlMode + FORCE_CAST;
        }
        if (formattedSql.matches(SqlRegexUtil.CREATE_TABLE_WITH)){
            uglySqlMode = uglySqlMode + CREATE_TABLE_WITH;
        }
        if (formattedSql.matches(SqlRegexUtil.DICTIONARY)){
            uglySqlMode = uglySqlMode + DICTIONARY;
        }
        if (formattedSql.matches(SqlRegexUtil.EXPLAIN)){
            uglySqlMode = uglySqlMode + EXPLAIN;
        }
        Matcher createCollateMatter = CREATE_COLLATE_PATTERN.matcher(formattedSql);
        if (createCollateMatter.find()){
            uglySqlMode = uglySqlMode + CREATE_COLLATE;
        }
        Matcher uglyIntervalMatter = UGLY_INTERVAL_PATTERN.matcher(formattedSql);
        if (uglyIntervalMatter.find()){
            uglySqlMode = uglySqlMode + UGLY_INTERVAL;
        }
        Matcher createTableCommentMatter = CREATE_TABLE_COMMENT_PATTERN.matcher(formattedSql);
        if (createTableCommentMatter.find()){
            uglySqlMode = uglySqlMode + CREATE_TABLE_COMMENT;
        }
        sqlMode = uglySqlMode;
    }

    public String parseUglySql() {
        int i = 1;
        int currentMode = uglySqlMode;
        while (currentMode > 0 && currentMode >= i) {
            boolean b = parseUgly(i);
            if (b) {
                currentMode -= i;
            }
            i <<= 1;
        }
        if (formattedSql.endsWith(",")){
            formattedSql = formattedSql.substring(0,formattedSql.length()-1);
        }
        return formattedSql;
    }

    public ParseResult parserUglySql(){
        ParseResult parseResult = new ParseResult();
        String parsedSql = parseUglySql();
        parseResult.setStandardSql(parsedSql);
        //针对comment_on特殊处理
        if ((sqlMode & COMMENT_ON_TABLE) == COMMENT_ON_TABLE){
            parseResult.setSqlType(SqlType.COMMENT_ON);
            Matcher matcher = COMMENT_ON_TABLE_PATTERN.matcher(sql);
            if (matcher.find()){
                String db = matcher.group("db");
                if(StringUtils.isNotEmpty(db)){
                    parseResult.setCurrentDb(db);
                }
                String table = matcher.group("table");
                if (StringUtils.isNotEmpty(table)){
                    Table table1 = new Table();
                    table1.setName(table);
                    parseResult.setMainTable(table1);
                }
            }
        }else if ( (sqlMode & COMMENT_ON_COLUMN) == COMMENT_ON_COLUMN){
            parseResult.setSqlType(SqlType.COMMENT_ON);
            Matcher matcher = COMMENT_ON_COLUMN_PATTERN.matcher(sql);
            if (matcher.find()){
                String columnStr = matcher.group("column");
                String[] split = columnStr.split("\\.");
                if (split.length == 3){
                    parseResult.setCurrentDb(split[0]);
                    Table table = new Table();
                    table.setName(split[1]);
                    parseResult.setMainTable(table);
                }else if (split.length == 2){
                    Table table = new Table();
                    table.setName(split[0]);
                    parseResult.setMainTable(table);
                }
            }
        }

        return parseResult;
    }

    private boolean parseUgly(int sqlMode) {
        int flag = uglySqlMode & sqlMode;
        if (flag == 0) {
            return false;
        }
        Matcher matcher = null;
        String group = "";
        switch (sqlMode) {
            case CREATE_TEMP:
                matcher = CREATE_TEMP_PATTEN.matcher(formattedSql);
                if (matcher.find()) {
                    group = matcher.group(1);
                    formattedSql = formattedSql.replaceFirst(group, "create table ");
                    //临时表也需要解析血缘，因为，临时表可能作为中间表，最终血缘落在普通表上。
//                    formattedSql = "";
                }
                break;
            case COMMENT_ON_TABLE:
                matcher = COMMENT_ON_TABLE_PATTERN.matcher(formattedSql);
                if (matcher.find()){
                    //如果处理后变为空字符串，即为不需要解析血缘
                    formattedSql = "";
                }
                break;
            case COMMENT_ON_COLUMN:
                matcher = COMMENT_ON_COLUMN_PATTERN.matcher(formattedSql);
                if (matcher.find()){
                    formattedSql = "";
                }
                break;
            case DISTRIBUTE_BY:
                matcher = CREATE_DISTRIBUTE_BY_PATTERN.matcher(formattedSql);
                if(matcher.find()){
                    group = matcher.group(2);
                    formattedSql = formattedSql.replaceFirst(SqlRegexUtil.str2RegexStr(group), "");
                }
                break;
            case PARTITION_BY:
                matcher = PARTITION_BY_PATTERN.matcher(formattedSql);
                if (matcher.find()){
                    group = matcher.group(2);
                    //难以得知字段数据类型，暂时去掉 PARTITIONED BY
                    formattedSql = formattedSql.replaceFirst(SqlRegexUtil.str2RegexStr(group),"");
                }
                break;
            case TABLE_SPACE:
                matcher = TABLE_SPACE_PATTERN.matcher(formattedSql);
                if (matcher.find()){
                    group = matcher.group(2);
                    formattedSql = formattedSql.replaceFirst(group,"");
                }
                break;
            case TRUNCATE_TABLE:
                matcher = TRUNCATE_TABLE_PATTERN.matcher(formattedSql);
                if (matcher.find()){
                    formattedSql = "";
                }
                break;
            case FORCE_CAST:
                matcher = FORCE_CAST_PATTERN.matcher(formattedSql);
                while (matcher.find()){
                    group = matcher.group(1);
                    formattedSql = formattedSql.replaceAll(group,"");
                }
                break;
            case CREATE_TABLE_WITH:
                matcher = CREATE_TABLE_WITH_PATTERN.matcher(formattedSql);
                if (matcher.find()){
                    group = matcher.group(2);
                    formattedSql = formattedSql.replaceFirst(SqlRegexUtil.str2RegexStr(group),"");
                }
                break;
            case DICTIONARY:
                matcher = DICTIONARY_PATTERN.matcher(formattedSql);
                if (matcher.find()){
                    group = matcher.group(2);
                    formattedSql = formattedSql.replaceFirst(group," ");
                }
                break;
            case EXPLAIN:
                matcher = EXPLAIN_PATTERN.matcher(formattedSql);
                if (matcher.find()){
                    formattedSql = "";
                }
                break;
            case CREATE_COLLATE:
                matcher = CREATE_COLLATE_PATTERN.matcher(formattedSql);
                while (matcher.find()){
                    group = matcher.group(1);
                    formattedSql = formattedSql.replaceAll(group,"");
                }
                break;
            case UGLY_INTERVAL:
                matcher = UGLY_INTERVAL_PATTERN.matcher(formattedSql);
                while (matcher.find()){
                    group = matcher.group(1);
                    formattedSql = formattedSql.replaceAll(group,"iNTERVAL '1' day ");
                }
                break;
            case CREATE_TABLE_COMMENT:
                matcher = TABLE_COMMENT_PATTERN.matcher(formattedSql);
                while (matcher.find()){
                    group = matcher.group("comment");
                    formattedSql = formattedSql.replaceAll(group," ");
                }
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean isTemp() {
        return (this.getSqlMode() & IUglySqlHandler.CREATE_TEMP) == IUglySqlHandler.CREATE_TEMP;
    }

    @Override
    public String parseUglySql(String sql) {
        this.sql = sql;
        initSql();
        initSqlMode();
        int i = 1;
        int currentMode = uglySqlMode;
        while (currentMode > 0 && i <= currentMode) {
            boolean b = parseUgly(i);
            if (b) {
                currentMode -= i;
            }
            i <<= 1;
        }
        if (formattedSql.endsWith(",")) {
            formattedSql = formattedSql.substring(0, formattedSql.length() - 1);
        }
        return formattedSql;
    }
}
