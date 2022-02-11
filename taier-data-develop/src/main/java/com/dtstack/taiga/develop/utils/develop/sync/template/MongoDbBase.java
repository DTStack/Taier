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
import com.dtstack.taiga.common.exception.RdosDefineException;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/3 13:33
 */
@Data
public class MongoDbBase extends BaseSource{

    private String hostPorts = "localhost:27017";

    private String username = "";

    private String password = "";

    private String database = "";

    private String collectionName = "";

    private List column;

    public void checkFormat(JSONObject data){
        data = data.getJSONObject("parameter");

        if(StringUtils.isEmpty(data.getString("hostPorts"))){
            throw new RdosDefineException("hostPorts 不能为空");
        }

        if(StringUtils.isEmpty(data.getString("database"))){
            throw new RdosDefineException("database 不能为空");
        }

        if(StringUtils.isEmpty(data.getString("collectionName"))){
            throw new RdosDefineException("collectionName 不能为空");
        }

        if(data.get("column") == null){
            throw new RdosDefineException("column 不能为空");
        }

        if(!(data.get("column") instanceof JSONArray)){
            throw new RdosDefineException("column 必须为数组格式");
        }

        JSONArray column = data.getJSONArray("column");
        if(column.isEmpty()){
            throw new RdosDefineException("column 不能为空");
        }
    }
}
