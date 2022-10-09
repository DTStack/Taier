package com.dtstack.taier.datasource.plugin.inceptor;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hive.jdbc.HiveDriver;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.util.Properties;

/**
 * inceptor驱动 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
@Slf4j
public class InceptorDriverUtil {

    private static final HiveDriver HIVE_DRIVER = new HiveDriver();

    /**
     * INCEPTOR_JDBC 前缀
     */
    private static final String JDBC_PREFIX = "jdbc:hive2://";

    /**
     * INCEPTOR_JDBC 前缀长度
     */
    private static final Integer JDBC_PREFIX_LENGTH = JDBC_PREFIX.length();

    /**
     * 解析 URL 配置信息
     *
     * @param url        数据源连接url
     * @param properties 数据源配置
     * @return 配置详细信息
     */
    private static DriverPropertyInfo[] parseProperty(String url, Properties properties) {
        try {
            return HIVE_DRIVER.getPropertyInfo(url, properties);
        } catch (Exception e) {
            throw new SourceException(String.format("Spark parse URL exception : %s", e.getMessage()), e);
        }
    }

    /**
     * 获取 Schema 信息
     *
     * @param url 数据源连接url
     * @return 从url中解析出 schema 信息
     */
    private static String getSchema(String url) {
        return parseProperty(url, null)[2].value;
    }

    /**
     * 设置 Schema 信息
     *
     * @param conn 数据源连接
     * @param url  数据源连接url
     * @return 设置schema后的数据源连接
     */
    public static Connection setSchema(Connection conn, String url, String schema) {
        String schemaSet = StringUtils.isBlank(schema) ? getSchema(url) : schema;
        if (StringUtils.isBlank(schemaSet)) {
            return conn;
        }
        try {
            conn.setSchema(schemaSet);
        } catch (Exception e) {
            throw new SourceException(String.format("Setting schema exception : %s", e.getMessage()), e);
        }
        return conn;
    }

    /**
     * 去除 Schema 信息
     *
     * @param url 数据源连接url
     * @return 去除 Schema 信息的数据源连接url
     */
    public static String removeSchema(String url) {
        String schema = getSchema(url);
        return removeSchema(url, schema);
    }

    /**
     * 去除 Schema 信息
     *
     * @param url    数据源连接url
     * @param schema schema信息
     * @return 去除 Schema 信息的数据源连接url
     */
    private static String removeSchema(String url, String schema) {
        if (StringUtils.isBlank(schema) || !url.toLowerCase().contains(JDBC_PREFIX)) {
            return url;
        }
        String urlWithoutPrefix = url.substring(JDBC_PREFIX_LENGTH);
        return JDBC_PREFIX + urlWithoutPrefix.replaceFirst("/" + schema, "/");
    }
}
