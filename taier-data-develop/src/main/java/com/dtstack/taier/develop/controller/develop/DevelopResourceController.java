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

import com.dtstack.taier.common.annotation.FileUpload;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.dto.devlop.BatchResourceVO;
import com.dtstack.taier.develop.dto.devlop.CatalogueVO;
import com.dtstack.taier.develop.mapstruct.vo.BatchCatalogueMapstructTransfer;
import com.dtstack.taier.develop.mapstruct.vo.BatchResourceMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.DevelopResourceService;
import com.dtstack.taier.develop.vo.develop.query.BatchResourceAddVO;
import com.dtstack.taier.develop.vo.develop.query.BatchResourceBaseVO;
import com.dtstack.taier.develop.vo.develop.result.BatchCatalogueResultVO;
import com.dtstack.taier.develop.vo.develop.result.BatchGetResourceByIdResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "资源管理", tags = {"资源管理"})
@RestController
@RequestMapping(value = "/batchResource")
public class DevelopResourceController {

    @Autowired
    private DevelopResourceService batchResourceService;

    @ApiOperation(value = "添加资源")
    @PostMapping(value = "addResource")
    @FileUpload
    public R<BatchCatalogueResultVO> addResource(BatchResourceAddVO batchResourceAddVO, MultipartFile file) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() throws RdosDefineException {
                CatalogueVO catalogue = batchResourceService.addResource(BatchResourceMapstructTransfer.INSTANCE.resourceVOToResourceAddDTO(batchResourceAddVO));
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @ApiOperation(value = "替换资源")
    @PostMapping(value = "replaceResource")
    @FileUpload
    public R<Void> replaceResource(BatchResourceAddVO batchResourceAddVO, MultipartFile file) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() throws RdosDefineException {
                batchResourceService.replaceResource(BatchResourceMapstructTransfer.INSTANCE.resourceVOToResourceAddDTO(batchResourceAddVO));
                return null;
            }
        }.execute();
    }


    @ApiOperation(value = "获取资源详情", response = BatchResourceVO.class)
    @PostMapping(value = "getResourceById")
    public R<BatchGetResourceByIdResultVO> getResourceById(@RequestBody BatchResourceBaseVO batchResourceBaseVO) {
        return new APITemplate<BatchGetResourceByIdResultVO>() {
            @Override
            protected BatchGetResourceByIdResultVO process() throws RdosDefineException {
                BatchResourceVO resourceById = batchResourceService.getResourceById(batchResourceBaseVO.getResourceId());
                return BatchResourceMapstructTransfer.INSTANCE.batchResourceVOToBatchGetResourceByIdResultVO(resourceById);
            }
        }.execute();
    }

    @ApiOperation(value = "删除资源")
    @PostMapping(value = "deleteResource")
    public R<Long> deleteResource(@RequestBody(required = false) BatchResourceBaseVO batchResourceBaseVO) {
        return new APITemplate<Long>() {
            @Override
            protected Long process() throws RdosDefineException {
                return batchResourceService.deleteResource(batchResourceBaseVO.getTenantId(), batchResourceBaseVO.getResourceId());
            }
        }.execute();
    }

}
