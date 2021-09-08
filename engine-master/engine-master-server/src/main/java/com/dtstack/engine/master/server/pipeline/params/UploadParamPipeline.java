package com.dtstack.engine.master.server.pipeline.params;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.server.pipeline.IPipeline;
import com.dtstack.engine.master.server.scheduler.JobParamReplace;
import com.dtstack.engine.common.enums.EScheduleJobType;
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

        String uploadPath = this.uploadSqlTextToHdfs(taskShade.getSqlText(), taskShade.getTaskType(),
                taskShade.getName(), taskShade.getTenantId(), taskShade.getProjectId(), taskParamShades, scheduleJob.getCycTime(),
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
        } else if (taskType.equals(EScheduleJobType.PYTHON.getVal()) ||
                taskType.equals(EScheduleJobType.NOTEBOOK.getVal())) {
            fileName = String.format("python_%s_%s_%s_%s.py", tenantId, projectId,
                    taskName, System.currentTimeMillis());
        } else if (taskType.equals(EScheduleJobType.DEEP_LEARNING.getVal())) {
            fileName = String.format("learning_%s_%s_%s_%s.py", tenantId, projectId,
                    taskName, System.currentTimeMillis());
        } else if (taskType.equals(EScheduleJobType.SPARK_PYTHON.getVal())) {
            fileName = String.format("pyspark_%s_%s_%s_%s.py", tenantId, projectId,
                    taskName, System.currentTimeMillis());
        } else if (taskType.equals(EScheduleJobType.TENSORFLOW_1_X.getVal())) {
            fileName = String.format("tensorflow_%s_%s_%s_%s.py", tenantId, projectId,
                    taskName, System.currentTimeMillis());
        } else if (taskType.equals(EScheduleJobType.KERAS.getVal())) {
            fileName = String.format("keras_%s_%s_%s_%s.py", tenantId, projectId,
                    taskName, System.currentTimeMillis());
        } else if (taskType.equals(EScheduleJobType.PYTORCH.getVal())) {
            fileName = String.format("pytorch_%s_%s_%s_%s.py", tenantId, projectId,
                    taskName, System.currentTimeMillis());
        } else {
            throw new RdosDefineException("not support upload file taskType " + taskType);
        }
        try {
            //content统一处理参数
            if (StringUtils.isNotBlank(content) && CollectionUtils.isNotEmpty(taskParamShades)) {
                content = new JobParamReplace().paramReplace(content, taskParamShades, cycTime);
            }
            String hdfsPath = fileUploadPath + fileName;
            if (EScheduleJobType.SHELL.getVal().equals(taskType)) {
                content = content.replaceAll("\r\n", System.getProperty("line.separator"));
            }
            String typeName = pluginInfo.getString(ConfigConstant.TYPE_NAME_KEY);
            String hdfsUploadPath = workerOperator.uploadStringToHdfs(typeName, pluginInfo.toJSONString(), content, hdfsPath);
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
