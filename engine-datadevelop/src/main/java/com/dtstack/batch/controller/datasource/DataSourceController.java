package com.dtstack.batch.controller.datasource;

import com.dtstack.batch.bo.datasource.DsInfoIdParam;
import com.dtstack.batch.bo.datasource.DsListParam;
import com.dtstack.batch.service.datasource.impl.DatasourceService;
import com.dtstack.batch.service.datasource.impl.DsInfoService;
import com.dtstack.batch.service.datasource.impl.DsTypeService;
import com.dtstack.batch.utils.Asserts;
import com.dtstack.batch.vo.datasource.DsDetailVO;
import com.dtstack.batch.vo.datasource.DsListVO;
import com.dtstack.batch.vo.datasource.DsTypeListVO;
import com.dtstack.engine.common.exception.BizException;
import com.dtstack.engine.common.lang.web.R;
import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.common.util.APITemplate;
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
@RequestMapping(value = "/node/datasource/dataSource")
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
            protected DsDetailVO process() throws BizException {
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
            protected Boolean process() throws BizException {
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


}
