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

package com.dtstack.taiga.scheduler.server.pipeline.operator;

import com.dtstack.taiga.common.util.Base64Util;
import com.dtstack.taiga.scheduler.server.pipeline.IPipeline;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class Base64OperatorPipeline extends IPipeline.AbstractPipeline {

    public Base64OperatorPipeline(String pipelineKey) {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) {
        String base64String = (String) super.getExecuteValue(actionParam,pipelineParam);
        if (StringUtils.isNotBlank(base64String)) {
            pipelineParam.put(pipelineKey, Base64Util.baseEncode(base64String));
        }
    }
}
