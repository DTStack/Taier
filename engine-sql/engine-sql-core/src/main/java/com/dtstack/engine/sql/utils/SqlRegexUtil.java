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


package com.dtstack.engine.sql.utils;

import com.dtstack.engine.sql.TableOperateEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiangbo
 * @date 2019/5/21
 */
public class SqlRegexUtil {

    public static final String KEY_DB = "db";

    public static final String KEY_TABLE = "table";

    public static final String KEY_LIKE_DB = "likeDb";

    public static final String KEY_LIKE_TABLE = "likeTable";

    public static final String CREATE_TABLE_LIKE_REGEX = "(?i)create\\s+table\\s+(if\\s+not\\s+exists\\s+){0,1}(?<table>[a-zA-Z0-9_.]+\\s+)like\\s+(?<likeTable>[a-zA-Z0-9_.]+)";

    public static final String CREATE_TABLE_REGEX = "(?i)create\\s+table\\s+(if\\s+not\\s+exists\\s+)?(?<table>[a-zA-Z0-9_.]+)\\s*.*";

    /**
     *
     */
    public static final String CREATE_TEMP_TABLE = "(?i)(create\\s+temp\\s+table\\s+).*";

    public static Pattern CREATE_TEMP_TABLE_PATTERN = Pattern.compile(CREATE_TEMP_TABLE);

    /**
     * comment on table
     */
    public static final String COMMENT_ON_TABLE = "(?i)comment\\s+on\\s+table\\s+((?<db>[a-zA-Z0-9_]+)\\.)*(?<table>[a-zA-Z0-9_]+)\\s+is\\s+.*";

    /**
     * comment on column
     */
    public static final String COMMENT_ON_COLUMN = "(?i)comment\\s+on\\s+column\\s+(?<column>[a-zA-Z0-9_\\.]+)\\s+is\\s+.*";

    /**
     * create distribute by
     */
    public static final String CREATE_DISTRIBUTE_BY = "(?i)create\\s+(temp\\s+)?table\\s+.*(distribute\\s+by\\s+hash\\s*\\((?<cols>([a-zA-Z0-9_,\\s]*))\\)\\s*).*";

    public static final String PARTITION_BY = "(?i)create\\s+(temp\\s+)?table\\s+.*(partition\\s+by\\s+values\\((?<cols>([a-zA-Z0-9_,\\s]*))\\)\\s*).*";

    public static final String TABLE_SPACE = "(?i)create\\s+(temp\\s+)?table\\s+.*(tablespace\\s+[a-zA-Z0-9_]+\\s*).*";

    public static final String CREATE_TABLE_COMMENT = "(?i)create\\s+table\\s+.*(?<comment>(comment\\s+('[\\u4e00-\\u9fa5_a-zA-Z0-9]+'|\"[\\u4e00-\\u9fa5_a-zA-Z0-9]+\"))).*";

    public static final String FORCE_CAST = "(?i)(?<forced>(\\:\\:\\s*[a-zA-Z0-9]+))";

    public static final String CREATE_TABLE_WITH = "(?i)create\\s+(temp\\s+)?table\\s+.*(with\\s*\\((?<val>([a-zA-Z0-9_,=\\s]*))\\)\\s*).*";

    public static final String DICTIONARY = "(?i)create\\s+(temp\\s+)?table\\s+.*(\\s+DICTIONARY)\\s*.*";

    public static final String EXPLAIN = "(?i)explain\\s+.*";

    public static final String COLLATE = "(?i)(?<collates>(COLLATE\\s+([a-zA-Z0-9_\\.\"]+)\\s*))";

    public static final String UGLY_INTERVAL = "(?i)[\\s\\(,](interval\\s+\'[-]{0,1}[0-9\\sa-zA-Z]+\')";

    public static final String NONSUPPORT_UNEQUAL = "(?i)(!=)";

    public static final String LIMIT = "(?i)limit\\s+[0-9]+\\s*(\\,\\s*[0-9]+){0,1}";

    public static final String TRUNCATE_TABLE = "(?i)TRUNCATE\\s+TABLE\\s+.*";

    public static final String PARTITIONED_BY = "(?i)(partitioned|partition)\\s*(by)?\\s*\\([^\\(\\)]+\\)";

    public static final String STORED_REGEX = "(?i)stored\\s+as\\s+[a-z]+";

    public static final String PARTITION_REGEX = "(?i)PARTITION\\s+([\\w\\s<=,\\(\\)'\"]+)stored";

