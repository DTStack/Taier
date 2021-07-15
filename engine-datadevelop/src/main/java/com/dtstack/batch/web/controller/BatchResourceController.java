package com.dtstack.batch.web.controller;

import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.mapstruct.vo.BatchResourceMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.BatchResourceService;
import com.dtstack.batch.vo.BatchResourceVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.resource.vo.query.BatchResourceAddWithUrlVO;
import com.dtstack.batch.web.resource.vo.query.BatchResourceBaseVO;
import com.dtstack.batch.web.resource.vo.query.BatchResourcePageQueryVO;
import com.dtstack.batch.web.resource.vo.query.BatchResourceRenameResourceVO;
import com.dtstack.batch.web.resource.vo.result.BatchGetResourceByIdResultVO;
import com.dtstack.batch.web.resource.vo.result.BatchGetResourcesResultVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.exception.biz.BizException;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "资源管理", tags = {"资源管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchResource")
public class BatchResourceController {

    @Autowired
    private BatchResourceService batchResourceService;

    @ApiOperation(value = "添加资源路径")
    @PostMapping(value = "addResourceWithUrl")
    public R<Long> addResourceWithUrl(@RequestBody(required = false) BatchResourceAddWithUrlVO vo) {
        return new APITemplate<Long>() {
            @Override
            protected Long process() throws BizException {
                return batchResourceService.addResourceWithUrl(vo.getId(), vo.getResourceName(), vo.getOriginFileName(), vo.getUrl(), vo.getResourceDesc(), vo.getResourceType(), vo.getNodePid(), vo.getUserId(), vo.getTenantId(), vo.getProjectId());
            }
        }.execute();
    }

    @ApiOperation(value = "资源分页查询")
    @PostMapping(value = "pageQuery")
    public R<PageResult<List<BatchGetResourceByIdResultVO>>> pageQuery(@RequestBody BatchResourcePageQueryVO vo) {
        return new APITemplate<PageResult<List<BatchGetResourceByIdResultVO>>>() {
            @Override
            protected PageResult<List<BatchGetResourceByIdResultVO>> process() throws BizException {
                PageResult<List<BatchResourceVO>> listPageResult = batchResourceService.pageQuery(BatchResourceMapstructTransfer.INSTANCE.resourceVOToResourceDTO(vo));
                return BatchResourceMapstructTransfer.INSTANCE.pageResultToBatchGetResourceByIdResultVOPageResult(listPageResult);
            }
        }.execute();
    }

    @ApiOperation(value = "修改资源名称", response = BatchResource.class)
    @PostMapping(value = "renameResource")
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
    public R<BatchGetResourcesResultVO> renameResource(@RequestBody(required = false) BatchResourceRenameResourceVO vo) {
        return new APITemplate<BatchGetResourcesResultVO>() {
            @Override
            protected BatchGetResourcesResultVO process() throws BizException {
                BatchResource batchResource = batchResourceService.renameResource(vo.getUserId(), vo.getResourceId(), vo.getName());
                return BatchResourceMapstructTransfer.INSTANCE.batchResourceToBatchGetResourcesResultVO(batchResource);
            }
        }.execute();
    }

    @ApiOperation(value = "获取资源详情", response = BatchResourceVO.class)
    @PostMapping(value = "getResourceById")
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
    public R<BatchGetResourceByIdResultVO> getResourceById(@RequestBody BatchResourceBaseVO batchResourceBaseVO) {
        return new APITemplate<BatchGetResourceByIdResultVO>() {
            @Override
            protected BatchGetResourceByIdResultVO process() throws BizException {
                BatchResourceVO resourceById = batchResourceService.getResourceById(batchResourceBaseVO.getResourceId());
                return BatchResourceMapstructTransfer.INSTANCE.batchResourceVOToBatchGetResourceByIdResultVO(resourceById);
            }
        }.execute();
    }

    @ApiOperation(value = "删除资源")
    @PostMapping(value = "deleteResource")
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
    public R<Long> deleteResource(@RequestBody(required = false) BatchResourceBaseVO batchResourceBaseVO) {
        return new APITemplate<Long>() {
            @Override
            protected Long process() throws BizException {
                return batchResourceService.deleteResource(batchResourceBaseVO.getResourceId(), batchResourceBaseVO.getProjectId(), batchResourceBaseVO.getDtuicTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "获取资源列表")
    @PostMapping(value = "getResources")
    @Security(code = AuthCode.DATADEVELOP_BATCH_RESOURCEMANAGER)
    public R<List<BatchGetResourcesResultVO>> getResources(@RequestBody BatchResourceBaseVO batchResourceBaseVO) {
        return new APITemplate<List<BatchGetResourcesResultVO>>() {
            @Override
            protected List<BatchGetResourcesResultVO> process() throws BizException {
                List<BatchResource> resources = batchResourceService.getResources(batchResourceBaseVO.getProjectId());
                return BatchResourceMapstructTransfer.INSTANCE.batchResourceListToBatchGetResourcesResultVOList(resources);
            }
        }.execute();
    }
}
