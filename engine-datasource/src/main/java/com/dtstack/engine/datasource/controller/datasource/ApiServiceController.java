package com.dtstack.engine.datasource.controller.datasource;

import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.engine.common.exception.BizException;
import dt.insight.plat.lang.coc.template.APITemplate;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import com.dtstack.engine.datasource.facade.datasource.ApiServiceFacade;
import com.dtstack.engine.datasource.param.datasource.DsIdListParam;
import com.dtstack.engine.datasource.param.datasource.HelloParam;
import com.dtstack.engine.datasource.param.datasource.api.*;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceInfoVO;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceListVO;
import com.dtstack.engine.datasource.vo.datasource.api.DsShiftReturnVO;
import com.google.common.collect.Maps;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Api(tags = {"数据源中心-第三方对接"})
@RestController
@RequestMapping(value = "/api/publicService/apiService")
public class ApiServiceController {

    @Autowired
    private ApiServiceFacade apiServiceFacade;


    @ApiOperation("外部-产品已被引入的数据源信息分页列表(需要做个性化处理)")
    @PostMapping("/appDsPage")
    public R<PageResult<List<DsServiceListVO>>> appDsPage(@RequestBody DsServiceListParam listParam) {
        return new APITemplate<PageResult<List<DsServiceListVO>>>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(listParam.getAppType(), "产品type不能为空!");
            }