    public static final String TBLPROPERTIES_REGEX = "(?i)TBLPROPERTIES\\s*\\([^\\(\\)]+\\)";

    public static final String INSERT_OVERWRITE_REGEX = "(?i)(INSERT\\s+OVERWRITE\\s+)";

    public static final String PARTITION_PT_REGEX = "(?i)(PARTITION\\s*\\(([^\\)]+)\\))";

    public static final String SHOW_REGEX = "(?i)(^show\\s+.*)";

    public static final String DROP_DATABASE = "(?i)(^drop\\s+DATABASE\\s+.*)";

    public static final String CREATE_DATABASE = "(?i)(^create\\s+DATABASE\\s+.*)";

    public static final String REFRESH_REGEX = "(?i)(^refresh\\s+.*)";

    public static final String DROP_REGEX = "(?i)(drop\\s+(table|view|database)(\\s+if\\s+not\\s+exists\\s+|\\s+if\\s+exists\\s+|\\s+)(?<table>\\w+.*)*)";

    private static Pattern likeSqlPattern = Pattern.compile(CREATE_TABLE_LIKE_REGEX);

    private static Pattern createSqlPattern = Pattern.compile(CREATE_TABLE_REGEX);

    private static Pattern dropSqlPattern = Pattern.compile(DROP_REGEX);

    public static final String KEY_DB_TABLE = "dbTable";

    public static final String ALTER_TABLE_REGEX = "(?i)alter\\s+table\\s+(only\\s+)*(?<table>[a-zA-Z0-9\\._]+)\\s+.*";

    public static final String INSERT_TABLE_REGEX = "(?i)insert\\s+table\\s+(?<table>[a-zA-Z0-9\\._]+)\\s+.*";

    public static final String ALTER_RENAME_REGEX = "(?i)alter\\s+table\\s+(?<oldTable>\\w+)\\s+rename\\s+to\\s+(?<newTable>\\w+)";

    public static final String ALTER_ADD_COLUMN_REGEX = "(?i)alter\\s+table\\s+\\w.+\\s+add\\s+columns.*";

    public static final String ALTER_DROP_COLUMN_REGEX = "(?i)alter\\s+table\\s+\\w.+\\s+drop\\s+column.*";

    public static final String ALTER_ADD_PARTITION_REGEX = "(?i)alter\\s+table\\s+(\\w.+)\\s+add\\s+partition\\s*\\(([^\\)]*)";

    public static final String INSERT_INTO_TABLE_REGEX = "(?i)(insert\\s+(into|overwrite)\\s+(?<table>table)\\s+.*)";

    public static final String ROW_FORMAT_REGEX = "(?i)(ROW\\s+([^]]+))(WITH|STORED|SERDEPROPERTIES|LOCATION|LIFECYCLE|TBLPROPERTIES)";

    public static final String LOCATION_REGEX = "(?i)location\\s+'(([^']+))'";

    public static final String PRIMARY_REGEX = "(?i)(,*\\s*primary\\s+key\\s*(\\(([^)]+)\\))*)";
    public static final String WITH_SERDEPROPERTIES_REGEX = "(?i)(with\\s+serdeproperties\\s*\\((([^)]+))\\))";

    public static final String EXTERNAL_REGEX = "(?i)(external)";

    public static final Pattern pattern_add_partition = Pattern.compile(SqlRegexUtil.ALTER_ADD_PARTITION_REGEX);
    public static final Pattern pattern_add_column = Pattern.compile(SqlRegexUtil.ALTER_ADD_COLUMN_REGEX);
    public static final Pattern pattern_drop_column = Pattern.compile(SqlRegexUtil.ALTER_DROP_COLUMN_REGEX);
    public static final Pattern pattern_rename = Pattern.compile(SqlRegexUtil.ALTER_RENAME_REGEX);
    public static final Pattern row_pattern = Pattern.compile(SqlRegexUtil.ROW_FORMAT_REGEX);
    /**
     * 不允许执行操作角色sql
     */
    public static final Pattern UNSUPPORTED_SQL = Pattern.compile("(?i)^(drop\\s+role|create\\s+role|revoke|grant)\\s+.*");
    /**
     * 刷新全db的表
     **/
    public static final Pattern INVALIDATE_METADATA_PATTERN = Pattern.compile("^(?i)\\s*invalidate\\s+metadata\\s*(;)?\\s*$");
    /**
     * 删除db下table表信息
     */
    public static final Pattern INVALIDATE_TABLE_PATTERN = Pattern.compile("^(?i)invalidate\\s+metadata\\s+(?<table>\\S+)");

