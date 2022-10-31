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

package com.dtstack.taier.develop.service.template.sqlserver;


import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;
import java.util.Map;

/**
 * Date: 2020/2/20
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class SqlServerCdcReaderParam extends DaPluginParam {
    /**
     * 0从任务运行时开始
     * 1按时间选择
     * 2按文件选择
     * 3用户手动输入
     */
    private Integer collectType;

    /**
     * 分组分表
     */
    private Map<String, Object> distributeTable;

    /**
     * 监听操作类型
     * DAoperators数组
     */
    private List<Integer> cat;

    /**
     * 嵌套JSON平铺
     */
    private Boolean pavingData;

    /**
     * 手动输入的lsn
     */
    private String lsn;

    private String schema;

    private Boolean allTable;

    private List<String> table;

    private Integer rdbmsDaType;

    private Long pollInterval;


    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public Map<String, Object> getDistributeTable() {
        return distributeTable;
    }

    public void setDistributeTable(Map<String, Object> distributeTable) {
        this.distributeTable = distributeTable;
    }

    public List<Integer> getCat() {
        return cat;
    }

    public void setCat(List<Integer> cat) {
        this.cat = cat;
    }

    public Boolean getPavingData() {
        return pavingData;
    }

    public void setPavingData(Boolean pavingData) {
        this.pavingData = pavingData;
    }

    public String getLsn() {
        return lsn;
    }

    public void setLsn(String lsn) {
        this.lsn = lsn;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Boolean getAllTable() {
        return allTable;
    }

    public void setAllTable(Boolean allTable) {
        this.allTable = allTable;
    }

    public List<String> getTable() {
        return table;
    }

    public void setTable(List<String> table) {
        this.table = table;
    }

    public Integer getRdbmsDaType() {
        return rdbmsDaType;
    }

    public void setRdbmsDaType(Integer rdbmsDaType) {
        this.rdbmsDaType = rdbmsDaType;
    }

    public Long getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(Long pollInterval) {
        this.pollInterval = pollInterval;
    }
}
