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

package com.dtstack.taier.develop.controller.develop;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.develop.mapstruct.vo.FileMapstructTransfer;
import com.dtstack.taier.develop.service.schedule.JobHistoryService;
import com.dtstack.taier.develop.vo.datasource.CheckPointListVO;
import com.dtstack.taier.develop.vo.schedule.FileInfoVO;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.pojo.FileResult;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
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
    private DatasourceOperator datasourceOperator;

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
            throw new TaierDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        String applicationId = checkPointVO.getApplicationId();
        String engineId = jobHistoryService.getEngineIdByApplicationId(applicationId);
        if (StringUtils.isBlank(engineId)) {
            return R.ok(new ArrayList<>(0));
        }
        JSONObject configByKey = clusterService.getConfigByKey(scheduleJob.getTenantId(), EComponentType.FLINK.getConfName(), null);
        JSONObject deployConfig = configByKey.getJSONObject(EDeployMode.PERJOB.getMode());
        String pointPathDir;
        if (checkPointVO.isGetSavePointPath()) {
            String prefixId = engineId.substring(0, 6);
            pointPathDir = deployConfig.getString(ConfigConstant.SAVE_POINTS_DIR) + File.separator + "savepoint-" + prefixId + "-*";
        } else {
            pointPathDir = deployConfig.getString(ConfigConstant.CHECK_POINTS_DIR) + File.separator + engineId;
        }
        if (StringUtils.isBlank(pointPathDir)) {
            throw new TaierDefineException(ErrorCode.CONFIG_ERROR);
        }
        Long clusterId = clusterService.getClusterIdByTenantId(scheduleJob.getTenantId());
        JSONObject pluginInfo = componentService.wrapperConfig(clusterId, EComponentType.HDFS.getTypeCode(), null, null);
        String typeName = componentService.buildHdfsTypeName(scheduleJob.getTenantId(),clusterId);
        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, typeName);
        List<FileResult> fileResults = datasourceOperator.listFiles(pluginInfo, scheduleJob.getTenantId(), pointPathDir, checkPointVO.isGetSavePointPath());
        fileResults = fileResults.stream().filter(file -> !file.getPath().endsWith("shared") && !file.getPath().endsWith("taskowned")).collect(Collectors.toList());
        return R.ok(FileMapstructTransfer.INSTANCE.toInfoVO(fileResults));
    }
}
