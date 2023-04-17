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
import com.dtstack.taier.scheduler.utils.CreateJsonFileUtil;
import com.dtstack.taier.scheduler.utils.FileUtil;
import com.dtstack.taier.scheduler.utils.ScriptUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 处理脚本类型任务参数
 */
@Service
public class DataxService {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private DatasourceOperator datasourceOperator;

    @Autowired
    private PluginWrapper pluginWrapper;

    /**
     * 创建DataX脚本
     */
    private static final String CREATE_TEMP_DATAX_PY = "%s %s/bin/datax.py   %s";

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
    public void handDataxParams(Map<String, Object> actionParam, ScheduleTaskShade task, List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) throws IOException {

        String sqlText = Objects.toString(actionParam.get("sqlText"), "");
        if (StringUtils.isEmpty(sqlText)) {
            throw new TaierDefineException("sqlText can't null or empty string");
        }
        if (CollectionUtils.isNotEmpty(taskParamsToReplace)) {
            sqlText = JobParamReplace.paramReplace(sqlText, taskParamsToReplace, scheduleJob.getCycTime());
        }
        dealDataxExeParams(actionParam, task, scheduleJob, sqlText);
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

    private void dealDataxExeParams(Map<String, Object> actionParam, ScheduleTaskShade task, ScheduleJob scheduleJob,
                                            String sqlText) throws IOException {
        // 获取组件信息
        Map<String, Object> pluginInfo = pluginWrapper.wrapperPluginInfo(task.getTaskType(), task.getTaskParams(), task.getComputeType(),
                task.getComponentVersion(), task.getTenantId(), task.getQueueName());
        JSONObject pluginInfoJson = new JSONObject(pluginInfo);

        String command = "";

        String commandConfigPath = pluginInfoJson.getString(ConfigConstant.COMPONENT_EXECUTE_DIR);

        String logFilePath = String.format("%s/log/%s.log", commandConfigPath, scheduleJob.getJobId());
        FileUtils.writeStringToFile(new File(logFilePath), "", StandardCharsets.UTF_8);
        //写入datax的json文件
        String tempPath = pluginInfoJson.getString(ConfigConstant.DATAX_TASK_TEMP);
        String dataxPath = pluginInfoJson.getString(ConfigConstant.DATAX_LOCAL_PATH);
        String pythonPath = pluginInfoJson.getString(ConfigConstant.DATAX_PYTHON_BIN);
        if (StringUtils.isEmpty(tempPath)){
            throw new TaierDefineException("datax.task.temp is null");
        }
        if (StringUtils.isEmpty(dataxPath)){
            throw new TaierDefineException("datax.local.path is null");
        }
        //生成datax的json文件
        String taskTempPath = CreateJsonFileUtil.createJsonFile(task.getSqlText(), tempPath, task.getName());
        if (StringUtils.isBlank(taskTempPath)) {
            throw new TaierDefineException("创建datax.json文件失败");
        }
        command= String.format(CREATE_TEMP_DATAX_PY, pythonPath, dataxPath, taskTempPath);
        JSONObject shellParams = new JSONObject();
        shellParams.put("shellCommand", command);
        shellParams.put("shellLogPath", logFilePath);

        actionParam.put("shellParams", shellParams.toJSONString());
    }


}
