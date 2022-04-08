package com.dtstack.taier.develop.controller.datasource;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.bo.datasource.DsInfoIdParam;
import com.dtstack.taier.develop.bo.datasource.DsKafkaDataParam;
import com.dtstack.taier.develop.bo.datasource.DsListParam;
import com.dtstack.taier.develop.bo.datasource.DsPollPreviewParam;
import com.dtstack.taier.develop.bo.datasource.DsTableListBySchemaParam;
import com.dtstack.taier.develop.bo.datasource.DsTypeListParam;
import com.dtstack.taier.develop.mapstruct.datasource.DsDetailTransfer;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.datasource.impl.DsTypeService;
import com.dtstack.taier.develop.utils.Asserts;
import com.dtstack.taier.develop.vo.datasource.DsDetailVO;
import com.dtstack.taier.develop.vo.datasource.DsInfoVO;
import com.dtstack.taier.develop.vo.datasource.DsListVO;
import com.dtstack.taier.develop.vo.datasource.DsTypeListVO;
import com.dtstack.taier.develop.vo.develop.query.KafkaTopicGetVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author 全阅
 * @Description: 数据源中心控制层
 * @Date: 2021/3/8 18:57
 */
@Api(tags = {"数据源中心-数据源管理"})
@RestController
@RequestMapping(value = "/dataSource/manager")
public class DataSourceController {

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private DsTypeService dsTypeService;

    @Autowired
    private DatasourceService datasourceService;

    @ApiOperation("数据源列表分页信息")
    @PostMapping("page")
    public R<PageResult<List<DsListVO>>> dsPage(@RequestBody DsListParam dsListParam) {

        return R.ok(dsInfoService.dsPage(dsListParam));
    }

    @ApiOperation("获取数据源基本详情")
    @PostMapping("detail")
    public R<DsDetailVO> dsDetail(@RequestBody DsInfoIdParam dsInfoIdParam) {
        return new APITemplate<DsDetailVO>() {
            @Override
            protected DsDetailVO process() throws RdosDefineException {
                Asserts.notNull(dsInfoIdParam.getDataInfoId(), "数据源Id不能为空");
                DsInfo dsInfo = dsInfoService.dsInfoDetail(dsInfoIdParam.getDataInfoId());
                return DsDetailTransfer.INSTANCE.toInfoVO(dsInfo);
            }
        }.execute();
    }

    @ApiOperation("删除一条数据源实例")
    @PostMapping("delete")
    public R<Boolean> deleteById(@RequestBody DsInfoIdParam dsInfoIdParam) {
        return new APITemplate<Boolean>() {
            @Override
            protected Boolean process() throws RdosDefineException {
                Asserts.notNull(dsInfoIdParam.getDataInfoId(), "数据源Id不能为空");
                return dsInfoService.delDsInfo(dsInfoIdParam.getDataInfoId());
            }
        }.execute();
    }

    @ApiOperation("数据源类型下拉列表")
    @PostMapping("type/list")
    public R<List<DsTypeListVO>> dsTypeList() {
        return R.ok(dsTypeService.dsTypeList());
    }


    @ApiOperation("根据租户id查询数据源列表")
    @GetMapping("queryByTenantId")
    public R<List<DsInfoVO>> queryByTenantId(@RequestParam("tenantId") Long tenantId) {
        return R.ok(dsInfoService.queryByTenantId(tenantId));
    }

    @ApiOperation("根据租户id查询数据源列表")
    @PostMapping("listDataSourceBaseInfo")
    public R<List<DsInfoVO>> listDataSourceBaseInfo(@RequestBody DsTypeListParam dsTypeListParam) {
        return R.ok(dsInfoService.listDataSourceBaseInfo(dsTypeListParam.getType(), dsTypeListParam.getTenantId()));
    }

    @PostMapping("listTablesBySchema")
    @ApiOperation(value = "获取指定Schema下的表")
    public R<List<String>> listTablesBySchema(@Validated @RequestBody DsTableListBySchemaParam dsTableListBySchemaParam) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return dsInfoService.listTablesBySchema(dsTableListBySchemaParam.getSourceId(), dsTableListBySchemaParam.getSchema(), dsTableListBySchemaParam.getSearchKey());
            }
        }.execute();
    }

    @PostMapping("pollPreview")
    @ApiOperation(value = "数据预览")
    public R<JSONObject> pollPreview(@Validated @RequestBody DsPollPreviewParam pollPreviewParam) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
               return dsInfoService.pollPreview(pollPreviewParam.getSourceId(), pollPreviewParam.getTableName(), pollPreviewParam.getSchema());
            }
        }.execute();
    }

    @PostMapping("getTopicData")
    @ApiOperation(value = "查询Topic数据")
    public R<List<String>> getTopicData(@Validated @RequestBody DsKafkaDataParam kafkaDataParam) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return dsInfoService.getTopicData(kafkaDataParam.getSourceId(), kafkaDataParam.getTopic(), kafkaDataParam.getPreviewModel());
            }
        }.execute();
    }


    @PostMapping(value = "getKafkaTopics")
    @ApiOperation(value = "获取表列表")
    public R<List<String>> getKafkaTopics(@RequestBody(required = false) KafkaTopicGetVO sourceVO) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() {
                return datasourceService.getKafkaTopics(sourceVO.getSourceId());
            }
        }.execute();
    }

}
