package com.dtstack.batch.controller;

import com.dtstack.batch.mapstruct.vo.BatchCatalogueMapstructTransfer;
import com.dtstack.batch.mapstruct.vo.BatchResourceMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.BatchResourceService;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.web.catalogue.vo.result.BatchCatalogueResultVO;
import com.dtstack.batch.web.resource.vo.query.BatchResourceAddVO;
import com.dtstack.engine.common.annotation.FileUpload;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.exception.biz.BizException;
import dt.insight.plat.lang.web.R;
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
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
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
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
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
