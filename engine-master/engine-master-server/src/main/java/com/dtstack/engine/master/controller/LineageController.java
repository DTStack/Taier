package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.pojo.LevelAndCount;
import com.dtstack.engine.api.vo.lineage.*;
import com.dtstack.engine.api.vo.lineage.param.*;
import com.dtstack.engine.common.util.ValidateUtil;
import com.dtstack.engine.master.router.DtRequestParam;
import com.dtstack.lineage.impl.LineageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

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
    public void parseAndSaveTableLineage(@RequestBody ParseTableLineageParam param) {
        ValidateUtil.validateNotNull(param.getAppType(),"应用类型不能为空");
        ValidateUtil.validateNotNull(param.getDtUicTenantId(),"uic租户id不能为空");
        ValidateUtil.validateNotNull(param.getSql(),"sql不能为空");
        ValidateUtil.validateNotNull(param.getDefaultDb(),"默认数据哭不能为空");
        lineageService.parseAndSaveTableLineage(param.getDtUicTenantId(), param.getAppType(), param.getSql(), param.getDefaultDb(), param.getEngineDataSourceId(), param.getDataSourceType(),param.getUniqueKey());
    }

    @RequestMapping(value = "/parseColumnLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析字段级血缘关系")
    public ColumnLineageParseInfo parseColumnLineage(@RequestBody ParseColumnLineageParam parseColumnLineageParam) {
        ValidateUtil.validateNotNull(parseColumnLineageParam.getDataSourceType(),"数据源类型不能为空");
        ValidateUtil.validateNotNull(parseColumnLineageParam.getDefaultDb(),"默认数据库不能为空");
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
    public void manualAddTableTable(@RequestBody LineageTableTableParam lineageTableTableParam) {
        for (LineageTableTableVO tableTable:lineageTableTableParam.getLineageTableTableVOs()){
            checkLineageTableTableVO(tableTable);
        }
        lineageService.manualAddTableLineage(lineageTableTableParam.getLineageTableTableVOs());
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
    public void manualAddColumnColumn(@RequestBody LineageColumnColumnParam lineageTableTableParam) {
        for (LineageColumnColumnVO columnColumnVO:lineageTableTableParam.getLineageTableTableVOs()){
            checkLineageColumnColumnVO(columnColumnVO);
        }
        lineageService.manualAddColumnLineage(lineageTableTableParam.getLineageTableTableVOs());
    }

    @RequestMapping(value = "/manualDeleteColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "手动删除字段级血缘")
    public void manualDeleteColumnColumn(@RequestBody LineageColumnColumnVO lineageTableTableVO) {
        checkLineageColumnColumnVO(lineageTableTableVO);
        lineageService.manualDeleteColumnLineage(lineageTableTableVO);
    }

    private void checkQueryColumnLineageParam(QueryTableLineageParam queryTableLineageParam){
        ValidateUtil.validateNotNull(queryTableLineageParam.getAppType(),"应用类型不能为空");
//        ValidateUtil.validateNotNull(queryTableLineageParam.getDtUicTenantId(),"uic租户id不能为空");
//        ValidateUtil.validateNotNull(queryTableLineageParam.getSourceType(),"数据源类型不能为空");
        ValidateUtil.validateNotNull(queryTableLineageParam.getTableName(),"表名称不能为空");
//        ValidateUtil.validateNotNull(queryTableLineageParam.getDbName(),"数据库名称不能为空");
        Integer level = queryTableLineageParam.getLevel();
        if(level == null){
            level = 20;
        }else if(level>20 || level<=0){
            level = 20;
        }
        queryTableLineageParam.setLevel(level);

    }
    @RequestMapping(value = "/queryTableInputLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表上游血缘")
    public List<LineageTableTableVO> queryTableInputLineage(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        checkQueryColumnLineageParam(queryTableLineageParam);
        return lineageService.queryTableInputLineage(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryTableInputLineageCount", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表上游血缘表数量")
    public Integer queryTableInputLineageCount(@RequestBody QueryTableLineageParam queryTableLineageParam){
        checkQueryColumnLineageParam(queryTableLineageParam);
        return lineageService.queryTableInputLineage(queryTableLineageParam).size();
    }


    @RequestMapping(value = "/queryTableResultLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表下游血缘")
    public List<LineageTableTableVO> queryTableResultLineage(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        checkQueryColumnLineageParam(queryTableLineageParam);
        return lineageService.queryTableResultLineage(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryTableResultLineageCount", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表下游血缘表数量")
    public Integer queryTableResultLineageCount(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        checkQueryColumnLineageParam(queryTableLineageParam);
        return lineageService.queryTableResultLineage(queryTableLineageParam).size();
    }

    @RequestMapping(value = "/queryTableLineages", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表血缘")
    public List<LineageTableTableVO> queryTableLineages(@RequestBody QueryTableLineageParam queryTableLineageParam) {
        checkQueryColumnLineageParam(queryTableLineageParam);
        return lineageService.queryTableLineages(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryTableLineageByTaskIdAndAppType", method = {RequestMethod.POST})
    @ApiOperation("根据taskId和appType查询表级血缘")
    public List<LineageTableTableVO> queryTableLineageByTaskIdAndAppType(@DtRequestParam Long taskId,@DtRequestParam Integer appType){

        return lineageService.queryTableLineageByTaskIdAndAppType(taskId,appType);
    }

    private void checkQueryColumnLineageParam(QueryColumnLineageParam queryColumnLineageParam){
        ValidateUtil.validateNotNull(queryColumnLineageParam.getAppType(),"应用类型不能为空");
//        ValidateUtil.validateNotNull(queryColumnLineageParam.getDtUicTenantId(),"uic租户id不能为空");
//        ValidateUtil.validateNotNull(queryColumnLineageParam.getSourceType(),"数据源类型不能为空");
        ValidateUtil.validateNotNull(queryColumnLineageParam.getTableName(),"表名称不能为空");
//        ValidateUtil.validateNotNull(queryColumnLineageParam.getDbName(),"数据库名称不能为空");
        ValidateUtil.validateNotNull(queryColumnLineageParam.getColumnName(),"字段名称不能为空");
        Integer level = queryColumnLineageParam.getLevel();
        if(level == null){
            level = 20;
        }else if(level>20 || level<=0){
            level = 20;
        }
        queryColumnLineageParam.setLevel(level);
    }

    @RequestMapping(value = "/queryColumnInputLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段上游血缘")
    public List<LineageColumnColumnVO> queryColumnInputLineage(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        checkQueryColumnLineageParam(queryColumnLineageParam);
        return lineageService.queryColumnInputLineage(queryColumnLineageParam);
    }

    @RequestMapping(value = "/queryColumnInputLineageCount", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段上游血缘字段数量")
    public Integer queryColumnInputLineageCount(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        checkQueryColumnLineageParam(queryColumnLineageParam);
        return lineageService.queryColumnInputLineage(queryColumnLineageParam).size();
    }

    @RequestMapping(value = "/queryColumnResultLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段下游血缘")
    public List<LineageColumnColumnVO> queryColumnResultLineage(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        checkQueryColumnLineageParam(queryColumnLineageParam);
        return lineageService.queryColumnResultLineage(queryColumnLineageParam);
    }

    @RequestMapping(value = "/queryColumnResultLineageCount", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段下游血缘")
    public Integer queryColumnResultLineageCount(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        checkQueryColumnLineageParam(queryColumnLineageParam);
        return lineageService.queryColumnResultLineage(queryColumnLineageParam).size();
    }

    @RequestMapping(value = "/queryColumnLineages", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段血缘")
    public List<LineageColumnColumnVO> queryColumnLineages(@RequestBody QueryColumnLineageParam queryColumnLineageParam) {
        checkQueryColumnLineageParam(queryColumnLineageParam);
        return lineageService.queryColumnLineages(queryColumnLineageParam);
    }

    @RequestMapping(value = "/queryColumnLineageByTaskIdAndAppType", method = {RequestMethod.POST})
    @ApiOperation("根据taskId和appType查询字段血缘")
    public List<LineageColumnColumnVO> queryColumnLineageByTaskIdAndAppType(@DtRequestParam Long taskId,@DtRequestParam Integer appType){

        return lineageService.queryColumnLineageByTaskIdAndAppType(taskId,appType);
    }

    @RequestMapping(value = "/acquireOldTableTable", method = {RequestMethod.POST})
    @ApiOperation(value = "批量同步表级血缘")
    public void acquireOldTableTable(@RequestBody LineageTableTableParam lineageTableTableParam){
        lineageService.acquireOldTableTable(lineageTableTableParam.getLineageTableTableVOs());
    }

    @RequestMapping(value = "/acquireOldColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "批量同步字段级血缘")
    public void acquireOldColumnColumn(@RequestBody LineageColumnColumnParam lineageTableTableParam) {
        lineageService.acquireOldColumnColumn(lineageTableTableParam.getLineageTableTableVOs());
    }

    @RequestMapping(value = "/queryTableLineageInputColumns", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表的血缘上游字段列表")
    public List<String> queryTableLineageInputColumns(@RequestBody QueryTableLineageColumnParam queryTableLineageColumnParam) {
        return lineageService.queryTableInputLineageColumns(queryTableLineageColumnParam);
    }

    @RequestMapping(value = "/queryTableLineageResultColumns", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表的血缘下游字段列表")
    public List<String> queryTableLineageResultColumns(@RequestBody QueryTableLineageColumnParam queryTableLineageColumnParam) {
        return lineageService.queryTableResultLineageColumns(queryTableLineageColumnParam);
    }

    @RequestMapping(value = "/parseTables", method = {RequestMethod.POST})
    @ApiOperation(value = "解析sql表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sql", value = "待解析sql"),
            @ApiImplicitParam(name = "defaultDb", value = "默认数据库"),
            @ApiImplicitParam(name = "sourceType", value = "数据源类型")
    }
    )
    public List<com.dtstack.engine.api.pojo.lineage.Table> parseTables(@DtRequestParam String sql, @DtRequestParam String defaultDb, @DtRequestParam Integer sourceType) {
        ValidateUtil.validateNotNull(sql,"sql不能为空");
        ValidateUtil.validateNotNull(defaultDb,"默认数据库不能为空");
        ValidateUtil.validateNotNull(sourceType,"数据源类型不能为空");
        return lineageService.parseTables(sql, defaultDb, sourceType);
    }


    @RequestMapping(value = "/parseFunction", method = {RequestMethod.POST})
    @ApiOperation(value = "解析sql方法信息")
    @ApiImplicitParam(name = "sql", value = "待解析sql")
    public Set<String> parseFunction(String sql){

        return lineageService.parseFunction(sql);
    }


    @RequestMapping(value = "/queryTableInputLineageCountAndLevel", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表上游血缘表数量和层数")
    public LevelAndCount queryTableInputLineageCountAndLevel(@RequestBody QueryTableLineageParam queryTableLineageParam){

         return lineageService.queryTableInputLineageCountAndLevel(queryTableLineageParam);
    }

    @RequestMapping(value = "/queryTableResultLineageCountAndLevel", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表下游血缘表数量和层数")
    public LevelAndCount queryTableResultLineageCountAndLevel(@RequestBody QueryTableLineageParam queryTableLineageParam){

        return lineageService.queryTableResultLineageCountAndLevel(queryTableLineageParam);
    }

    @RequestMapping(value = "/deleteLineageByTaskIdAndAppType", method = {RequestMethod.POST})
    @ApiOperation(value = "根据taskId和appType删除血缘")
    public void deleteLineageByTaskIdAndAppType(@RequestBody DeleteLineageParam deleteLineageParam){

        lineageService.deleteLineageByTaskIdAndAppType(deleteLineageParam);
    }

}
