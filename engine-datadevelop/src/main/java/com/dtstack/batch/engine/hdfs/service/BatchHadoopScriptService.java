package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.enums.TaskOperateType;
import com.dtstack.batch.service.job.IBatchScriptService;
import com.dtstack.dtcenter.common.enums.ComputeType;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.EScriptType;
import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.engine.api.service.TaskParamApiClient;
import com.dtstack.engine.api.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.api.vo.template.TaskTemplateVO;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行脚本模式下的选中的脚本
 * Date: 2019/5/20
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchHadoopScriptService implements IBatchScriptService {

    private static final String TASK_NAME_PREFIX = "run_%s_task_%s";

    @Autowired
    private TaskParamApiClient taskParamApiClient;

    @Autowired
    private BatchHadoopSelectSqlService batchHadoopSelectSqlService;

    @Override
    public JSONObject buildPythonShellArgs(Integer scriptType, String scriptName,
                                           Long tenantId, Long projectId,
                                           String content, String jobId,
                                           Long userId) {

        String appType = getAppType(scriptType);
        Integer taskType = getTaskType(scriptType);
        Integer engineType = getEngineType(scriptType);

        TaskTemplateVO taskTemplateParam = new TaskTemplateVO();
        taskTemplateParam.setEngineType(engineType);
        taskTemplateParam.setComputeType(ComputeType.BATCH.getType());

        ApiResponse<TaskTemplateResultVO> engineParamTmplByComputeType = taskParamApiClient.getEngineParamTmplByComputeType(taskTemplateParam);
        String taskParams = "";
        if (engineParamTmplByComputeType != null) {
            taskParams = formatLearnTaskParams(engineParamTmplByComputeType.getData().getParams());
        }

        JSONObject exeArgs = new JSONObject();
        exeArgs.put("--app-name", scriptName);
        exeArgs.put("--cmd-opts", "");
        exeArgs.put("--files", null);
        exeArgs.put("operateModel", TaskOperateType.EDIT.getType());
        exeArgs.put("--app-type", appType);

        return batchHadoopSelectSqlService.buildPythonShellArgs(taskType, exeArgs.toJSONString(), scriptName, content,
                taskParams, tenantId, projectId, jobId);
    }

    @Override
    public JSONObject buildPythonShellArgs(Integer taskType, String exeArgs, String name,
                                           String content, String taskParams, Long tenantId,
                                           Long projectId, String jobId) {
        return null;
    }

    @Override
    public JSONObject buildSaveData(Integer scriptType, String scriptName) {
        Integer operateModel;
        operateModel = TaskOperateType.RESOURCE.getType();

        String appType = getAppType(scriptType);

        JSONObject exeArgs = new JSONObject();
        exeArgs.put("--app-name", scriptName);
        exeArgs.put("--cmd-opts", "");
        exeArgs.put("--files", null);
        exeArgs.put("operateModel", TaskOperateType.EDIT.getType());
        exeArgs.put("--app-type", appType);

        JSONObject saveData = new JSONObject();
        saveData.put("operateModel", operateModel);
        saveData.put("exeArgs", exeArgs);

        return saveData;
    }

    @Override
    public String createTaskName(Integer scriptType) {
        Integer taskType = getTaskType(scriptType);
        String taskName = "";

        if (taskType.equals(EJobType.PYTHON.getVal())) {
            taskName = String.format(TASK_NAME_PREFIX, "python", System.currentTimeMillis());
        } else if (taskType.equals(EJobType.SHELL.getVal())) {
            taskName = String.format(TASK_NAME_PREFIX, "shell", System.currentTimeMillis());
        } else if (taskType.equals(EJobType.SPARK_PYTHON.getVal())) {
            taskName = String.format(TASK_NAME_PREFIX, "pyspark", System.currentTimeMillis());
        }

        return taskName;
    }

    private String getAppType(Integer scriptType) {
        String appType;
        if (scriptType.equals(EScriptType.Shell.getType())) {
            appType = "shell";
        } else {
            EngineType pythonType = EngineType.getByEScriptType(scriptType);
            appType = pythonType.getEngineName();
        }

        return appType;
    }

    private Integer getTaskType(Integer scriptType) {
        Integer taskType;
        if (scriptType.equals(EScriptType.Shell.getType())) {
            taskType = EJobType.SHELL.getVal();
        } else {
            taskType = EJobType.PYTHON.getVal();
        }

        return taskType;
    }

    private Integer getEngineType(Integer scriptType) {
        Integer engineType;
        if (scriptType.equals(EScriptType.Shell.getType())) {
            engineType = EngineType.Shell.getVal();
        } else {
            EngineType pythonType = EngineType.getByEScriptType(scriptType);
            engineType = pythonType.getVal();
        }

        return engineType;
    }


    private String formatLearnTaskParams(String taskParams) {
        List<String> params = new ArrayList<>();

        for (String param : taskParams.split("\r|\n")) {
            if (StringUtils.isNotEmpty(param.trim()) && !param.trim().startsWith("#")) {
                String[] parts = param.split("=");
                params.add(String.format("%s=%s", parts[0].trim(), parts[1].trim()));
            }
        }

        return StringUtils.join(params, "\n");
    }

}
