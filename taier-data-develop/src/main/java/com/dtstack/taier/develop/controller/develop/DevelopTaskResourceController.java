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
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskResourceService;
import com.dtstack.taier.develop.vo.develop.query.BatchTaskResourceGetResourcesVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopResourceResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "资源任务管理", tags = {"资源任务管理"})
@RestController
@RequestMapping(value = "/batchTaskResource")
public class DevelopTaskResourceController {

    @Autowired
    private DevelopTaskResourceService resourceService;

    @PostMapping(value = "getResources")
    @ApiOperation("获得 资源-任务 列表")
    public R<List<DevelopResourceResultVO>> getResources(@RequestBody BatchTaskResourceGetResourcesVO vo) {
        return new APITemplate<List<DevelopResourceResultVO> >() {
            @Override
            protected List<DevelopResourceResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.DevelopResourceListToDevelopResourceResultVOList(resourceService.getResources(vo.getTaskId(), vo.getType()));
            }
        }.execute();
    }
}
