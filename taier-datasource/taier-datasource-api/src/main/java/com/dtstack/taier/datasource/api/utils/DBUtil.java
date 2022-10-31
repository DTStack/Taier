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

package com.dtstack.taier.datasource.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * db util
 *
 * @author ：wangchuan
 * date：Created in 15:33 2022/9/23
 * company: www.dtstack.com
 */
@Slf4j
public class DBUtil {

    /**
     * 执行 sql，无需结果集
     *
     * @param conn      数据库连接
     * @param sql       需要执行的 sql
     * @param closeConn 是否关闭连接
     */
    public static void executeSqlWithoutResultSet(Connection conn, String sql, Boolean closeConn) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.execute(sql);
        } catch (Exception e) {
            throw new SourceException("Sql execute exception : " + e.getMessage(), e);
        } finally {
            DBUtil.closeDBResources(null, statement, closeConn ? conn : null);
        }
    }

    /**
     * 关闭数据库资源信息
     *
     * @param rs   结果集
     * @param stmt statement
     * @param conn 数据库连接
     */
    public static void closeDBResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (null != rs) {
                rs.close();
            }
            if (null != stmt) {
                stmt.close();
            }
            if (null != conn) {
                conn.close();
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * hive task param -> properties
     *
     * @param taskParams 任务配置
     * @return properties
     */
    public static String propToJson(String taskParams) {
        Properties properties = new Properties();
        // 空指针判断
        if (StringUtils.isBlank(taskParams)) {
            return null;
        }
        try {
            properties.load(new ByteArrayInputStream(taskParams.replace("hiveconf:", "").getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            log.error("taskParams change error : {}", e.getMessage(), e);
        }
        JSONObject result = new JSONObject();
        properties.forEach((key, value) -> result.put(key.toString(), value));
        return result.toJSONString();
    }
}
