package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnParam;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;
import java.util.List;
import java.util.Map;

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
     * @param sql 原始sql
     * @param defaultDb 默认数据库
     * @param engineSourceId engine数据源id
     * @return 异步执行，无返回值
     */
    @RequestLine("POST /node/lineage/parseAndSaveTableLineage")
    ApiResponse parseAndSaveTableLineage(@Param("appType")Integer appType,@Param("sql")String sql, @Param("defaultDb")String defaultDb, @Param("engineSourceId")Long engineSourceId);

    /**
     * 解析字段级血缘关系
     * @param sql
     * @param defaultDb
     * @param tableColumnsMap
     * @return 返回字段血缘解析结果
     */
    @RequestLine("POST /node/lineage/parseColumnLineage")
    ApiResponse<ColumnLineageParseInfo> parseColumnLineage(@Param("sql")String sql, @Param("defaultDb")String defaultDb, @Param("tableColumnsMap")Map<String, List<Column>> tableColumnsMap);

    /**
     * 解析字段级血缘关系并存储
     * @param appType
     * @param sql
     * @param defaultDb
     * @param engineSourceId
     * @return 异步解析，无返回结果
     */
    @RequestLine("POST /node/lineage/parseAndSaveColumnLineage")
    ApiResponse parseAndSaveColumnLineage(@Param("appType")Integer appType,@Param("sql")String sql, @Param("defaultDb")String defaultDb,@Param("engineSourceId") Long engineSourceId);

    /**
     * 手动添加表级血缘
     * @param lineageTableTableVO
     * @return 无返回结果
     */
    @RequestLine("POST /node/lineage/manualAddTableTable")
    ApiResponse manualAddTableTable( LineageTableTableVO lineageTableTableVO);

    /**
     * 手动删除表级血缘
     * @param lineageTableTableVO
     * @return 无返回结果
     */
    @RequestLine("POST /node/lineage/manualDeleteTableTable")
    ApiResponse manualDeleteTableTable( LineageTableTableVO lineageTableTableVO);

    /**
     * 手动添加字段级血缘
     * @param lineageColumnColumnVO
     * @return 无返回结果
     */
    @RequestLine("POST /node/lineage/manualAddColumnColumn")
    ApiResponse manualAddColumnColumn( LineageColumnColumnVO lineageColumnColumnVO);

    /**
     * 手动删除字段级血缘
     * @param lineageColumnColumnVO
     * @return 无返回结果
     */
    @RequestLine("POST /node/lineage/manualDeleteColumnColumn")
    ApiResponse manualDeleteColumnColumn(LineageColumnColumnVO lineageColumnColumnVO);

    /**
     * 根据表id查询表上游血缘关系
     * @param appType
     * @param tableId
     * @return 返回当前表的上游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryTableInputLineage")
    ApiResponse<List<LineageTableTableVO>> queryTableInputLineage(@Param("appType") Long appType,@Param("tableId") Long tableId);

    /**
     * 根据表id查询表下游血缘关系
     * @param appType
     * @param tableId
     * @return 返回当前表的下游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryTableResultLineage")
    ApiResponse<List<LineageTableTableVO>> queryTableResultLineage(@Param("appType")Long appType, @Param("tableId")Long tableId);

    /**
     * 根据表id查询表上下游血缘关系
     * @param appType
     * @param tableId
     * @return 返回当前表的上下游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryTableLineages")
    ApiResponse<List<LineageTableTableVO>> queryTableLineages(@Param("appType")Long appType, @Param("tableId")Long tableId);

    /**
     * 查询字段上游字段血缘
     * @param appType
     * @param tableId
     * @param columnName
     * @return 返回当前字段的上游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryColumnInoutLineage")
    ApiResponse<List<LineageColumnColumnVO>> queryColumnInoutLineage(@Param("appType")Long appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);

    /**
     * 查询字段下游字段血缘
     * @param appType
     * @param tableId
     * @param columnName
     * @return 返回当前表的下游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryColumnResultLineage")
    ApiResponse<List<LineageColumnColumnVO>> queryColumnResultLineage(@Param("appType")Long appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);

    /**
     * 查询字段所有血缘关系
     * @param appType
     * @param tableId
     * @param columnName
     * @return 返回当前表的上下游血缘列表。使用双亲表示法表示树。
     */
    @RequestLine("POST /node/lineage/queryColumnLineages")
    ApiResponse<List<LineageColumnColumnVO>> queryColumnLineages(@Param("appType")Long appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);

    /**
     * 推送历史血缘数据
     * @param lineageTableTableVOs
     * @return
     */
    @RequestLine("POST /node/lineage/acquireOldColumnColumn")
    ApiResponse<List<LineageColumnColumnVO>> acquireOldColumnColumn(LineageColumnColumnParam lineageTableTableVOs);
}
