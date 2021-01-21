package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnParam;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableParam;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.ParseTableLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryTableLineageParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * @author chener
 * @Classname LineageService
 * @Description 解析接口
 * @Date 2020/10/22 20:37
 * @Created chener@dtstack.com
 */
public interface LineageService extends DtInsightServer {

    /**
     * 解析sql基本信息
     * @param sql sql
     * @Param defaultDb 默认数据库
     * @Param sourceType 数据源类型
     * @return 返回基本解析信息
     */
    @RequestLine("POST /node/lineage/parseSqlInfo")
    ApiResponse<SqlParseInfo> parseSqlInfo(@Param("sql") String sql,@Param("defaultDb") String defaultDb, @Param("sourceType") Integer sourceType);

    /**
     * 解析sql表级血缘
     * @param sql 原始sql
     * @param defaultDb 默认数据库
     * @param sourceType 数据库类型
     * @return 返回表血缘解析结果
     */
    @RequestLine("POST /node/lineage/parseTableLineage")
    ApiResponse<TableLineageParseInfo> parseTableLineage(@Param("sql")String sql, @Param("defaultDb")String defaultDb,@Param("sourceType")Integer sourceType);

    /**
     * 解析表级血缘并存储血缘关系。
     * @return 异步执行，无返回值
     */
    @RequestLine("POST /node/lineage/parseAndSaveTableLineage")
    ApiResponse parseAndSaveTableLineage(ParseTableLineageParam param);

    /**
     * 解析字段级血缘关系
     * @return 返回字段血缘解析结果
     */
    @RequestLine("POST /node/lineage/parseColumnLineage")
    ApiResponse<ColumnLineageParseInfo> parseColumnLineage(ParseColumnLineageParam param);

    /**
     * 解析字段级血缘关系并存储
     * @return 异步解析，无返回结果
     */
    @RequestLine("POST /node/lineage/parseAndSaveColumnLineage")
    ApiResponse parseAndSaveColumnLineage(ParseColumnLineageParam param);

    /**
     * 手动添加表级血缘
     * @param lineageTableTableParam
     * @return 无返回结果
     */
    @RequestLine("POST /node/lineage/manualAddTableTable")
    ApiResponse manualAddTableTable( LineageTableTableParam lineageTableTableParam);

    /**
     * 手动删除表级血缘
     * @param lineageTableTableVO
     * @return 无返回结果
     */
    @RequestLine("POST /node/lineage/manualDeleteTableTable")
    ApiResponse manualDeleteTableTable( LineageTableTableVO lineageTableTableVO);

    /**
     * 手动添加字段级血缘
     * @param lineageColumnColumnParam
     * @return 无返回结果
     */
    @RequestLine("POST /node/lineage/manualAddColumnColumn")
    ApiResponse manualAddColumnColumn( LineageColumnColumnParam lineageColumnColumnParam);

    /**
     * 手动删除字段级血缘
     * @param lineageColumnColumnVO
     * @return 无返回结果
     */
    @RequestLine("POST /node/lineage/manualDeleteColumnColumn")
    ApiResponse manualDeleteColumnColumn(LineageColumnColumnVO lineageColumnColumnVO);

    /**
     * 根据表id查询表上游血缘关系
     * @param param
     * @return 返回当前表的上游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryTableInputLineage")
    ApiResponse<List<LineageTableTableVO>> queryTableInputLineage(QueryTableLineageParam param);

    /**
     * 根据表id查询表上游血缘表数量
     * @param param
     * @return 返回当前表的上游血缘表数量。
     */
    @RequestLine("POST /node/lineage/queryTableInputLineageCount")
    ApiResponse<Integer> queryTableInputLineageCount(QueryTableLineageParam param);

    /**
     * 根据表id查询表下游血缘关系
     * @param param
     * @return 返回当前表的下游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryTableResultLineage")
    ApiResponse<List<LineageTableTableVO>> queryTableResultLineage(QueryTableLineageParam param);

    /**
     * 根据表id查询表下游血缘表数量
     * @param param
     * @return 返回当前表的下游血缘表数量。
     */
    @RequestLine("POST /node/lineage/queryTableResultLineageCount")
    ApiResponse<Integer> queryTableResultLineageCount(QueryTableLineageParam param);

    /**
     * 根据表id查询表上下游血缘关系
     * @param param
     * @return 返回当前表的上下游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryTableLineages")
    ApiResponse<List<LineageTableTableVO>> queryTableLineages(QueryTableLineageParam param);

    /**
     * 查询字段上游字段血缘
     * @param param
     * @return 返回当前字段的上游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryColumnInputLineage")
    ApiResponse<List<LineageColumnColumnVO>> queryColumnInputLineage(QueryColumnLineageParam param);

    /**
     * 查询字段上游血缘字段数量
     * @param param
     * @return 返回当前字段的上游血缘字段数量。
     */
    @RequestLine("POST /node/lineage/queryColumnInputLineageCount")
    ApiResponse<Integer> queryColumnInputLineageCount(QueryColumnLineageParam param);

    /**
     * 查询字段下游字段血缘
     * @param param
     * @return 返回当前表的下游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryColumnResultLineage")
    ApiResponse<List<LineageColumnColumnVO>> queryColumnResultLineage(QueryColumnLineageParam param);

    /**
     * 查询字段下游血缘字段数量
     * @param param
     * @return 返回当前表的下游血缘字段数量。
     */
    @RequestLine("POST /node/lineage/queryColumnResultLineageCount")
    ApiResponse<Integer> queryColumnResultLineageCount(QueryColumnLineageParam param);

    /**
     * 查询字段所有血缘关系
     * @param param
     * @return 返回当前表的上下游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryColumnLineages")
    ApiResponse<List<LineageColumnColumnVO>> queryColumnLineages(QueryColumnLineageParam param);

    /**
     * 推送历史血缘数据，字段级血缘会自动添加相应的表级血缘
     * @param lineageTableTableVOs
     * @return
     */
    @RequestLine("POST /node/lineage/acquireOldColumnColumn")
    ApiResponse acquireOldColumnColumn(LineageColumnColumnParam lineageTableTableVOs);

    /**
     * 推送历史血缘数据。常用于推送手动维护的血缘信息
     * @param lineageTableTableParam
     * @return
     */
    @RequestLine("POST /node/lineage/acquireOldTableTable")
    ApiResponse acquireOldTableTable(LineageTableTableParam lineageTableTableParam);
}
