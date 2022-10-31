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
import com.dtstack.taier.develop.service.develop.saver.DefaultTaskSaver;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskConfiguration implements ApplicationContextAware {
    @Autowired
    private DefaultTaskSaver defaultTaskSaver;

    private ConcurrentHashMap<EScheduleJobType, ITaskRunner> taskRunners = new ConcurrentHashMap(EScheduleJobType.values().length);
    private ConcurrentHashMap<EScheduleJobType, ITaskSaver> taskSavers = new ConcurrentHashMap(EScheduleJobType.values().length);

    public ITaskRunner get(EScheduleJobType jobType) {
        return taskRunners.get(jobType);
    }

    public ITaskRunner get(Integer taskType) {
        EScheduleJobType jobType = EScheduleJobType.getByTaskType(taskType);
        return taskRunners.get(jobType);
    }

    public ITaskSaver getSave(Integer taskType) {
        EScheduleJobType jobType = EScheduleJobType.getByTaskType(taskType);
        return taskSavers.getOrDefault(jobType, defaultTaskSaver);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ITaskRunner> beansOfRunner = applicationContext.getBeansOfType(ITaskRunner.class);
        beansOfRunner.forEach((t, service) -> {
            List<EScheduleJobType> support = service.support();
            if (CollectionUtils.isNotEmpty(support)) {
                for (EScheduleJobType eScheduleJobType : support) {
                    taskRunners.put(eScheduleJobType, service);
                }
            }
        });

        Map<String, ITaskSaver> beansOfSaver = applicationContext.getBeansOfType(ITaskSaver.class);
        beansOfSaver.forEach((t, service) -> {
            List<EScheduleJobType> support = service.support();
            if (CollectionUtils.isNotEmpty(support)) {
                for (EScheduleJobType eScheduleJobType : support) {
                    taskSavers.put(eScheduleJobType, service);
                }
            }

        });
        taskSavers.put(EScheduleJobType.DATA_ACQUISITION, taskSavers.get(EScheduleJobType.SYNC));
    }
}
