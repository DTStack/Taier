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
import com.dtstack.taier.develop.dto.devlop.CatalogueVO;
import com.dtstack.taier.develop.mapstruct.vo.DevelopCatalogueMapstructTransfer;
import com.dtstack.taier.develop.mapstruct.vo.DevelopCatalogueMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.DevelopCatalogueService;
import com.dtstack.taier.develop.vo.develop.query.DevelopCatalogueAddVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopCatalogueGetVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopCatalogueUpdateVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopCatalogueResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "目录管理", tags = {"目录管理"})
@RestController
@RequestMapping(value = "/batchCatalogue")
public class DevelopCatalogueController {

    @Autowired
    private DevelopCatalogueService batchCatalogueService;

    @PostMapping(value = "addCatalogue")
    @ApiOperation(value = "新增目录")
    public R<DevelopCatalogueResultVO> addCatalogue(@RequestBody DevelopCatalogueAddVO vo) {
        return new APITemplate<DevelopCatalogueResultVO>() {
            @Override
            protected DevelopCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.addCatalogue(DevelopCatalogueMapstructTransfer.INSTANCE.newCatalogueAddVoToCatalogueVo(vo));
                return DevelopCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @PostMapping(value = "getCatalogue")
    @ApiOperation(value = "获取目录")
    public R<DevelopCatalogueResultVO> getCatalogue(@RequestBody DevelopCatalogueGetVO vo) {
        return new APITemplate<DevelopCatalogueResultVO>() {
            @Override
            protected DevelopCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.getCatalogue(vo.getIsGetFile(), vo.getNodePid(), vo.getCatalogueType(), vo.getTenantId());
                return DevelopCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @PostMapping(value = "updateCatalogue")
    @ApiOperation(value = "更新目录")
    public R<Void> updateCatalogue(@RequestBody DevelopCatalogueUpdateVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchCatalogueService.updateCatalogue(DevelopCatalogueMapstructTransfer.INSTANCE.newCatalogueUpdateVoToCatalogueVo(vo));
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "deleteCatalogue")
    @ApiOperation(value = "删除目录")
    public R<Void> deleteCatalogue(@RequestBody DevelopCatalogueAddVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchCatalogueService.deleteCatalogue(DevelopCatalogueMapstructTransfer.INSTANCE.newCatalogueAddVoToCatalogueVo(vo));
                return null;
            }
        }.execute();
    }

}
