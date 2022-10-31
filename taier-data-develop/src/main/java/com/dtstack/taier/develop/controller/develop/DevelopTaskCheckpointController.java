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
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.dto.devlop.CheckPointTimeRangeResultDTO;
import com.dtstack.taier.develop.dto.devlop.StreamTaskCheckpointVO;
import com.dtstack.taier.develop.mapstruct.vo.TaskCheckpointTransfer;
import com.dtstack.taier.develop.service.develop.impl.StreamTaskCheckpointService;
import com.dtstack.taier.develop.vo.develop.query.GetCheckPointTimeRangeVO;
import com.dtstack.taier.develop.vo.develop.query.GetCheckpointListVO;
import com.dtstack.taier.develop.vo.develop.query.GetSavePointVO;
import com.dtstack.taier.develop.vo.develop.query.TaskCheckPointQueryVO;
import com.dtstack.taier.develop.vo.develop.result.GetCheckPointTimeRangeResultVO;
import com.dtstack.taier.develop.vo.develop.result.GetCheckpointListResultVO;
import com.dtstack.taier.develop.vo.develop.result.GetSavePointResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:07 2020/10/12
 * @Description：任务快照管理
 */
@Api(value = "任务快照管理", tags = {"任务快照管理"})
@RestController
@RequestMapping(value =  "/streamTaskCheckpoint")
public class DevelopTaskCheckpointController {
    @Autowired
    StreamTaskCheckpointService taskCheckpointService;

    @ApiOperation("获取任务的 CheckPoint 可选时间范围")
    @PostMapping(value = "getCheckpointTimeRange")
    public R<GetCheckPointTimeRangeResultVO> getCheckpointTimeRange(@RequestBody @Validated GetCheckPointTimeRangeVO queryVO) {
        return new APITemplate<GetCheckPointTimeRangeResultVO>() {
            @Override
            protected GetCheckPointTimeRangeResultVO process() {
                CheckPointTimeRangeResultDTO checkPointTimeRangeResultDto = taskCheckpointService.getCheckpointTimeRange(queryVO.getJobId());
                return TaskCheckpointTransfer.INSTANCE.getCheckPointTimeRangeResult(checkPointTimeRangeResultDto);
            }
        }.execute();
    }

    @ApiOperation("获取任务所有的 CheckPoint 信息")
    @PostMapping(value = "pageQuery")
    public R<JSONObject> pageQuery(@RequestBody @Validated TaskCheckPointQueryVO queryVO) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return taskCheckpointService.pageQuery(queryVO.getJobId(), queryVO.getStartTime(), queryVO.getEndTime());
            }
        }.execute();
    }

    @ApiOperation("获取指定范围内的可选 CheckPoint 的列表")
    @PostMapping(value = "getCheckpointList")
    public R<List<StreamTaskCheckpointVO>> getCheckpointList(@RequestBody @Validated GetCheckpointListVO queryVO) {
        return new APITemplate<List<StreamTaskCheckpointVO>>() {
            @Override
            protected List<StreamTaskCheckpointVO> process() {
                List<StreamTaskCheckpointVO> checkpointList = taskCheckpointService.getCheckpointListVo(queryVO.getJobId(), queryVO.getStartTime(), queryVO.getEndTime());
                return checkpointList;
            }
        }.execute();
    }

    @ApiOperation(value = "获取任务保存的CheckPoint")
    @PostMapping(value = "getSavePoint")
    public R<GetSavePointResultVO> getSavePoint(@RequestBody @Validated GetSavePointVO queryVO) {
        return new APITemplate<GetSavePointResultVO>() {
            @Override
            protected GetSavePointResultVO process() {
                StreamTaskCheckpointVO savePoint = taskCheckpointService.getSavePoint(queryVO.getJobId());
                return TaskCheckpointTransfer.INSTANCE.getSavePointResult(savePoint);
            }
        }.execute();
    }
}
