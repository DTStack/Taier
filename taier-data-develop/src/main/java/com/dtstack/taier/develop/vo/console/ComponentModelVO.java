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

package com.dtstack.taier.develop.vo.console;

import com.dtstack.taier.common.util.Pair;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("组件模版信息")
public class ComponentModelVO {

    @ApiModelProperty(notes = "组件名称")
    private String name;

    @ApiModelProperty(notes = "是否允许多版本")
    private boolean allowCoexistence;

    @ApiModelProperty(notes = "组件依赖前置组件")
    private List<Integer> dependOn;

    @ApiModelProperty(notes = "组件可选择版本")
    private List<Pair<String, List<Pair>>> versionDictionary;

    @ApiModelProperty(notes = "组件所属类型")
    private Integer owner;

    @ApiModelProperty(notes = "组件所属code")
    private Integer componentCode;

    @ApiModelProperty(notes = "是否允许kerberos")
    private boolean allowKerberos;

    @ApiModelProperty(notes = "参数上传类型: 1:zip")
    private Integer uploadConfigType;

    public List<Pair<String, List<Pair>>> getVersionDictionary() {
        return versionDictionary;
    }

    public void setVersionDictionary(List<Pair<String, List<Pair>>> versionDictionary) {
        this.versionDictionary = versionDictionary;
    }

    public boolean isAllowKerberos() {
        return allowKerberos;
    }

    public void setAllowKerberos(boolean allowKerberos) {
        this.allowKerberos = allowKerberos;
    }

    public Integer getUploadConfigType() {
        return uploadConfigType;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(Integer componentCode) {
        this.componentCode = componentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAllowCoexistence() {
        return allowCoexistence;
    }

    public void setAllowCoexistence(boolean allowCoexistence) {
        this.allowCoexistence = allowCoexistence;
    }

    public List<Integer> getDependOn() {
        return dependOn;
    }

    public void setDependOn(List<Integer> dependOn) {
        this.dependOn = dependOn;
    }

    public void setUploadConfigType(Integer uploadConfigType) {
        this.uploadConfigType = uploadConfigType;
    }
}
