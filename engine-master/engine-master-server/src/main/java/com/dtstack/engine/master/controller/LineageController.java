package com.dtstack.engine.master.controller;


import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryTableLineageParam;
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
        Validate.notNull(sql);
        Validate.notNull(defaultDb);
        Validate.notNull(sourceType);
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
        Validate.notNull(sql);
        Validate.notNull(defaultDb);
        Validate.notNull(sourceType);
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
        Validate.notNull(appType);
        Validate.notNull(uicTenantId);
        Validate.notNull(sql);
        Validate.notNull(defaultDb);
        lineageService.parseAndSaveTableLineage(uicTenantId, appType, sql, defaultDb, engineSourceId, sourceType,uniqueKey);
    }

    @RequestMapping(value = "/parseColumnLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析字段级血缘关系")
    public ColumnLineageParseInfo parseColumnLineage(@RequestBody ParseColumnLineageParam parseColumnLineageParam) {
        Validate.notNull(parseColumnLineageParam.getAppType());
        Validate.notNull(parseColumnLineageParam.getDataSourceType());
        Validate.notNull(parseColumnLineageParam.getDefaultDb());
        Validate.notNull(parseColumnLineageParam.getDtUicTenantId());
        Validate.notNull(parseColumnLineageParam.getSql());
        Validate.notNull(parseColumnLineageParam.getTableColumnsMap());
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
        Validate.notNull(parseColumnLineageParam.getAppType());
        Validate.notNull(parseColumnLineageParam.getDataSourceType());
        Validate.notNull(parseColumnLineageParam.getDefaultDb());
        Validate.notNull(parseColumnLineageParam.getDtUicTenantId());
        Validate.notNull(parseColumnLineageParam.getSql());
        lineageService.parseAndSaveColumnLineage(parseColumnLineageParam);
    }

    @RequestMapping(value = "/manualAddTableTable", method = {RequestMethod.POST})
    @ApiOperation(value = "手动添加表级血缘")
    public void manualAddTableTable(@RequestBody LineageTableTableVO lineageTableTableVO) {
        Validate.notNull(lineageTableTableVO.getAppType());
        Validate.notNull(lineageTableTableVO.getDtUicTenantId());
        Validate.notNull(lineageTableTableVO.getInputTableInfo());
        Validate.notNull(lineageTableTableVO.getDtUicTenantId());
        Validate.notNull(lineageTableTableVO.getResultTableInfo());
        lineageService.manualAddTableLineage(lineageTableTableVO);
    }

    @RequestMapping(value = "/manualDeleteTableTable", method = {RequestMethod.POST})
    @ApiOperation(value = "手动删除表级血缘")
    public void manualDeleteTableTable(@RequestBody LineageTableTableVO lineageTableTableVO) {
        Validate.notNull(lineageTableTableVO.getAppType());
        Validate.notNull(lineageTableTableVO.getDtUicTenantId());
        Validate.notNull(lineageTableTableVO.getInputTableInfo());
        Validate.notNull(lineageTableTableVO.getDtUicTenantId());
        Validate.notNull(lineageTableTableVO.getResultTableInfo());
        lineageService.manualDeleteTableLineage(lineageTableTableVO);
    }

    @RequestMapping(value = "/manualAddColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "手动添加字段级血缘")
    public void manualAddColumnColumn(@RequestBody LineageColumnColumnVO lineageTableTableVO) {
        Validate.notNull(lineageTableTableVO.getAppType());
        Validate.notNull(lineageTableTableVO.getDtUicTenantId());
        Validate.notNull(lineageTableTableVO.getInputTableInfo());
        Validate.notNull(lineageTableTableVO.getDtUicTenantId());
        Validate.notNull(lineageTableTableVO.getResultTableInfo());
        Validate.notNull(lineageTableTableVO.getInputColumnName());
        Validate.notNull(lineageTableTableVO.getResultColumnName());
        lineageService.manualAddColumnLineage(lineageTableTableVO);
    }

    @RequestMapping(value = "/manualDeleteColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "手动删除字段级血缘")
    public void manualDeleteColumnColumn(@RequestBody LineageColumnColumnVO lineageTableTableVO) {
        Validate.notNull(lineageTableTableVO.getAppType());
        Validate.notNull(lineageTableTableVO.getDtUicTenantId());
        Validate.notNull(lineageTableTableVO.getInputTableInfo());
        Validate.notNull(lineageTableTableVO.getDtUicTenantId());
        Validate.notNull(lineageTableTableVO.getResultTableInfo());
        Validate.notNull(lineageTableTableVO.getInputColumnName());
        Validate.notNull(lineageTableTableVO.getResultColumnName());
        lineageService.manualDeleteColumnLineage(lineageTableTableVO);
    }

    @RequestMapping(value = "/queryTableInputLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表上游血缘")
    public List<LineageTableTableVO> queryTableInputLineage(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        Validate.notNull(queryTableLineageParam.getAppType());
        Validate.notNull(queryTableLineageParam.getDtUicTenantId());
        Validate.notNull(queryTableLineageParam.getSourceType());
        Validate.notNull(queryTableLineageParam.getTableName());
        Validate.notNull(queryTableLineageParam.getDbName());
        return lineageService.queryTableInputLineage(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryTableResultLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表下游血缘")
    public List<LineageTableTableVO> queryTableResultLineage(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        Validate.notNull(queryTableLineageParam.getAppType());
        Validate.notNull(queryTableLineageParam.getDtUicTenantId());
        Validate.notNull(queryTableLineageParam.getSourceType());
        Validate.notNull(queryTableLineageParam.getTableName());
        Validate.notNull(queryTableLineageParam.getDbName());
        return lineageService.queryTableResultLineage(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryTableLineages", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表血缘")
    public List<LineageTableTableVO> queryTableLineages(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        Validate.notNull(queryTableLineageParam.getAppType());
        Validate.notNull(queryTableLineageParam.getDtUicTenantId());
        Validate.notNull(queryTableLineageParam.getSourceType());
        Validate.notNull(queryTableLineageParam.getTableName());
        Validate.notNull(queryTableLineageParam.getDbName());
        return lineageService.queryTableLineages(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryColumnInputLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段上游血缘")
    public List<LineageColumnColumnVO> queryColumnInputLineage(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        Validate.notNull(queryColumnLineageParam.getAppType());
        Validate.notNull(queryColumnLineageParam.getDtUicTenantId());
        Validate.notNull(queryColumnLineageParam.getSourceType());
        Validate.notNull(queryColumnLineageParam.getTableName());
        Validate.notNull(queryColumnLineageParam.getDbName());
        Validate.notNull(queryColumnLineageParam.getColumnName());
        return lineageService.queryColumnInputLineage(queryColumnLineageParam);
    }

    @RequestMapping(value = "/queryColumnResultLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段下游血缘")
    public List<LineageColumnColumnVO> queryColumnResultLineage(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        Validate.notNull(queryColumnLineageParam.getAppType());
        Validate.notNull(queryColumnLineageParam.getDtUicTenantId());
        Validate.notNull(queryColumnLineageParam.getSourceType());
        Validate.notNull(queryColumnLineageParam.getTableName());
        Validate.notNull(queryColumnLineageParam.getDbName());
        Validate.notNull(queryColumnLineageParam.getColumnName());
        return lineageService.queryColumnResultLineage(queryColumnLineageParam);
    }

    @RequestMapping(value = "/queryColumnLineages", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段血缘")
    public List<LineageColumnColumnVO> queryColumnLineages(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        Validate.notNull(queryColumnLineageParam.getAppType());
        Validate.notNull(queryColumnLineageParam.getDtUicTenantId());
        Validate.notNull(queryColumnLineageParam.getSourceType());
        Validate.notNull(queryColumnLineageParam.getTableName());
        Validate.notNull(queryColumnLineageParam.getDbName());
        Validate.notNull(queryColumnLineageParam.getColumnName());
        return lineageService.queryColumnLineages(queryColumnLineageParam);
    }
}