    private static Pattern alterSqlPattern = Pattern.compile(ALTER_TABLE_REGEX);

    private static Pattern insertSqlPattern = Pattern.compile(INSERT_TABLE_REGEX);

    private static Pattern notCheckPattern = Pattern.compile("(?i)^(set|use|explain)\\s*.*");

    private static Pattern descSqlPattern = Pattern.compile("(?i)^(describe|desc)\\s*.*");

    public static boolean isCreateTemp(String originSql) {
        String sql = originSql.trim().replace("\r", "")
                .replace("\n", "")
                .replace("\t", "");
        return sql.matches(CREATE_TEMP_TABLE);
    }

    public static String removeTempKey(String originSql) {
        String sql = originSql.trim().replace("\r", "")
                .replace("\n", "")
                .replace("\t", "");

        Matcher matcher = CREATE_TEMP_TABLE_PATTERN.matcher(sql);
        if (matcher.find()) {
            String group = matcher.group(1);
            sql = sql.replace(group, "create table ");
        }

        return sql;
    }

    public static boolean isCreateLike(String originSql) {
        String sql = originSql.replace("\r", "")
                .replace("\n", "")
                .replace("\t", "");
        if (sql.contains("(")) {
            return false;
        }
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        return sql.matches(CREATE_TABLE_LIKE_REGEX);
    }

    public static boolean isAlterSql(String sql) {
        return sql.matches(ALTER_TABLE_REGEX);
    }

    public static boolean isShowSql(String sql) {
        return sql.matches(SHOW_REGEX);
    }

    public static boolean isDataBaseOperate(String sql) {
        return sql.matches(DROP_DATABASE)||sql.matches(CREATE_DATABASE);
    }

    public static boolean isDropSql(String sql) {
        return sql.matches(DROP_REGEX);
    }

    public static boolean isRefreshSql(String sql){
        return sql.matches(REFRESH_REGEX);
    }

    public static boolean isInvalidateTableSql(String sql) {
        Matcher matcher = INVALIDATE_TABLE_PATTERN.matcher(sql);
        return matcher.find();
    }

    public static Map<String, String> parseDbTableFromDropSql(String sql) {
        Map<String, String> dbTable = new HashMap<>(4);
        Matcher matcher = dropSqlPattern.matcher(sql);
        if (matcher.find()) {
            String group = matcher.group(KEY_TABLE);
            if (StringUtils.isNotBlank(group)) {
                if (group.contains(".")) {
                    String[] split = group.split("\\.");
                    if (split.length > 1) {
                        dbTable.put(KEY_DB, split[0].trim());
                        dbTable.put(KEY_TABLE, split[1].trim());
                    } else {
                        dbTable.put(KEY_TABLE, group.trim());
                    }
                } else {
                    dbTable.put(KEY_TABLE, group.trim());
                }
            }
        }
        return dbTable;
    }

    /**
     * 从create like语句中解析数据库和表
     *
     * @param sql
     * @return
     */
    public static Map<String, String> parseDbTableFromLikeSql(String sql) {
        Map<String, String> dbTable = new HashMap<>(4);
        Matcher matcher = likeSqlPattern.matcher(sql);
        if (matcher.find()) {
            String table = matcher.group(KEY_TABLE);
            if (StringUtils.isNotBlank(table)) {
                String[] split = table.split("\\.");
                if (split.length > 1) {
                    dbTable.put(KEY_DB, split[0].trim());
                    dbTable.put(KEY_TABLE, split[1].trim());
                } else {
                    dbTable.put(KEY_TABLE, table.trim());
                }

            }
            String likeTable = matcher.group(KEY_LIKE_TABLE);
            if (StringUtils.isNotBlank(likeTable)) {
                String[] split = likeTable.split("\\.");
                if (split.length > 1) {
                    dbTable.put(KEY_LIKE_DB, split[0].trim());
                    dbTable.put(KEY_LIKE_TABLE, split[1].trim());
                } else {
                    dbTable.put(KEY_LIKE_TABLE, likeTable.trim());
                }
            }
        }

        return dbTable;
    }

