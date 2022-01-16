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

package com.dtstack.engine.master.vo.schedule.task.shade;

import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.pager.PageResult;

import java.util.List;


/**
 * @Auther: dazhi
 * @Date: 2020/7/30 11:54 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskShadePageVO{

    private PageResult<List<ScheduleTaskVO>> pageResult;
    private Integer publishedTasks;


    public PageResult<List<ScheduleTaskVO>> getPageResult() {
        return pageResult;
    }

    public void setPageResult(PageResult<List<ScheduleTaskVO>> pageResult) {
        this.pageResult = pageResult;
    }

    public Integer getPublishedTasks() {
        return publishedTasks;
    }

    public void setPublishedTasks(Integer publishedTasks) {
        this.publishedTasks = publishedTasks;
    }
}
