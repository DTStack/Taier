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
import com.dtstack.engine.lineage.pojo.TableLineage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author chener
 * @Classname TableLineageInfo
 * @Description 表级血缘解析结果对象
 * @Date 2020/10/15 11:05
 * @Created chener@dtstack.com
 */
@ApiModel
public class TableLineageParseInfo extends SqlParseInfo {

    /**
     * 表级血缘  包含数据库 表
     */
    @ApiModelProperty("表级血缘")
    private List<TableLineage> tableLineages;

    /**
     * sql中涉及到的表
     */
    @ApiModelProperty("sql中使用到的表")
    private List<Table> tables;

    public List<TableLineage> getTableLineages() {
        return tableLineages;
    }

    public void setTableLineages(List<TableLineage> tableLineages) {
        this.tableLineages = tableLineages;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
