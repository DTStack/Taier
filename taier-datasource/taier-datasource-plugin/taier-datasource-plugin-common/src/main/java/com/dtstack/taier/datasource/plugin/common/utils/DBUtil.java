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

package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.plugin.common.base.CallBack;
import com.dtstack.taier.datasource.api.dto.SqlMultiDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.datasource.plugin.common.service.ConnectionDealer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:13 2020/1/13
 * @Description：数据库工具类
 */
@Slf4j
public class DBUtil {

    /**
     * 默认最大查询条数
     */
    private static final Integer MAX_QUERY_ROW = 5000;

    /**
     * 字段重复时的重命名规则
     */
    private static final String REPEAT_SIGN = "%s(%s)";

    /**
     * 是否为查询语句
     */
    private static final Pattern pattern = Pattern.compile("^select");

    /**
     * 存储函数调用sql正则匹配
     * select xxx()
     */
    private static final Pattern PG_FUNCTION_PATTERN = Pattern.compile("^select +\\w+\\([\\S\\s]*\\)$");

    /**
     * 存储函数调用sql正则匹配
     * select * from xxx()
     */
    private static final Pattern PG_LONGER_FUNCTION_PATTERN = Pattern.compile("^select +\\* +from +\\w+\\([\\S\\s]*\\)$");

    /**
     * 自定义 output 方法
     */
    public static final Pattern OUTPUT_DIR_PATTERN = Pattern.compile("^output\\sdirectory\\s(.*)$");

    public static final Pattern INSERT_DIR_PATTERN = Pattern.compile("^(insert\\soverwrite\\sdirectory\\s)'(.*)'\\srow\\s(.*)\\sfrom\\s(.*)$");

    /**
     * 超时自动关闭
     */
    public static final Map<Integer, Boolean> DONT_NEED_CLOSE_MAP = new HashMap<>();

    /**
     * 根据 SQL 查询
     *
     * @param conn jdbc connection
     * @param sql  需要执行的 sql
     * @return 执行的结果集
     */
    public static List<Map<String, Object>> executeSql(Connection conn, String sql) {
        return executeSql(conn, sql, null);
    }

    /**
     * 根据 SQL 查询
     *
     * @param conn jdbc connection
     * @param sql  需要执行的 sql
     * @return 执行的结果集
     */
    public static List<Map<String, Object>> executeSql(Connection conn, String sql, Integer queryTimeout) {
        return executeSql(conn, sql, -1, queryTimeout, false, null);
    }

