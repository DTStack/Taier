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

package com.dtstack.taier.develop.vo.fill;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleFillDataInfoVO {

    /**
     * 补数据类型： 0 批量补数据 1 工程补数据
     * 如果
     * fillDataType = 0时，taskIds字段有效。
     * 必填
     */
    @NotNull(message = "fillDataType is not null")
    @Min(value = 0,message = " Supplement data type: 0 Develop supplement data 1 Project supplement data")
    @Max(value = 1,message = " Supplement data type: 0 Develop supplement data 1 Project supplement data")
    @ApiModelProperty(value = "补数据类型： 0 批量补数据fillDataType = 0时，taskIds字段有效" ,required = true)
    private Integer fillDataType;


    /**
     * 批量补数据任务列表
     */
    @ApiModelProperty(value = "批量补数据任务列表:fillDataType = 0 且 rootTaskId == null的时候，有效" )
    private List<FillDataChooseTaskVO> taskIds;

    /**
     * 头节点
     */
    @ApiModelProperty(value = "批量补数据任务列表:fillDataType = 0有效,rootTaskId优先级大于taskIds" )
    private FillDataChooseTaskVO rootTaskId;

    public Integer getFillDataType() {
        return fillDataType;
    }

    public void setFillDataType(Integer fillDataType) {
        this.fillDataType = fillDataType;
    }

    public List<FillDataChooseTaskVO> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<FillDataChooseTaskVO> taskIds) {
        this.taskIds = taskIds;
    }

    public FillDataChooseTaskVO getRootTaskId() {
        return rootTaskId;
    }

    public void setRootTaskId(FillDataChooseTaskVO rootTaskId) {
        this.rootTaskId = rootTaskId;
    }
}
