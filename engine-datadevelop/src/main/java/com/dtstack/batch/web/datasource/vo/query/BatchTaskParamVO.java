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

package com.dtstack.batch.web.datasource.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("自定义参数")
public class BatchTaskParamVO {

    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "自定义参数类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "自定义参数名称", example = "xxxx", required = true)
    private String paramName;

    @ApiModelProperty(value = "自定义参数替换值", example = "xxxx", required = true)
    private String paramCommand;
}
