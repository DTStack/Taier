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

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.develop.impl.FlinkTaskService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: qianyi
 * @Date: 2022/05/29/5:14 PM
 */
@Component
public class FlinkSqlTaskSaver extends AbstractTaskSaver {
    @Autowired
    private FlinkTaskService flinkTaskService;

    public static Logger LOGGER = LoggerFactory.getLogger(FlinkSqlTaskSaver.class);


    @Override
    public TaskResourceParam beforeProcessing(TaskResourceParam taskResourceParam) {
        flinkTaskService.convertTableStr(taskResourceParam);
        return taskResourceParam;
    }



    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.FLINK_SQL);
    }
}
