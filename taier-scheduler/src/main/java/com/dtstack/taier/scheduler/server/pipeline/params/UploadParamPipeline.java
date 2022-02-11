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

package com.dtstack.taier.scheduler.server.pipeline.params;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.dto.ScheduleTaskParamShade;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.server.pipeline.IPipeline;
import com.dtstack.taier.scheduler.server.pipeline.JobParamReplace;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class UploadParamPipeline extends IPipeline.AbstractPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadParamPipeline.class);

    public static final String fileUploadPathKey = "fileUploadPath";
    public static final String workOperatorKey = "workOperator";
    public static final String pluginInfoKey = "pluginInfo";
    public static final String pipelineKey = "uploadPath";

    public UploadParamPipeline() {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws RdosDefineException {
        if (pipelineParam.containsKey(pipelineKey)) {
            return;
        }
        ScheduleTaskShade taskShade = (ScheduleTaskShade) pipelineParam.get(taskShadeKey);
        if (null == taskShade) {
            throw new RdosDefineException("upload param pipeline task shade can not be null");
        }
        ScheduleJob scheduleJob = (ScheduleJob) pipelineParam.get(scheduleJobKey);
        if (null == scheduleJob) {
            throw new RdosDefineException("upload param pipeline schedule job can not be null");
        }
        String fileUploadPath = (String) pipelineParam.get(fileUploadPathKey);
        if (StringUtils.isBlank(fileUploadPath)) {
            throw new RdosDefineException("upload param pipeline fileUploadPath can not be null");
        }
        WorkerOperator workerOperator = (WorkerOperator) pipelineParam.get(workOperatorKey);
        if (null == workerOperator) {
            throw new RdosDefineException("upload param pipeline workerOperator can not be null");
        }
        JSONObject pluginInfo = (JSONObject) pipelineParam.get(pluginInfoKey);
        if (null == pluginInfo) {
            throw new RdosDefineException("upload param pipeline pluginInfo can not be null");
        }
        @SuppressWarnings("unchecked")
        List<ScheduleTaskParamShade> taskParamShades = (List) pipelineParam.get(taskParamsToReplaceKey);

        String uploadPath = this.uploadSqlTextToHdfs((String)actionParam.get("sqlText"), taskShade.getTaskType(),
                taskShade.getName(), taskShade.getTenantId(),0L, taskParamShades, scheduleJob.getCycTime(),
                fileUploadPath, pluginInfo, workerOperator, scheduleJob.getJobId());

        pipelineParam.put(pipelineKey, uploadPath);
    }


    private String uploadSqlTextToHdfs(String content, Integer taskType, String taskName, Long tenantId, Long projectId,
                                       List<ScheduleTaskParamShade> taskParamShades, String cycTime, String fileUploadPath,
                                       JSONObject pluginInfo, WorkerOperator workerOperator, String jobId) throws RdosDefineException {
        String fileName = null;
        if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
            fileName = String.format("shell_%s_%s_%s_%s.sh", tenantId, projectId,
                    taskName, System.currentTimeMillis());
        } else {
            throw new RdosDefineException("not support upload file taskType " + taskType);
        }
        try {
            //content统一处理参数
            if (StringUtils.isNotBlank(content) && CollectionUtils.isNotEmpty(taskParamShades)) {
                content = JobParamReplace.paramReplace(content, taskParamShades, cycTime);
            }
            String hdfsPath = fileUploadPath + fileName;
            if (EScheduleJobType.SHELL.getVal().equals(taskType)) {
                content = content.replaceAll("\r\n", System.getProperty("line.separator"));
            }
            String hdfsUploadPath = workerOperator.uploadStringToHdfs(pluginInfo.toJSONString(), content, hdfsPath);
            if (StringUtils.isBlank(hdfsUploadPath)) {
                throw new RdosDefineException("Update task to HDFS failure hdfsUploadPath is blank");
            }
            return hdfsUploadPath;
        } catch (Exception e) {
            LOGGER.error("Update task to HDFS failure: ERROR {}", jobId, e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }
    }
}
