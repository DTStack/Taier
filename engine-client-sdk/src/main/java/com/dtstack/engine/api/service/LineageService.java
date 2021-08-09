package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pojo.LevelAndCount;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnParam;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableParam;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.param.*;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;
import java.util.List;
import java.util.Set;

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
     * 查询表的血缘上游字段列表
     * @param queryTableLineageColumnParam
     * @return
     */
    @RequestLine("POST /node/lineage/queryTableLineageInputColumns")
    ApiResponse<List<String>> queryTableLineageInputColumns(QueryTableLineageColumnParam queryTableLineageColumnParam);

    /**
     * 查询表的血缘下游字段列表
     * @param queryTableLineageColumnParam
     * @return
     */
    @RequestLine("POST /node/lineage/queryTableLineageResultColumns")
    ApiResponse<List<String>> queryTableLineageResultColumns(QueryTableLineageColumnParam queryTableLineageColumnParam);


    /**
     * @author ZYD
     * @Description 解析sql表信息
     * @Date 2021/3/11 14:04
     * @param sql:
     * @param defaultDb:
     * @param sourceType:
     * @return: com.dtstack.sdk.core.common.ApiResponse<java.util.List<com.dtstack.engine.api.pojo.lineage.Table>>
     **/
    @RequestLine("POST /node/lineage/parseTables")
    ApiResponse<List<Table>> parseTables(@Param("sql") String sql,@Param("defaultDb") String defaultDb, @Param("sourceType") Integer sourceType);


    /**
     * @author ZYD
     * @Description
     * @Date 2021/4/2 16:09
     * @param sql: 解析sql方法信息
     * @return: com.dtstack.sdk.core.common.ApiResponse<java.util.Set<java.lang.String>>
     **/
    @RequestLine("POST /node/lineage/parseFunction")
    ApiResponse<Set<String>> parseFunction(@Param("sql") String sql);



    /**
     * 查询表上游血缘表数量和层数
     * @param queryTableLineageParam
     * @return
     */
    @RequestLine("POST /node/lineage/queryTableInputLineageCountAndLevel")
    ApiResponse<LevelAndCount> queryTableInputLineageCountAndLevel(QueryTableLineageParam queryTableLineageParam);

    /**
     * 查询表下游血缘表数量和层数
     * @param queryTableLineageParam
     * @return
     */
    @RequestLine("POST /node/lineage/queryTableResultLineageCountAndLevel")
    ApiResponse<LevelAndCount> queryTableResultLineageCountAndLevel(QueryTableLineageParam queryTableLineageParam);



    /**
     * 功能描述:根据taskId和appType查询表级血缘
     * @author zyd
     * @date 2021/4/16 9:57 上午
     * @param taskId
     * @param appType
     * @return
    */
    @RequestLine("POST /node/lineage/queryTableLineageByTaskIdAndAppType")
    ApiResponse<List<LineageTableTableVO>> queryTableLineageByTaskIdAndAppType(@Param("taskId") Long taskId,@Param("appType") Integer appType);


    /**
     * 功能描述:根据taskId和appType查询字段血缘
     * @author zyd
     * @date 2021/4/16 9:57 上午
     * @param taskId
     * @param appType
     * @return
     */
    @RequestLine("POST /node/lineage/queryColumnLineageByTaskIdAndAppType")
    ApiResponse<List<LineageColumnColumnVO>> queryColumnLineageByTaskIdAndAppType(@Param("taskId") Long taskId,@Param("appType") Integer appType);


    /**
     * 功能描述:根据taskId和appType删除血缘
     * @author zyd
     * @date 2021/4/16 10:51 上午
     * @return  * @param deleteLineageParam
    */
    @RequestLine("POST /node/lineage/deleteLineageByTaskIdAndAppType")
    ApiResponse deleteLineageByTaskIdAndAppType(DeleteLineageParam deleteLineageParam);
}
