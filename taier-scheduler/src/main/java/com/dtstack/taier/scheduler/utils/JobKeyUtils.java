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

import com.dtstack.taier.pluginapi.util.MathUtil;

/**
 * @author yuebai
 * @date 2021-02-01
 */
public class JobKeyUtils {

    public static  String getJobTriggerTimeFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 1) {
            return "";
        }

        String timeStr = strings[strings.length - 1];
        if (timeStr.length() < 8) {
            return "";
        }

        return timeStr.substring(0, 8);
    }


    /**
     * 此处获取的时候schedule_task_shade 的id 不是task_id
     * @param jobKey
     * @return
     */
    public static Long getTaskShadeIdFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 2) {
            return -1L;
        }

        String id = strings[strings.length - 2];
        try {
            return MathUtil.getLongVal(id);
        } catch (Exception e) {
            return -1L;
        }
    }

    /**
     * 生成jobkey
     * @param keyPreStr 前缀
     * @param taskId 任务id
     * @param triggerTime 计划时间
     * @return jobKey
     */
    public static String generateJobKey(String keyPreStr, Long taskId, String triggerTime) {
        triggerTime = triggerTime.replace("-", "").replace(":", "").replace(" ", "");
        return keyPreStr + "_" + taskId + "_" + triggerTime;
    }
}
