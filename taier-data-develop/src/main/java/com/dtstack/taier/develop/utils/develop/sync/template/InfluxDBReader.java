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

package com.dtstack.taier.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

public class InfluxDBReader extends InfluxDBBase implements Reader {

    /**
     * 自定义的查询语句
     *
     */
    private String customSql;

    /**
     * 数据过滤
     */
    private String where;

    /**
     * 切分键
     */
    private String splitPK;

    /**
     * 响应格式
     */
    private String format = "msgpack";

    @Override
    public JSONObject toReaderJson() {
        //构建reader
        JSONObject result = new JSONObject();
        result.put("name", PluginName.InfluxDB_R);
        //构建parameter
        JSONObject parameter = new JSONObject();
        parameter.put("sourceIds", getSourceIds());
        parameter.put("username", this.getUsername());
        parameter.put("password", this.getPassword());
        parameter.put("customSql", this.getCustomSql());
        parameter.put("where", this.getWhere());
        parameter.put("splitPk", this.getSplitPK());
        parameter.put("format", this.getFormat());
        parameter.put("extralConfig", this.getExtralConfig());
        parameter.put("column", ColumnUtil.getColumns(this.column, PluginName.HDFS_R));
        JSONObject connection = new JSONObject();
        connection.put("url", Lists.newArrayList(this.getUrl()));
        connection.put("table", Lists.newArrayList(this.getTable()));
        connection.put("schema", StringUtils.isNotBlank(this.getSchema()) ? this.getSchema() : "");
        parameter.put("connection", Lists.newArrayList(connection));
        result.put("parameter", parameter);
        return result;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        if (data == null){
            throw new RdosDefineException("reader is not null");
        }
    }

    public String getCustomSql() {
        return customSql;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getSplitPK() {
        return splitPK;
    }

    public void setSplitPK(String splitPK) {
        this.splitPK = splitPK;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
