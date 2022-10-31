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

package com.dtstack.taier.scheduler.dto.fill;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/9/9 5:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleFillDataInfoDTO
{

    /**
     * 补数据类型： 0 批量补数据 1 工程补数据
     * 如果
     * fillDataType = 0时，taskIds字段有效。
     * fillDataType = 1 projects、whitelist、blacklist 有效
     * 必填
     */
    private Integer fillDataType;

    /**
     * 批量补数据任务列表
     *
     * fillDataType = 2 且 rootTaskId == null的时候，有效
     */
    private List<FillDataChooseTaskDTO> taskIds;

    /**
     * 头节点
     *
     * fillDataType = 2 时有效，rootTaskId优先级大于taskIds
     */
    private FillDataChooseTaskDTO rootTaskId;

    public Integer getFillDataType() {
        return fillDataType;
    }

    public void setFillDataType(Integer fillDataType) {
        this.fillDataType = fillDataType;
    }

    public List<FillDataChooseTaskDTO> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<FillDataChooseTaskDTO> taskIds) {
        this.taskIds = taskIds;
    }

    public FillDataChooseTaskDTO getRootTaskId() {
        return rootTaskId;
    }

    public void setRootTaskId(FillDataChooseTaskDTO rootTaskId) {
        this.rootTaskId = rootTaskId;
    }
}
