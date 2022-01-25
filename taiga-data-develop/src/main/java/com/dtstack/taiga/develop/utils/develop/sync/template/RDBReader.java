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
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.common.template.Reader;
import com.dtstack.taiga.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taiga.develop.utils.develop.sync.util.ColumnUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/12
 */
public class RDBReader extends RDBBase implements Reader {

    public RDBReader() {
    }
    /**
     * splitPk代表的字段进行数据分片
     */
    private String splitPK;
    /**
     * where条件
     */
    private String where;

    /**
     * 增量字段
     */
    private String increColumn;

    private JSONArray connections;

    private String customSql;

    private DataSourceType dataSourceType;

    public String getCustomSql() {
        return customSql;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
    }

    public String getSplitPK() {
		return splitPK;
	}

	public void setSplitPK(String splitPK) {
		this.splitPK = splitPK;
	}

	public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(DataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("username", this.getUsername());
        parameter.put("password", this.getPassword());
        parameter.put("splitPk", this.getSplitPK());
        parameter.put("where", this.getWhere());
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(), PluginName.MySQLD_R));

        if(this.getJdbcUrl() != null){
            JSONObject conn = new JSONObject(true);
            conn.put("jdbcUrl", Arrays.asList(this.getJdbcUrl()));
            conn.put("password",this.getPassword());
            conn.put("username",this.getUsername());
            conn.put("table",this.getTable());
            conn.put("sourceId", this.getSourceId());
            conn.put("schema", isNeedSchema());
            connections = new JSONArray();
            connections.add(conn);
        }

        if(connections != null && connections.size() > 0){
            Map<String,Object> conn = (Map)connections.get(0);
            this.setJdbcUrl(((List<String>)conn.get("jdbcUrl")).get(0));
            String pass = Objects.isNull(conn.get("password"))?"":conn.get("password").toString();
            this.setPassword(pass);
            this.setUsername(MapUtils.getString(conn, "username"));
            if(conn.get("table") instanceof String){
                this.setTable(Arrays.asList((String)conn.get("table")));
            } else {
                this.setTable((List<String>) conn.get("table"));
            }
        }

        boolean isMultiTable = (CollectionUtils.isNotEmpty(this.getConnections()) && this.getConnections().size() > 1) ||
                (CollectionUtils.isNotEmpty(this.getTable()) && this.getTable().size() > 1);

        // 增量配置
        parameter.put("increColumn", Optional.ofNullable(this.getIncreColumn()).orElse(""));
        parameter.put("startLocation", "");

        parameter.put("connection", connections);

        JSONObject reader = new JSONObject(true);
        switch (this.getType()) {
            case MySql:
            case TiDB:
                if(isMultiTable){
                    reader.put("name", PluginName.MySQLD_R);
                } else {
                    reader.put("name", PluginName.MySQL_R);
                }
                break;
            case Clickhouse:
                reader.put("name", PluginName.Clickhouse_R);
                break;
            case Polardb_For_MySQL:
                reader.put("name", PluginName.Polardb_for_MySQL_R);
                break;
            case Oracle:
                reader.put("name", PluginName.Oracle_R);
                break;
            case SQLServer:
                reader.put("name", PluginName.SQLServer_R);
                break;
            case HIVE:
            case HIVE3:
            case HIVE1X:
                reader.put("name", PluginName.Hive_R);
                break;
            case PostgreSQL:
            case LIBRA:
                reader.put("name", PluginName.PostgreSQL_R);
                break;
            case DB2:
                reader.put("name", PluginName.DB2_R);
                break;
            case GBase8a:
                reader.put("name", PluginName.GBase_R);
                break;
            case Phoenix:
                reader.put("name", PluginName.Phoenix_R);
                break;
            case Phoenix5:
                reader.put("name", PluginName.Phoenix5_R);
                break;
            case DMDB:
                reader.put("name", PluginName.DM_R);
                break;
            case Greenplum6:
                reader.put("name", PluginName.GREENPLUM_R);
                break;
            case KINGBASE8:
                reader.put("name", PluginName.KINGBASE_R);
                break;
            case ADB_FOR_PG:
                reader.put("name", PluginName.ADB_FOR_PG_R);
                break;
            default:
                throw new RdosDefineException(ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);

        }
        parameter.put("customSql", Optional.ofNullable(getCustomSql()).orElse(""));
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        reader.put("parameter", parameter);

        return reader;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    public JSONArray getConnections() {
        return connections;
    }

    public void setConnections(JSONArray connections) {
        this.connections = connections;
    }

    @Override
    public void checkFormat(JSONObject data) {

        String name = data.getString("name");
        data = data.getJSONObject("parameter");

        if (data.get("connection") == null){
            throw new RdosDefineException("connection 不能为空");
        }

        if (!(data.get("connection") instanceof JSONArray)){
            throw new RdosDefineException("connection 必须为数组格式");
        }

        JSONArray connections = data.getJSONArray("connection");
        if(connections.isEmpty()){
            throw new RdosDefineException("connection 不能为空");
        }

        if (!name.equals(PluginName.MySQLD_R) && connections.size() > 1){
            throw new RdosDefineException("暂不支持多个数据源写入");
        }

        for (Object connection : connections) {
            JSONObject conn = (JSONObject) connection;
            if(StringUtils.isEmpty(conn.getString("jdbcUrl"))){
                throw new RdosDefineException("jdbcUrl 不能为空");
            }

            if (conn.get("table") == null){
                throw new RdosDefineException("table 不能为空");
            }

            if(!(conn.get("table") instanceof JSONArray)){
                throw new RdosDefineException("table 必须为数组格式");
            }

            JSONArray tables = conn.getJSONArray("table");
            if (tables.isEmpty()){
                throw new RdosDefineException("table 不能为空");
            }

            if (name.equals(PluginName.Polardb_for_MySQL_R) && tables.size() > 1) {
                throw new RdosDefineException("polard 不支持多表");
            }

            if(!name.equals(PluginName.MySQLD_R) && tables.size() > 1){
                //fixme 虽然只有mysql支持分库分表，但如果其他关系型数据库配置多个表，flinkx也是只取第一个表（隐藏逻辑）
            }

            for (Object table : tables) {
                if (!(table instanceof String)){
                    throw new RdosDefineException("table 必须为字符串数组格式");
                }
            }
        }

        if (data.get("column") == null){
            throw new RdosDefineException("column 不能为空");
        }

        if (!(data.get("column") instanceof JSONArray)){
            throw new RdosDefineException("column 必须为数组格式");
        }

        JSONArray column = data.getJSONArray("column");
        if(column.isEmpty()){
            throw new RdosDefineException("column 不能为空");
        }
    }

    public String getIncreColumn() {
        return increColumn;
    }

    public void setIncreColumn(String increColumn) {
        this.increColumn = increColumn;
    }
}
