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

import java.util.List;

@Data
@ApiModel("sql或者脚本执行结果信息")
public class BatchExecuteDataResultVO<T> {

    @ApiModelProperty(value = "任务 ID", example = "3")
    private String jobId;

    @ApiModelProperty(value = "sql", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "执行结果")
    private List<T> result;

    @ApiModelProperty(value = "引擎类别", example = "1")
    private Integer taskType;

}
