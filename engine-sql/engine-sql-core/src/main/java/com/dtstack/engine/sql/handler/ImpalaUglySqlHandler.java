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
public class ImpalaUglySqlHandler implements IUglySqlHandler {

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


    private static final int CREATE_COLLATE = 0b100000000000;
    private static Pattern CREATE_COLLATE_PATTERN = Pattern.compile(SqlRegexUtil.COLLATE);

    private static final int UGLY_INTERVAL = 0b1000000000000;
    private static Pattern UGLY_INTERVAL_PATTERN = Pattern.compile(SqlRegexUtil.UGLY_INTERVAL);

    private static final int NONSUPPORT_UNEQUAL = 0b10000000000000;
    private static Pattern NONSUPPORT_UNEQUAL_PATTERN = Pattern.compile(SqlRegexUtil.NONSUPPORT_UNEQUAL);


    private static final int INSERT_OVERWRITE = 0b10000000000000000;
    private static Pattern INSERT_OVERWRITE_PATTERN = Pattern.compile(SqlRegexUtil.INSERT_OVERWRITE_REGEX);

    private static final int PARTITION_PT = 0b100000000000000000;
    private static Pattern PARTITION_PT_PATTERN = Pattern.compile(SqlRegexUtil.PARTITION_PT_REGEX);


    private static final int INSERT_INTO_TABLE = 0b1000000000000000000;
    private static Pattern INSERT_INTO_TABLE_PATTERN = Pattern.compile(SqlRegexUtil.INSERT_INTO_TABLE_REGEX);

    private static final int PRIMARY = 0b10000000000000000000;
    private static Pattern PRIMARY_REGEX_PATTERN = Pattern.compile(SqlRegexUtil.PRIMARY_REGEX);


    private static final int LEFT_RIGHT = 0b100000000000000000000;
    private static Pattern LEFT_RIGHT_PATTERN = Pattern.compile("(?i)(left|right)\\s*\\(([^\\),]+),[0-9]+\\)");

    /**
     * insert into table(column) partition() select 语句解析异常，去掉partition
     */
    private static final int INSERT_INTO_PARTITION = 0b1000000000000000000000;
    private static Pattern INSERT_INTO_PARTITION_PATTERN = Pattern.compile("(?i)insert\\s+into\\s+(table\\s+)?(?<table>[a-zA-Z0-9_.])+\\s*\\((?<cols>([a-zA-Z0-9_,\\s]*?))\\)\\s*(?<parition>partition\\s*\\(.*\\))\\s*select.*");

    private int uglySqlMode = 0b00000000;
    private int sqlMode = 0b00000000;




    /*
    ----------impala 自己特殊关键字------------
     */

    private static String ENCODING_REGEX = "(?i)ENCODING\\s+AUTO_ENCODING\\s+COMPRESSION\\s+DEFAULT_COMPRESSION";

    public int getSqlMode() {
        return sqlMode;
    }

    public ImpalaUglySqlHandler() {
    }

