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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.develop.common.template.Reader;
import com.dtstack.taiga.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taiga.develop.utils.develop.sync.util.ColumnUtil;

/**
 * @author jiangbo
 * @date 2018/7/3 13:35
 */
public class MongoDbReader extends MongoDbBase implements Reader {

    private String filter = "{}";

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("hostPorts",this.getHostPorts());
        parameter.put("username",this.getUsername());
        parameter.put("password",this.getPassword());
        parameter.put("database",this.getDatabase());
        parameter.put("collectionName",this.getCollectionName());
        parameter.put("column", ColumnUtil.getColumns(this.getColumn(), PluginName.MongoDB_R));
        parameter.put("filter",this.getFilter());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject reader = new JSONObject(true);

        reader.put("name", PluginName.MongoDB_R);
        reader.put("parameter", parameter);
        return reader;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {
        super.checkFormat(data);
    }
}
