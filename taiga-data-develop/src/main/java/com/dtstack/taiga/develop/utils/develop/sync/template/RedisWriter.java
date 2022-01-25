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
import com.dtstack.taiga.develop.common.template.Writer;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/3 13:13
 */
public class RedisWriter extends RedisBase implements Writer {

    private List<Integer> keyIndexes = new ArrayList<>();

    private String keyFieldDelimiter = "\u0001";

    private int timeout = 3000;

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    private long expireTime = 0;

    private String writeMode = "insert";

    private String type = "string";

    private String mode = "set";

    private String valueFieldDelimiter = "\u0001";

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("hostPort",this.getHostPort());
        parameter.put("password",this.getPassword());
        parameter.put("database",this.getDatabase());
        parameter.put("keyIndexes",this.getKeyIndexes());
        parameter.put("keyFieldDelimiter",this.getKeyFieldDelimiter());
        parameter.put("timeout",this.getTimeout());
        parameter.put("dateFormat",this.getDateFormat());
        parameter.put("expireTime",this.getExpireTime());
        parameter.put("writeMode",this.getWriteMode());
        parameter.put("type",this.getType());
        parameter.put("mode",this.getMode());
        parameter.put("valueFieldDelimiter",this.getValueFieldDelimiter());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());

        JSONObject writer = new JSONObject(true);

        writer.put("name","rediswriter");
        writer.put("parameter", parameter);
        return writer;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    public List<Integer> getKeyIndexes() {
        return keyIndexes;
    }

    public void setKeyIndexes(List<Integer> keyIndexes) {
        this.keyIndexes = keyIndexes;
    }

    public String getKeyFieldDelimiter() {
        return keyFieldDelimiter;
    }

    public void setKeyFieldDelimiter(String keyFieldDelimiter) {
        this.keyFieldDelimiter = keyFieldDelimiter;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getValueFieldDelimiter() {
        return valueFieldDelimiter;
    }

    public void setValueFieldDelimiter(String valueFieldDelimiter) {
        this.valueFieldDelimiter = valueFieldDelimiter;
    }

    @Override
    public void checkFormat(JSONObject data) {
        data = data.getJSONObject("parameter");
        if(StringUtils.isEmpty(data.getString("hostPort"))){
            throw new RdosDefineException("Redis的 reader 插件必须填写 hostPort");
        }

        if (!data.containsKey("keyIndexes") || data.getJSONArray("keyIndexes").isEmpty()){
            throw new RdosDefineException("Redis的 reader 插件必须填写 keyIndexes");
        }

        if(!(data.get("keyIndexes") instanceof JSONArray)){
            throw new RdosDefineException("keyIndexes 必须为数组类型");
        }

        for (Object indexes : data.getJSONArray("keyIndexes")) {
            if (!(indexes instanceof Integer)){
                throw new RdosDefineException("keyIndexes 参数必须为数值类型的数组");
            }
        }
    }
}
