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

package com.dtstack.batch.web.task.vo.result;

import com.dtstack.batch.web.task.vo.result.BatchTaskRecordResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("任务操作信息")
public class BatchTaskRecordQueryRecordsResultVO {

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage = 0;

    @ApiModelProperty(value = "数据")
    private List<BatchTaskRecordResultVO>  data;

    @ApiModelProperty(value = "每页展示条数", example = "10")
    private Integer pageSize = 0;

    @ApiModelProperty(value = "总条数", example = "98")
    private Integer totalCount = 0;

    @ApiModelProperty(value = "总页数", example = "9")
    private Integer totalPage = 0;

}
