package com.dtstack.taier.scheduler.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.dto.ScheduleTaskParamShade;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.scheduler.PluginWrapper;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.server.pipeline.JobParamReplace;
import com.dtstack.taier.scheduler.utils.FileUtil;
import com.dtstack.taier.scheduler.utils.ScriptUtil;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.server.pipeline.JobParamReplace;
import com.dtstack.taier.scheduler.utils.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 处理脚本类型任务参数
 */
@Service
public class ScriptService {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private DatasourceOperator datasourceOperator;

    @Autowired
    private PluginWrapper pluginWrapper;

    /**
     * 处理脚本参数
     * on-yarn sqlText 上传到 hdfs 得到路径后，替换占位符
     * standalone 处理脚本参数得到command
     *
     * @param actionParam
     * @param task
     * @param taskParamsToReplace
     * @param scheduleJob
     * @throws Exception
     */
    public void handScriptParams(Map<String, Object> actionParam, ScheduleTaskShade task, List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) throws IOException {

        Integer taskType = task.getTaskType();
        String sqlText = Objects.toString(actionParam.get("sqlText"), "");
        if (StringUtils.isEmpty(sqlText)) {
            throw new TaierDefineException("sqlText can't null or empty string");
        }
        if (CollectionUtils.isNotEmpty(taskParamsToReplace)) {
            sqlText = JobParamReplace.paramReplace(sqlText, taskParamsToReplace, scheduleJob.getCycTime());
        }
        if (EScheduleJobType.SHELL.getVal().equals(taskType)) {
            sqlText = sqlText.replaceAll("\r\n", System.getProperty("line.separator"));
        }

        // 区分任务的类型 on-yarn 还是 standalone
        EDeployMode deployMode = EDeployMode.STANDALONE;
        if (EScheduleJobType.SHELL.getType().equals(taskType)
                || EScheduleJobType.PYTHON.getType().equals(taskType)) {
            deployMode = TaskParamsUtils.parseScriptDeployTypeByTaskParams(task.getTaskParams());
        }

        if (EDeployMode.RUN_ON_YARN.equals(deployMode)) {
            dealScriptExeParams(actionParam, task, scheduleJob, sqlText);
        }
        if (EDeployMode.STANDALONE.equals(deployMode)) {
            dealScriptStandAloneParams(actionParam, task, scheduleJob, sqlText);
        }
    }

    /**
     * 处理脚本类型任务 exeArgs参数信息
     *
     * @param actionParam
     * @param task
     * @param scheduleJob
     * @param sqlText
     */
    private void dealScriptExeParams(Map<String, Object> actionParam, ScheduleTaskShade task, ScheduleJob scheduleJob,
                                     String sqlText) {
        String exeArgs = Objects.toString(actionParam.get("exeArgs"), "");
        JSONObject exeArgsJson = new JSONObject();
        String hdfsPath = uploadToHdfs(sqlText, task, scheduleJob);
        exeArgsJson.put("scriptFilePath", hdfsPath);
        actionParam.put("exeArgs", exeArgs);
    }

    /**
     * 将脚本上传到 hdfs
     *
     * @param sqlText
     * @param task
     * @param scheduleJob
     * @return
     */
    private String uploadToHdfs(String sqlText, ScheduleTaskShade task, ScheduleJob scheduleJob) {
        JSONObject pluginInfo = clusterService.pluginInfoJSON(task.getTenantId(), task.getTaskType(), null, null, null);
        String hdfsPath = environmentContext.getHdfsTaskPath() + (FileUtil.getUploadFileName(task.getTaskType(), scheduleJob.getJobId()));
        return datasourceOperator.uploadToHdfs(pluginInfo, task.getTenantId(), sqlText, hdfsPath);
    }

    private void dealScriptStandAloneParams(Map<String, Object> actionParam, ScheduleTaskShade task, ScheduleJob scheduleJob,
                                            String sqlText) throws IOException {
        // 获取组件信息
        Map<String, Object> pluginInfo = pluginWrapper.wrapperPluginInfo(task.getTaskType(), task.getTaskParams(), task.getComputeType(),
                task.getComponentVersion(), task.getTenantId(), task.getQueueName());
        JSONObject pluginInfoJson = new JSONObject(pluginInfo);

        String command = "";

        // 区分任务类型 shell、python2、python3
        String commandConfigPath = pluginInfoJson.getString(ConfigConstant.COMPONENT_EXECUTE_DIR);
        if (EScheduleJobType.SHELL.getType().equals(task.getTaskType())) {
            command = ScriptUtil.buildShellCommand(String.format("%s/%s/%s.sh", commandConfigPath, scheduleJob.getJobId(), scheduleJob.getJobName()), sqlText);
        }
        if (EScheduleJobType.PYTHON.getType().equals(task.getTaskType())) {
            // 处理python任务版本信息
            JSONObject exeArgsJson = JSONObject.parseObject(actionParam.get("exeArgs").toString());
            String pythonVersion = exeArgsJson.getString(ConfigConstant.APP_TYPE);
            String pythonBinPath = "";
            if ("python3".equalsIgnoreCase(pythonVersion)) {
                pythonBinPath = pluginInfoJson.getString(ConfigConstant.COMPONENT_PYTHON_3_BIN);
            }
            if ("python2".equalsIgnoreCase(pythonVersion)) {
                pythonBinPath = pluginInfoJson.getString(ConfigConstant.COMPONENT_PYTHON_2_BIN);
            }

            if (StringUtils.isBlank(pythonBinPath)) {
                throw new TaierDefineException(String.format("jobId: %s 未匹配到对应的python版本信息", scheduleJob.getJobId()));
            }
            command = ScriptUtil.buildPythonCommand(String.format("%s/%s/%s.py", commandConfigPath, scheduleJob.getJobId(), scheduleJob.getJobName()),
                    sqlText, pythonBinPath);
        }
        actionParam.put("shellCommand", command);
    }


}
