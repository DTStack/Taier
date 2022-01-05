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

package com.dtstack.batch.controller.batch;

import com.dtstack.batch.mapstruct.vo.BatchCatalogueMapstructTransfer;
import com.dtstack.batch.service.impl.BatchCatalogueService;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.web.catalogue.vo.query.BatchCatalogueAddVO;
import com.dtstack.batch.web.catalogue.vo.query.BatchCatalogueGetVO;
import com.dtstack.batch.web.catalogue.vo.query.BatchCatalogueUpdateVO;
import com.dtstack.batch.web.catalogue.vo.result.BatchCatalogueResultVO;
import com.dtstack.engine.common.lang.coc.APITemplate;
import com.dtstack.engine.common.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "目录管理", tags = {"目录管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchCatalogue")
public class BatchCatalogueController {

    @Autowired
    private BatchCatalogueService batchCatalogueService;

    @PostMapping(value = "addCatalogue")
    @ApiOperation(value = "新增目录")
    public R<BatchCatalogueResultVO> addCatalogue(@RequestBody BatchCatalogueAddVO vo) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.addCatalogue(BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueAddVoToCatalogueVo(vo));
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @PostMapping(value = "getCatalogue")
    @ApiOperation(value = "获取目录")
    public R<BatchCatalogueResultVO> getCatalogue(@RequestBody BatchCatalogueGetVO vo) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.getCatalogue(vo.getIsGetFile(), vo.getNodePid(), vo.getCatalogueType(), vo.getUserId(), vo.getTenantId(), vo.getTaskType(), vo.getParentId());
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @PostMapping(value = "updateCatalogue")
    @ApiOperation(value = "更新目录")
    public R<Void> updateCatalogue(@RequestBody BatchCatalogueUpdateVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchCatalogueService.updateCatalogue(BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueUpdateVoToCatalogueVo(vo), vo.getUserId());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "deleteCatalogue")
    @ApiOperation(value = "删除目录")
    public R<Void> deleteCatalogue(@RequestBody BatchCatalogueAddVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchCatalogueService.deleteCatalogue(BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueAddVoToCatalogueVo(vo));
                return null;
            }
        }.execute();
    }

}