    /**
     * 从create 语句中解析数据库和表
     *
     * @param sql
     * @return
     */
    public static Map<String, String> parseDbTableFromCreateSql(String sql) {
        Map<String, String> dbTable = new HashMap<>(2);
        Matcher matcher = createSqlPattern.matcher(sql);
        if (matcher.find()) {
            String table = matcher.group(KEY_TABLE);
            if (StringUtils.isNotBlank(table)) {
                String[] split = table.split("\\.");
                if (split.length > 1) {
                    dbTable.put(KEY_DB, split[0].trim());
                    dbTable.put(KEY_TABLE, split[1].trim());
                } else {
                    dbTable.put(KEY_TABLE, table.trim());
                }

            }
        }

        return dbTable;
    }

    public static Map<String, String> parseDbTableFromAlterSql(String sql) {
        Map<String, String> dbTableMap = new HashMap<>(2);
        Matcher matcher = alterSqlPattern.matcher(sql);
        if (matcher.find()) {
            String dbTable = matcher.group(KEY_TABLE);
            if (dbTable.contains(".")) {
                String[] splits = dbTable.split("\\.");
                dbTableMap.put(KEY_DB, splits[0]);
                dbTableMap.put(KEY_TABLE, splits[1]);
            } else {
                dbTableMap.put(KEY_TABLE, dbTable);
            }
        }

        return dbTableMap;
    }

    public static Map<String, String> parseDbTableFromInsertSql(String sql) {
        Map<String, String> dbTableMap = new HashMap<>(2);
        Matcher matcher = insertSqlPattern.matcher(sql);
        if (matcher.find()) {
            String dbTable = matcher.group(KEY_TABLE);
            if (dbTable.contains(".")) {
                String[] splits = dbTable.split("\\.");
                dbTableMap.put(KEY_DB, splits[0]);
                dbTableMap.put(KEY_TABLE, splits[1]);
            } else {
                dbTableMap.put(KEY_TABLE, dbTable);
            }
        }

        return dbTableMap;
    }


    public static Map<String, String> parseDbTableFromInvalidateSql(String sql) {
        Map<String, String> dbTable = new HashMap<>(2);
        Matcher matcher = INVALIDATE_TABLE_PATTERN.matcher(sql);
        if (matcher.find()) {
            String table = matcher.group(KEY_TABLE);
            String[] split = table.split("\\.");
            if (split.length > 1) {
                dbTable.put(KEY_DB, split[0].trim());
                dbTable.put(KEY_TABLE, split[1].trim());
            } else {
                dbTable.put(KEY_TABLE, table.trim());
            }
        }
        return dbTable;
    }

    public static String str2RegexStr(String str) {
        if (StringUtils.isEmpty(str)) {
            return StringUtils.EMPTY;
        }
        return str.
                replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)")
                .replaceAll("\\.", "\\\\.");
    }

    public static TableOperateEnum getAlterEnum(String sql) {
        Matcher addPartition = pattern_add_partition.matcher(sql);
        if (addPartition.find()) {
            return TableOperateEnum.ALTERTABLE_ADDPARTS;
        }
        Matcher addColumns = pattern_add_column.matcher(sql);
        if (addColumns.find()) {
            return TableOperateEnum.ALTERTABLE_ADDCOLS;
        }

        Matcher dropColumns = pattern_drop_column.matcher(sql);
        if (dropColumns.find()) {
            return TableOperateEnum.ALTERTABLE_DROPPARTS;
        }

        Matcher renameColumns = pattern_rename.matcher(sql);
        if (renameColumns.find()) {
            return TableOperateEnum.ALTERTABLE_RENAME;
        }
        return TableOperateEnum.ALTER;
    }

    public static boolean isAllowSql(String sql) {
        try {
            if (StringUtils.isBlank(sql)) {
                return true;
            }
            Matcher metaMatcher = INVALIDATE_METADATA_PATTERN.matcher(sql);
            if (metaMatcher.find()) {
                return false;
            }
            Matcher matcher = UNSUPPORTED_SQL.matcher(sql);
            if (matcher.find()) {
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }


    public static boolean notCheckSql(String sql){
        if(StringUtils.isBlank(sql)){
            return true;
        }
        Matcher matcher = notCheckPattern.matcher(sql);
        if(matcher.find()){
            return true;
        }
        return false;
    }

    /**
     * 是否是desc
     * @param sql
     * @return
     */
    public static boolean isDescSql(String sql){
        if(StringUtils.isBlank(sql)){
            return true;
        }
        Matcher matcher = descSqlPattern.matcher(sql);
        if(matcher.find()){
            return true;
        }
        return false;
    }

}
