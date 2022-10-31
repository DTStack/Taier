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

package com.dtstack.taier.develop.service.template.es;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.service.template.BaseWriterPlugin;
import com.dtstack.taier.develop.service.template.PluginName;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author daojin
 */
public class Es7Writer extends BaseWriterPlugin {

    private String index;
    private int bulkAction = 100;
    private String username;
    private String password;
    private List<String> hosts;
    private List column;
    private List<Long> sourceIds;
    private SslConfig sslConfig;

    @Override
    public String pluginName() {
        return  PluginName.ES7_W;
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

        if (StringUtils.isBlank(parameter.getString("index"))){
            throw new RdosDefineException("index 不能为空");
        }

        if (StringUtils.isBlank(parameter.getString("bulkAction"))){
            throw new RdosDefineException("bulkAction 不能为空");
        }

        if (StringUtils.isBlank(parameter.getString("username"))) {
            throw new RdosDefineException("username 不能为空");
        }

        if (StringUtils.isBlank(parameter.getString("password"))) {
            throw new RdosDefineException("password 不能为空");
        }
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getBulkAction() {
        return bulkAction;
    }

    public void setBulkAction(int bulkAction) {
        this.bulkAction = bulkAction;
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

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public List getColumn() {
        return column;
    }

    public void setColumn(List column) {
        this.column = column;
    }

    @Override
    public List<Long> getSourceIds() {
        return sourceIds;
    }

    @Override
    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }
}
