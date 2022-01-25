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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.exception.DtCenterDefException;
import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.common.template.Writer;
import com.dtstack.taiga.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taiga.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/12
 */
public class RDBWriter extends RDBBase implements Writer {
    public final String UPSERT = "upsert";

    public RDBWriter() {
    }

    private String writeMode = "";
    private String session;
    private String preSql;
    private String postSql;

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getPreSql() {
        return preSql;
    }

    public void setPreSql(String preSql) {
        this.preSql = preSql;
    }

    public String getPostSql() {
        return postSql;
    }

    public void setPostSql(String postSql) {
        this.postSql = postSql;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject connection = new JSONObject(true);
        connection.put("jdbcUrl", this.getJdbcUrl());
        connection.put("table", this.getTable());
        connection.put("schema", isNeedSchema());

        JSONObject parameter = new JSONObject(true);
        parameter.put("username", this.getUsername());
        parameter.put("password", this.getPassword());
        parameter.put("connection", Lists.newArrayList(connection));
        parameter.put("session", StringUtils.isNotBlank(this.getSession()) ? Lists.newArrayList(this.getSession()) : Lists.newArrayList());
        parameter.put("preSql", StringUtils.isNotBlank(this.getPreSql()) ? Lists.newArrayList(this.getPreSql().trim().split(";")) : Lists.newArrayList());
        parameter.put("postSql", StringUtils.isNotBlank(this.getPostSql()) ? Lists.newArrayList(this.getPostSql().trim().split(";")) : Lists.newArrayList());
        parameter.put("writeMode", this.getWriteMode());
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(), PluginName.MySQLD_R));
        parameter.put("sourceIds", getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject writer = new JSONObject(true);
        switch (this.getType()) {
            case MySql:
            case TiDB:
            case TDDL:
                writer.put("name", PluginName.MySQL_W);
                break;
            case Clickhouse:
                writer.put("name", PluginName.Clichhouse_W);
                break;
            case Polardb_For_MySQL:
                writer.put("name", PluginName.Polardb_for_MySQL_W);
                break;
            case Oracle:
                writer.put("name", PluginName.Oracle_W);
                break;
            case SQLServer:
                writer.put("name", PluginName.SQLServer_W);
                break;
            case HIVE:
            case HIVE1X:
                writer.put("name", PluginName.Hive_W);
                break;
            case PostgreSQL:
            case LIBRA:
                writer.put("name", PluginName.PostgreSQL_W);
                break;
            case DB2:
                writer.put("name", PluginName.DB2_W);
                break;
            case GBase8a:
                writer.put("name", PluginName.GBase_W);
                break;
            case Phoenix:
                writer.put("name", PluginName.Phoenix_W);
                // 特殊处理写入模式，200302_3.10_beta2 只支持 upsert
                parameter.put("writeMode", StringUtils.isBlank(this.getWriteMode()) ? UPSERT : this.getWriteMode());
                break;
            case Phoenix5:
                writer.put("name", PluginName.Phoenix5_W);
                parameter.put("writeMode", StringUtils.isBlank(this.getWriteMode()) ? UPSERT : this.getWriteMode());
                break;
            case DMDB:
                writer.put("name", PluginName.DM_W);
                break;
            case Greenplum6:
                writer.put("name", PluginName.GREENPLUM_W);
                break;
            case KINGBASE8:
                writer.put("name", PluginName.KINGBASE_W);
                break;
            case INCEPTOR:
                writer.put("name", PluginName.INCEPTOR_W);
                break;
            case ADB_FOR_PG:
                writer.put("name", PluginName.ADB_FOR_PG_W);
                break;
            default:
                throw new RdosDefineException(ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);

        }
        writer.put("parameter", parameter);

        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        String name = data.getString("name");
        data = data.getJSONObject("parameter");

        if (name.equals(PluginName.Clichhouse_W)) {
            // 1、clickhouse不支持修改删除，写入模式只能为insert into
            String writeMode = data.getString("writeMode");
            if (!"insert".equalsIgnoreCase(writeMode)) {
                throw new DtCenterDefException("clickhouse 写入模式只能为insert into");
            }
        }

        if (data.get("column") == null){
            throw new RdosDefineException("column 不能为空");
        }

        if (data.get("column") == null) {
            throw new RdosDefineException("需要匹配映射");
        }

        if (!(data.get("column") instanceof JSONArray)) {
            throw new RdosDefineException("column 必须为数组格式");
        }

        JSONArray column = data.getJSONArray("column");
        if (column.isEmpty()) {
            throw new RdosDefineException("需要匹配映射");
        }

        if (data.get("connection") == null) {
            throw new RdosDefineException("connection 不能为空");
        }

        if (!(data.get("connection") instanceof JSONArray)) {
            throw new RdosDefineException("connection 必须为数组格式");
        }

        JSONArray connections = data.getJSONArray("connection");
        if (connections.isEmpty()) {
            throw new RdosDefineException("connection 不能为空");
        }

        if (connections.size() > 1) {
            throw new RdosDefineException("暂不支持多个数据源写入");
        }

        if (StringUtils.isEmpty(connections.getJSONObject(0).getString("jdbcUrl"))) {
            throw new RdosDefineException("jdbcUrl 不能为空");
        }

        if (connections.getJSONObject(0).get("table") == null) {
            throw new RdosDefineException("table 不能为空");
        }

        if (!(connections.getJSONObject(0).get("table") instanceof JSONArray)) {
            throw new RdosDefineException("table 必须为数组格式");
        }

        JSONArray tables = connections.getJSONObject(0).getJSONArray("table");
        if (tables.isEmpty()) {
            throw new RdosDefineException("table 不能为空");
        }

        if (tables.size() > 1) {
            throw new RdosDefineException("暂不支持多张表写入");
        }

        for (Object table : tables) {
            if (!(table instanceof String)) {
                throw new RdosDefineException("table 必须为字符串数组格式");
            }
        }
    }
}
