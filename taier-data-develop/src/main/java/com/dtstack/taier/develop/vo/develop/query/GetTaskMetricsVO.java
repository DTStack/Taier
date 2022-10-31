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

package com.dtstack.taier.develop.vo.develop.query;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author zhiChen
 * @date 2021/1/7 19:44
 * @see
 */
public class GetTaskMetricsVO {

    @ApiModelProperty(value = "任务ID", example = "111", required = true)
    private Long taskId;

    @ApiModelProperty(value = "结束时间", example = "2021-04-15 19:53:02", required = true)
    private Timestamp end;

    @ApiModelProperty(value = "时间跨度", example = "1m", required = true)
    private String timespan;

    @ApiModelProperty(value = "图表名称列表", required = true)
    private List<String> chartNames;

}
