package com.dtstack.taier.develop.controller.datasource;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.bo.datasource.DsInfoIdParam;
import com.dtstack.taier.develop.bo.datasource.DsListParam;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.datasource.impl.DsTypeService;
import com.dtstack.taier.develop.utils.Asserts;
import com.dtstack.taier.develop.vo.datasource.DsDetailVO;
import com.dtstack.taier.develop.vo.datasource.DsInfoVO;
import com.dtstack.taier.develop.vo.datasource.DsListVO;
import com.dtstack.taier.develop.vo.datasource.DsTypeListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                return dsInfoService.dsInfoDetail(dsInfoIdParam.getDataInfoId());
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


}
