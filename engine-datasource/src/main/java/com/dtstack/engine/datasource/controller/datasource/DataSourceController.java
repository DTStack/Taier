package com.dtstack.engine.datasource.controller.datasource;

import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.engine.common.exception.BizException;
import dt.insight.plat.lang.coc.template;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import com.dtstack.engine.datasource.facade.datasource.DatasourceFacade;
import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import com.dtstack.engine.datasource.param.datasource.DsInfoIdParam;
import com.dtstack.engine.datasource.param.datasource.DsListParam;
import com.dtstack.engine.datasource.param.datasource.ProductAuthParam;
import com.dtstack.engine.datasource.service.impl.datasource.DsAppListService;
import com.dtstack.engine.datasource.service.impl.datasource.DsAuthRefService;
import com.dtstack.engine.datasource.service.impl.datasource.DsInfoService;
import com.dtstack.engine.datasource.service.impl.datasource.DsTypeService;
import com.dtstack.engine.datasource.vo.datasource.*;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Api(tags = {"数据源中心-数据源管理"})
@RestController
@RequestMapping(value = "/api/publicService/dataSource")
public class DataSourceController {

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private DsAuthRefService authRefService;

    @Autowired
    private DsAppListService dsAppListService;

    @Autowired
    private DsTypeService dsTypeService;

    @Autowired
    private DatasourceFacade datasourceFacade;

    @ApiOperation("数据源列表分页信息")
    @PostMapping("page")
    public R<PageResult<List<DsListVO>>> dsPage(@RequestBody DsListParam dsListParam) {
        return new APITemplate<PageResult<List<DsListVO>>>() {
            @Override
            protected PageResult<List<DsListVO>> process() throws BizException {
                return dsInfoService.dsPage(dsListParam);
            }
        }.execute();
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
        return new APITemplate<List<DsTypeListVO>>() {
            @Override
            protected List<DsTypeListVO> process() throws BizException {
                return dsTypeService.dsTypeList();
            }
        }.execute();
    }

    @ApiOperation("授权产品下拉列表")
    @PostMapping("product/list")
    public R<List<ProductListVO>> productList(@RequestBody PubSvcBaseParam baseParam) {
        return new APITemplate<List<ProductListVO>>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.hasText(baseParam.getDtToken(), "用户token不能为空!");
            }

            @Override
            protected List<ProductListVO> process() throws BizException {
                return dsAppListService.getAppList(baseParam);
            }
        }.execute();
    }

    @ApiOperation("产品授权界面")
    @PostMapping("auth/product/list")
    public R<List<AuthProductListVO>> authProductList(@RequestBody DsInfoIdParam dsInfoIdParam) {
        return new APITemplate<List<AuthProductListVO>>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(dsInfoIdParam.getDataInfoId(), "数据源Id不能为空");
            }

            @Override
            protected List<AuthProductListVO> process() throws BizException {
                return datasourceFacade.authProductList(dsInfoIdParam);
            }
        }.execute();
    }

    @ApiOperation("产品授权")
    @PostMapping("product/auth")
    public R<Boolean> productAuth(@RequestBody ProductAuthParam productAuthParam) {
        return new APITemplate<Boolean>() {

            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(productAuthParam.getDataInfoId(), "数据源主键id不能为空");
                Asserts.isTrue(Objects.equals(productAuthParam.getIsAuth(), 1), "产品授权状态只能为1");
            }

            @Override
            protected Boolean process() throws BizException {
                return authRefService.productAuth(productAuthParam);
            }
        }.execute();
    }

}
