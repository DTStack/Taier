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

package com.dtstack.taiga.develop.controller.batch;

import com.dtstack.taiga.common.lang.coc.APITemplate;
import com.dtstack.taiga.common.lang.web.R;
import com.dtstack.taiga.develop.mapstruct.vo.FunctionMapstructTransfer;
import com.dtstack.taiga.develop.service.impl.BatchFunctionService;
import com.dtstack.taiga.develop.vo.BatchFunctionVO;
import com.dtstack.taiga.develop.vo.TaskCatalogueVO;
import com.dtstack.taiga.develop.web.function.vo.query.*;
import com.dtstack.taiga.develop.web.function.vo.result.BatchFunctionAddResultVO;
import com.dtstack.taiga.develop.web.function.vo.result.BatchFunctionQueryResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "函数管理", tags = {"函数管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchFunction")
public class BatchFunctionController {

    @Autowired
    private BatchFunctionService batchFunctionService;

    @PostMapping(value = "getFunction")
    @ApiOperation(value = "获取函数")
    public R<BatchFunctionQueryResultVO> getFunction(@RequestBody BatchFunctionBaseVO vo) {
        return new APITemplate<BatchFunctionQueryResultVO>() {
            @Override
            protected BatchFunctionQueryResultVO process() {
                BatchFunctionVO function = batchFunctionService.getFunction(vo.getFunctionId());
                return FunctionMapstructTransfer.INSTANCE.newFunctionToFunctionResultVo(function);
            }
        }.execute();
    }

    @PostMapping(value = "addOrUpdateFunction")
    @ApiOperation(value = "添加函数 or 修改函数")
    public R<BatchFunctionAddResultVO> addOrUpdateFunction(@RequestBody BatchFunctionAddVO vo) {
        return new APITemplate<BatchFunctionAddResultVO>() {
            @Override
            protected BatchFunctionAddResultVO process() {
                TaskCatalogueVO result = batchFunctionService.addOrUpdateFunction(FunctionMapstructTransfer.INSTANCE.newFunctionAddVoToFunctionVo(vo), vo.getResourceId(), vo.getTenantId());
                return FunctionMapstructTransfer.INSTANCE.newTaskCatalogueVoToFunctionAddResultVo(result);
            }
        }.execute();
    }

    @PostMapping(value = "moveFunction")
    @ApiOperation(value = "移动函数")
    public R<Void> moveFunction(@RequestBody BatchFunctionMoveVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchFunctionService.moveFunction(vo.getUserId(), vo.getFunctionId(), vo.getNodePid());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "deleteFunction")
    @ApiOperation(value = "删除函数")
    public R<Void> deleteFunction(@RequestBody BatchFunctionDeleteVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchFunctionService.deleteFunction(vo.getUserId(), vo.getFunctionId());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "getAllFunctionName")
    @ApiOperation(value = "获取所有函数名")
    public R<List<String>> getAllFunctionName(@RequestBody BatchFunctionNameVO vo) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return batchFunctionService.getAllFunctionName(vo.getTenantId(), vo.getTaskType());
            }
        }.execute();
    }


}
