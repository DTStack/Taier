const baseUrl = APP_CONF.API_BASE_URL || '';

export default {
    CREATE_TABLE: `${baseUrl}/batch/hiveMetaData/createTable`,
    SEARCH_TABLE: `${baseUrl}/batch/hiveMetaData/showTables`,
    GET_TABLE: `${baseUrl}/batch/hiveMetaData/getTable`,
    PREVIEW_TABLE: `${baseUrl}/batch/hiveMetaData/getData`,
    SAVE_TABLE: `${baseUrl}/batch/hiveMetaData/alterTable`,
    SEARCH_LOG: `${baseUrl}/batch/hiveActionRecord/getRecords`,
    GET_CREATE_CODE: `${baseUrl}/batch/hiveMetaData/createSql`,
    DROP_TABLE: `${baseUrl}/batch/hiveMetaData/dropTable`,
    DDL_CREATE_TABLE: `${baseUrl}/batch/hiveMetaData/ddlCreateTable`,
    CHECK_TABLE_EXIST: `${baseUrl}/batch/hiveMetaData/isTableExist`,
    GET_TABLES_BY_NAME: `${baseUrl}/batch/hiveMetaData/getTableList`, // 查询指定名称表信息(支持模糊查询)
    CHECK_HDFSLOC_EXIST: `${baseUrl}/batch/hiveMetaData/isHdfsDirExist`,
    GET_TABLE_PARTITION: `${baseUrl}/batch/hiveTablePartition/getPartitions`, // 获取表分区信息 
    UPLOAD_TABLE_DATA: `${baseUrl}/upload/batch/hiveDataImport/importData`, // 
    CHECK_TABLE_PARTITION: `${baseUrl}/batch/hiveMetaData/checkPartitionExists`, // 检查表分区

    // ========= 血缘关系 =========
    GET_REL_TABLE_TREE: `${baseUrl}/batch/batchBloodRelation/getTree`, // 获取血缘关系树
    GET_REL_TABLE_INFO: `${baseUrl}/batch/batchBloodRelation/getTableInfo`, // 获取关系表详情
    GET_PARENT_REL_TABLES: `${baseUrl}/batch/batchBloodRelation/getUpstream`, // 获取父级血缘关系
    GET_CHILD_REL_TABLES: `${baseUrl}/batch/batchBloodRelation/getDownstream`, // 获取子集血缘关系
    GET_REL_TABLE_TASKS: `${baseUrl}/batch/batchBloodRelation/getRelateTask`, // 获取血缘表关系任务

    // ========= 脏数据管理 =========
    GET_DIRTY_DATA_TREND: `${baseUrl}/batch/batchDirtyData/dataTrend`, // 脏数据产生趋势
    TOP30_DIRTY_DATA: `${baseUrl}/batch/batchDirtyData/dataTop`, // 脏数据产生TOP30任务
    GET_DIRTY_DATA_TABLES: `${baseUrl}/batch/batchDirtyData/getTables`, // 脏数据表列表
    GET_PUB_SYNC_TASK: `${baseUrl}/batch/batchDirtyData/getPubSyncTask`, // 已发布的数据同步任务
    GET_DIRTY_DATA_TABLE_INFO: `${baseUrl}/batch/batchDirtyData/tableInfo`, // 脏数据表详情
    GET_DIRTY_TABLE_OVERVIEW: `${baseUrl}/batch/batchDirtyData/dataOverview`, // 脏数据概览
    COUNT_DIRTY_DATA: `${baseUrl}/batch/batchDirtyData/countInfo`, // 脏数据统计信息
    GET_DIRTY_DATA_ANALYTICS: `${baseUrl}/batch/batchDirtyData/reason`, // 原因分析
};