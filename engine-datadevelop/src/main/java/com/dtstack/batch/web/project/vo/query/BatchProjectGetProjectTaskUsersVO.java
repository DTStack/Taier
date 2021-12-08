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
@ApiModel("获取项目用户信息")
public class BatchProjectGetProjectTaskUsersVO extends DtInsightAuthParam {

   @ApiModelProperty(value = "项目 ID", hidden = true)
   private Long projectId;

   @ApiModelProperty(value = "租户 ID", hidden = true)
   private Long tenantId;

   @ApiModelProperty(value = "用户 ID", hidden = true)
   private Long userId;

   @ApiModelProperty(value = "用户名称", required = true, example = "test")
   private String name;

   @ApiModelProperty(value = "当前页", required = true, example = "1")
   private Integer currentPage;

   @ApiModelProperty(value = "展示条数", required = true, example = "10")
   private Integer pageSize;

}
