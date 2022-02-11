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

package com.dtstack.taiga.scheduler.server.pipeline;

import com.dtstack.taiga.common.enums.EParamType;
import com.dtstack.taiga.common.util.TimeParamOperator;
import com.dtstack.taiga.dao.dto.ScheduleTaskParamShade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 变量替换
 * command--->${}格式的里面的参数需要计算,其他的直接替换
 * Date: 2017/6/6
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class JobParamReplace {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobParamReplace.class);


    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{(.*?)\\}|\\@@\\{(.*?)\\}");

    private final static String VAR_FORMAT = "${%s}";

    private final static String VAR_COMPONENT = "@@{%s}";


    public static String paramReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime) {

        if (CollectionUtils.isEmpty(paramList)) {
            return sql;
        }

        Matcher matcher = PARAM_PATTERN.matcher(sql);
        if (!matcher.find()) {
            return sql;
        }

        for (Object param : paramList) {
            Integer type;
            String paramName;
            String paramCommand;
            type = ((ScheduleTaskParamShade) param).getType();
            paramName = ((ScheduleTaskParamShade) param).getParamName();
            paramCommand = ((ScheduleTaskParamShade) param).getParamCommand();

            String targetVal = convertParam(type, paramName, paramCommand, cycTime, ((ScheduleTaskParamShade) param).getTaskId());
            String parseSymbol = convertSymbol(type);
            String replaceStr = String.format(parseSymbol, paramName);
            sql = sql.replace(replaceStr, targetVal);
        }

        return sql;
    }

    private static String convertSymbol(Integer type) {
        if (EParamType.COMPONENT.getType().equals(type)) {
            return VAR_COMPONENT;
        } else {
            return VAR_FORMAT;
        }
    }

    public static String convertParam(Integer type, String paramName, String paramCommand, String cycTime, Long taskId) {

        String command = paramCommand;
        if (EParamType.SYS_TYPE.getType().equals(type)) {
            // 特殊处理 bdp.system.currenttime
            if ("bdp.system.runtime".equals(paramName)) {
                return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
            }

            command = TimeParamOperator.transform(command, cycTime);
            return command;
        } else if (EParamType.COMPONENT.getType().equals(type)) {
            return command;
        } else {
            return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
        }
    }
}
