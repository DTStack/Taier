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

import com.dtstack.engine.lineage.pojo.ColumnLineage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author chener
 * @Classname ColumnLineageInfo
 * @Description 字段级血缘解析结果
 * @Date 2020/10/15 11:11
 * @Created chener@dtstack.com
 */
@ApiModel("字段级血缘结果对象")
public class ColumnLineageParseInfo extends TableLineageParseInfo{
    /**
     * 字段血缘，包含数据库，表，字段三部分
     *
     */
    @ApiModelProperty("字段级血缘关系列表")
    private List<ColumnLineage> columnLineages;

    public List<ColumnLineage> getColumnLineages() {
        return columnLineages;
    }

    public void setColumnLineages(List<ColumnLineage> columnLineages) {
        this.columnLineages = columnLineages;
    }
}