    /**
     * 直接执行 sql
     *
     * @param conn         jdbc connection
     * @param sql          需要执行的 sql
     * @param limit        限制条数
     * @param queryTimeout 查询超时时间
     * @param setMaxRow    是否设置最大条数
     * @param fieldProcess 结果处理
     * @return 执行结果
     */
    public static List<Map<String, Object>> executeSql(Connection conn, String sql, Integer limit, Integer queryTimeout, Boolean setMaxRow, CallBack<Object, Object> fieldProcess) {
        AssertUtils.notBlank(sql, "execute sql can't be null.");
        List<Map<String, Object>> result = Lists.newArrayList();
        ResultSet res = null;
        Statement statement = null;
        try {
            statement = conn.createStatement();
            if (queryTimeout != null) {
                try {
                    statement.setQueryTimeout(queryTimeout);
                } catch (Exception e) {
                    log.debug(String.format("statement set QueryTimeout exception,%s", e.getMessage()), e);
                }
            }

            int maxRow = Objects.isNull(limit) ? MAX_QUERY_ROW : limit;
            boolean isSetMaxRow = Objects.isNull(setMaxRow) || BooleanUtils.isTrue(setMaxRow);
            if (isSetMaxRow) {
                // 设置返回最大条数
                statement.setMaxRows(maxRow);
            }

            if (statement.execute(sql)) {
                res = statement.getResultSet();
                int columns = res.getMetaData().getColumnCount();
                List<String> columnName = Lists.newArrayList();
                for (int i = 0; i < columns; i++) {
                    columnName.add(res.getMetaData().getColumnLabel(i + 1));
                }

                int num = 0;
                while (res.next()) {
                    if (isSetMaxRow && num++ >= maxRow) {
                        break;
                    }
                    Map<String, Object> row = Maps.newLinkedHashMap();
                    Map<String, Integer> columnRepeatSign = Maps.newHashMap();
                    for (int i = 0; i < columns; i++) {
                        String column = dealRepeatColumn(row, columnName.get(i), columnRepeatSign);
                        Object value = res.getObject(i + 1);
                        // 增加字段处理
                        if (Objects.nonNull(fieldProcess)) {
                            value = fieldProcess.execute(value);
                        }
                        row.put(column, value);
                    }
                    result.add(row);
                }
            }

        } catch (Exception e) {
            throw new SourceException(String.format("SQL execute exception：%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(res, statement, null);
        }
        return result;
    }

    /**
     * 根据 SQL 查询, 调用 PreparedStatement 执行预编译 sql
     *
     * @param conn         jdbc 链接
     * @param sql          需要执行的 sql, 可以带 ? 占位符
     * @param limit        限制条数
     * @param preFields    预编译字段值
     * @param queryTimeout 查询超时时间
     * @param setMaxRow    是否设置最大返回条数
     * @param fieldProcess 结果集处理回调
     * @return 执行的结果集
     */
    public static List<Map<String, Object>> executePreSql(Connection conn, String sql, Integer limit, List<Object> preFields, Integer queryTimeout, Boolean setMaxRow, CallBack<Object, Object> fieldProcess) {
        AssertUtils.notBlank(sql, "execute sql can't be null.");
        List<Map<String, Object>> result = Lists.newArrayList();
        ResultSet res = null;
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sql);
            //设置查询超时时间
            if (queryTimeout != null) {
                try {
                    statement.setQueryTimeout(queryTimeout);
                } catch (Exception e) {
                    log.debug(String.format("statement set QueryTimeout exception,%s", e.getMessage()), e);
                }
            }
            int maxRow = Objects.isNull(limit) ? MAX_QUERY_ROW : limit;
            boolean isSetMaxRow = Objects.isNull(setMaxRow) || BooleanUtils.isTrue(setMaxRow);
            if (isSetMaxRow) {
                // 设置返回最大条数
                statement.setMaxRows(maxRow);
            }

            // 支持预编译sql
            if (preFields != null && !preFields.isEmpty()) {
                for (int i = 0; i < preFields.size(); i++) {
                    statement.setObject(i + 1, preFields.get(i));
                }
            }
            res = statement.executeQuery();
            int columns = res.getMetaData().getColumnCount();
            List<String> columnName = Lists.newArrayList();
            for (int i = 0; i < columns; i++) {
                columnName.add(res.getMetaData().getColumnLabel(i + 1));
            }

            int num = 0;
            while (res.next()) {
                if (isSetMaxRow && num++ >= maxRow) {
                    break;
                }
                Map<String, Object> row = Maps.newLinkedHashMap();
                Map<String, Integer> columnRepeatSign = Maps.newHashMap();
                for (int i = 0; i < columns; i++) {
                    String column = dealRepeatColumn(row, columnName.get(i), columnRepeatSign);
                    Object value = res.getObject(i + 1);
                    // 增加字段处理
                    if (Objects.nonNull(fieldProcess)) {
                        value = fieldProcess.execute(value);
                    }
                    row.put(column, value);
                }
                result.add(row);
            }
        } catch (Exception e) {
            throw new SourceException(String.format("SQL executed exception, %s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(res, statement, null);
        }
        return result;
    }

    /**
     * 根据 sql, 预编译执行 update、insert 等语句
     *
     * @param conn            jdbc connection
     * @param sql             需要执行的 sql
     * @param preUpsertFields 预编译字段值
     * @param autoCommit      是否自动提交事务
     * @param rollback        是否回滚
     * @return 影响的条数
     */
    public static Integer executeUpdate(Connection conn, String sql, List<List<Object>> preUpsertFields, Boolean autoCommit, Boolean rollback) {
        AssertUtils.notBlank(sql, "execute sql can't be null.");
        int result = 0;
        int[] res;
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sql);
            conn.setAutoCommit(autoCommit);
            if (preUpsertFields != null && !preUpsertFields.isEmpty()) {
                for (List<Object> objList : preUpsertFields) {
                    for (int i = 0; i < objList.size(); i++) {
                        statement.setObject(i + 1, objList.get(i));
                    }
                    statement.addBatch();
                }
            }
            res = statement.executeBatch();
            if (rollback) {
                conn.rollback();
            }
            if (!autoCommit && !rollback) {
                conn.commit();
            }
        } catch (Exception e) {
            if (!autoCommit) {
                try {
                    conn.rollback();
                } catch (SQLException exception) {
                    throw new SourceException(String.format("execute sql error, rollback sql: %s, cause by：%s", sql, e.getMessage()), e);
                }
            }
            throw new SourceException(String.format("SQL executed exception, %s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(null, statement, null);
        }

        for (int i : res) {
            result += i;
        }
        return result;
    }

    /**
     * 执行多条 sql, 返回 sql 执行结果集
     *
     * @param conn         连接信息
     * @param sqlMultiDTOS 多条 sql
     * @param limit        限制条数
     * @param queryTimeout 超时时间
     * @return 结果集
     */
    public static Map<String, List<Map<String, Object>>> executeMultiQuery(Connection conn, List<SqlMultiDTO> sqlMultiDTOS, Integer limit, Integer queryTimeout, Boolean setMaxRow, CallBack<Object, Object> fieldProcess) {
        Map<String, List<Map<String, Object>>> result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(sqlMultiDTOS)) {
            log.warn("sqlMultiDTOS is empty.");
            return result;
        }
        ResultSet res = null;
        Statement statement = null;
        try {
            statement = conn.createStatement();
            if (queryTimeout != null) {
                try {
                    statement.setQueryTimeout(queryTimeout);
                } catch (Exception e) {
                    log.debug(String.format("statement set QueryTimeout exception,%s", e.getMessage()), e);
                }
            }
            int maxRow = Objects.isNull(limit) ? MAX_QUERY_ROW : limit;
            boolean isSetMaxRow = Objects.isNull(setMaxRow) || BooleanUtils.isTrue(setMaxRow);
            if (isSetMaxRow) {
                // 设置返回最大条数
                statement.setMaxRows(maxRow);
            }
            for (SqlMultiDTO sqlMultiDTO : sqlMultiDTOS) {
                if (StringUtils.isEmpty(sqlMultiDTO.getSql()) || StringUtils.isBlank(sqlMultiDTO.getUniqueKey())) {
                    log.warn("sql or unique is null.");
                    continue;
                }
                List<Map<String, Object>> singleResult = Lists.newArrayList();
                if (statement.execute(sqlMultiDTO.getSql())) {
                    res = statement.getResultSet();
                    int columns = res.getMetaData().getColumnCount();
                    List<String> columnName = Lists.newArrayList();
                    for (int i = 0; i < columns; i++) {
                        columnName.add(res.getMetaData().getColumnLabel(i + 1));
                    }

                    int num = 0;
                    while (res.next()) {
                        if (isSetMaxRow && num++ >= maxRow) {
                            break;
                        }
                        Map<String, Object> row = Maps.newLinkedHashMap();
                        Map<String, Integer> columnRepeatSign = Maps.newHashMap();
                        for (int i = 0; i < columns; i++) {
                            String column = dealRepeatColumn(row, columnName.get(i), columnRepeatSign);
                            Object value = res.getObject(i + 1);
                            // 增加字段处理
                            if (Objects.nonNull(fieldProcess)) {
                                value = fieldProcess.execute(value);
                            }
                            row.put(column, value);
                        }
                        singleResult.add(row);
                    }
                }
                result.put(sqlMultiDTO.getUniqueKey(), singleResult);
            }


        } catch (Exception e) {
            throw new SourceException(String.format("SQL execute exception：%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(res, statement, null);
        }
        return result;
    }

    /**
     * 根据 sql, 执行 update、insert 等语句
     *
     * @param conn       jdbc connection
     * @param sql        需要执行的 sql
     * @param autoCommit 是否自动提交事务
     * @param rollback   是否回滚
     * @return 影响的条数
     */
    public static Integer executeUpdate(Connection conn, String sql, Boolean autoCommit, Boolean rollback) {
        Statement statement = null;
        try {
            conn.setAutoCommit(autoCommit);
            statement = conn.createStatement();
            Integer result = statement.executeUpdate(sql);
            if (rollback) {
                conn.rollback();
            }
            if (!autoCommit && !rollback) {
                conn.commit();
            }
            return result;
        } catch (Exception e) {
            if (!autoCommit) {
                try {
                    conn.rollback();
                } catch (SQLException exception) {
                    throw new SourceException(String.format("execute sql error, rollback sql: %s, cause by：%s", sql, e.getMessage()), e);
                }
            }
            throw new SourceException(String.format("execute sql: %s, cause by：%s", sql, e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(null, statement, null);
        }
    }

    /**
     * 处理 executeQuery 查询结果字段重复字段
     *
     * @param row              当前行的数据
     * @param column           当前查询字段名
     * @param columnRepeatSign 当前字段重复次数
     * @return 处理后的重复字段名
     */
    public static String dealRepeatColumn(Map<String, Object> row, String column, Map<String, Integer> columnRepeatSign) {
        boolean repeat = row.containsKey(column);
        if (repeat) {
            // 如果 column 重复则在 column 后进行增加 (1),(2)... 区分处理
            boolean contains = columnRepeatSign.containsKey(column);
            if (!contains) {
                columnRepeatSign.put(column, 1);
            } else {
                columnRepeatSign.put(column, columnRepeatSign.get(column) + 1);
            }
            return String.format(REPEAT_SIGN, column, columnRepeatSign.get(column));
        } else {
            return column;
        }
    }

    /**
     * 执行查询，无需结果集
     *
     * @param conn connection
     * @param sql  执行 sql
     */
    public static void executeSqlWithoutResultSet(Connection conn, String sql) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.execute(sql);
        } catch (Exception e) {
            throw new SourceException(String.format("execute sql: %s, cause by：%s", sql, e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(null, statement, null);
        }
    }

    /**
     * 重置表类型
     * {@link java.sql.DatabaseMetaData#getTableTypes()}
     *
     * @param queryDTO 查询条件
     * @return 查询表类型
     */
    public static String[] getTableTypes(SqlQueryDTO queryDTO) {
        if (ArrayUtils.isNotEmpty(queryDTO.getTableTypes())) {
            return queryDTO.getTableTypes();
        }

        String[] types = new String[BooleanUtils.isTrue(queryDTO.getView()) ? 2 : 1];
        types[0] = "TABLE";
        if (BooleanUtils.isTrue(queryDTO.getView())) {
            types[1] = "VIEW";
        }
        return types;
    }

    /**
     * 关闭数据库资源信息
     *
     * @param rs   jdbc 执行结果集
     * @param stmt execute statement
     * @param conn jdbc connection
     */
    public static void closeDBResources(ResultSet rs, Statement stmt, Connection conn) {
        closeDBResources(rs, stmt, conn, false);
    }

    /**
     * 关闭数据库资源信息
     *
     * @param rs         jdbc 执行结果集
     * @param stmt       execute statement
     * @param conn       jdbc connection
     * @param forceClose 强制关闭
     */
    public static void closeDBResources(ResultSet rs, Statement stmt, Connection conn, boolean forceClose) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (null != stmt) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (null != conn) {
            try {
                if (!conn.isClosed() && (forceClose || ConnectionDealer.needClose(conn))) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * JDBC 每次读取数据的行数
     */
    public static void setFetchSize(Statement statement, SqlQueryDTO sqlQueryDTO) {

        if (!ReflectUtil.fieldExists(SqlQueryDTO.class, "fetchSize")) {
            return;
        }
        Integer fetchSize = sqlQueryDTO.getFetchSize();
        setFetchSize(statement, fetchSize);
    }

    public static void setFetchSize(Statement statement, Integer fetchSize) {
        try {
            if (fetchSize != null && fetchSize > 0) {
                statement.setFetchSize(fetchSize);
            }
        } catch (Exception e) {
            log.error("set fetchSize error,{}", e.getMessage());
        }
    }

    public static boolean isSelectSql(String sql) {
        Matcher matcher = pattern.matcher(sql.toLowerCase().trim());
        return matcher.find();
    }

    /**
     * 判断sql是否为postgresql 存储过程执行语句
     *
     * @param sql sql语句
     * @return 校验结果 true:是 false: 否
     */
    public static boolean isSelectFunctionSql(String sql) {
        Matcher matcher = PG_FUNCTION_PATTERN.matcher(sql.toLowerCase().trim());
        Matcher matcher1 = PG_LONGER_FUNCTION_PATTERN.matcher(sql.toLowerCase().trim());
        return matcher.find() || matcher1.find();
    }
}
