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

package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("生成建表SQL实体信息")
public class DevelopDatasourceTableCreateSQLVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "表名", example = "dev", required = true)
    private String tableName;

    @ApiModelProperty(value = "原数据源 ID",  example = "3", required = true)
    private Long originSourceId;

    @ApiModelProperty(value = "目标数据源 ID",  example = "11", required = true)
    private Long targetSourceId;

    @ApiModelProperty(value = "分区",  example = "/dev", required = true)
    private String partition;

    @ApiModelProperty(value = "原数据源 schema 信息", example = "schema")
    private String originSchema;

    @ApiModelProperty(value = "目标数据源 schema 信息", example = "schema")
    private String targetSchema;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getOriginSourceId() {
        return originSourceId;
    }

    public void setOriginSourceId(Long originSourceId) {
        this.originSourceId = originSourceId;
    }

    public Long getTargetSourceId() {
        return targetSourceId;
    }

    public void setTargetSourceId(Long targetSourceId) {
        this.targetSourceId = targetSourceId;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getOriginSchema() {
        return originSchema;
    }

    public void setOriginSchema(String originSchema) {
        this.originSchema = originSchema;
    }

    public String getTargetSchema() {
        return targetSchema;
    }

    public void setTargetSchema(String targetSchema) {
        this.targetSchema = targetSchema;
    }
}
