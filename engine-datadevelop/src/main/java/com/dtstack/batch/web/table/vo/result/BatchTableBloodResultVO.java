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

package com.dtstack.batch.web.table.vo.result;

import com.dtstack.batch.web.pager.PageResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("表血缘信息")
public class BatchTableBloodResultVO {

    @ApiModelProperty(value = "所属项目 ID", example = "1")
    private Long belongProjectId;

    @ApiModelProperty(value = "数据源ID", example = "3")
    private Long dataSourceId;

    @ApiModelProperty(value = "数据源名称", example = "default")
    private String dataSource;

    @ApiModelProperty(value = "表名称", example = "user")
    private String tableName;

    @ApiModelProperty(value = "表 Id", example = "43")
    private Long tableId;

    @ApiModelProperty(value = "数据源类别", example = "1")
    private Integer dataSourceType;

    @ApiModelProperty(value = "表字段")
    private List<String> columns;

    @ApiModelProperty(value = "父血缘信息")
    private List<BatchTableBloodResultVO> parentTables;

    @ApiModelProperty(value = "子血缘信息")
    private List<BatchTableBloodResultVO> childTables;

    @ApiModelProperty(value = "父血缘信息")
    private PageResult<List<BatchTableBloodResultVO>> parentResult;

    @ApiModelProperty(value = "子血缘信息")
    private PageResult<List<BatchTableBloodResultVO>> childResult;

}
