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

package com.dtstack.taiga.scheduler.impl.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-14
 */
@ApiModel
public class ClientTemplate implements Serializable {
    /**
     * 前端界面展示 名称
     */
    @ApiModelProperty(notes = "前端界面展示 名称")
    private String key;
    /**
     * 前端界面展示 多选值
     */
    @ApiModelProperty(notes = "前端界面展示 多选值")
    private List<ClientTemplate> values;
    /**
     * 前端界面展示类型  0: 输入框 1:单选:
     */
    @ApiModelProperty(notes = "前端界面展示类型  0: 输入框 1:单选:")
    private String type;
    /**
     * 默认值
     */
    @ApiModelProperty(notes = "默认值")
    private Object value;
    /**
     * 是否必填 默认必须
     */
    @ApiModelProperty(notes = "是否必填 默认非必须")
    private Boolean required = false;

    @ApiModelProperty(notes = "依赖配置")
    private String dependencyKey;

    @ApiModelProperty(notes = "依赖配置的值")
    private String dependencyValue;

    @JSONField(serialize = false)
    private Long id = 0L;

    private List<Integer> deployTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDependencyValue() {
        return dependencyValue;
    }

    public void setDependencyValue(String dependencyValue) {
        this.dependencyValue = dependencyValue;
    }

    public String getDependencyKey() {
        return dependencyKey;
    }

    public void setDependencyKey(String dependencyKey) {
        this.dependencyKey = dependencyKey;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<ClientTemplate> getValues() {
        return values;
    }

    public void setValues(List<ClientTemplate> values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ClientTemplate(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public List<Integer> getDeployTypes() {
        return deployTypes;
    }

    public void setDeployTypes(List<Integer> deployTypes) {
        this.deployTypes = deployTypes;
    }

    public ClientTemplate() {
    }
}
