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

import java.util.List;

@Data
@ApiModel("运行sql返回信息")
public class BatchExecuteResultVO<T> {

    @ApiModelProperty(value = "是否继续", example = "false")
    private Boolean isContinue = false;

    @ApiModelProperty(value = "下载", example = "1")
    private String download;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "发送到引擎生成的jobid", example = "1")
    private String  jobId;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "结果")
    private List<T> result;

    @ApiModelProperty(value = "信息", example = "1")
    private String msg;

    @ApiModelProperty(value = "sql文本", example = "1")
    private String sqlText;
}
