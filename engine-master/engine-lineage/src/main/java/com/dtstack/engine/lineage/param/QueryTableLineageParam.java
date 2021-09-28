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

package com.dtstack.engine.lineage.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname QueryTableLineageParam
 * @Description 查询表血缘关系参数
 * @Date 2020/11/3 10:41
 * @Created chener@dtstack.com
 */
@ApiModel("查询表血缘关系参数")
public class QueryTableLineageParam {
    @ApiModelProperty("应用类型")
    private Integer appType;

    @ApiModelProperty("数据源类型")
    private Integer sourceType;

    @ApiModelProperty("数据源名称，资产可能存在仅有数据源名称的数据源")
    private String sourceName;

    @ApiModelProperty(value = "数据源中心id")
    private Long dataInfoId;

    @ApiModelProperty("uic租户id")
    private Long dtUicTenantId;

    @ApiModelProperty("schema名称")
    private String schemaName;

    @ApiModelProperty("db名称")
    private String dbName;

    @ApiModelProperty("表名称")
    private String tableName;

    @ApiModelProperty("查询最大层数")
    private Integer level;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Long dataInfoId) {
        this.dataInfoId = dataInfoId;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
