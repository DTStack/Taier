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
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.dto.devlop.DevelopResourceVO;
import com.dtstack.taier.develop.dto.devlop.CatalogueVO;
import com.dtstack.taier.develop.mapstruct.vo.DevelopCatalogueMapstructTransfer;
import com.dtstack.taier.develop.mapstruct.vo.DevelopResourceMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.DevelopResourceService;
import com.dtstack.taier.develop.vo.develop.query.DevelopResourceAddVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopResourceBaseVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopCatalogueResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopGetResourceByIdResultVO;
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
@RequestMapping(value = "/resource")
public class DevelopResourceController {

    @Autowired
    private DevelopResourceService DevelopResourceService;

    @ApiOperation(value = "添加资源")
    @PostMapping(value = "addResource")
    @FileUpload
    public R<DevelopCatalogueResultVO> addResource(DevelopResourceAddVO DevelopResourceAddVO, MultipartFile file) {
        return new APITemplate<DevelopCatalogueResultVO>() {
            @Override
            protected DevelopCatalogueResultVO process() throws TaierDefineException {
                CatalogueVO catalogue = DevelopResourceService.addResource(DevelopResourceMapstructTransfer.INSTANCE.resourceVOToResourceAddDTO(DevelopResourceAddVO));
                return DevelopCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @ApiOperation(value = "替换资源")
    @PostMapping(value = "replaceResource")
    @FileUpload
    public R<Void> replaceResource(DevelopResourceAddVO DevelopResourceAddVO, MultipartFile file) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() throws TaierDefineException {
                DevelopResourceService.replaceResource(DevelopResourceMapstructTransfer.INSTANCE.resourceVOToResourceAddDTO(DevelopResourceAddVO));
                return null;
            }
        }.execute();
    }


    @ApiOperation(value = "获取资源详情", response = DevelopResourceVO.class)
    @PostMapping(value = "getResourceById")
    public R<DevelopGetResourceByIdResultVO> getResourceById(@RequestBody DevelopResourceBaseVO DevelopResourceBaseVO) {
        return new APITemplate<DevelopGetResourceByIdResultVO>() {
            @Override
            protected DevelopGetResourceByIdResultVO process() throws TaierDefineException {
                DevelopResourceVO resourceById = DevelopResourceService.getResourceById(DevelopResourceBaseVO.getResourceId());
                return DevelopResourceMapstructTransfer.INSTANCE.DevelopResourceVOToDevelopGetResourceByIdResultVO(resourceById);
            }
        }.execute();
    }

    @ApiOperation(value = "删除资源")
    @PostMapping(value = "deleteResource")
    public R<Long> deleteResource(@RequestBody(required = false) DevelopResourceBaseVO DevelopResourceBaseVO) {
        return new APITemplate<Long>() {
            @Override
            protected Long process() throws TaierDefineException {
                return DevelopResourceService.deleteResource(DevelopResourceBaseVO.getTenantId(), DevelopResourceBaseVO.getResourceId());
            }
        }.execute();
    }

}
