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

package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("租户下所有的项目返回信息")
public class BatchTenantProjectResultVO {

    @ApiModelProperty(value = "项目 ID", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "项目别名", example = "dev")
    private String projectAlias;

    @ApiModelProperty(value = "租户ID", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "项目类型", example = "1")
    private Integer projectType;

    @ApiModelProperty(value = "项目状态", example = "1")
    private Integer status;

}
