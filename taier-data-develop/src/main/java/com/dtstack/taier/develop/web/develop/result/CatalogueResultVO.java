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

package com.dtstack.taier.develop.web.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("目录信息")
public class CatalogueResultVO {

    @ApiModelProperty(value = "文件夹名", example = "数据开发")
    private String nodeName;

    @ApiModelProperty(value = "父文件夹 ID", example = "23")
    private Long nodePid;

    @ApiModelProperty(value = "创建用户", example = "3")
    private Long createUserId;

    @ApiModelProperty(value = "目录层级", example = "3")
    private Integer level;

    @ApiModelProperty(value = "引擎类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "序号", example = "2")
    private Integer orderVal;

    @ApiModelProperty(value = "类目类别", example = "1")
    private Integer catalogueType;

    @ApiModelProperty(value = "ID", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "租户 ID", example = "")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", example = "")
    private Long projectId;

    @ApiModelProperty(value = "平台类别", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "创建时间", example = "")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

}
