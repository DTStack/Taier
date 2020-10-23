package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;

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
     * @param sql
     * @return
     */
    ApiResponse<SqlParseInfo> parseBaseInfo(String sql);

    /**
     * 解析sql表级血缘
     * @param sql 原始sql
     * @param defaultDb 默认数据库
     * @return
     */
    ApiResponse<TableLineageParseInfo> parseTableLineage(String sql, String defaultDb);

    /**
     * 解析表级血缘并存储血缘关系。
     * @param sql 原始sql
     * @param defaultDb 默认数据库
     * @param engineSourceId engine数据源id
     * @return
     */
    ApiResponse parseAndSaveTableLineage(Integer appType,String sql, String defaultDb, Long engineSourceId);

    /**
     * 解析字段级血缘关系
     * @param sql 原始sql
     * @param defaultDb
     * @param tableColumnsMap
     * @return
     */
    ApiResponse<ColumnLineageParseInfo> parseColumnLineage(String sql, String defaultDb, Map<String, List<Column>> tableColumnsMap);

    /**
     * 解析字段级血缘关系并存储
     * @return
     */
    ApiResponse parseAndSaveColumnLineage(Integer appType,String sql, String defaultDb, Long engineSourceId);
}
