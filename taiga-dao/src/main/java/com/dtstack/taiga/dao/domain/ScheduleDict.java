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

package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author yuebai
 * @date 2021-03-02
 */
@TableName("schedule_dict")
public class ScheduleDict extends BaseEntity {
    /**
     * 字典code
     */
    private String dictCode;
    /**
     * 字典名称
     */
    private String dictName;
    /**
     * 字典value
     */
    private String dictValue;
    /**
     * 字典类型
     */
    private Integer type;
    /**
     * 字典依赖名称
     */
    private String dependName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 字段value数据类型
     */
    private String dataType;

    private String dictDesc;

    public String getDictDesc() {
        return dictDesc;
    }

    public void setDictDesc(String dictDesc) {
        this.dictDesc = dictDesc;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDependName() {
        return dependName;
    }

    public void setDependName(String dependName) {
        this.dependName = dependName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
