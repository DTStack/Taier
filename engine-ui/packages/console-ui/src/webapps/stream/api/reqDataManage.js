import { STREAM_BASE_URL } from 'config/base';

export default {
    CREATE_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/createTable`,
    SEARCH_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/showTables`,
    GET_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/getTable`,
    PREVIEW_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/getData`,
    SAVE_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/alterTable`,
    SEARCH_LOG: `${STREAM_BASE_URL}/batch/batchHiveActionRecord/getRecords`,
    GET_CREATE_CODE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/createSql`,
    DROP_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/dropTable`,
    DDL_CREATE_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/ddlCreateTable`,
    CHECK_TABLE_EXIST: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/isTableExist`,
    GET_TABLES_BY_NAME: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/getTableList`, // 查询指定名称表信息(支持模糊查询)
    CHECK_HDFSLOC_EXIST: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/isHdfsDirExist`,
    GET_TABLE_PARTITION: `${STREAM_BASE_URL}/batch/batchHiveTablePartition/getPartitions`, // 获取表分区信息 

    UPLOAD_TABLE_DATA: `${STREAM_BASE_URL}/upload/batch/batchHiveDataImport/importData`, // 
    CHECK_TABLE_PARTITION: `${STREAM_BASE_URL}/batch/batchHiveTablePartition/checkPartitionExists`, // 检查表分区
    NEW_SEARCH_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/pageQuery`,//新的查询表的详细信息
    QUERY_TABLE: `${STREAM_BASE_URL}/batch/batchHiveTableInfo/tableQuery`,// 表查询

    GET_USERS_IN_TENANT:`${STREAM_BASE_URL}/common/user/getUsersInTenant`,//用户列表

    // ========= 数据类目 =========
    GET_TABLE_CATALOGUE: `${STREAM_BASE_URL}/batch/batchHiveCatalogue/getHiveCatalogue`, // 获取表目录
    ADD_TABLE_CATALOGUE: `${STREAM_BASE_URL}/batch/batchHiveCatalogue/addCatalogue`, // 增加目录
    DEL_TABLE_CATALOGUE: `${STREAM_BASE_URL}/batch/batchHiveCatalogue/deleteCatalogue`, // 删除目录
    UPDATE_TABLE_CATALOGUE: `${STREAM_BASE_URL}/batch/batchHiveCatalogue/updateHiveCatalogue`, // 更新目录
    ADD_TABLE_TO_CATALOGUE: `${STREAM_BASE_URL}/batch/hiveTableCatalogue/updateHiveCatalogue`, // 添加表到数据类目
    DEL_TABLE_IN_CATALOGUE: `${STREAM_BASE_URL}/batch/hiveTableCatalogue/deleteTableCatalogue`, // 删除数据类目中的表
    APPLY_TABLE: `${STREAM_BASE_URL}/batch/batchApply/apply`, // 申请资源
    
    // ========= 血缘关系 =========
    GET_REL_TABLE_TREE: `${STREAM_BASE_URL}/batch/batchTableBlood/getTree`, // 获取血缘关系树
    GET_REL_TABLE_INFO: `${STREAM_BASE_URL}/batch/batchTableBlood/getTableInfo`, // 获取关系表详情
    GET_PARENT_REL_TABLES: `${STREAM_BASE_URL}/batch/batchTableBlood/getUpstream`, // 获取父级血缘关系
    GET_CHILD_REL_TABLES: `${STREAM_BASE_URL}/batch/batchTableBlood/getDownstream`, // 获取子集血缘关系
    GET_REL_TABLE_TASKS: `${STREAM_BASE_URL}/batch/batchTableBlood/getRelateTask`, // 获取血缘表关系任务
    GET_REL_TABLE_COLUMNS: `${STREAM_BASE_URL}/batch/batchTableBlood/getColumns`, // 获取表字段
    GET_REL_TABLE_UP_DOWN_COLUMNS: `${STREAM_BASE_URL}/batch/batchTableBlood/getUpAndDownStreamColumns`, // 获取表上下游字段

    // ========= 脏数据管理 =========
    GET_DIRTY_DATA_TREND: `${STREAM_BASE_URL}/batch/batchDirtyData/dataTrend`, // 脏数据产生趋势
    TOP30_DIRTY_DATA: `${STREAM_BASE_URL}/batch/batchDirtyData/dataTop`, // 脏数据产生TOP30任务
    GET_DIRTY_DATA_TABLES: `${STREAM_BASE_URL}/batch/batchDirtyData/getTables`, // 脏数据表列表
    GET_PUB_SYNC_TASK: `${STREAM_BASE_URL}/batch/batchDirtyData/getPubSyncTask`, // 已发布的数据同步任务
    GET_DIRTY_DATA_TABLE_INFO: `${STREAM_BASE_URL}/batch/batchDirtyData/tableInfo`, // 脏数据表详情
    GET_DIRTY_TABLE_OVERVIEW: `${STREAM_BASE_URL}/batch/batchDirtyData/dataOverview`, // 脏数据概览
    COUNT_DIRTY_DATA: `${STREAM_BASE_URL}/batch/batchDirtyData/countInfo`, // 脏数据统计信息
    GET_DIRTY_DATA_ANALYTICS: `${STREAM_BASE_URL}/batch/batchDirtyData/reason`, // 原因分析

    // ========= 表权限管理 =========
    APPLY_LIST: `${STREAM_BASE_URL}/batch/batchApply/pageQuery`, // 申请列表
    APPLY_SOURCE: `${STREAM_BASE_URL}/batch/batchApply/apply`, // 申请资源
    APPLY_CANCEL: `${STREAM_BASE_URL}/batch/batchApply/cancel`, // 取消申请
    REVOKE_PERMISSION: `${STREAM_BASE_URL}/batch/batchApply/revoke`, // 撤回权限
    APPLY_HANDLE: `${STREAM_BASE_URL}/batch/batchApply/getRelateTask`, // 申请处理
    ADD_MARK: `${STREAM_BASE_URL}/batch/batchHiveTableCollect/collect`, // 取消收藏
    CANCEL_MARK: `${STREAM_BASE_URL}/batch/batchHiveTableCollect/cancelCollect`, // 取消收藏
    ApplY_REPLY:`${STREAM_BASE_URL}/batch/batchApply/reply`,//申请处理

    // ===== 项目表数据统计 ===== //
    PROJECT_TABLE_COUNT: `${STREAM_BASE_URL}/batch/batchHiveTableCount/tableCount`, // 表总量
    PROJECT_STORE_COUNT: `${STREAM_BASE_URL}/batch/batchHiveTableCount/totalSize`, // 表总存储量
    PROJECT_STORE_TOP: `${STREAM_BASE_URL}/batch/batchHiveTableCount/projectSizeTopOrder`, // 项目占用排行
    PROJECT_TABLE_STORE_TOP: `${STREAM_BASE_URL}/batch/batchHiveTableCount/tableSizeTopOrder`, // 表占用排行
    PROJECT_DATA_OVERVIEW: `${STREAM_BASE_URL}/batch/batchHiveTableCount/dataHistory`, // 数据趋势概览
};