package com.dtstack.taier.datasource.plugin.impala;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:18 2020/12/23
 * @Description：Impala 工具类
 */
@Slf4j
public class ImpalaDriverUtil {
    /**
     * 设置 Schema 信息
     *
     * @param conn
     * @param url
     * @return
     */
    public static Connection setSchema(Connection conn, String schema) {
        if (StringUtils.isBlank(schema)) {
            return conn;
        }

        try (Statement stmt = conn.createStatement()){
            stmt.execute("use " + schema);
        } catch (SQLException e) {
            log.error("Hive set Schema exception :{} ", e.getMessage(), e);
        }
        return conn;
    }
}
