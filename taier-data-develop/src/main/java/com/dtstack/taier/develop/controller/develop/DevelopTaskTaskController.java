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

import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.BatchTaskTaskService;
import com.dtstack.taier.develop.web.develop.query.BatchTaskTaskAddOrUpdateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(value = "任务依赖管理", tags = {"任务依赖管理"})
@RestController
@RequestMapping(value = "/batchTaskTask")
public class DevelopTaskTaskController {

    @Autowired
    private BatchTaskTaskService batchTaskTaskService;

    @PostMapping(value = "addOrUpdateTaskTask")
    @ApiOperation("添加或者修改任务依赖")
    public R<Void> addOrUpdateTaskTask(@RequestBody BatchTaskTaskAddOrUpdateVO taskVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchTaskTaskService.addOrUpdateTaskTask(taskVO.getTaskId(), TaskMapstructTransfer.INSTANCE.
                        batchTaskTaskAddOrUpdateDependencyVOsToBatchTasks(taskVO.getDependencyVOS()));
                return null;
            }
        }.execute();
    }


}
