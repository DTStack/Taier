package com.dtstack.taier.datasource.plugin.influxdb;

import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.InfluxDBSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * influxDB 操作工具类
 *
 * @author ：wangchuan
 * date：Created in 上午10:36 2021/6/7
 * company: www.dtstack.com
 */
public class InfluxDBUtil {

    /**
     * 处理数据库，将 sqlQueryDTO 中的 schema 设置到 sourceDTO 中
     *
     * @param sourceDTO   数据源连接信息
     * @param sqlQueryDTO 查询信息
     */
    public static ISourceDTO dealDb(ISourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        if (Objects.isNull(sqlQueryDTO)) {
            return sourceDTO;
        }
        if (StringUtils.isBlank(sqlQueryDTO.getSchema())) {
            return sourceDTO;
        }
        ((InfluxDBSourceDTO) sourceDTO).setDatabase(sqlQueryDTO.getSchema());
        return sourceDTO;
    }

    /**
     * 执行返回单行结果集的命令（类SQL），并且只支持执行单条 sql
     *
     * @param influxDB influxDB 客户端
     * @param command  命令
     * @param isClose  是否关闭 influxDB 客户端
     * @return 结果集
     */
    public static List<String> queryWithOneWay(InfluxDB influxDB, String command, Boolean isClose) {
        return queryWithOneWay(influxDB, command, null, isClose);
    }

    /**
     * 执行返回单行结果集的命令（类SQL），并且只支持执行单条 sql
     *
     * @param influxDB influxDB 客户端
     * @param command  命令
     * @param database 数据库
     * @param isClose  是否关闭 influxDB 客户端
     * @return 结果集
     */
    public static List<String> queryWithOneWay(InfluxDB influxDB, String command, String database, Boolean isClose) {
        List<Map<String, Object>> result = queryWithMap(influxDB, command, database, isClose);
        return result.stream().filter(row -> CollectionUtils.isNotEmpty(row.keySet())).map(row -> {
            Object value = row.get(new ArrayList<>(row.keySet()).get(0));
            return Objects.nonNull(value) ? value.toString() : null;
        }).collect(Collectors.toList());
    }

    /**
     * 执行influxDB命令（类SQL），并且只支持执行单条 sql
     *
     * @param influxDB influxDB 客户端
     * @param command  命令
     * @param isClose  是否关闭 influxDB 客户端
     * @return 结果集
     */
    public static List<Map<String, Object>> queryWithMap(InfluxDB influxDB, String command, Boolean isClose) {
        return queryWithMap(influxDB, command, null, isClose);
    }

    /**
     * 执行influxDB命令（类SQL），并且只支持执行单条 sql
     *
     * @param influxDB influxDB 客户端
     * @param command  命令
     * @param database 数据库
     * @param isClose  是否关闭 influxDB 客户端
     * @return 结果集
     */
    public static List<Map<String, Object>> queryWithMap(InfluxDB influxDB, String command, String database, Boolean isClose) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        if (StringUtils.isBlank(command)) {
            throw new SourceException("command cannot be null.");
        }
        try {
            Query query = StringUtils.isNoneBlank(database) ? new Query(command, database) : new Query(command);
            QueryResult queryResult = influxDB.query(query);
            // 判断执行是否有异常信息，有异常则抛出
            if (queryResult.hasError()) {
                throw new SourceException(String.format("execute influxDB command [%s] error: %s", command, queryResult.getError()));
            }
            List<QueryResult.Result> results = queryResult.getResults();
            if (CollectionUtils.isEmpty(results)) {
                return resultList;
            }
            // results 结果是查询多条 sql 的结果，这里查询只有单条 sql ，取第一个返回值
            List<QueryResult.Series> series = results.get(0).getSeries();
            // 一个 series 代表一条线
            if (CollectionUtils.isEmpty(series)) {
                return resultList;
            }
            for (QueryResult.Series single : series) {
                List<String> columns = single.getColumns();
                // 里面每一个 list 表示一行数据
                List<List<Object>> values = single.getValues();
                if (CollectionUtils.isEmpty(columns) || CollectionUtils.isEmpty(values)) {
                    continue;
                }
                for (List<Object> value : values) {
                    Map<String, Object> row = Maps.newLinkedHashMap();
                    Map<String, Integer> columnRepeatSign = Maps.newHashMap();
                    for (int i = 0; i < columns.size(); i++) {
                        // 处理别名重复的情况
                        String column = DBUtil.dealRepeatColumn(row, columns.get(i), columnRepeatSign);
                        row.put(column, value.get(i));
                    }
                    resultList.add(row);
                }

            }
        } catch (Exception e) {
            if (e instanceof SourceException) {
                throw e;
            }
            throw new SourceException(String.format("execute influxDB command [%s] error: %s", command, e.getMessage()), e);
        } finally {
            if (BooleanUtils.isTrue(isClose)) {
                influxDB.close();
            }
        }
        return resultList;
    }

    /**
     * 执行influxDB命令（类SQL），并且只支持执行单条 sql，返回数据类型为 List<List<Object>>
     *
     * @param influxDB influxDB 客户端
     * @param command  命令
     * @param isClose  是否关闭 influxDB 客户端
     * @return 结果集
     */
    public static List<List<Object>> queryWithList(InfluxDB influxDB, String command, Boolean isClose) {
        return queryWithList(influxDB, command, null, isClose);
    }

    /**
     * 执行influxDB命令（类SQL），并且只支持执行单条 sql，返回数据类型为 List<List<Object>>
     *
     * @param influxDB influxDB 客户端
     * @param command  命令
     * @param database 数据库
     * @param isClose  是否关闭 influxDB 客户端
     * @return 结果集
     */
    public static List<List<Object>> queryWithList(InfluxDB influxDB, String command, String database, Boolean isClose) {
        if (StringUtils.isBlank(command)) {
            throw new SourceException("command cannot be null.");
        }
        List<List<Object>> resultList = Lists.newArrayList();
        try {
            Query query = StringUtils.isNoneBlank(database) ? new Query(command, database) : new Query(command);
            QueryResult queryResult = influxDB.query(query);
            // 判断执行是否有异常信息，有异常则抛出
            if (queryResult.hasError()) {
                throw new SourceException(String.format("execute influxDB command [%s] error: %s", command, queryResult.getError()));
            }
            List<QueryResult.Result> results = queryResult.getResults();
            if (CollectionUtils.isEmpty(results)) {
                return resultList;
            }
            // results 结果是查询多条 sql 的结果，这里查询只有单条 sql ，取第一个返回值
            List<QueryResult.Series> series = results.get(0).getSeries();
            // 一个 series 代表一条线
            if (CollectionUtils.isEmpty(series)) {
                return resultList;
            }
            for (QueryResult.Series single : series) {
                List<String> columns = single.getColumns();
                // 里面每一个 list 表示一行数据
                List<List<Object>> values = single.getValues();
                if (CollectionUtils.isEmpty(columns)) {
                    continue;
                }
                // 添加列信息
                resultList.add(Lists.newArrayList(columns));
                resultList.addAll(values);
            }
        } catch (Exception e) {
            if (e instanceof SourceException) {
                throw e;
            }
            throw new SourceException(String.format("execute influxDB command [%s] error: %s", command, e.getMessage()), e);
        } finally {
            if (BooleanUtils.isTrue(isClose)) {
                influxDB.close();
            }
        }
        return resultList;
    }
}
