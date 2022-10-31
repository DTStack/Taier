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

package com.dtstack.taier.scheduler.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @Auther: dazhi
 * @Date: 2022/1/18 3:16 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobExecuteOrderUtil {

    /**
     * 按照计划时间生成具体排列序号
     *
     * @param triggerTime 计划时间
     * @param count
     * @return
     */
    public static Long buildJobExecuteOrder(String triggerTime, Integer count) {
        if (StringUtils.isBlank(triggerTime)) {
            throw new RuntimeException("cycTime is not null");
        }

        // 时间格式 yyyyMMddHHmmss  截取 jobExecuteOrder = yyMMddHHmm +  9位的自增
        String substring = triggerTime.substring(2, triggerTime.length() - 2);
        String increasing = String.format("%09d", count);
        return Long.parseLong(substring+increasing);
    }
}
