package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname LineageService
 * @Description 解析接口
 * @Date 2020/10/22 20:37
 * @Created chener@dtstack.com
 */
@ApiModel
public interface LineageService extends DtInsightServer {

    /**
     * 解析sql基本信息
     * @param sql sql
     * @Param sourceType 数据源类型
     * @return
     */
    @ApiModelProperty("解析sql基本类型")
    ApiResponse<SqlParseInfo> parseBaseInfo(String sql,Integer sourceType);

    /**
     * 解析sql表级血缘
     * @param sql 原始sql
     * @param defaultDb 默认数据库
     * @param sourceType 数据库类型
     * @return
     */
    @ApiModelProperty("解析表级血缘关系")
    ApiResponse<TableLineageParseInfo> parseTableLineage(String sql, String defaultDb,Integer sourceType);

    /**
     * 解析表级血缘并存储血缘关系。
     * @param sql 原始sql
     * @param defaultDb 默认数据库
     * @param engineSourceId engine数据源id
     * @return
     */
    @ApiModelProperty("解析并保存表级血缘关系")
    ApiResponse parseAndSaveTableLineage(Integer appType,String sql, String defaultDb, Long engineSourceId);

    /**
     * 解析字段级血缘关系
     * @param sql
     * @param defaultDb
     * @param tableColumnsMap
     * @return
     */
    @ApiModelProperty("解析字段级血缘关系")
    ApiResponse<ColumnLineageParseInfo> parseColumnLineage(String sql, String defaultDb, Map<String, List<Column>> tableColumnsMap);

    /**
     * 解析字段级血缘关系并存储
     * @param appType
     * @param sql
     * @param defaultDb
     * @param engineSourceId
     * @return
     */
    @ApiModelProperty("解析并存储字段级血缘关系")
    ApiResponse parseAndSaveColumnLineage(Integer appType,String sql, String defaultDb, Long engineSourceId);

    /**
     * 手动添加表级血缘
     * @param appType
     * @param lineageTableTableVO
     * @return
     */
    @ApiModelProperty("手动添加表级血缘关系")
    ApiResponse manualAddTableTable(Integer appType, LineageTableTableVO lineageTableTableVO);

    /**
     * 手动删除表级血缘
     * @param appType
     * @param lineageTableTableVO
     * @return
     */
    @ApiModelProperty("手动删除表级血缘关系")
    ApiResponse manualDeleteTableTable(Integer appType, LineageTableTableVO lineageTableTableVO);

    /**
     * 手动添加字段级血缘
     * @param appType
     * @param lineageColumnColumnVO
     * @return
     */
    @ApiModelProperty("手动添加字段级血缘关系")
    ApiResponse manualAddColumnColumn(Integer appType, LineageColumnColumnVO lineageColumnColumnVO);

    /**
     * 手动删除字段级血缘
     * @param appType
     * @param lineageColumnColumnVO
     * @return
     */
    @ApiModelProperty("手动删除字段级血缘")
    ApiResponse manualDeleteColumnColumn(Integer appType, LineageColumnColumnVO lineageColumnColumnVO);
}
