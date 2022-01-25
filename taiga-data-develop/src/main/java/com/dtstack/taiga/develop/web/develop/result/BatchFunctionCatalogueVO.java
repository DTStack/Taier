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

package com.dtstack.taiga.develop.web.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("函数目录结果信息")
public class BatchFunctionCatalogueVO {
    @ApiModelProperty(value = "父目录")
    private BatchFunctionCatalogueVO parentCatalogue;

    @ApiModelProperty(value = "节点名称", example = "a")
    private String nodeName;

    @ApiModelProperty(value = "节点父id", example = "3")
    private Long nodePid;

    @ApiModelProperty(value = "目录层级 0:一级 1:二级 n:n+1级", example = "1")
    private Integer level;

    @ApiModelProperty(value = "创建用户")
    private Long createUserId;

    @ApiModelProperty(value = "engine类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "节点值", example = "1")
    private Integer orderVal;

    @ApiModelProperty(value = "目录类型", example = "1")
    private Integer catalogueType;
}