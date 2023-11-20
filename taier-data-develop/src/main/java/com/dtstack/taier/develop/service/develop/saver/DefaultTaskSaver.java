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

package com.dtstack.taier.develop.service.develop.saver;

import com.dtstack.taier.common.enums.EComputeType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskTaskService;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.EJobType;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * HiveSql 实现
 *
 * @author ：zhaiyue
 * @date ：2022/06/26 00:11
 * @description：
 */
@Service
public class DefaultTaskSaver extends AbstractTaskSaver {

    @Autowired
    private UserService userService;


    @Override
    public TaskResourceParam beforeProcessing(TaskResourceParam taskResourceParam) {
        // sql 任务必须选择数据源
        EScheduleJobType scheduleJobType = EScheduleJobType.getByTaskType(taskResourceParam.getTaskType());
        taskResourceParam.setTaskParams(taskResourceParam.getTaskParams() == null ? taskTemplateService.getTaskTemplate(taskResourceParam.getTaskType(), taskResourceParam.getComponentVersion()).getParams() : taskResourceParam.getTaskParams());
        taskResourceParam.setComputeType(ComputeType.BATCH.getType());
        if (EComputeType.BATCH.getType() == scheduleJobType.getComputeType().getType() && EJobType.SQL.getType() == scheduleJobType.getEngineJobType()) {
            if (null == taskResourceParam.getDatasourceId()) {
                throw new TaierDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
            }
        }
        // 如果是修改任务的基本属性（目录、名称），禁止处理任务信息
        if (BooleanUtils.isTrue(taskResourceParam.getEditBaseInfo())
                && Objects.nonNull(taskResourceParam.getId())
                && taskResourceParam.getId() > 0) {
            return taskResourceParam;
        }

        dealSqlTask(taskResourceParam);

        dealScheduleConf(taskResourceParam);

        return taskResourceParam;
    }

    /**
     * 创建任务
     *
     * @param taskParam
     * @return
     */
    private void dealSqlTask(TaskResourceParam taskParam) {
        if (StringUtils.isNotBlank(taskParam.getSqlText())) {
            return;
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String initSqlTask = String.format(SQL_NOTE_TEMPLATE, taskParam.getName(), EScheduleJobType.getByTaskType(taskParam.getTaskType()).getName(),
                userService.getUserName(taskParam.getCreateUserId()), sdf.format(System.currentTimeMillis()),
                (StringUtils.isBlank(taskParam.getTaskDesc()) ? "" : taskParam.getTaskDesc().replace("\n", " ")));
        taskParam.setSqlText(initSqlTask);
    }

    /**
     * 处理调度信息
     *
     * @param taskParam
     */
    private void dealScheduleConf(TaskResourceParam taskParam) {
        if (StringUtils.isNotBlank(taskParam.getScheduleConf())) {
            return;
        }
        taskParam.setScheduleConf(DEFAULT_SCHEDULE_CONF);
    }

    @Override
    public List<EScheduleJobType> support() {
        return null;
    }

}
