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
import com.dtstack.taier.develop.service.develop.impl.DevelopTenantComponentService;
import com.dtstack.taier.develop.vo.develop.query.DevelopTenantComponentSchemaSelectVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTenantComponentSelectVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTenantComponentUpdateVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTenantComponentResultVO;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Api(value = "租户组件任务信息管理", tags = {"租户组件任务信息管理"})
@RestController
@RequestMapping(value = "/tenantComponent")
public class DevelopTenantComponentController {

    @Autowired
    private DevelopTenantComponentService developTenantComponentService;

    @PostMapping(value = "selectTenantComponentList")
    @ApiOperation(value = "获取当前租户配置的任务组件运行信息")
    public R<List<DevelopTenantComponentResultVO>> getByTenantAndTaskType(@RequestBody DevelopTenantComponentSelectVO selectVO) {
        return new APITemplate<List<DevelopTenantComponentResultVO>>() {
            @Override
            protected List<DevelopTenantComponentResultVO> process() {
                return developTenantComponentService.selectTenantComponentList(selectVO.getTenantId());
            }
        }.execute();
    }

    @PostMapping(value = "saveTenantComponentInfo")
    @ApiOperation(value = "保存组件运行schema信息")
    public R<Void> saveTenantComponentInfo(@RequestBody DevelopTenantComponentUpdateVO updateVO) {
        return new APITemplate<Void>() {
            @Override
            protected void checkParams() {
                Preconditions.checkNotNull(updateVO.getTaskType(), "parameters of taskType not be null.");
                Preconditions.checkNotNull(updateVO.getSchema(), "parameters of schema not be null.");
            }
            @Override
            protected Void process() {
                developTenantComponentService.saveTenantComponentInfo(updateVO.getTenantId(), updateVO.getTaskType(), updateVO.getSchema());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "getByTenantAndTaskType")
    @ApiOperation(value = "获取任务类型可配置的shema")
    public R<List<String>> getByTenantAndTaskType(@RequestBody DevelopTenantComponentSchemaSelectVO selectVO) {
        return new APITemplate<List<String>>() {
            @Override
            protected void checkParams() {
                Preconditions.checkNotNull(selectVO.getTaskType(), "parameters of taskType not be null.");
            }
            @Override
            protected List<String> process() {
                return developTenantComponentService.getAllSchemaByTenantAndTaskType(selectVO.getTenantId(), selectVO.getTaskType());
            }
        }.execute();
    }

}
