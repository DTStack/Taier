package com.dtstack.engine.master.controller;


import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryTableLineageParam;
import com.dtstack.engine.common.util.ValidateUtil;
import com.dtstack.engine.master.router.DtRequestParam;
import com.dtstack.lineage.impl.LineageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author chener
 * @Classname LineageController
 * @Description sql解析
 * @Date 2020/11/2 15:16
 * @Created chener@dtstack.com
 */
@RestController
@RequestMapping("/node/lineage")
@Api(value = "/node/lineage", tags = {"sql解析接口"})
public class LineageController {

    @Autowired
    private LineageService lineageService;

    @RequestMapping(value = "/parseSqlInfo", method = {RequestMethod.POST})
    @ApiOperation(value = "解析sql基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sql", value = "待解析sql"),
            @ApiImplicitParam(name = "defaultDb", value = "默认数据库"),
            @ApiImplicitParam(name = "sourceType", value = "数据源类型")
    }
    )
    public SqlParseInfo parseSqlInfo(@DtRequestParam String sql, @DtRequestParam String defaultDb, @DtRequestParam Integer sourceType) {
        ValidateUtil.validateNotNull(sql,"sql不能为空");
        ValidateUtil.validateNotNull(defaultDb,"默认数据库不能为空");
        ValidateUtil.validateNotNull(sourceType,"数据源类型不能为空");
        return lineageService.parseSql(sql, defaultDb, sourceType);
    }

    @RequestMapping(value = "/parseTableLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析sql表级血缘关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sql", value = "待解析sql"),
            @ApiImplicitParam(name = "defaultDb", value = "默认数据库"),
            @ApiImplicitParam(name = "sourceType", value = "数据源类型")
    }
    )
    public TableLineageParseInfo parseTableLineage(@DtRequestParam String sql, @DtRequestParam String defaultDb, @DtRequestParam Integer sourceType) {
        ValidateUtil.validateNotNull(sql,"sql不能为空");
        ValidateUtil.validateNotNull(defaultDb,"默认数据源不能为空");
        ValidateUtil.validateNotNull(sourceType,"数据源类型不能为空");
        return lineageService.parseTableLineage(sql, defaultDb, sourceType);
    }

    @RequestMapping(value = "/parseAndSaveTableLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析sql表级血缘关系并存储")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appType", value = "应用类型"),
            @ApiImplicitParam(name = "uicTenantId", value = "uic租户id"),
            @ApiImplicitParam(name = "sql", value = "待解析sql"),
            @ApiImplicitParam(name = "defaultDb", value = "默认数据库"),
            @ApiImplicitParam(name = "engineSourceId", value = "引擎数据源id，需要提前将数据源推送到engine")
    }
    )
    public void parseAndSaveTableLineage(@DtRequestParam Integer appType, @DtRequestParam Long uicTenantId, @DtRequestParam String sql, @DtRequestParam String defaultDb, @DtRequestParam Long engineSourceId,@DtRequestParam Integer sourceType, @DtRequestParam String uniqueKey) {
        ValidateUtil.validateNotNull(appType,"应用类型不能为空");
        ValidateUtil.validateNotNull(uicTenantId,"uic租户id不能为空");
        ValidateUtil.validateNotNull(sql,"sql不能为空");
        ValidateUtil.validateNotNull(defaultDb,"默认数据哭不能为空");
        lineageService.parseAndSaveTableLineage(uicTenantId, appType, sql, defaultDb, engineSourceId, sourceType,uniqueKey);
    }

    @RequestMapping(value = "/parseColumnLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析字段级血缘关系")
    public ColumnLineageParseInfo parseColumnLineage(@RequestBody ParseColumnLineageParam parseColumnLineageParam) {
        ValidateUtil.validateNotNull(parseColumnLineageParam.getAppType(),"应用类型不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getDataSourceType(),"数据源类型不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getDefaultDb(),"默认数据库不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getDtUicTenantId(),"uic租户id不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getSql(),"sql不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getTableColumnsMap(),"表字段map不能为空");
        return lineageService.parseColumnLineage(parseColumnLineageParam.getSql(), parseColumnLineageParam.getDataSourceType(), parseColumnLineageParam.getDefaultDb(), parseColumnLineageParam.getTableColumnsMap());
    }

    @RequestMapping(value = "/parseAndSaveColumnLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析字段级血缘关系并存储")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appType", value = "应用类型"),
            @ApiImplicitParam(name = "sql", value = "待解析sql"),
            @ApiImplicitParam(name = "tenantId", value = "uic租户id"),
            @ApiImplicitParam(name = "defaultDb", value = "默认数据库"),
            @ApiImplicitParam(name = "engineSourceId", value = "引擎数据源id，资产需要提前将数据源推送到engine")
    }
    )
    public void parseAndSaveColumnLineage(@RequestBody ParseColumnLineageParam parseColumnLineageParam) {
        ValidateUtil.validateNotNull(parseColumnLineageParam.getAppType(),"应用类型不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getDataSourceType(),"数据源类型不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getDefaultDb(),"默认数据库不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getDtUicTenantId(),"uic租户id不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getSql(),"sql不能为空");
        lineageService.parseAndSaveColumnLineage(parseColumnLineageParam);
    }

    private void checkLineageTableTableVO(LineageTableTableVO lineageTableTableVO){
        ValidateUtil.validateNotNull(lineageTableTableVO.getAppType(),"应用类型不能为空");
        ValidateUtil.validateNotNull(lineageTableTableVO.getDtUicTenantId(),"uic租户id不能为空");
        ValidateUtil.validateNotNull(lineageTableTableVO.getInputTableInfo(),"输入表信息不能为空");
        ValidateUtil.validateNotNull(lineageTableTableVO.getResultTableInfo(),"结果表信息不能为空");
    }

    @RequestMapping(value = "/manualAddTableTable", method = {RequestMethod.POST})
    @ApiOperation(value = "手动添加表级血缘")
    public void manualAddTableTable(@RequestBody LineageTableTableVO lineageTableTableVO) {
        checkLineageTableTableVO(lineageTableTableVO);
        lineageService.manualAddTableLineage(lineageTableTableVO);
    }

    @RequestMapping(value = "/manualDeleteTableTable", method = {RequestMethod.POST})
    @ApiOperation(value = "手动删除表级血缘")
    public void manualDeleteTableTable(@RequestBody LineageTableTableVO lineageTableTableVO) {
        checkLineageTableTableVO(lineageTableTableVO);
        lineageService.manualDeleteTableLineage(lineageTableTableVO);
    }

    private void checkLineageColumnColumnVO(LineageColumnColumnVO lineageTableTableVO){
        ValidateUtil.validateNotNull(lineageTableTableVO.getAppType(),"应用类型不能为空");
        ValidateUtil.validateNotNull(lineageTableTableVO.getDtUicTenantId(),"uic租户id不能为空");
        ValidateUtil.validateNotNull(lineageTableTableVO.getInputTableInfo(),"输入表信息不能为空");
        ValidateUtil.validateNotNull(lineageTableTableVO.getResultTableInfo(),"输出表信息不能为空");
        ValidateUtil.validateNotNull(lineageTableTableVO.getInputColumnName(),"输入字段名不能为空");
        ValidateUtil.validateNotNull(lineageTableTableVO.getResultColumnName(),"输出字段名不能为空");
    }
    @RequestMapping(value = "/manualAddColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "手动添加字段级血缘")
    public void manualAddColumnColumn(@RequestBody LineageColumnColumnVO lineageTableTableVO) {
        checkLineageColumnColumnVO(lineageTableTableVO);
        lineageService.manualAddColumnLineage(lineageTableTableVO);
    }

    @RequestMapping(value = "/manualDeleteColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "手动删除字段级血缘")
    public void manualDeleteColumnColumn(@RequestBody LineageColumnColumnVO lineageTableTableVO) {
        checkLineageColumnColumnVO(lineageTableTableVO);
        lineageService.manualDeleteColumnLineage(lineageTableTableVO);
    }

    private void checkQueryColumnLineageParam(QueryTableLineageParam queryTableLineageParam){
        ValidateUtil.validateNotNull(queryTableLineageParam.getAppType(),"应用类型不能为空");
        ValidateUtil.validateNotNull(queryTableLineageParam.getDtUicTenantId(),"uic租户id不能为空");
        ValidateUtil.validateNotNull(queryTableLineageParam.getSourceType(),"数据源类型不能为空");
        ValidateUtil.validateNotNull(queryTableLineageParam.getTableName(),"表名称不能为空");
        ValidateUtil.validateNotNull(queryTableLineageParam.getDbName(),"数据库名称不能为空");
    }
    @RequestMapping(value = "/queryTableInputLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表上游血缘")
    public List<LineageTableTableVO> queryTableInputLineage(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        checkQueryColumnLineageParam(queryTableLineageParam);
        return lineageService.queryTableInputLineage(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryTableResultLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表下游血缘")
    public List<LineageTableTableVO> queryTableResultLineage(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        checkQueryColumnLineageParam(queryTableLineageParam);
        return lineageService.queryTableResultLineage(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryTableLineages", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表血缘")
    public List<LineageTableTableVO> queryTableLineages(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        checkQueryColumnLineageParam(queryTableLineageParam);
        return lineageService.queryTableLineages(queryTableLineageParam);
    }

    private void checkQueryColumnLineageParam(QueryColumnLineageParam queryColumnLineageParam){
        ValidateUtil.validateNotNull(queryColumnLineageParam.getAppType(),"应用类型不能为空");
        ValidateUtil.validateNotNull(queryColumnLineageParam.getDtUicTenantId(),"uic租户id不能为空");
        ValidateUtil.validateNotNull(queryColumnLineageParam.getSourceType(),"数据源类型不能为空");
        ValidateUtil.validateNotNull(queryColumnLineageParam.getTableName(),"表名称不能为空");
        ValidateUtil.validateNotNull(queryColumnLineageParam.getDbName(),"数据库名称不能为空");
        ValidateUtil.validateNotNull(queryColumnLineageParam.getColumnName(),"字段名称不能为空");
    }

    @RequestMapping(value = "/queryColumnInputLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段上游血缘")
    public List<LineageColumnColumnVO> queryColumnInputLineage(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        checkQueryColumnLineageParam(queryColumnLineageParam);
        return lineageService.queryColumnInputLineage(queryColumnLineageParam);
    }

    @RequestMapping(value = "/queryColumnResultLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段下游血缘")
    public List<LineageColumnColumnVO> queryColumnResultLineage(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        checkQueryColumnLineageParam(queryColumnLineageParam);
        return lineageService.queryColumnResultLineage(queryColumnLineageParam);
    }

    @RequestMapping(value = "/queryColumnLineages", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段血缘")
    public List<LineageColumnColumnVO> queryColumnLineages(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        checkQueryColumnLineageParam(queryColumnLineageParam);
        return lineageService.queryColumnLineages(queryColumnLineageParam);
    }

    @RequestMapping(value = "/acquireOldTableTable", method = {RequestMethod.POST})
    @ApiOperation(value = "批量同步表级血缘")
    public void acquireOldTableTable(@RequestBody List<LineageTableTableVO> lineageTableTableVOs){
        lineageService.acquireOldTableTable(lineageTableTableVOs);
    }

    @RequestMapping(value = "/acquireOldColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "批量同步字段级血缘")
    public void acquireOldColumnColumn(@RequestBody List<LineageColumnColumnVO> lineageTableTableVOs) {
        lineageService.acquireOldColumnColumn(lineageTableTableVOs);
    }
}
