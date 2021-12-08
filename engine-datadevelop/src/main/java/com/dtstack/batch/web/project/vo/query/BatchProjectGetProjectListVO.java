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

package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据项目名/别名分页查询信息")
public class BatchProjectGetProjectListVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "模糊名称", example = "模糊名称", required = true)
    private String fuzzyName;

    @ApiModelProperty(value = "项目类型", example = "1", required = true)
    private Integer projectType;

    @ApiModelProperty(value = "排序规则", example = "rps.stick")
    private String orderBy;

    @ApiModelProperty(value = "排序", example = "desc")
    private String sort;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer page;

    @ApiModelProperty(value = "总页数", example = "10", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "目录ID", example = "1", required = true)
    private Long catalogueId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Boolean isAdmin;
}
