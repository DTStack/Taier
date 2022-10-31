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

package com.dtstack.taier.develop.enums.develop;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.scheduler.enums.ESchedulePeriodType;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 工作流调度属性工具类
 *
 * @author 昆卡
 * @version 4.3.x-SNAPSHOT
 * @since 2021/10/25
 */

public enum WorkFlowScheduleConfEnum {
    /**
     * 分钟
     */
    MIN(String.valueOf(ESchedulePeriodType.MIN.getVal())) {
        @Override
        public void checkWorkFlowChildScheduleConf(JSONObject oldJsonObject, JSONObject newJsonObject) {
            validate(oldJsonObject, newJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, BEGIN_HOUR_KEY_NAME,
                    BEGIN_MIN_KEY_NAME, GAP_MIN_KEY_NAME, END_HOUR_KEY_NAME, END_MIN_KEY_NAME);
        }

        @Override
        public void handleWorkFlowChildScheduleConf(Task childNodeTask, JSONObject parentJsonObject) {
            applyParentScheduleConf(childNodeTask, parentJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME,
                    BEGIN_HOUR_KEY_NAME, BEGIN_MIN_KEY_NAME, GAP_MIN_KEY_NAME, END_HOUR_KEY_NAME, END_MIN_KEY_NAME,
                    PERIOD_TYPE);
        }
    },

    /**
     * 小时
     */
    HOUR(String.valueOf(ESchedulePeriodType.HOUR.getVal())) {
        @Override
        public void checkWorkFlowChildScheduleConf(JSONObject oldJsonObject, JSONObject newJsonObject) {
            validate(oldJsonObject, newJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, BEGIN_HOUR_KEY_NAME,
                    BEGIN_MIN_KEY_NAME, GAP_HOUR_KEY_NAME, END_HOUR_KEY_NAME, END_MIN_KEY_NAME);
        }

        @Override
        public void handleWorkFlowChildScheduleConf(Task childNodeTask, JSONObject parentJsonObject) {
            applyParentScheduleConf(childNodeTask, parentJsonObject, BEGIN_HOUR_KEY_NAME, BEGIN_MIN_KEY_NAME,
                    BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, GAP_HOUR_KEY_NAME, END_HOUR_KEY_NAME, END_MIN_KEY_NAME,
                    PERIOD_TYPE);
        }
    },

    /**
     * 天
     */
    DAY(String.valueOf(ESchedulePeriodType.DAY.getVal())) {
        @Override
        public void checkWorkFlowChildScheduleConf(JSONObject oldJsonObject, JSONObject newJsonObject) {
            validate(oldJsonObject, newJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, HOUR_KEY_NAME, MIN_KEY_NAME);
        }

        @Override
        public void handleWorkFlowChildScheduleConf(Task childNodeTask, JSONObject parentJsonObject) {
            applyParentScheduleConf(childNodeTask, parentJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, HOUR_KEY_NAME,
                    MIN_KEY_NAME, PERIOD_TYPE);
        }
    },

    /**
     * 周
     */
    WEEK(String.valueOf(ESchedulePeriodType.WEEK.getVal())) {
        @Override
        public void checkWorkFlowChildScheduleConf(JSONObject oldJsonObject, JSONObject newJsonObject) {
            validate(oldJsonObject, newJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, WEEKDAY_KEY_NAME, HOUR_KEY_NAME,
                    MIN_KEY_NAME);
        }

        @Override
        public void handleWorkFlowChildScheduleConf(Task childNodeTask, JSONObject parentJsonObject) {
            applyParentScheduleConf(childNodeTask, parentJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, WEEKDAY_KEY_NAME,
                    HOUR_KEY_NAME, MIN_KEY_NAME, PERIOD_TYPE);
        }
    },