            @Override
            protected PageResult<List<DsServiceListVO>> process() throws BizException {
                return apiServiceFacade.appDsPage(listParam);
            }
        }.execute();
    }

    @ApiOperation("外部-引入数据源分页列表")
    @PostMapping("/importDsPage")
    public R<PageResult<List<DsServiceListVO>>> importDsPage(@RequestBody DsServiceListParam listParam) {
        return new APITemplate<PageResult<List<DsServiceListVO>>>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(listParam.getAppType(), "产品type不能为空!");
            }

            @Override
            protected PageResult<List<DsServiceListVO>> process() throws BizException {
//                DsServiceListVO vo = new DsServiceListVO();
//                vo.setDataName("test");
//                vo.setDataInfoId(1L);
//                vo.setDataDesc("desc");
//                vo.setIsMeta(0);
//                vo.setStatus(1);
//                vo.setLinkJson("{}");
//                vo.setGmtModified(new Date());
//                return PageUtil.transfer(Arrays.asList(vo), listParam, 1);
                return apiServiceFacade.importDsPage(listParam);
            }
        }.execute();
    }


    @ApiOperation("外部-引入数据源下拉数据源类型列表")
    @GetMapping("/type/listByApp")
    public R<List<String>> dsTypeListByApp(@ApiParam(name = "appType", value = "产品type") @RequestParam("appType") Integer appType) {
        return new APITemplate<List<String>>() {

            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(appType, "产品type不能为空!");
            }

            @Override
            protected List<String> process() throws BizException {
                return apiServiceFacade.dsTypeListByProduct(appType);
            }
        }.execute();
    }

    @ApiOperation("外部-确认引入")
    @PostMapping("/appImportDs")
    public R<Boolean> appImportDs(@RequestBody ProductImportParam importParam) {
        return new APITemplate<Boolean>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(importParam.getAppType(), "产品type不能为空!");
                Asserts.notEmpty(importParam.getDataInfoIdList(), "数据源主键List不能为空!");
            }

            @Override
            protected Boolean process() throws BizException {
//                return Boolean.TRUE;
                return apiServiceFacade.productImportDs(importParam);
            }
        }.execute();
    }

    @ApiOperation("外部-取消引入")
    @PostMapping("/appCancelDs")
    public R<Boolean> appCancelDs(@RequestBody ProductImportParam importParam) {
        return new APITemplate<Boolean>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(importParam.getAppType(), "产品type不能为空!");
                Asserts.notEmpty(importParam.getDataInfoIdList(), "数据源主键List不能为空!");
            }

            @Override
            protected Boolean process() throws BizException {
//                return Boolean.TRUE;
                return apiServiceFacade.productCancelDs(importParam);
            }
        }.execute();
    }

    @ApiOperation("外部-创建Meta数据源")
    @PostMapping("/createMetaDs")
    public R<DsShiftReturnVO> createMetaDs(@RequestBody CreateDsParam createDsParam) {
        return new APITemplate<DsShiftReturnVO>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(createDsParam.getAppType(), "产品type不能为空!");
                Asserts.notNull(createDsParam.getType(), "数据源type不能为空!");
                Asserts.notNull(createDsParam.getDsTenantId(), "创建数据源租户ID不能为空!");
                Asserts.notNull(createDsParam.getDsDtuicTenantId(), "创建数据源dtUic租户ID不能为空!");
                Asserts.isTrue(Objects.equals(createDsParam.getIsMeta(), 1), "创建Meta数据源Meta只能为1");
                Asserts.hasText(createDsParam.getDataJson(), "数据源的信息Json不能为空");
            }

            @Override
            protected DsShiftReturnVO process() throws BizException {
//                DsShiftReturnVO vo = new DsShiftReturnVO();
//                vo.setDataInfoId(99L);
//                vo.setDataName("达也测试数据库");
//                return vo;
                return apiServiceFacade.createMetaDs(createDsParam);
            }
        }.execute();
    }

    @ApiOperation("外部-迁移外部产品数据源信息接口")
    @PostMapping("/shiftAppDs")
    public R<List<DsShiftReturnVO>> shiftAppDs(@RequestBody List<CreateDsParam> paramList) {
        return new APITemplate<List<DsShiftReturnVO>>() {

            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.isTrue(paramList.size() <= 20, "数据源迁移一批数量不能大于20!");
                for (CreateDsParam createDsParam : paramList) {
                    Asserts.notNull(createDsParam.getAppType(), "产品type不能为空!");
                    Asserts.notNull(createDsParam.getType(), "数据源type不能为空!");
                    Asserts.notNull(createDsParam.getDsTenantId(), "创建数据源租户ID不能为空!");
                    Asserts.notNull(createDsParam.getDsDtuicTenantId(), "创建数据源dtUic租户ID不能为空!");
                    Asserts.isTrue(Objects.equals(createDsParam.getIsMeta(), 1), "创建Meta数据源Meta只能为1");
                    Asserts.hasText(createDsParam.getDataJson(), "数据源的信息Json不能为空");
                }
            }

            @Override
            protected List<DsShiftReturnVO> process() throws BizException {
//                DsShiftReturnVO vo = new DsShiftReturnVO();
//                vo.setDataInfoId(99L);
//                vo.setDataName("达也测试数据库");
//                return Arrays.asList(vo);
                return apiServiceFacade.shiftAppDs(paramList);
            }
        }.execute();
    }

    @ApiOperation("外部-通过数据源实例Id获取全部信息")
    @GetMapping("/getDataInfoById")
    public R<DsServiceInfoVO> getDsInfoById(@ApiParam(value = "数据源实例Id", name = "dataInfoId") @RequestParam("dataInfoId") Long dataInfoId) {
        return new APITemplate<DsServiceInfoVO>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(dataInfoId, "数据源实例主键id不能为空");
            }

            @Override
            protected DsServiceInfoVO process() throws BizException {
//                DsServiceInfoVO vo = new DsServiceInfoVO();
//                vo.setDataInfoId(1L);
//                vo.setDataType("MySQL");
//                vo.setDataVersion(null);
//                vo.setType(1);
//                vo.setDataName("达也测试数据源");
//                vo.setDataDesc("数据源简介");
//                vo.setDataJson("{}");
//                vo.setLinkJson("{}");
//                vo.setStatus(1);
//                vo.setIsMeta(0);
//                vo.setGmtCreate(new Date());
//                vo.setGmtModified(new Date());
//                return vo;
                return apiServiceFacade.getDsInfoById(dataInfoId);
            }
        }.execute();
    }

    @ApiOperation("外部-通过数据源实例IdList获取数据源列表")
    @PostMapping("/getDsInfoListByIdList")
    public R<List<DsServiceInfoVO>> getDsInfoListByIdList(@ApiParam(value = "数据源实例IdList", name = "dsListParam")@RequestBody DsIdListParam dsIdListParam) {
        return new APITemplate<List<DsServiceInfoVO>>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(dsIdListParam, "dsIdListParam不能为空");
                Asserts.notNull(dsIdListParam.getDataInfoIdList(), "数据源实例主键id列表不能为空");
            }

            @Override
            protected List<DsServiceInfoVO> process() throws BizException {
                return apiServiceFacade.getDsInfoListByIdList(dsIdListParam.getDataInfoIdList());
            }
        }.execute();
    }

    @ApiOperation("外部-通过平台数据源实例Id和appType获取数据源信息")
    @GetMapping("/getDsInfoByOldDataInfoId")
    public R<DsServiceInfoVO> getDsInfoByOldDataInfoId(@ApiParam(value = "各平台数据源实例Id", name = "oldDataInfoId") @RequestParam("oldDataInfoId") Long oldDataInfoId,
                                                       @ApiParam(value = "平台枚举值", name = "appType") @RequestParam("appType") Integer appType) {
        return new APITemplate<DsServiceInfoVO>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(oldDataInfoId, "数据源实例主键id不能为空");
                Asserts.notNull(appType,"appType不能为空");
            }

            @Override
            protected DsServiceInfoVO process() throws BizException {
                return apiServiceFacade.getDsInfoByOldDataInfoId(oldDataInfoId,appType);
            }
        }.execute();
    }

    @ApiOperation("外部-回滚产品创建的默认数据源")
    @PostMapping("/rollDsInfoById")
    public R<Boolean> rollDsInfoById(@RequestBody RollDsParam rollDsParam) {
        return new APITemplate<Boolean>() {

            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notNull(rollDsParam.getDataInfoId(), "数据源主键id不能为空");
                Asserts.notNull(rollDsParam.getAppType(), "产品type不能为空");
                Asserts.notNull(rollDsParam.getDsTenantId(), "创建数据源租户ID不能为空!");
                Asserts.notNull(rollDsParam.getDsDtuicTenantId(), "创建数据源dtUic租户ID不能为空!");
            }

            @Override
            protected Boolean process() throws BizException {
                return apiServiceFacade.rollDsInfoById(rollDsParam);
            }
        }.execute();
    }

    @ApiOperation("console-修改控制台中心数据源集群")
    @PostMapping("editConsoleDs")
    public R<List<DsShiftReturnVO>> editConsoleDs(@RequestBody EditConsoleParam consoleParam) {
        return new APITemplate<List<DsShiftReturnVO>>(){

            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.notEmpty(consoleParam.getDsDtuicTenantIdList(), "dtuic租户id不能为空");
                Asserts.notNull(consoleParam.getType(), "数据源类型不能为空");
                Asserts.hasText(consoleParam.getJdbcUrl(), "修改的jdbcUrl不能为空");
            }

            @Override
            protected List<DsShiftReturnVO> process() throws BizException {
//                DsShiftReturnVO vo1 = new DsShiftReturnVO();
//                DsShiftReturnVO vo2 = new DsShiftReturnVO();
//                vo1.setDataName("数据源1");
//                vo1.setDataInfoId(1L);
//                vo2.setDataName("数据源2");
//                vo2.setDataInfoId(2L);
//                return Arrays.asList(vo1, vo2);
                return apiServiceFacade.editConsoleDs(consoleParam);
            }
        }.execute();
    }



    @ApiOperation("测试SDK Client")
    @PostMapping("say-hello")
    public R<String> sayHello(@RequestBody HelloParam param) {
        return new APITemplate<String>() {
            @Override
            protected String process() throws BizException {
                return String.format("hello to %s", param.getName());
            }
        }.execute();

    }

    public static void main(String[] args) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("", "");
    }

}
