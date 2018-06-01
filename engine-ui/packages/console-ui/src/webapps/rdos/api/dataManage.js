import localDb from 'utils/localDb'
import utils from 'utils'

import http from './http'
import dataManageReq from './reqDataManage';

export default {

    // ============== dataManage 数据管理 ==================
    createTable(params) {
        return http.post(dataManageReq.CREATE_TABLE, params)
    },
    searchTable(params) {
        return http.post(dataManageReq.SEARCH_TABLE, params)
    },
    getTable(params) {
        return http.post(dataManageReq.GET_TABLE, params)
    },
    getTablesByName(params) {
        return http.post(dataManageReq.GET_TABLES_BY_NAME, params)
    },
    previewTable(params) {
        return http.post(dataManageReq.PREVIEW_TABLE, params)
    },
    saveTable(params) {
        return http.post(dataManageReq.SAVE_TABLE, params)
    },
    searchLog(params) {
        return http.post(dataManageReq.SEARCH_LOG, params)
    },
    getProjectUsersData(params) {
        return http.post(req.GET_PROJECT_USERS, params)
    },
    getCreateTableCode(params) {
        return http.post(dataManageReq.GET_CREATE_CODE, params)
    },
    dropTable(params) {
        return http.post(dataManageReq.DROP_TABLE, params)
    },
    createDdlTable(params) {
        return http.post(dataManageReq.DDL_CREATE_TABLE, params)
    },
    uploadTableData(params) {
        return http.post(dataManageReq.UPLOAD_TABLE_DATA, params)
    },
    checkTableExist(params) {
        return http.post(dataManageReq.CHECK_TABLE_EXIST, params)
    },
    checkHdfsLocExist(params) {
        return http.post(dataManageReq.CHECK_HDFSLOC_EXIST, params)
    },
    getTablePartition(params) {
        return http.post(dataManageReq.GET_TABLE_PARTITION, params)
    },
    checkTablePartition(params) {
        return http.post(dataManageReq.CHECK_TABLE_PARTITION, params)
    },
    importLocalData(params) {// 导入本地数据
        return http.postAsFormData(dataManageReq.UPLOAD_TABLE_DATA, params)
    },

    // =========== 血缘关系 ==================//
    getTableRelTree(params) {
        return http.post(dataManageReq.GET_REL_TABLE_TREE, params)
    },
    getRelTableInfo(params) {
        return http.post(dataManageReq.GET_REL_TABLE_INFO, params)
    },
    getParentRelTable(params) {
        return http.post(dataManageReq.GET_PARENT_REL_TABLES, params)
    },
    getChildRelTables(params) {
        return http.post(dataManageReq.GET_CHILD_REL_TABLES, params)
    },
    getRelTableTasks(params) {
        return http.post(dataManageReq.GET_REL_TABLE_TASKS, params)
    },
    getRelTableColumns(params) {
        return http.post(dataManageReq.GET_REL_TABLE_COLUMNS, params)
    },
    getRelTableUpDownColumns(params) {
        return http.post(dataManageReq.GET_REL_TABLE_UP_DOWN_COLUMNS, params)
    },

    // =========== 类目管理 ==================//
    getDataCatalogues(params) {
        return http.post(dataManageReq.GET_TABLE_CATALOGUE, params)
    },
    addDataCatalogue(params) {
        return http.post(dataManageReq.ADD_TABLE_CATALOGUE, params)
    },
    delDataCatalogue(params) {
        return http.post(dataManageReq.DEL_TABLE_CATALOGUE, params)
    },
    updateDataCatalogue(params) {
        return http.post(dataManageReq.UPDATE_TABLE_CATALOGUE, params)
    },
    addTableToCatalogue(params) {
        return http.post(dataManageReq.ADD_TABLE_TO_CATALOGUE, params)
    },
    delTableInCatalogue(params) {
        return http.post(dataManageReq.DEL_TABLE_IN_CATALOGUE, params)
    },

    // =========== 脏数据 ==================//
    getDirtyDataTrend(params) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TREND, params)
    },
    top30DirtyData(params) {
        return http.post(dataManageReq.TOP30_DIRTY_DATA, params)
    },
    getDirtyDataTables(params) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TABLES, params)
    },
    getPubSyncTask(params) {// 导入本地数据
        return http.post(dataManageReq.GET_PUB_SYNC_TASK, params)
    },
    getDirtyDataTableInfo(params) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TABLE_INFO, params)
    },
    getDirtyDataTableOverview(params) {
        return http.post(dataManageReq.GET_DIRTY_TABLE_OVERVIEW, params)
    },
    countDirtyData(params) {
        return http.post(dataManageReq.COUNT_DIRTY_DATA, params)
    },
    getDirtyDataAnalytics(params) {
        return http.post(dataManageReq.GET_DIRTY_DATA_ANALYTICS, params)
    },

    // ============ 表权限控制 ============
    getApplyList(params) {
        return http.post(dataManageReq.APPLY_LIST, params)
    },
    applySource(params) {
        return http.post(dataManageReq.APPLY_SOURCE, params)
    },
    revoke(params) {
        return http.post(dataManageReq.REVOKE_PERMISSION, params)
    },
    cancelApply(params) {
        return http.post(dataManageReq.APPLY_CANCEL, params)
    },
    approveApply(params) {
        return http.post(dataManageReq.APPLY_HANDLE, params)
    },


    // =========== 项目表统计 ==================//
    countProjectTable(params) {
        return http.post(dataManageReq.PROJECT_TABLE_COUNT, params)
    },
    countProjectStore(params) {
        return http.post(dataManageReq.PROJECT_STORE_COUNT, params)
    },
    getProjectStoreTop(params) {
        return http.post(dataManageReq.PROJECT_STORE_TOP, params)
    },
    getProjectTableStoreTop(params) {
        return http.post(dataManageReq.PROJECT_TABLE_STORE_TOP, params)
    },
    getProjectDataOverview(params) {
        return http.post(dataManageReq.PROJECT_DATA_OVERVIEW, params)
    },
}
