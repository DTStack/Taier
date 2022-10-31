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

package com.dtstack.taier.scheduler.server.builder.cron;

import com.dtstack.taier.scheduler.server.builder.ScheduleConf;

/**
 * @Auther: dazhi
 * @Date: 2021/12/30 6:50 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface IScheduleConfParser {

    /**
     * 解析前端封装的任务运行
     *
     * @param scheduleConf 任务运行周期
     * @return corn 表达式
     */
    String parse(ScheduleConf scheduleConf);
}
