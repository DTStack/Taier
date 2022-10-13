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

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.common.enums.EParamType;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.common.util.TimeParamOperator;
import com.dtstack.taier.dao.domain.DevelopSysParameter;
import com.dtstack.taier.dao.domain.DevelopTaskParam;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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

@Component("developJobParamReplace")
public class JobParamReplace {

    @Autowired
    private DevelopSysParamService developSysParamService;

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    private final static String VAR_FORMAT = "${%s}";

    public String paramReplace(String sql, List paramList, String cycTime) {

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
            if (param instanceof DevelopTaskParamShade) {
                type = ((DevelopTaskParamShade) param).getType();
                paramName = ((DevelopTaskParamShade) param).getParamName();
                paramCommand = ((DevelopTaskParamShade) param).getParamCommand();
            } else if (param instanceof Map) {
                DevelopTaskParam taskParam = PublicUtil.mapToObject((Map<String, Object>) param, DevelopTaskParam.class);
                type = taskParam.getType();
                paramName = taskParam.getParamName();
                paramCommand = taskParam.getParamCommand();
            } else {
                type = ((DevelopTaskParam) param).getType();
                paramName = ((DevelopTaskParam) param).getParamName();
                paramCommand = ((DevelopTaskParam) param).getParamCommand();
            }

            String replaceStr = String.format(VAR_FORMAT, paramName);
            // 判断参数是否存在 SQL 中
            if (!sql.contains(replaceStr)) {
                continue;
            }

            String targetVal = convertParam(type, paramName, paramCommand, cycTime);
            sql = sql.replace(replaceStr, targetVal);
        }

        return sql;
    }


    public String convertParam(Integer type, String paramName, String paramCommand, String cycTime) {

        String command = null;
        if (EParamType.SYS_TYPE.getType().equals(type)) {
            DevelopSysParameter sysParameter = developSysParamService.getBatchSysParamByName(paramName);
            command = sysParameter.getParamCommand();

            // 特殊处理 bdp.system.current.time
            if ("bdp.system.runtime".equals(sysParameter.getParamName())) {
                return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
            }
        } else {
            command = paramCommand;
            return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
        }

        command = TimeParamOperator.transform(command, cycTime);
        return command;
    }

}
