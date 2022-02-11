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
import org.apache.commons.lang.StringUtils;

public class EsReader extends EsBase implements Reader {

    private JSONObject query;

    private String username;
    private String password;

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("address", address);
        parameter.put("query", query);
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(), PluginName.ES_R));
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());
        parameter.put("username", this.getUsername());
        parameter.put("password", this.getPassword());
        JSONObject writer = new JSONObject(true);
        writer.put("name", PluginName.ES_R);
        writer.put("parameter", parameter);

        return writer;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    public JSONObject getQuery() {
        return query;
    }

    public void setQuery(JSONObject query) {
        this.query = query;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void checkFormat(JSONObject data) {

        if (StringUtils.isBlank(data.getString("name"))){
            throw new RdosDefineException("name 不能为空");
        }

        JSONObject parameter = data.getJSONObject("parameter");

        if (StringUtils.isBlank(parameter.getString("address"))){
            throw new RdosDefineException("address 不能为空");
        }
        checkArray(parameter, "column");
    }
}
