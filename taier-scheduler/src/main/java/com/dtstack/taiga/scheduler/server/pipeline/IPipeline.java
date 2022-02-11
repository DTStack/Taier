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

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public interface IPipeline {

    void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception;

    IPipeline getNextPipeline();

    void setNextPipeline(IPipeline nextPipeline);

    void execute(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception;

    abstract class AbstractPipeline implements IPipeline {
        /**
         * 下一个处理的pipeline
         */
        private IPipeline nextPipeline;
        /**
         * pipeline处理的字符串key
         */
        public String pipelineKey;

        public static final String taskShadeKey = "taskShade";
        public static final String scheduleJobKey = "scheduleJob";
        public static final String taskParamsToReplaceKey = "taskParamsToReplace";

        public AbstractPipeline(String pipelineKey) {
            this.pipelineKey = pipelineKey;
        }

        @Override
        public IPipeline getNextPipeline() {
            return nextPipeline;
        }

        public void setNextPipeline(IPipeline nextPipeline) {
            this.nextPipeline = nextPipeline;
        }

        @Override
        public void execute(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
            IPipeline current = this;
            while (null != current) {
                current.pipeline(actionParam, pipelineParam);
                current = current.getNextPipeline();
            }
        }

        public Object getExecuteValue(Map<String, Object> actionParam, Map<String, Object> pipelineParam){
            return pipelineParam.getOrDefault(pipelineKey,actionParam.get(pipelineKey));
        }
    }

}
