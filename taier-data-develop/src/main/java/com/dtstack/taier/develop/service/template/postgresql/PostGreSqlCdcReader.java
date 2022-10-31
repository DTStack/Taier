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

package com.dtstack.taier.develop.service.template.postgresql;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.dtstack.taier.develop.service.template.PluginName;

import java.util.List;

/**
 * @author huoyun
 * @date 2021/4/13 2:06 下午
 * @company: www.dtstack.com
 */
public class PostGreSqlCdcReader extends BaseReaderPlugin {

    /**
     * PostGreSQL数据库的jdbc连接字符串
     */
    private String url;

    /**
     * 数据库名
     */
    private String databaseName;

    /**
     * 数据源的用户名
     */
    private String username;

    /**
     * 数据源指定用户名的密码
     */
    private String password;

    /**
     * 需要解析的数据表，格式为schema.table
     */
    private List<String> tableList;

    /**
     * 需要解析的数据更新类型，包括insert、update、delete三种
     */
    private String cat;

    /**
     *是否将解析出的json数据拍平
     */
    private Boolean pavingData;

    /**
     * 状态更新间隔，默认2000ms
     */
    private Long statusInterval = 2000L;

    /**
     * 要读取PostgreSQL WAL日志序列号的开始位置
     */
    private String lsn;

    /**
     * 复制槽名字
     */
    private String slotName;

    /**
     * 是否允许创建复制槽，默认值true
     */
    private Boolean allowCreateSlot = Boolean.TRUE;

    /**
     * 复制槽是否为临时的
     */
    private Boolean temporary;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
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

    public List<String> getTableList() {
        return tableList;
    }

    public void setTableList(List<String> tableList) {
        this.tableList = tableList;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public Boolean getPavingData() {
        return pavingData;
    }

    public void setPavingData(Boolean pavingData) {
        this.pavingData = pavingData;
    }

    public Long getStatusInterval() {
        return statusInterval;
    }

    public void setStatusInterval(Long statusInterval) {
        this.statusInterval = statusInterval;
    }

    public String getLsn() {
        return lsn;
    }

    public void setLsn(String lsn) {
        this.lsn = lsn;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public Boolean getAllowCreateSlot() {
        return allowCreateSlot;
    }

    public void setAllowCreateSlot(Boolean allowCreateSlot) {
        this.allowCreateSlot = allowCreateSlot;
    }

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean temporary) {
        this.temporary = temporary;
    }

    @Override
    public String pluginName() {
        return PluginName.PGWAL_R;
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {

    }
}
