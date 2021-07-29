package com.dtstack.batch.web.controller;

import com.dtstack.batch.mapstruct.vo.BatchCatalogueMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.BatchCatalogueService;
import com.dtstack.batch.vo.CatalogueVO;
import com.dtstack.batch.web.catalogue.vo.query.*;
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

@Api(value = "目录管理", tags = {"目录管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchCatalogue")
public class BatchCatalogueController {

    @Autowired
    private BatchCatalogueService batchCatalogueService;

    @PostMapping(value = "addCatalogue")
    @ApiOperation(value = "新增目录")
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
    public R<BatchCatalogueResultVO> addCatalogue(@RequestBody BatchCatalogueAddVO vo) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.addCatalogue(BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueAddVoToCatalogueVo(vo), vo.getProjectId(), vo.getUserId());
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @PostMapping(value = "getLocation")
    @ApiOperation(value = "获取位置")
    public R<BatchCatalogueResultVO> getLocation(@RequestBody BatchCatalogueLocationVO vo) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.getLocation(vo.getProjectId(), vo.getUserId(), vo.getCatalogueType(),
                        vo.getId(), vo.getName(), vo.getTenantId());
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @PostMapping(value = "getCatalogue")
    @ApiOperation(value = "获取目录")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_QUERY)
    public R<BatchCatalogueResultVO> getCatalogue(@RequestBody BatchCatalogueGetVO vo) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.getCatalogue(vo.getAppointProjectId(), vo.getProjectId(), vo.getIsGetFile(),
                        vo.getNodePid(), vo.getCatalogueType(), vo.getUserId().intValue(), vo.getTenantId(), vo.getTaskType(), vo.getParentId());
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @PostMapping(value = "updateCatalogue")
    @ApiOperation(value = "更新目录")
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
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
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
    public R<Void> deleteCatalogue(@RequestBody BatchCatalogueAddVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchCatalogueService.deleteCatalogue(BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueAddVoToCatalogueVo(vo));
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "getProjectTableList")
    @ApiOperation(value = "获取项目下表列表")
    @Security(code = AuthCode.DATAMANAGER_TABLMANAGER_QUERY)
    public R<BatchCatalogueResultVO> getProjectTableList(@RequestBody BatchCatalogueProjectTableListVO vo) {
        return new APITemplate<BatchCatalogueResultVO>() {
            @Override
            protected BatchCatalogueResultVO process() {
                CatalogueVO catalogue = batchCatalogueService.getProjectTableList(vo.getTableName(), vo.getProjectIdentifier(), vo.getTaskType(), vo.getScriptType(), vo.getUserId(), vo.getIsRoot());
                return BatchCatalogueMapstructTransfer.INSTANCE.newCatalogueVoToCatalogueResultVo(catalogue);
            }
        }.execute();
    }

    @PostMapping(value = "createCataloguePath")
    @ApiOperation(value = "新增路径")
    public R<Long> createCataloguePath(@RequestBody BatchCataloguePathVO vo) {
        return new APITemplate<Long>() {
            @Override
            protected Long process() {
                return batchCatalogueService.createCataloguePath(vo.getNameList(), vo.getRootName(), vo.getProjectId(), vo.getUserId());
            }
        }.execute();
    }

}
