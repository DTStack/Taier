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


import com.dtstack.engine.lineage.pojo.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname SqlInfo
 * @Description 解析sql基本信息对象
 * @Date 2020/10/15 10:58
 * @Created chener@dtstack.com
 */
@ApiModel
public class SqlParseInfo extends BaseParseResult{

    /**
     * 主数据库
     */
    @ApiModelProperty("主数据库")
    private String mainDb;

    /**
     * DDL、DML操作的表对象
     */
    @ApiModelProperty("DDL、DML语句解析出的操作对象")
    private Table mainTable;

    public String getMainDb() {
        return mainDb;
    }

    public void setMainDb(String mainDb) {
        this.mainDb = mainDb;
    }

    public Table getMainTable() {
        return mainTable;
    }

    public void setMainTable(Table mainTable) {
        this.mainTable = mainTable;
    }
}