    public ImpalaUglySqlHandler(String originSql) {
        if (StringUtils.isEmpty(originSql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        this.sql = originSql;
        initSql();
        initSqlMode();
    }

    public ImpalaUglySqlHandler(String originSql, int sqlMode) {
        initSql();
        this.uglySqlMode = sqlMode;
    }

    private void initSql() {
        formattedSql = SqlFormatUtil.getStandardSql(SqlFormatUtil.formatSql(sql));
        formattedSql = formattedSql.trim();
        formattedSql = formattedSql.replaceAll("%","/");
        formattedSql = formattedSql.replaceAll("\\s+", " ");
        formattedSql = formattedSql.replaceAll("(?i)(sort\\s+by\\s*\\(.*\\)\\s*)", " ");
        formattedSql = formattedSql.replaceAll("(?i)(\\s+varchar\\s*,)"," STRING ,");
        formattedSql = formattedSql.replaceAll("(?i)(left\\()","nvl(");
//        formattedSql = formattedSql.replaceAll("(?i)(\\s+exchange\\s*,)"," exchange_Ranm ");
        formattedSql = SqlFormatUtil.removeComment(formattedSql);
        if(SqlFormatUtil.isCreateSql(formattedSql)){
            formattedSql = SqlFormatUtil.removeDoubleQuotesComment(formattedSql);
            //去除 external
            formattedSql = formattedSql.replaceAll(SqlRegexUtil.EXTERNAL_REGEX," ");
            //去除KUDU 建表分区语句
            formattedSql = formattedSql.replaceAll(SqlRegexUtil.PARTITION_REGEX," STORED ");
            try {
                Matcher matcher = SqlRegexUtil.row_pattern.matcher(formattedSql);
                if(matcher.find()){
                    formattedSql = formattedSql.replace(matcher.group(1)," ");
                }
            } catch (Exception e){

            }
            //array struct map 特殊结构 映射为 string 处理
            formattedSql = SqlFormatUtil.formatType(formattedSql);
            formattedSql = formattedSql.replaceAll(SqlRegexUtil.WITH_SERDEPROPERTIES_REGEX," ");
            //去除存储
            formattedSql = formattedSql.replaceAll(SqlRegexUtil.STORED_REGEX,"");
            formattedSql = formattedSql.replaceAll(SqlRegexUtil.TBLPROPERTIES_REGEX,"");
            formattedSql = formattedSql.replaceAll(SqlRegexUtil.LOCATION_REGEX,"");
            formattedSql = formattedSql.replaceAll(ENCODING_REGEX,"");
        }
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
        if (formattedSql.matches(SqlRegexUtil.CREATE_DISTRIBUTE_BY)) {
            uglySqlMode = uglySqlMode + DISTRIBUTE_BY;
        }
        if (formattedSql.matches(SqlRegexUtil.PARTITION_BY)) {
            uglySqlMode = uglySqlMode + PARTITION_BY;
        }

        if (formattedSql.matches(SqlRegexUtil.TABLE_SPACE)) {
            uglySqlMode = uglySqlMode + TABLE_SPACE;
        }
        Matcher forceCastMatter = FORCE_CAST_PATTERN.matcher(formattedSql);
        if (forceCastMatter.find()) {
            uglySqlMode = uglySqlMode + FORCE_CAST;
        }
        if (formattedSql.matches(SqlRegexUtil.CREATE_TABLE_WITH)) {
            uglySqlMode = uglySqlMode + CREATE_TABLE_WITH;
        }
        if (formattedSql.matches(SqlRegexUtil.DICTIONARY)) {
            uglySqlMode = uglySqlMode + DICTIONARY;
        }
        Matcher createCollateMatter = CREATE_COLLATE_PATTERN.matcher(formattedSql);
        if (createCollateMatter.find()) {
            uglySqlMode = uglySqlMode + CREATE_COLLATE;
        }
        Matcher uglyIntervalMatcher = UGLY_INTERVAL_PATTERN.matcher(formattedSql);
        if (uglyIntervalMatcher.find()) {
            uglySqlMode = uglySqlMode + UGLY_INTERVAL;
        }
        Matcher nonsupportUnequalMatter = NONSUPPORT_UNEQUAL_PATTERN.matcher(formattedSql);
        if (nonsupportUnequalMatter.find()) {
            uglySqlMode = uglySqlMode + NONSUPPORT_UNEQUAL;
        }
        Matcher insertOverWriteMatcher = INSERT_OVERWRITE_PATTERN.matcher(formattedSql);
        if (insertOverWriteMatcher.find()) {
            uglySqlMode = uglySqlMode + INSERT_OVERWRITE;
        }
        Matcher insertIntoTablePattern = INSERT_INTO_TABLE_PATTERN.matcher(formattedSql);
        if (insertIntoTablePattern.find()) {
            uglySqlMode = uglySqlMode + INSERT_INTO_TABLE;
        }
        Matcher primaryPattern = PRIMARY_REGEX_PATTERN.matcher(formattedSql);
        if (primaryPattern.find()) {
            uglySqlMode = uglySqlMode + PRIMARY;
        }
        Matcher leftMatcher = LEFT_RIGHT_PATTERN.matcher(formattedSql);
        if (leftMatcher.find()) {
            uglySqlMode = uglySqlMode + LEFT_RIGHT;
        }
        Matcher insertPartitionMatter = INSERT_INTO_PARTITION_PATTERN.matcher(formattedSql);
        if (insertPartitionMatter.find()) {
            uglySqlMode = uglySqlMode + INSERT_INTO_PARTITION;
        }
        sqlMode = uglySqlMode;
    }

    public ParseResult parserUglySql() {
        ParseResult parseResult = new ParseResult();
        String parsedSql = this.parseUglySql(this.sql);
        parseResult.setStandardSql(parsedSql);
        //针对comment_on特殊处理
        if ((sqlMode & COMMENT_ON_TABLE) == COMMENT_ON_TABLE) {
            parseResult.setSqlType(SqlType.COMMENT_ON);
            Matcher matcher = COMMENT_ON_TABLE_PATTERN.matcher(sql);
            if (matcher.find()) {
                String db = matcher.group("db");
                if (StringUtils.isNotEmpty(db)) {
                    parseResult.setCurrentDb(db);
                }
                String table = matcher.group("table");
                if (StringUtils.isNotEmpty(table)) {
                    Table table1 = new Table();
                    table1.setName(table);
                    parseResult.setMainTable(table1);
                }
            }
        } else if ((sqlMode & COMMENT_ON_COLUMN) == COMMENT_ON_COLUMN) {
            parseResult.setSqlType(SqlType.COMMENT_ON);
            Matcher matcher = COMMENT_ON_COLUMN_PATTERN.matcher(sql);
            if (matcher.find()) {
                String columnStr = matcher.group("column");
                String[] split = columnStr.split("\\.");
                if (split.length == 3) {
                    parseResult.setCurrentDb(split[0]);
                    Table table = new Table();
                    table.setName(split[1]);
                    parseResult.setMainTable(table);
                } else if (split.length == 2) {
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
                if (matcher.find()) {
                    //如果处理后变为空字符串，即为不需要解析血缘
                    formattedSql = "";
                }
                break;
            case COMMENT_ON_COLUMN:
                matcher = COMMENT_ON_COLUMN_PATTERN.matcher(formattedSql);
                if (matcher.find()) {
                    formattedSql = "";
                }
                break;
            case DISTRIBUTE_BY:
                matcher = CREATE_DISTRIBUTE_BY_PATTERN.matcher(formattedSql);
                if (matcher.find()) {
                    group = matcher.group(2);
                    formattedSql = formattedSql.replaceFirst(SqlRegexUtil.str2RegexStr(group), "");
                }
                break;
            case PARTITION_BY:
                matcher = PARTITION_BY_PATTERN.matcher(formattedSql);
                if (matcher.find()) {
                    group = matcher.group(2);
                    //难以得知字段数据类型，暂时去掉 PARTITIONED BY
                    formattedSql = formattedSql.replaceFirst(SqlRegexUtil.str2RegexStr(group), "");
                }
                break;
            case TABLE_SPACE:
                matcher = TABLE_SPACE_PATTERN.matcher(formattedSql);
                if (matcher.find()) {
                    group = matcher.group(2);
                    formattedSql = formattedSql.replaceFirst(group, "");
                }
                break;
            case FORCE_CAST:
                matcher = FORCE_CAST_PATTERN.matcher(formattedSql);
                while (matcher.find()) {
                    group = matcher.group(1);
                    formattedSql = formattedSql.replaceAll(group, "");
                }
                break;
            case CREATE_TABLE_WITH:
                matcher = CREATE_TABLE_WITH_PATTERN.matcher(formattedSql);
                if (matcher.find()) {
                    group = matcher.group(2);
                    formattedSql = formattedSql.replaceFirst(SqlRegexUtil.str2RegexStr(group), "");
                }
                break;
            case DICTIONARY:
                matcher = DICTIONARY_PATTERN.matcher(formattedSql);
                if (matcher.find()) {
                    group = matcher.group(2);
                    formattedSql = formattedSql.replaceFirst(group, " ");
                }
                break;
//            case EXPLAIN:
//                matcher = EXPLAIN_PATTERN.matcher(formattedSql);
//                if (matcher.find()) {
//                    formattedSql = "";
//                }
//                break;
            case CREATE_COLLATE:
                matcher = CREATE_COLLATE_PATTERN.matcher(formattedSql);
                while (matcher.find()) {
                    group = matcher.group(1);
                    formattedSql = formattedSql.replaceAll(group, "");
                }
                break;
            case UGLY_INTERVAL:
                matcher = UGLY_INTERVAL_PATTERN.matcher(formattedSql);
                while (matcher.find()) {
                    group = matcher.group(1);
                    formattedSql = formattedSql.replaceAll(group, " interval '1' day ");
                }
                break;
            case NONSUPPORT_UNEQUAL:
                matcher = NONSUPPORT_UNEQUAL_PATTERN.matcher(formattedSql);
                while (matcher.find()) {
                    group = matcher.group(1);
                    formattedSql = formattedSql.replaceAll(group, " = ");
                }
                break;
            case INSERT_OVERWRITE:
                matcher = INSERT_OVERWRITE_PATTERN.matcher(formattedSql);
                while (matcher.find()) {
                    group = matcher.group(1);
                    formattedSql = formattedSql.replace(group, "insert into ");
                }
                break;
            case INSERT_INTO_TABLE:
                matcher = INSERT_INTO_TABLE_PATTERN.matcher(formattedSql);
                while (matcher.find()){
                    group = matcher.group(SqlRegexUtil.KEY_TABLE);
                    formattedSql = formattedSql.replace(group, " ");
                }
                break;

            case PRIMARY:
                matcher = PRIMARY_REGEX_PATTERN.matcher(formattedSql);
                while (matcher.find()){
                    group = matcher.group(0);
                    formattedSql = formattedSql.replace(group, " ");
                }
                break;


            case LEFT_RIGHT:
                matcher = LEFT_RIGHT_PATTERN.matcher(formattedSql);
                while (matcher.find()){
                    if(matcher.groupCount()>1){
                        group = matcher.group(2);
                        formattedSql = formattedSql.replace(matcher.group(0), group);
                    }
                }
                break;

            case INSERT_INTO_PARTITION:
                matcher = INSERT_INTO_PARTITION_PATTERN.matcher(formattedSql);
                while (matcher.find()){
                    String parition = matcher.group("parition");
                    formattedSql = formattedSql.replace(parition," ");
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
