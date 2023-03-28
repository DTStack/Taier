package com.dtstack.taier.scheduler.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.dto.ScheduleTaskParamShade;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.server.pipeline.JobParamReplace;
import com.dtstack.taier.scheduler.utils.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void handScriptParams(Map<String, Object> actionParam, ScheduleTaskShade task, List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {

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
            dealScriptStandAloneParams(actionParam, task, scheduleJob);
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
        if (!exeArgs.contains("${uploadPath}")) {
            return;
        }
        String hdfsPath = uploadToHdfs(sqlText, task, scheduleJob);
        // cyc scheduling，should in time replace placeHolder to hdfs path
        exeArgs = exeArgs.replace("${uploadPath}", hdfsPath);
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

    private void dealScriptStandAloneParams (Map<String, Object> actionParam, ScheduleTaskShade task, ScheduleJob scheduleJob) {
        // 获取组件信息
        // 区分任务类型 shell、python2、python3
        // 组装shell参数
        actionParam.put("shellCommand", "echo 1");
    }


}
