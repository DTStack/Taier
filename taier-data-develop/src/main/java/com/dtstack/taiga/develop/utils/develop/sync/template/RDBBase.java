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

package com.dtstack.taiga.develop.utils.develop.sync.template;

import com.dtstack.dtcenter.loader.source.DataBaseType;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/12
 */
public abstract class RDBBase extends BaseSource{

    /**
     * rdb中 需要schema key的
     */
    private static List<DataBaseType> needSchema = Lists.newArrayList(DataBaseType.Oracle, DataBaseType.SQLServer, DataBaseType.Greenplum6, DataBaseType.PostgreSQL, DataBaseType.LIBRA, DataBaseType.ADB_FOR_PG);

    /**
     * 密码
     */
    private String password;
    /**
     * 用户名
     */
    private String username;
    /**
     * jdbcurl
     */
    private String jdbcUrl;
    /**
     * sourceId 做替换数据源信息用
     */
    private Long sourceId;

    /**
     * 表名
     */
    private List<String> table;
    /**
     * 列名
     */
    private List column;
    /**
     * schema
     */
    private String schema;

    /**
     * 数据库类型,mysql 、oracle、 hive
     */
    private DataBaseType type;

    public DataBaseType getType() {
        return type;
    }

    public void setType(DataBaseType type) {
        this.type = type;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public List<String> getTable() {
        return table;
    }

    public void setTable(List<String> table) {
        this.table = table;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 是否需要schema
     * @return
     */
    protected Object isNeedSchema(){
        return StringUtils.isNotBlank(this.getSchema())? this.getSchema(): needSchema.contains(getType())? "" : null;
    }
}
