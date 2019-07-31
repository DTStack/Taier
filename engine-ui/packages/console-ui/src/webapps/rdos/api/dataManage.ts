import http from './http'
import dataManageReq from './reqDataManage';
import req from './req';

export default {

    // ============== dataManage 数据管理 ==================
    createTable (params: any) {
        return http.post(dataManageReq.CREATE_TABLE, params)
    },
    searchTable (params: any) {
        return http.post(dataManageReq.SEARCH_TABLE, params)
    },
    queryTable (params: any) {
        return http.post(dataManageReq.QUERY_TABLE, params)
    },
    newSearchTable (params: any) {
        return http.post(dataManageReq.NEW_SEARCH_TABLE, params)
    },
    getTable (params: any) {
        return http.post(dataManageReq.GET_TABLE, params)
    },
    getTablesByName (params: any) {
        return http.post(dataManageReq.GET_TABLES_BY_NAME, params)
    },
    getTableListByProjectList (params: any) {
        return http.post(dataManageReq.GET_TABLE_LIST_BY_PROJECT, params)
    },
    previewTable (params: any) {
        return http.post(dataManageReq.PREVIEW_TABLE, params)
    },
    saveTable (params: any) {
        return http.post(dataManageReq.SAVE_TABLE, params)
    },
    searchLog (params: any) {
        return http.post(dataManageReq.SEARCH_LOG, params)
    },
    getProjectUsersData (params: any) {
        return http.post(req.GET_PROJECT_USERS, params)
    },
    getCreateTableCode (params: any) {
        return http.post(dataManageReq.GET_CREATE_CODE, params)
    },
    dropTable (params: any) {
        return http.post(dataManageReq.DROP_TABLE, params)
    },
    createDdlTable (params: any) {
        return http.post(dataManageReq.DDL_CREATE_TABLE, params)
    },
    uploadTableData (params: any) {
        return http.post(dataManageReq.UPLOAD_TABLE_DATA, params)
    },
    checkTableExist (params: any) {
        return http.post(dataManageReq.CHECK_TABLE_EXIST, params)
    },
    checkHdfsLocExist (params: any) {
        return http.post(dataManageReq.CHECK_HDFSLOC_EXIST, params)
    },
    getTablePartition (params: any) {
        return http.post(dataManageReq.GET_TABLE_PARTITION, params)
    },
    checkTablePartition (params: any) {
        return http.post(dataManageReq.CHECK_TABLE_PARTITION, params)
    },
    importLocalData (params: any) { // 导入本地数据
        return http.postAsFormData(dataManageReq.UPLOAD_TABLE_DATA, params)
    },
    applyTable (params: any) {
        return http.post(dataManageReq.APPLY_TABLE, params)
    },
    getUsersInTenant (params: any) {
        return http.post(dataManageReq.GET_USERS_IN_TENANT, params)
    },
    // =========== 血缘关系 ==================//
    getTableRelTree (params: any) {
        return http.post(dataManageReq.GET_REL_TABLE_TREE, params)
    },
    getRelTableInfo (params: any) {
        return http.post(dataManageReq.GET_REL_TABLE_INFO, params)
    },
    getParentRelTable (params: any) {
        return http.post(dataManageReq.GET_PARENT_REL_TABLES, params)
    },
    getChildRelTables (params: any) {
        return http.post(dataManageReq.GET_CHILD_REL_TABLES, params)
    },
    getRelTableTasks (params: any) {
        return http.post(dataManageReq.GET_REL_TABLE_TASKS, params)
    },
    getRelTableColumns (params: any) {
        return http.post(dataManageReq.GET_REL_TABLE_COLUMNS, params)
    },
    getRelTableUpDownColumns (params: any) {
        return http.post(dataManageReq.GET_REL_TABLE_UP_DOWN_COLUMNS, params)
    },

    // =========== 类目管理 ==================//
    getDataCatalogues (params: any) {
        return http.post(dataManageReq.GET_TABLE_CATALOGUE, params)
    },
    addDataCatalogue (params: any) {
        return http.post(dataManageReq.ADD_TABLE_CATALOGUE, params)
    },
    delDataCatalogue (params: any) {
        return http.post(dataManageReq.DEL_TABLE_CATALOGUE, params)
    },
    updateDataCatalogue (params: any) {
        return http.post(dataManageReq.UPDATE_TABLE_CATALOGUE, params)
    },
    addTableToCatalogue (params: any) {
        return http.post(dataManageReq.ADD_TABLE_TO_CATALOGUE, params)
    },
    delTableInCatalogue (params: any) {
        return http.post(dataManageReq.DEL_TABLE_IN_CATALOGUE, params)
    },
    addMark (params: any) {
        return http.post(dataManageReq.ADD_MARK, params)
    },
    cancelMark (params: any) {
        return http.post(dataManageReq.CANCEL_MARK, params)
    },

    // =========== 脏数据 ==================//
    getDirtyDataTrend (params: any) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TREND, params)
    },
    top30DirtyData (params: any) {
        return http.post(dataManageReq.TOP30_DIRTY_DATA, params)
    },
    getDirtyDataTables (params: any) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TABLES, params)
    },
    getPubSyncTask (params: any) { // 导入本地数据
        return http.post(dataManageReq.GET_PUB_SYNC_TASK, params)
    },
    getDirtyDataTableInfo (params: any) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TABLE_INFO, params)
    },
    getDirtyDataTableOverview (params: any) {
        return http.post(dataManageReq.GET_DIRTY_TABLE_OVERVIEW, params)
    },
    countDirtyData (params: any) {
        return http.post(dataManageReq.COUNT_DIRTY_DATA, params)
    },
    getDirtyDataAnalytics (params: any) {
        return http.post(dataManageReq.GET_DIRTY_DATA_ANALYTICS, params)
    },

    // ============ 表权限控制 ============
    getApplyList (params: any) {
        return http.post(dataManageReq.APPLY_LIST, params)
    },
    applySource (params: any) {
        return http.post(dataManageReq.APPLY_SOURCE, params)
    },
    revoke (params: any) {
        return http.post(dataManageReq.REVOKE_PERMISSION, params)
    },
    cancelApply (params: any) {
        return http.post(dataManageReq.APPLY_CANCEL, params)
    },
    applyReply (params: any) {
        return http.post(dataManageReq.ApplY_REPLY, params)
    },
    getDdlList (params: any) {
        return http.post(dataManageReq.GET_DDL_LIST, params)
    },
    getDmlList (params: any) {
        return http.post(dataManageReq.GET_DML_LIST, params)
    },
    getSimpleColumns (params: any) {
        return http.post(dataManageReq.GET_SIMPLE_COLUMNS, params)
    },
    getApplyDetail (params: any) {
        return http.post(dataManageReq.GET_APPLY_DETAIL, params)
    },

    // =========== 项目表统计 ==================//
    countProjectTable (params: any) {
        return http.post(dataManageReq.PROJECT_TABLE_COUNT, params)
    },
    countProjectStore (params: any) {
        return http.post(dataManageReq.PROJECT_STORE_COUNT, params)
    },
    getProjectStoreTop (params: any) {
        return http.post(dataManageReq.PROJECT_STORE_TOP, params)
    },
    getProjectTableStoreTop (params: any) {
        return http.post(dataManageReq.PROJECT_TABLE_STORE_TOP, params)
    },
    getProjectDataOverview (params: any) {
        return http.post(dataManageReq.PROJECT_DATA_OVERVIEW, params)
    },
    // =========== 数据脱敏 ==================//
    // 脱敏管理
    searchDesensitization (params: any) {
        return http.post(dataManageReq.GET_DESENSITIZATION_LIST, params)
    },
    // 检查新增脱敏是否有权限
    voidCheckPermission (params: any) {
        return http.post(dataManageReq.CHECK_PERMISSION, params)
    },
    addDesensitization (params: any) {
        return http.post(dataManageReq.ADD_DESENSITIZATION, params)
    },
    delDesensitization (params: any) {
        return http.post(dataManageReq.DEL_DESENSITIZATION, params)
    },
    getTableList (params: any) {
        return http.post(dataManageReq.GET_TABLE_LIST, params)
    },
    getColumnsList (params: any) {
        return http.post(dataManageReq.GET_COLUMNS_LIST, params)
    },
    //  添加脱敏时获取脱敏列表
    getDesRulesList (params: any) {
        return http.post(dataManageReq.GET_DESRULES_LIST, params)
    },
    checkUpwardColumns (params: any) {
        return http.post(dataManageReq.CHECK_UPWARD_COLUMNS, params)
    },
    viewTableRelation (params: any) {
        return http.post(dataManageReq.VIEW_TABLE_RELATION, params)
    },
    getRelatedPorjects (params: any) {
        return http.post(dataManageReq.GET_RELATED_PROJECTS, params)
    },
    updateOpenStatus (params: any) {
        return http.post(dataManageReq.UPDATE_OPEN_STATUS, params)
    },
    // 血缘
    getTree (params: any) {
        return http.post(dataManageReq.GET_TREE, params)
    },
    getChildColumns (params: any) {
        return http.post(dataManageReq.GET_DOWNCOLUMNS, params)
    },
    getParentColumns (params: any) {
        return http.post(dataManageReq.GET_UPCOLUMNS, params)
    },
    updateLineageStatus (params: any) {
        return http.post(dataManageReq.UPDATA_LINEAGE_STATUS, params) // 链路启用/禁用
    },
    // 规则管理
    searchRule (params: any) {
        return http.post(dataManageReq.GET_RULE_LIST, params)
    },
    addRule (params: any) {
        return http.post(dataManageReq.ADD_RULE, params)
    },
    editRule (params: any) {
        return http.post(dataManageReq.EDIT_RULE, params)
    },
    updateRule (params: any) {
        return http.post(dataManageReq.UPDATE_RULE, params)
    },
    delRule (params: any) {
        return http.post(dataManageReq.DEL_RULE, params)
    }
}
