package com.dtstack.batch.controller;

import com.dtstack.batch.mapstruct.vo.BatchCatalogueMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.BatchCatalogueService;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.web.catalogue.vo.query.BatchCatalogueProjectCataVO;
import com.dtstack.batch.web.catalogue.vo.result.BatchCatalogueResultVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "common管理", tags = {"common管理"})
@RestController
@RequestMapping(value = "/api/rdos/common")
public class BatchCommonController {

    @Autowired
    private BatchCatalogueService batchCatalogueService;

    @PostMapping(value = "batchCatalogue/getProjectCatalogue")
    @ApiOperation(value = "获取项目目录")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_QUERY)
    public R<BatchCatalogueResultVO> getProjectCatalogue(@RequestBody BatchCatalogueProjectCataVO vo) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.getProjectCatalogue(vo.getTenantId(), vo.getIsGetFile(), vo.getUserId(), vo.getIsRoot(), vo.getIsAdmin());
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }
}
