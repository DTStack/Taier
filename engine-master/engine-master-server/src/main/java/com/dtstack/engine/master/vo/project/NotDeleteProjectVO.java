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

package com.dtstack.engine.master.vo.project;

import com.dtstack.engine.master.vo.task.NotDeleteTaskVO;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/3/22 9:51 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NotDeleteProjectVO {

    private String taskName;

    private List<NotDeleteTaskVO> notDeleteTaskVOList;
    
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<NotDeleteTaskVO> getNotDeleteTaskVOList() {
        return notDeleteTaskVOList;
    }

    public void setNotDeleteTaskVOList(List<NotDeleteTaskVO> notDeleteTaskVOList) {
        this.notDeleteTaskVOList = notDeleteTaskVOList;
    }
}
