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

package com.dtstack.engine.lineage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageTableInfoVO
 * @Description 表信息
 * @Date 2020/10/30 10:25
 * @Created chener@dtstack.com
 */
@ApiModel("表信息")
public class LineageTableVO {

    @ApiModelProperty("表id")
    private Long tableId;

    @ApiModelProperty("表名")
    private String tableName;

    @ApiModelProperty("schema名称")
    private String schemaName;

    @ApiModelProperty("数据库名")
    private String dbName;

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

    /**
     * 数据源信息
     */
    @ApiModelProperty("数据源信息")
    private LineageDataSourceVO dataSourceVO;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LineageDataSourceVO getDataSourceVO() {
        return dataSourceVO;
    }

    public void setDataSourceVO(LineageDataSourceVO dataSourceVO) {
        this.dataSourceVO = dataSourceVO;
    }
}
