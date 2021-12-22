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
import com.dtstack.batch.mapstruct.vo.BatchResourceMapstructTransfer;
import com.dtstack.batch.service.impl.BatchResourceService;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.web.catalogue.vo.result.BatchCatalogueResultVO;
import com.dtstack.batch.web.resource.vo.query.BatchResourceAddVO;
import com.dtstack.engine.common.annotation.FileUpload;
import com.dtstack.engine.common.exception.BizException;
import com.dtstack.engine.common.lang.coc.APITemplate;
import com.dtstack.engine.common.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Api(value = "上传管理", tags = {"上传管理"})
@RestController
@RequestMapping("/api/rdos/upload/batch")
public class BatchFileUploadController {

    @Autowired
    private BatchResourceService batchResourceService;


    @ApiOperation(value = "添加资源")
    @PostMapping(value = "batchResource/addResource")
    @FileUpload
    public R<BatchCatalogueResultVO> addResource(BatchResourceAddVO batchResourceAddVO, MultipartFile file) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() throws BizException {
                CatalogueVO catalogue = batchResourceService.addResource(BatchResourceMapstructTransfer.INSTANCE.resourceVOToResourceAddDTO(batchResourceAddVO));
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @ApiOperation(value = "替换资源")
    @PostMapping(value = "batchResource/replaceResource")
    @FileUpload
    public R<Void> replaceResource(BatchResourceAddVO batchResourceAddVO, MultipartFile file) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                batchResourceService.replaceResource(BatchResourceMapstructTransfer.INSTANCE.resourceVOToResourceAddDTO(batchResourceAddVO));
                return null;
            }
        }.execute();
    }

}
