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


import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;
import java.util.Map;

/**
 * @author huoyun
 * @date 2021/4/13 2:04 下午
 * @company: www.dtstack.com
 */
public class PostGreSqlCdcReaderParam extends DaPluginParam {

    /**
     * 库名
     */
    private String schema;

    /**
     * 是否全部表
     */
    private Boolean allTable;

    /**
     * 选中的表
     */
    private List<String> table;

    /**
     * 分组分表
     */
    private Map<String, Object> distributeTable;

    /**
     * @see com.dtstack.streamapp.service.enums.CollectType
     */
    private Integer collectType;

    /**
     * @see com.dtstack.streamapp.service.enums.SlotConfigEnum
     */
    private Integer slotConfig;

    /**
     * 手动输入的lsn
     */
    private String lsn;

    /**
     * 复制槽名字
     */
    private String slotName;

    /**
     * 复制槽是否为临时的
     */
    private Boolean temporary;

    /**
     * 监听操作类型
     * DAoperators数组
     */
    private List<Integer> cat;

    /**
     * 状态更新间隔
     */
    private Long statusInterval;

    /**
     * 嵌套JSON平铺
     */
    private Boolean pavingData;

    /**
     * 关系型数据库实时采集类型 1 binlog | 2 间隔轮询
     */
    private Integer rdbmsDaType;

    public Integer getRdbmsDaType() {
        return rdbmsDaType;
    }

    public void setRdbmsDaType(Integer rdbmsDaType) {
        this.rdbmsDaType = rdbmsDaType;
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

    public Map<String, Object> getDistributeTable() {
        return distributeTable;
    }

    public void setDistributeTable(Map<String, Object> distributeTable) {
        this.distributeTable = distributeTable;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public Integer getSlotConfig() {
        return slotConfig;
    }

    public void setSlotConfig(Integer slotConfig) {
        this.slotConfig = slotConfig;
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

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean temporary) {
        this.temporary = temporary;
    }

    public List<Integer> getCat() {
        return cat;
    }

    public void setCat(List<Integer> cat) {
        this.cat = cat;
    }

    public Long getStatusInterval() {
        return statusInterval;
    }

    public void setStatusInterval(Long statusInterval) {
        this.statusInterval = statusInterval;
    }

    public Boolean getPavingData() {
        return pavingData;
    }

    public void setPavingData(Boolean pavingData) {
        this.pavingData = pavingData;
    }
}
