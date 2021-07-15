package com.dtstack.batch.web.controller;

import com.dtstack.batch.domain.BatchDataCatalogue;
import com.dtstack.batch.mapstruct.vo.BatchCommonMapstructTransfer;
import com.dtstack.batch.mapstruct.vo.BatchDataCatalogueMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.table.impl.BatchDataCatalogueService;
import com.dtstack.batch.web.common.TreeNodeResultVO;
import com.dtstack.batch.web.table.vo.query.BatchDataCatalogueInfoVO;
import com.dtstack.batch.web.table.vo.query.BatchDataCatalogueVO;
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

@Api(value = "数据类目管理", tags = {"数据类目管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchDataCatalogue")
public class BatchDataCatalogueController {

    @Autowired
    private BatchDataCatalogueService batchDataCatalogueService;

    @PostMapping(value = "getCatalogue")
    @ApiOperation("获取租户下的类目")
    public R<TreeNodeResultVO> getCatalogue(@RequestBody(required = false) BatchDataCatalogueInfoVO vo) {
        return new APITemplate<TreeNodeResultVO>() {
            @Override
            protected TreeNodeResultVO process() {
                return BatchCommonMapstructTransfer.INSTANCE.treeNodeToResultVO(batchDataCatalogueService.getCatalogue(vo.getTenantId(),
                        vo.getIsGetFile()));
            }
        }.execute();
    }

    @PostMapping(value = "addCatalogue")
    @ApiOperation("添加类目")
    @Security(code = AuthCode.DATAMANAGER_CATALOGUE_EDIT)
    public R<TreeNodeResultVO> addCatalogue(@RequestBody BatchDataCatalogueVO batchDataCatalogueVO) {
        return new APITemplate<TreeNodeResultVO>() {
            @Override
            protected TreeNodeResultVO process() {
                BatchDataCatalogue dataCatalogue = BatchDataCatalogueMapstructTransfer.INSTANCE.BatchDataCatalogueVOToBatchDataCatalogue(batchDataCatalogueVO);
                return BatchCommonMapstructTransfer.INSTANCE.treeNodeToResultVO(batchDataCatalogueService.addCatalogue(dataCatalogue,
                        batchDataCatalogueVO.getUserId()));
            }
        }.execute();
    }

    @PostMapping(value = "deleteCatalogue")
    @ApiOperation("删除类目")
    @Security(code = AuthCode.DATAMANAGER_CATALOGUE_EDIT)
    public R<Void> deleteCatalogue(@RequestBody BatchDataCatalogueInfoVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchDataCatalogueService.deleteCatalogue(vo.getId());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "updateCatalogue")
    @ApiOperation("重命名目录名类目")
    @Security(code = AuthCode.DATAMANAGER_CATALOGUE_EDIT)
    public R<Void> updateCatalogue(@RequestBody BatchDataCatalogueInfoVO catalogueVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchDataCatalogueService.updateCatalogue(catalogueVO.getId(), catalogueVO.getNodeName(),
                        catalogueVO.getTenantId());
                return null;
            }
        }.execute();
    }

}