    /**
     * 月
     */
    MONTH(String.valueOf(ESchedulePeriodType.MONTH.getVal())) {
        @Override
        public void checkWorkFlowChildScheduleConf(JSONObject oldJsonObject, JSONObject newJsonObject) {
            validate(oldJsonObject, newJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, DAY_KEY_NAME, HOUR_KEY_NAME,
                    MIN_KEY_NAME);
        }

        @Override
        public void handleWorkFlowChildScheduleConf(Task childNodeTask, JSONObject parentJsonObject) {
            applyParentScheduleConf(childNodeTask, parentJsonObject, BEGIN_DATE_KEY_NAME, END_DATE_KEY_NAME, DAY_KEY_NAME,
                    HOUR_KEY_NAME, MIN_KEY_NAME, PERIOD_TYPE);
        }
    }
    ;
    /**
     * 开始小时键名
     */
    private static final String BEGIN_HOUR_KEY_NAME = "beginHour";
    /**
     * 开始分钟键名
     */
    private static final String BEGIN_MIN_KEY_NAME = "beginMin";
    /**
     * 间隔分钟键名
     */
    private static final String GAP_MIN_KEY_NAME = "gapMin";
    /**
     * 结束小时键名
     */
    private static final String END_HOUR_KEY_NAME = "endHour";
    /**
     * 结束分钟键名
     */
    private static final String END_MIN_KEY_NAME = "endMin";
    /**
     * 间隔小时键名
     */
    private static final String GAP_HOUR_KEY_NAME = "gapHour";
    /**
     * 小时键名
     */
    private static final String HOUR_KEY_NAME = "hour";
    /**
     * 分钟键名
     */
    private static final String MIN_KEY_NAME = "min";
    /**
     * 星期键名
     */
    private static final String WEEKDAY_KEY_NAME = "weekDay";
    /**
     * 天键名
     */
    private static final String DAY_KEY_NAME = "day";
    /**
     * CRON表达式键名
     */
    private static final String CRON_KEY_NAME = "cron";
    /**
     * 开始日期键名
     */
    private static final String BEGIN_DATE_KEY_NAME = "beginDate";
    /**
     * 结束日期键名
     */
    private static final String END_DATE_KEY_NAME = "endDate";
    /**
     * 调度周期键名
     */
    private static final String PERIOD_TYPE = "periodType";

    /**
     * 调度周期
     */
    private final String periodType;

    /**
     * 处理工作流子节点调度配置
     *
     * @param oldJsonObject 老周期配置json对象
     * @param newJsonObject 新周期配置json对象
     */
    public abstract void checkWorkFlowChildScheduleConf(JSONObject oldJsonObject, JSONObject newJsonObject);

    /**
     * 处理工作流子节点调度配置，父的属性部分给到子
     *
     * @param childNodeTask    子节点任务
     * @param parentJsonObject 工作流周期配置json对象
     */
    public abstract void handleWorkFlowChildScheduleConf(Task childNodeTask, JSONObject parentJsonObject);

    /**
     * 获取工作流调度周期枚举对象
     *
     * @param periodType 调度周期
     */
    public static String getCurrentPeriodType(String periodType) {
        if (StringUtils.isEmpty(periodType)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        for (WorkFlowScheduleConfEnum workFlowScheduleConfEnum : WorkFlowScheduleConfEnum.values()) {
            if (!workFlowScheduleConfEnum.getPeriodType().equals(periodType)) {
                continue;
            }
            return workFlowScheduleConfEnum.name();
        }
        throw new RdosDefineException("未知的调度周期");
    }

    private static void validate(JSONObject oldJsonObject, JSONObject newJsonObject, String... keyNameArray) {
        if (ArrayUtils.isEmpty(keyNameArray)) {
            return;
        }
        for (String keyName : keyNameArray) {
            if (!String.valueOf(oldJsonObject.getOrDefault(keyName, StringUtils.EMPTY)).equals(newJsonObject.getString(keyName))) {
                throw new RdosDefineException(ErrorCode.UNSUPPORTED_OPERATION);
            }
        }
    }

    private static void applyParentScheduleConf(Task childNodeTask, JSONObject parentJsonObject, String... keyNameArray) {
        if (ArrayUtils.isEmpty(keyNameArray)) {
            return;
        }
        final JSONObject childJsonObject = JSONObject.parseObject(childNodeTask.getScheduleConf());
        for (String keyName : keyNameArray) {
            if (parentJsonObject.containsKey(keyName)) {
                childJsonObject.put(keyName, parentJsonObject.get(keyName));
            }
        }
        childNodeTask.setScheduleConf(childJsonObject.toJSONString());
    }

    public String getPeriodType() {
        return periodType;
    }

    WorkFlowScheduleConfEnum(String periodType) {
        this.periodType = periodType;
    }
}
