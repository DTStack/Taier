package com.dtstack.taier.develop.controller.develop;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.develop.mapstruct.vo.FileMapstructTransfer;
import com.dtstack.taier.develop.service.schedule.JobHistoryService;
import com.dtstack.taier.develop.vo.datasource.CheckPointListVO;
import com.dtstack.taier.develop.vo.schedule.FileInfoVO;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.pojo.FileResult;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.service.ScheduleTaskShadeService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "checkpoint管理", tags = {"checkpoint管理"})
@RestController
@RequestMapping(value = "/checkpoint")
public class DevelopCheckpointController {

    @Autowired
    private JobHistoryService jobHistoryService;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @PostMapping(value = "/listCheckPoint")
    public R<List<FileInfoVO>> listCheckPoint(@RequestBody @Validated CheckPointListVO checkPointVO) throws Exception {
        String jobId = checkPointVO.getJobId();
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
        if (null == scheduleJob) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        String applicationId = checkPointVO.getApplicationId();
        String engineId = jobHistoryService.getEngineIdByApplicationId(applicationId);
        if (StringUtils.isBlank(engineId)) {
            return R.ok(new ArrayList<>(0));
        }
        Long taskId = scheduleJob.getTaskId();
        ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeService.getOne(Wrappers.lambdaQuery(ScheduleTaskShade.class)
                .eq(ScheduleTaskShade::getTaskId,taskId));
        if (null == scheduleTaskShade) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        JSONObject configByKey = clusterService.getConfigByKey(scheduleTaskShade.getTenantId(), EComponentType.FLINK.getConfName(), scheduleTaskShade.getComponentVersion());
        EDeployMode deployMode = TaskParamsUtils.parseDeployTypeByTaskParams(scheduleTaskShade.getTaskParams(), scheduleTaskShade.getComputeType());
        JSONObject deployConfig = configByKey.getJSONObject(deployMode.getMode());
        String checkPointDir = deployConfig.getString(ConfigConstant.CHECKPOINTS_DIR);
        if (StringUtils.isBlank(checkPointDir)) {
            throw new RdosDefineException(ErrorCode.CONFIG_ERROR);
        }
        Long clusterId = clusterService.getClusterIdByTenantId(scheduleTaskShade.getTenantId());
        JSONObject pluginInfo = componentService.wrapperConfig(clusterId, EComponentType.HDFS.getTypeCode(), scheduleTaskShade.getComponentVersion(), null);
        String typeName = componentService.buildUploadTypeName(clusterId);
        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY,typeName);
        List<FileResult> fileResults = workerOperator.listFile(checkPointDir + File.separator + engineId, pluginInfo.toJSONString());
        fileResults = fileResults.stream().filter(file -> !file.getPath().endsWith("shared") && !file.getPath().endsWith("taskowned")).collect(Collectors.toList());
        return R.ok(FileMapstructTransfer.INSTANCE.toInfoVO(fileResults));
    }
}
