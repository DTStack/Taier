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

package com.dtstack.taier.scheduler.dto.schedule;

import com.dtstack.taier.dao.domain.ScheduleTaskShade;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/31 9:54 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class SavaTaskDTO {

    /**
     * 任务
     */
    private ScheduleTaskShade scheduleTaskShade;

    /**
     * 父节点taskId
     */
    private List<Long>  parentTaskIdList;

    public ScheduleTaskShade getScheduleTaskShade() {
        return scheduleTaskShade;
    }

    public void setScheduleTaskShade(ScheduleTaskShade scheduleTaskShade) {
        this.scheduleTaskShade = scheduleTaskShade;
    }

    public List<Long> getParentTaskIdList() {
        return parentTaskIdList;
    }

    public void setParentTaskIdList(List<Long> parentTaskIdList) {
        this.parentTaskIdList = parentTaskIdList;
    }
}
