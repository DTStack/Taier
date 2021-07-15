package com.dtstack.batch.web.controller;

import com.dtstack.batch.mapstruct.vo.FunctionMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.BatchFunctionService;
import com.dtstack.batch.vo.TaskCatalogueVO;
import com.dtstack.batch.web.function.vo.query.*;
import com.dtstack.batch.web.function.vo.result.BatchFunctionAddResultVO;
import com.dtstack.batch.web.function.vo.result.BatchFunctionQueryResultVO;
import com.dtstack.batch.web.pager.PageResult;
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

import java.util.List;

@Api(value = "函数管理", tags = {"函数管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchFunction")
public class BatchFunctionController {

    @Autowired
    private BatchFunctionService batchFunctionService;

    @PostMapping(value = "getFunction")
    @ApiOperation(value = "获取函数")
    @Security(code = AuthCode.DATADEVELOP_BATCH_FUNCTIONMANAGER)
    public R<BatchFunctionQueryResultVO> getFunction(@RequestBody BatchFunctionBaseVO vo) {
        return new APITemplate<BatchFunctionQueryResultVO>() {
            @Override
            protected BatchFunctionQueryResultVO process() {
                com.dtstack.batch.vo.BatchFunctionVO function = batchFunctionService.getFunction(vo.getFunctionId());
                return FunctionMapstructTransfer.INSTANCE.newFunctionToFunctionResultVo(function);
            }
        }.execute();
    }

    @PostMapping(value = "getEngineIdentity")
    @ApiOperation(value = "获取引擎标识")
    @Security(code = AuthCode.DATADEVELOP_BATCH_FUNCTIONMANAGER)
    public R<String> getEngineIdentity(@RequestBody BatchFunctionIdentityVO vo) {
        return new APITemplate<String>() {
            @Override
            protected String process() {
                return batchFunctionService.getEngineIdentity(vo.getProjectId(), vo.getEngineType());
            }
        }.execute();
    }

    @PostMapping(value = "addGpProcedureOrFunction")
    @ApiOperation(value = "GreenPlum新增存储过程/函数")
    @Security(code = AuthCode.DATADEVELOP_BATCH_FUNCTIONMANAGER)
    public R<BatchFunctionAddResultVO> addGpProcedureOrFunction(@RequestBody BatchFunctionVO vo) {
        return new APITemplate<BatchFunctionAddResultVO>() {
            @Override
            protected BatchFunctionAddResultVO process() {
                TaskCatalogueVO result = batchFunctionService.addGpProcedureOrFunction(FunctionMapstructTransfer.INSTANCE.newFunctionVoToFunctionVo(vo), vo.getDtuicTenantId());
                return FunctionMapstructTransfer.INSTANCE.newTaskCatalogueVoToFunctionAddResultVo(result);
            }
        }.execute();
    }

    @PostMapping(value = "addOrUpdateFunction")
    @ApiOperation(value = "添加函数")
    @Security(code = AuthCode.DATADEVELOP_BATCH_FUNCTIONMANAGER)
    public R<BatchFunctionAddResultVO> addOrUpdateFunction(@RequestBody BatchFunctionAddVO vo) {
        return new APITemplate<BatchFunctionAddResultVO>() {
            @Override
            protected BatchFunctionAddResultVO process() {
                TaskCatalogueVO result = batchFunctionService.addOrUpdateFunction(FunctionMapstructTransfer.INSTANCE.newFunctionAddVoToFunctionVo(vo), vo.getResourceIds(), vo.getDtuicTenantId());
                return FunctionMapstructTransfer.INSTANCE.newTaskCatalogueVoToFunctionAddResultVo(result);
            }
        }.execute();
    }

    @PostMapping(value = "moveFunction")
    @ApiOperation(value = "移动函数")
    @Security(code = AuthCode.DATADEVELOP_BATCH_FUNCTIONMANAGER)
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
    @Security(code = AuthCode.DATADEVELOP_BATCH_FUNCTIONMANAGER)
    public R<Void> deleteFunction(@RequestBody BatchFunctionDeleteVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchFunctionService.deleteFunction(vo.getUserId(), vo.getProjectId(), vo.getFunctionId(), vo.getDtuicTenantId());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "getAllFunctionName")
    @ApiOperation(value = "获取所有函数名")
    @Security(code = AuthCode.DATADEVELOP_BATCH_FUNCTIONMANAGER)
    public R<List<String>> getAllFunctionName(@RequestBody BatchFunctionNameVO vo) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return batchFunctionService.getAllFunctionName(vo.getTenantId(), vo.getProjectId(), vo.getTaskType());
            }
        }.execute();
    }

    @PostMapping(value = "pageQuery")
    @ApiOperation(value = "自定义函数分页查询")
    @Security(code = AuthCode.DATADEVELOP_BATCH_FUNCTIONMANAGER)
    public R<PageResult<List<BatchFunctionQueryResultVO>>> pageQuery(@RequestBody BatchFunctionQueryVO vo) {
        return new APITemplate<PageResult<List<BatchFunctionQueryResultVO>>>() {
            @Override
            protected PageResult<List<BatchFunctionQueryResultVO>> process() {
                PageResult<List<com.dtstack.batch.vo.BatchFunctionVO>> result = batchFunctionService.pageQuery(FunctionMapstructTransfer.INSTANCE.newFunctionQueryVoToDTO(vo));
                return FunctionMapstructTransfer.INSTANCE.newFunctionVoToFunctionQueryResultVo(result);
            }
        }.execute();
    }

    @PostMapping(value = "sparkUdfCleanAndPublish")
    @ApiOperation(value = "Spark UDF 脏数据处理")
    public R<Void> sparkUdfCleanAndPublish() {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
              batchFunctionService.sparkUdfCleanAndPublish();
              return null;
            }
        }.execute();
    }
}
