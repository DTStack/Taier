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

package com.dtstack.taier.develop.service.template.ftp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.enums.develop.EWriterMode;
import com.dtstack.taier.develop.service.template.PluginName;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

import static com.dtstack.taier.develop.service.template.BaseReaderPlugin.dealExtralConfig;

/**
 * 
 * @since 1.3.1
 **/
public class FTPWriteParam extends FTPParam implements Writer {

    private String extralConfig;

    protected List<Long> sourceIds;

    /**
     * write mode
     *  overwrite append
     */
    private String writeMode;

    /**
     * source id
     */
    private Integer sourceId;

    /**
     * source type
     */
    private Integer type;

    @Override
    public JSONObject toWriterJson() {
        JSONObject param = new JSONObject();
        try {
            param = JSON.parseObject(JSON.toJSONString(this));
        } catch (Exception e) {
            param = JSON.parseObject(JSON.toJSON(this).toString());
        }

        // reset column index
        param.put("column", resetColumnIndex(param.getJSONArray("column")));

        if (resetWriteMode()) {
            // 前端传入是 replace 和 insert
            param.put("writeMode", EWriterMode
                    .sourceType(type)
                    .rewriterWriterMode(param.getString("writeMode")));
        }


        dealExtralConfig(param);
        JSONObject res = new JSONObject();
        res.put("name", pluginName());
        res.put("parameter", param);
        return res;
    }

    /**
     * reset column index
     * @param jsonColumns request parameter target column of the json text
     * @return list of converted entity objects
     */
    private List<FTPColumn> resetColumnIndex(JSONArray jsonColumns) {
        // transfer to entity
        List<FTPColumn> columns = JSONObject.parseArray(JSONObject.toJSONString(jsonColumns), FTPColumn.class);
        if (CollectionUtils.isNotEmpty(columns)) {
            int index = 0;
            for (FTPColumn column : columns) {
                column.setIndex(index++);
            }
        }
        return columns;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }

    @Override
    public boolean resetWriteMode() {
        return true;
    }

    public List<Long> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }

    public String pluginName() {
        return PluginName.FTP_W;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    @Override
    public Integer getSourceId() {
        return sourceId;
    }

    @Override
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
