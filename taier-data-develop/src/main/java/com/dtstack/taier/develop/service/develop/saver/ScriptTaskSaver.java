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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-11 20:52
 */
@Component
public class ScriptTaskSaver extends AbstractTaskSaver {
    @Autowired
    private DefaultTaskSaver defaultTaskSaver;

    @Override
    public List<EScheduleJobType> support() {
        return ImmutableList.of(EScheduleJobType.SHELL, EScheduleJobType.PYTHON);
    }

    @Override
    public TaskResourceParam beforeProcessing(TaskResourceParam taskResourceParam) {
        populateExeArgs(taskResourceParam);
        return taskResourceParam;
    }

    @Override
    public void afterProcessing(TaskResourceParam taskResourceParam, TaskVO taskVO) {
        defaultTaskSaver.afterProcessing(taskResourceParam, taskVO);
    }

    @Override
    public String processScheduleRunSqlText(Task task) {
        return task.getSqlText();
    }

    /**
     * 补充运行参数
     *
     * @param param
     */
    private void populateExeArgs(TaskResourceParam param) {
        JSONObject exeArgs = new JSONObject();
        exeArgs.put("operateModel", param.getOperateModel());
        exeArgs.put("--app-name", param.getName());
        exeArgs.put("--input", param.getInput());
        exeArgs.put("--output", param.getOutput());
        exeArgs.put("--files", param.getResourceIdList());
        exeArgs.put("--app-type", findAppType(param.getTaskType(), param.getPythonVersion()));

        final String exeArgsParam = param.getExeArgs();
        if (StringUtils.isNotEmpty(exeArgsParam)) {
            final JSONObject paramObj = JSON.parseObject(exeArgsParam);
            // 如果传入了 param.getExeArgs，就追加预置参数
            paramObj.putAll(exeArgs);
            exeArgs = paramObj;
        }
        param.setExeArgs(exeArgs.toJSONString());
    }

    private static String findAppType(Integer taskType, Integer pythonVersion) {
        if (EScheduleJobType.SHELL.getType().equals(taskType)) {
            return "shell";
        }
        if (EScheduleJobType.PYTHON.getType().equals(taskType) && Objects.nonNull(pythonVersion)) {
            switch (pythonVersion) {
                case 2:
                    return "python2";
                case 3:
                    return "python3";
            }
        }
        throw new RdosDefineException("not support pythonVersion:" + pythonVersion);
    }
}