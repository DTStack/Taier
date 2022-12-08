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

package com.dtstack.taier.develop.service.develop;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/7/13
 */
public interface ITaskSaver {

    /**
     * defines the types of scheduled jobs currently supported by saver
     * @see EScheduleJobType
     * @return current task supported schedule job types by saver
     */
    List<EScheduleJobType> support();

    TaskVO addOrUpdate(TaskResourceParam taskResourceParam);

    String processScheduleRunSqlText(Task task);

}
