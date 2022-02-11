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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.dao.dto.ScheduleTaskParamShade;
import com.dtstack.taiga.scheduler.server.pipeline.operator.*;
import com.dtstack.taiga.scheduler.server.pipeline.params.FileParamPipeline;
import com.dtstack.taiga.scheduler.server.pipeline.params.JobIdParamPipeline;
import com.dtstack.taiga.scheduler.server.pipeline.params.UploadParamPipeline;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author yuebai
 * @date 2021-05-17
 * <p>
 * pipeline
 * params的key对应的paramPipe类型中 已经固定处理方式
 * operator的pipe的处理方式为 先获取pipelineMap中对应pipelineKey的值 没有就从actionMap获取
 * 在通过key后面的operatorMethod进行顺序操作 如 modelParam 先jobParam替换 在url编码
 * <p>
 * pipeline 的key存在顺序依赖 后续操作依赖前一操作结果
 */
public class PipelineBuilder {

    private static final String paramKey = "params";
    private static final String operatorKey = "operator";
    public static final String pipelineKey = "pipeline";

    /**
     * {
     * "params":[
     * "uploadPath",
     * "file",
     * "jobId"
     * ],
     * "operator":[
     * {
     * "modelParam":[
     * "jobparam",
     * "url"
     * ]
     * },
     * {
     * "launch-cmd":[
     * "replace",
     * "base64"
     * ]
     * },
     * {
     * "exeArgs":[
     * "replace"
     * ]
     * }
     * ]
     * }
     * <p>
     * 提取 uploadPath ——> file ——> jobId
     * modelParam ——> jobParam ——> url
     * launch-cmd ——> replace ——> base64
     * exeArgs ——> replace
     * <p>
     * 合并 operator的值到actionMap
     *
     * @param pipelineConfig
     * @return
     */
    public static IPipeline buildPipeline(String pipelineConfig) {
        if (StringUtils.isBlank(pipelineConfig)) {
            return null;
        }
        JSONObject config = JSONObject.parseObject(pipelineConfig);
        JSONArray params = config.getJSONArray(paramKey);
        IPipeline firstPipeline = null;
        IPipeline currentPipeline = null;
        if (CollectionUtils.isNotEmpty(params)) {
            for (int i = 0; i < params.size(); i++) {
                IPipeline.AbstractPipeline pipeline = getPipelineByPipelineKey(params.getString(i), null);
                if (null == pipeline) {
                    continue;
                }
                if (null == currentPipeline) {
                    firstPipeline = pipeline;
                    currentPipeline = pipeline;
                } else {
                    currentPipeline.setNextPipeline(pipeline);
                    currentPipeline = currentPipeline.getNextPipeline();
                }
            }
        }
        JSONArray operatorConfig = config.getJSONArray(operatorKey);
        if (CollectionUtils.isNotEmpty(operatorConfig)) {
            for (int i = 0; i < operatorConfig.size(); i++) {
                JSONObject operatorObj = operatorConfig.getJSONObject(i);
                for (String operatorKey : operatorObj.keySet()) {
                    JSONArray configValue = operatorObj.getJSONArray(operatorKey);
                    if (null == configValue || configValue.size() == 0) {
                        continue;
                    }
                    for (int j = 0; j < configValue.size(); j++) {
                        IPipeline.AbstractPipeline pipeline = getPipelineByPipelineKey(configValue.getString(j), operatorKey);
                        if (null == pipeline) {
                            continue;
                        }
                        if (null == firstPipeline) {
                            firstPipeline = pipeline;
                            currentPipeline = firstPipeline;
                        } else {
                            currentPipeline.setNextPipeline(pipeline);
                            currentPipeline = currentPipeline.getNextPipeline();
                        }
                    }
                }
            }

        }
        if (null != currentPipeline) {
            currentPipeline.setNextPipeline(new MergeOperatorPipeline());
        }
        return firstPipeline;
    }


    public static IPipeline.AbstractPipeline getPipelineByPipelineKey(String key, String operatorKey) {
        switch (key.trim().toLowerCase()) {
            case "jobid":
                return new JobIdParamPipeline();
            case "file":
                return new FileParamPipeline();
            case "uploadpath":
                return new UploadParamPipeline();
            case "base64":
                return new Base64OperatorPipeline(operatorKey);
            case "url":
                return new URLEncoderOperatorPipeline(operatorKey);
            case "replace":
                return new ReplaceOperatorPipeline(operatorKey);
            case "jobparam":
                return new JobParamOperatorPipeline(operatorKey);
        }
        return null;
    }

    /**
     * 构建pipeline的基础map
     *
     * @param pipelineConfig
     * @param scheduleJob
     * @param scheduleTaskShade
     * @param taskParamsToReplace
     * @param uploadConsumer
     * @return
     */
    public static Map<String, Object> getPipelineInitMap(String pipelineConfig, ScheduleJob scheduleJob, ScheduleTaskShade scheduleTaskShade,
                                                         List<ScheduleTaskParamShade> taskParamsToReplace,
                                                         Consumer<Map<String, Object>> uploadConsumer) {
        Map<String, Object> pipelineMap = new HashMap<>();
        pipelineMap.put(IPipeline.AbstractPipeline.scheduleJobKey, scheduleJob);
        pipelineMap.put(IPipeline.AbstractPipeline.taskShadeKey, scheduleTaskShade);
        pipelineMap.put(IPipeline.AbstractPipeline.taskParamsToReplaceKey, taskParamsToReplace);
        JSONObject config = JSONObject.parseObject(pipelineConfig);
        if (null == config || config.size() == 0 || !config.containsKey(paramKey)) {
            return pipelineMap;
        }
        List<String> params = config.getJSONArray(paramKey).toJavaList(String.class);
        if (CollectionUtils.isEmpty(params)) {
            return pipelineMap;
        }
        if (params.contains(UploadParamPipeline.pipelineKey)) {
            //填充upload必要的参数
            uploadConsumer.accept(pipelineMap);
        }
        return pipelineMap;
    }


    /**
     * 默认sql 的组件只有
     * {
     * "params":[
     * "jobId"
     * ],
     * "operator":[
     * {
     * "sqlText":[
     * "jobParam",
     * "replace"
     * ]
     * }
     * ]
     * }
     *
     * @return
     */
    public static IPipeline buildDefaultSqlPipeline() {
        String defaultSqlPipeConfig = "{\n" +
                "    \"params\":[\n" +
                "        \"jobId\"\n" +
                "    ],\n" +
                "    \"operator\":[\n" +
                "        {\n" +
                "            \"sqlText\":[\n" +
                "                \"jobParam\",\n" +
                "                \"replace\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        return buildPipeline(defaultSqlPipeConfig);
    }

}

