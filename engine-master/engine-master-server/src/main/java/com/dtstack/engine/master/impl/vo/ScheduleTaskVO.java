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

package com.dtstack.engine.master.impl.vo;

import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.master.server.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.server.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.server.scheduler.parser.ScheduleFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class ScheduleTaskVO extends com.dtstack.engine.master.vo.ScheduleTaskVO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskVO.class);

    public ScheduleTaskVO() {
    }

    public ScheduleTaskVO(ScheduleTaskShade taskShade, boolean getSimpleParams) {
        BeanUtils.copyProperties(taskShade, this);
        //需要将task复制给id
        this.setId(taskShade.getTaskId());
        init();
        if (getSimpleParams) {
            /*//精简不需要的参数（尤其是长字符串）
            setTaskDesc(StringUtils.EMPTY);
            setTaskParams(StringUtils.EMPTY);
            setExeArgs(StringUtils.EMPTY);
            setMainClass(StringUtils.EMPTY);
            setScheduleConf(StringUtils.EMPTY);*/
        }
    }

    public ScheduleTaskVO(ScheduleTaskForFillDataDTO task) {
        BeanUtils.copyProperties(task, this);
        init();
    }

    private void init() {
/*
        if (StringUtils.isNotBlank(this.getScheduleConf())) {
            try {
                ScheduleCron cron = ScheduleFactory.parseFromJson(this.getScheduleConf());
                this.cron = cron.getCronStr();
                this.taskPeriodId = cron.getPeriodType();
                if (ESchedulePeriodType.MIN.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "分钟任务";
                } else if (ESchedulePeriodType.HOUR.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "小时任务";
                } else if (ESchedulePeriodType.DAY.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "天任务";
                } else if (ESchedulePeriodType.WEEK.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "周任务";
                } else if (ESchedulePeriodType.MONTH.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "月任务";
                }
            } catch (Throwable e) {
                LOGGER.error("ScheduleTaskVO.init error:", e);
            }
        }*/
    }
}
