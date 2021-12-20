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

package com.dtstack.batch.web.catalogue.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("目录路径信息")
public class BatchCataloguePathVO {
    @ApiModelProperty(value = "路径列表", required = true)
    private List<String> nameList;

    @ApiModelProperty(value = "父节点名称", example = "root_name", required = true)
    private String rootName;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "script类型", example = "1")
    private Integer scriptType;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;
}
