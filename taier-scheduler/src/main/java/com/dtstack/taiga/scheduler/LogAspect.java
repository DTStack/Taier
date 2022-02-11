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

package com.dtstack.taiga.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.dtstack.taiga.pluginapi.JobClient;
import com.dtstack.taiga.pluginapi.enums.RdosTaskStatus;
import com.google.common.collect.Lists;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author yuebai
 * @date 2021-01-28
 */

@Aspect
@Component
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    private static final ArrayList<String> filterMethod = Lists.newArrayList("getEngineMessageByHttp", "getEngineLog",
            "getRollingLogBaseInfo", "clusterResource", "getDefaultPluginConfig", "containerInfos", "judgeSlots");
    private static final ArrayList<String> logPluginInfoMethod = Lists.newArrayList("submitJob");
    private static final ArrayList<String> skipChangeMethod = Lists.newArrayList("getJobStatus");

    private static final PropertyFilter propertyFilter = (object, name, value) ->
            !(name.equalsIgnoreCase("pluginInfo") || name.equalsIgnoreCase("paramAction"));


    private static final PropertyFilter submitPropertyFilter = (object, name, value) ->
            !(name.equalsIgnoreCase("taskParams") || name.equalsIgnoreCase("paramAction"));

    @AfterReturning(pointcut = "execution(public * com.dtstack.taiga.scheduler.WorkerOperator.*(..))", returning = "ret")
    public void afterReturningAdvice(JoinPoint joinPoint, Object ret) {
        try {
            String methodName = joinPoint.getSignature().getName();
            if (filterMethod.contains(methodName)) {
                return;
            }
            String argsString = null;
            Object[] args = joinPoint.getArgs();
            Optional<Object> jobClientOpt = Arrays.stream(args)
                    .filter(a -> a instanceof JobClient)
                    .findFirst();
            if (jobClientOpt.isPresent()) {
                if (logPluginInfoMethod.contains(methodName)) {
                    argsString = JSONObject.toJSONString(jobClientOpt.get(),submitPropertyFilter);
                } else {
                    //忽略pluginInfo打印
                    argsString = JSONObject.toJSONString(jobClientOpt.get(), propertyFilter);
                }
            } else {
                if (skipChangeMethod.contains(methodName)) {
                    if (ret instanceof RdosTaskStatus && (RdosTaskStatus.RUNNING.equals(ret)|| RdosTaskStatus.SCHEDULED.equals(ret))) {
                        //状态获取 多以running 为主 过滤频繁打印
                        return;
                    }
                } else {
                    argsString = JSONObject.toJSONString(args);
                }
            }

            if (LOGGER.isInfoEnabled()) {
                JSONObject logInfo = new JSONObject(3);
                logInfo.put("method", joinPoint.getSignature().getDeclaringTypeName() + "." + methodName);
                logInfo.put("args", argsString);
                logInfo.put("return", JSONObject.toJSONString(ret));
                LOGGER.info(logInfo.toJSONString());
            }
        } catch (Exception e) {
            LOGGER.error("logAspect error ", e);
        }
    }

}