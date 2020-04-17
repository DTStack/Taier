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


package com.dtstack.schedule.common.jdbc;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/6/25
 */
public class JdbcUrlPropertiesValue {

    public static final boolean USE_CONNECTION_POOL = false;
    public static final int MAX_POOL_SIZE = 20;
    public static final int MIN_POOL_SIZE = 5;
    public static final int MAX_IDLE_TIME = 60;
    public static final int INITIAL_POOL_SIZE = 5;
    public static final int JDBC_IDEL = 1;
    public static final int MAX_ROWS = 5000;
    public static final int QUERY_TIMEOUT = 60000;
    public static final int CHECK_TIMEOUT = 60000;
    public static final Map<String, String> DRIVER_CLASS_NAME_MAP = new HashMap<>();
    static {
        DRIVER_CLASS_NAME_MAP.put("hive", "org.apache.hive.jdbc.HiveDriver");
        DRIVER_CLASS_NAME_MAP.put("libra", "org.postgresql.Driver");
    }

    public static void setNullPropertiesToDefaultValue(JdbcInfo jdbcInfo){
        if(StringUtils.isEmpty(jdbcInfo.getDriverClassName())){
            jdbcInfo.setDriverClassName(DRIVER_CLASS_NAME_MAP.get(jdbcInfo.getDriverClassName()));
        }

        if(jdbcInfo.getMaxRows() == null){
            jdbcInfo.setMaxRows(MAX_ROWS);
        }

        if(jdbcInfo.getQueryTimeout() == null){
            jdbcInfo.setQueryTimeout(QUERY_TIMEOUT);
        }

        if(jdbcInfo.getCheckTimeout() == null){
            jdbcInfo.setCheckTimeout(CHECK_TIMEOUT);
        }

        if(jdbcInfo.getUseConnectionPool() == null){
            jdbcInfo.setUseConnectionPool(USE_CONNECTION_POOL);
        } else if(!jdbcInfo.getUseConnectionPool()){
            return;
        }

        if(jdbcInfo.getJdbcIdel() == null){
            jdbcInfo.setJdbcIdel(JDBC_IDEL);
        }

        if(jdbcInfo.getMaxPoolSize() == null){
            jdbcInfo.setMaxPoolSize(MAX_POOL_SIZE);
        }

        if(jdbcInfo.getMinPoolSize() == null){
            jdbcInfo.setMinPoolSize(MIN_POOL_SIZE);
        }

        if(jdbcInfo.getInitialPoolSize() == null){
            jdbcInfo.setInitialPoolSize(INITIAL_POOL_SIZE);
        }
    }
}
