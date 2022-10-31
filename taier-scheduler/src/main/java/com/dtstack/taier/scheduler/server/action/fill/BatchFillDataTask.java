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

package com.dtstack.taier.scheduler.server.action.fill;

import com.dtstack.taier.dao.domain.ScheduleTaskTaskShade;
import com.dtstack.taier.scheduler.dto.fill.FillDataChooseTaskDTO;
import com.dtstack.taier.scheduler.dto.fill.FillDataInfoDTO;
import com.dtstack.taier.scheduler.enums.FillDataTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/9/13 1:50 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BatchFillDataTask extends AbstractRecursionFillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(BatchFillDataTask.class);

    public BatchFillDataTask(ApplicationContext applicationContext, FillDataInfoDTO fillDataInfoDTO) {
        super(applicationContext, fillDataInfoDTO);
    }

    @Override
    public FillDataTypeEnum setFillDataType(Integer fillDataType) {
        return FillDataTypeEnum.BATCH;
    }

    @Override
    public Set<Long> getRunList() {
        Set<Long> runList = Sets.newHashSet();
        FillDataChooseTaskDTO rootTaskId = this.fillDataInfoBO.getRootTaskId();

        if (rootTaskId == null) {
            List<FillDataChooseTaskDTO> taskIds = this.fillDataInfoBO.getTaskChooseList();
            taskIds.forEach(task -> runList.add(task.getTaskId()));
        } else {
            List<Long> taskIds = Lists.newArrayList(rootTaskId.getTaskId());
            runList.addAll(taskIds);
            int level = 0;
            List<ScheduleTaskTaskShade> scheduleTaskTaskShades = getScheduleTaskTaskShades(taskIds);
            while (CollectionUtils.isNotEmpty(scheduleTaskTaskShades)) {
                List<Long> taskId = scheduleTaskTaskShades.stream().map(ScheduleTaskTaskShade::getTaskId).collect(Collectors.toList());
                runList.addAll(taskId);

                level++;
                if (level > environmentContext.getFillDataRootTaskMaxLevel()) {
                    LOGGER.warn("rootTaskId:{} max:{} break cycle",rootTaskId.getTaskId(),level);
                    break;
                }

                scheduleTaskTaskShades = getScheduleTaskTaskShades(taskId);
            }
        }

        return runList;
    }




}
