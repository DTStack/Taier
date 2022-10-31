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

package com.dtstack.taier.scheduler.server.pipeline.operator;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.scheduler.server.pipeline.IPipeline;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class ReplaceOperatorPipeline extends IPipeline.AbstractPipeline {

    public ReplaceOperatorPipeline(String pipelineKey) {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
        String replaceString = (String) super.getExecuteValue(actionParam, pipelineParam);
        if (StringUtils.isBlank(replaceString)) {
            return;
        }
        for (String paramKey : pipelineParam.keySet()) {
            Object paramValue = pipelineParam.get(paramKey);
            if (paramValue instanceof String) {
                replaceString = replaceString.replace(String.format("${%s}", paramKey), (String) paramValue);
            }
        }
        pipelineParam.put(pipelineKey, replaceString);
    }
}
