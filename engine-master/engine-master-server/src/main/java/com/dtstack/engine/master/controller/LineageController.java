package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.param.QueryColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryTableLineageParam;
import com.dtstack.engine.master.router.DtRequestParam;
import com.dtstack.lineage.impl.LineageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "/node/lineage", tags = {"sql解析"})
public class LineageController {

    @Autowired
    private LineageService lineageService;

    @RequestMapping(value="/parseSqlInfo", method = {RequestMethod.POST})
    @ApiOperation(value = "解析sql基本信息")
    public SqlParseInfo parseBaseInfo(@DtRequestParam String sql,@DtRequestParam Integer sourceType){
        //TODO
        return null;
    }

    @RequestMapping(value="/parseTableLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析sql表级血缘关系")
    public TableLineageParseInfo parseTableLineage(@DtRequestParam String sql,@DtRequestParam String defaultDb,@DtRequestParam Integer sourceType){
        //TODO
        return null;
    }

    @RequestMapping(value="/parseAndSaveTableLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析sql表级血缘关系并存储")
    public void parseAndSaveTableLineage(@DtRequestParam Integer appType,@DtRequestParam String sql,@DtRequestParam String defaultDb,@DtRequestParam Long engineSourceId){
        //TODO
    }

    @RequestMapping(value="/parseColumnLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析字段级血缘关系")
    public ColumnLineageParseInfo parseColumnLineage(@DtRequestParam Integer appType, @DtRequestParam String sql, @DtRequestParam String defaultDb, @DtRequestParam Long engineSourceId){
        //TODO
        return null;
    }

    @RequestMapping(value="/parseAndSaveColumnLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "解析字段级血缘关系并存储")
    public ColumnLineageParseInfo parseAndSaveColumnLineage(@DtRequestParam Integer appType,@DtRequestParam String sql,@DtRequestParam String defaultDb,@DtRequestParam Long engineSourceId){
        //TODO
        return null;
    }

    @RequestMapping(value="/manualAddTableTable", method = {RequestMethod.POST})
    @ApiOperation(value = "手动添加表级血缘")
    public void manualAddTableTable(@RequestBody LineageTableTableVO lineageTableTableVO){
        //TODO
    }

    @RequestMapping(value="/manualDeleteTableTable", method = {RequestMethod.POST})
    @ApiOperation(value = "手动删除表级血缘")
    public void manualDeleteTableTable(@RequestBody LineageTableTableVO lineageTableTableVO){
        //TODO
    }

    @RequestMapping(value="/manualAddColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "手动添加字段级血缘")
    public void manualAddColumnColumn(@RequestBody LineageColumnColumnVO lineageTableTableVO){
        //TODO
    }

    @RequestMapping(value="/manualDeleteColumnColumn", method = {RequestMethod.POST})
    @ApiOperation(value = "手动删除字段级血缘")
    public void manualDeleteColumnColumn(@RequestBody LineageColumnColumnVO lineageTableTableVO){
        //TODO
    }

    @RequestMapping(value="/queryTableInputLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表上游血缘")
    public List<LineageTableTableVO> queryTableInputLineage(@RequestBody QueryTableLineageParam queryTableLineageParam){
        //TODO
//        List<LineageTableTable> lineageTableTables = lineageService.queryTableInputLineage(appType, tableId);
        return null;
    }

    @RequestMapping(value="/queryTableResultLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表下游血缘")
    public List<LineageTableTableVO> queryTableResultLineage(@RequestBody QueryTableLineageParam queryTableLineageParam){
//        List<LineageTableTable> lineageTableTables = lineageService.queryTableResultLineage(appType, tableId);
        //TODO
        return null;
    }

    @RequestMapping(value="/queryTableLineages", method = {RequestMethod.POST})
    @ApiOperation(value = "查询表血缘")
    public List<LineageTableTableVO> queryTableLineages(@RequestBody QueryTableLineageParam queryTableLineageParam){
//        List<LineageTableTable> lineageTableTables = lineageService.queryTableLineages(appType, tableId);
        //TODO
        return null;
    }

    @RequestMapping(value="/queryColumnInoutLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段上游血缘")
    public List<LineageColumnColumnVO> queryColumnInoutLineage(@RequestBody QueryColumnLineageParam queryColumnLineageParam){
//        List<LineageColumnColumn> lineageColumnColumns = lineageService.queryColumnInoutLineage(appType, tableId,columnName);
        //TODO
        return null;
    }

    @RequestMapping(value="/queryColumnResultLineage", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段下游血缘")
    public List<LineageColumnColumnVO> queryColumnResultLineage(@RequestBody QueryColumnLineageParam queryColumnLineageParam){
//        List<LineageColumnColumn> lineageColumnColumns = lineageService.queryColumnResultLineage(appType, tableId,columnName);
        //TODO
        return null;
    }

    @RequestMapping(value="/queryColumnLineages", method = {RequestMethod.POST})
    @ApiOperation(value = "查询字段血缘")
    public List<LineageColumnColumnVO> queryColumnLineages(@RequestBody QueryColumnLineageParam queryColumnLineageParam){
//        List<LineageColumnColumn> lineageColumnColumns = lineageService.queryColumnLineages(appType, tableId,columnName);
        //TODO
        return null;
    }
}
