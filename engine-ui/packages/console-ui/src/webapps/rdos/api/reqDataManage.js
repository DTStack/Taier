import { RDOS_BASE_URL } from 'config/base';

export default {
    CREATE_TABLE: `${RDOS_BASE_URL}/batch/hiveMetaData/createTable`,
    SEARCH_TABLE: `${RDOS_BASE_URL}/batch/hiveMetaData/showTables`,
    GET_TABLE: `${RDOS_BASE_URL}/batch/hiveMetaData/getTable`,
    PREVIEW_TABLE: `${RDOS_BASE_URL}/batch/hiveMetaData/getData`,
    SAVE_TABLE: `${RDOS_BASE_URL}/batch/hiveMetaData/alterTable`,
    SEARCH_LOG: `${RDOS_BASE_URL}/batch/hiveActionRecord/getRecords`,
    GET_CREATE_CODE: `${RDOS_BASE_URL}/batch/hiveMetaData/createSql`,
    DROP_TABLE: `${RDOS_BASE_URL}/batch/hiveMetaData/dropTable`,
    DDL_CREATE_TABLE: `${RDOS_BASE_URL}/batch/hiveMetaData/ddlCreateTable`,
    CHECK_TABLE_EXIST: `${RDOS_BASE_URL}/batch/hiveMetaData/isTableExist`,
    GET_TABLES_BY_NAME: `${RDOS_BASE_URL}/batch/hiveMetaData/getTableList`, // 查询指定名称表信息(支持模糊查询)
    CHECK_HDFSLOC_EXIST: `${RDOS_BASE_URL}/batch/hiveMetaData/isHdfsDirExist`,
    GET_TABLE_PARTITION: `${RDOS_BASE_URL}/batch/hiveTablePartition/getPartitions`, // 获取表分区信息 
    UPLOAD_TABLE_DATA: `${RDOS_BASE_URL}/upload/batch/hiveDataImport/importData`, // 
    CHECK_TABLE_PARTITION: `${RDOS_BASE_URL}/batch/hiveMetaData/checkPartitionExists`, // 检查表分区
   
    // ========= 数据类目 =========
    GET_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveCatalogue/getHiveCatalogue`, // 获取表目录
    ADD_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveCatalogue/addCatalogue`, // 增加目录
    DEL_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveCatalogue/deleteCatalogue`, // 删除目录
    UPDATE_TABLE_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveCatalogue/updateHiveCatalogue`, // 更新目录
    ADD_TABLE_TO_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveTableCatalogue/updateHiveCatalogue`, // 添加表到数据类目
    DEL_TABLE_IN_CATALOGUE: `${RDOS_BASE_URL}/batch/hiveTableCatalogue/deleteTableCatalogue`, // 删除数据类目中的表

    // ========= 血缘关系 =========
    GET_REL_TABLE_TREE: `${RDOS_BASE_URL}/batch/batchBloodRelation/getTree`, // 获取血缘关系树
    GET_REL_TABLE_INFO: `${RDOS_BASE_URL}/batch/batchBloodRelation/getTableInfo`, // 获取关系表详情
    GET_PARENT_REL_TABLES: `${RDOS_BASE_URL}/batch/batchBloodRelation/getUpstream`, // 获取父级血缘关系
    GET_CHILD_REL_TABLES: `${RDOS_BASE_URL}/batch/batchBloodRelation/getDownstream`, // 获取子集血缘关系
    GET_REL_TABLE_TASKS: `${RDOS_BASE_URL}/batch/batchBloodRelation/getRelateTask`, // 获取血缘表关系任务

    // ========= 脏数据管理 =========
    GET_DIRTY_DATA_TREND: `${RDOS_BASE_URL}/batch/batchDirtyData/dataTrend`, // 脏数据产生趋势
    TOP30_DIRTY_DATA: `${RDOS_BASE_URL}/batch/batchDirtyData/dataTop`, // 脏数据产生TOP30任务
    GET_DIRTY_DATA_TABLES: `${RDOS_BASE_URL}/batch/batchDirtyData/getTables`, // 脏数据表列表
    GET_PUB_SYNC_TASK: `${RDOS_BASE_URL}/batch/batchDirtyData/getPubSyncTask`, // 已发布的数据同步任务
    GET_DIRTY_DATA_TABLE_INFO: `${RDOS_BASE_URL}/batch/batchDirtyData/tableInfo`, // 脏数据表详情
    GET_DIRTY_TABLE_OVERVIEW: `${RDOS_BASE_URL}/batch/batchDirtyData/dataOverview`, // 脏数据概览
    COUNT_DIRTY_DATA: `${RDOS_BASE_URL}/batch/batchDirtyData/countInfo`, // 脏数据统计信息
    GET_DIRTY_DATA_ANALYTICS: `${RDOS_BASE_URL}/batch/batchDirtyData/reason`, // 原因分析

    // ========= 表权限管理 =========
    APPLY_LIST: `${RDOS_BASE_URL}/batch/batchApply/pageQuery`, // 申请列表
    APPLY_SOURCE: `${RDOS_BASE_URL}/batch/batchApply/apply`, // 申请资源
    APPLY_CANCEL: `${RDOS_BASE_URL}/batch/batchApply/cancel`, // 取消申请
    REVOKE_PERMISSION: `${RDOS_BASE_URL}/batch/batchApply/revoke`, // 撤回权限
    APPLY_HANDLE: `${RDOS_BASE_URL}/batch/batchApply/getRelateTask`, // 申请处理
};