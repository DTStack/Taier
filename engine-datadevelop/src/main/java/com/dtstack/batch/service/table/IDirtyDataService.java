package com.dtstack.batch.service.table;

import com.dtstack.batch.domain.BatchTableInfo;

import java.util.List;
import java.util.Map;

/**
 * 引擎的脏数据相关
 * Date: 2019/5/27
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface IDirtyDataService {

    /**
     * 创建脏数据表
     * @param dbName
     * @param tableName
     * @param dtuicTenantId
     * @return 建表语句
     * @throws Exception
     */
    String createDirtyTable(String dbName, String tableName, Long dtuicTenantId, Long projectId) throws Exception;

    /**
     * 创建脏数据表
     *
     * @param tableName
     * @param lifyDay
     * @param userId
     * @param taskId
     * @param taskName
     * @param tenantId
     * @param projectId
     * @return
     */
    String createDirtyTable(String tableName, Integer lifyDay, Long userId, Long taskId, String taskName,
                            Long tenantId, Long projectId);

    /**
     * 获取指定脏数据表的信息
     * @param tableInfo
     * @param projectId
     * @param dbName
     * @param partId
     * @param tenantId
     * @param dtuicTenantId
     * @param needMask
     * @param limit
     * @param errorType
     * @return
     * @throws Exception
     */
    List<List<String>> getDirtyData(BatchTableInfo tableInfo, Long projectId, String dbName,
                                    Long partId, Long tenantId, Long dtuicTenantId,
                                    Boolean needMask, Integer limit, String errorType, String dtToken);


    /**
     * 脏数据启用前的准备工作
     * eg:
     *    创建脏数据表
     * @param tableName
     * @param lifeDay
     * @param userId
     * @param taskId
     * @param taskName
     * @param tenantId
     * @param dtuicTenantId
     * @param projectId
     * @return
     */
    Map<String, Object> readyForSaveDirtyData(String tableName, Integer lifeDay, Long userId, Long taskId, String taskName,
                                              Long tenantId, Long dtuicTenantId, Long projectId);

}
