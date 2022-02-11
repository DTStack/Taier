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


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public abstract class OdpsBase extends BaseSource{

    protected String accessId;

    protected String accessKey;

    protected String project;

    protected String endPoint;

    protected String table;

    protected String partition;

    protected List column;

    protected Long sourceId;

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

    public void checkFormat(JSONObject data){
        data = data.getJSONObject("parameter");
        if (data.get("odpsConfig") == null){
            throw new RdosDefineException("odpsConfig 不能为空");
        }

        JSONObject odpsConfig = data.getJSONObject("odpsConfig");
        if(StringUtils.isEmpty(odpsConfig.getString("accessId"))){
            throw new RdosDefineException("accessId 不能为空");
        }

        if(StringUtils.isEmpty(odpsConfig.getString("accessKey"))){
            throw new RdosDefineException("accessKey 不能为空");
        }

        if(StringUtils.isEmpty(odpsConfig.getString("project"))){
            throw new RdosDefineException("project 不能为空");
        }

        if(StringUtils.isEmpty(data.getString("table"))){
            throw new RdosDefineException("table 不能为空");
        }

        if(data.get("column") == null){
            throw new RdosDefineException("column 不能为空");
        }

        if (!(data.get("column") instanceof JSONArray)){
            throw new RdosDefineException("column 必须为数组格式");
        }

        JSONArray column = data.getJSONArray("column");
        if (column.isEmpty()){
            throw new RdosDefineException("column 不能为空");
        }
    }
}
