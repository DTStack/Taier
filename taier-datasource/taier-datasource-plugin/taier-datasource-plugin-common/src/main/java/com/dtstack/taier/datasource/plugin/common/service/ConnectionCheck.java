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

package com.dtstack.taier.datasource.plugin.common.service;

import java.sql.Connection;

/**
 * connection check
 *
 * @author ：wangchuan
 * date：Created in 14:38 2022/9/27
 * company: www.dtstack.com
 */
public class ConnectionCheck {

    /**
     * conn
     */
    private Connection connection;

    /**
     * 放入的时间戳
     */
    private long timestamp;

    /**
     * 是否外部获取
     */
    private boolean externalObtain;

    /**
     * 过期时间
     */
    private long expireTime;

    /**
     * 数据源类型
     */
    private Integer sourceType;

    public ConnectionCheck() {
    }

    public ConnectionCheck(Connection connection, long timestamp,
                           boolean externalObtain, long expireTime, Integer sourceType) {
        this.connection = connection;
        this.timestamp = timestamp;
        this.externalObtain = externalObtain;
        this.expireTime = expireTime;
        this.sourceType = sourceType;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExternalObtain() {
        return externalObtain;
    }

    public void setExternalObtain(boolean externalObtain) {
        this.externalObtain = externalObtain;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
}
