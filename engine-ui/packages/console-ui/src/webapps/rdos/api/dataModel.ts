
import http from './http'
import dataModelReq from './reqDataModel';

export default {

    // ============ 配置中心 ============
    addModel (params: any) {
        return http.post(dataModelReq.MODEL_ADD, params)
    },
    deleteModel (params: any) {
        return http.post(dataModelReq.MODEL_DELETE, params)
    },
    updateModel (params: any) {
        return http.post(dataModelReq.MODEL_UPDATE, params)
    },
    getModels (params: any) {
        return http.post(dataModelReq.MODEL_LIST, params)
    },

    // ============ 指标 ============
    addModelIndex (params: any) {
        return http.post(dataModelReq.MODEL_INDEX_ADD, params)
    },
    deleteModelIndex (params: any) {
        return http.post(dataModelReq.MODEL_INDEX_DELETE, params)
    },
    updateModelIndex (params: any) {
        return http.post(dataModelReq.MODEL_INDEX_UPDATE, params)
    },
    getModelIndexs (params: any) {
        return http.post(dataModelReq.MODEL_INDEX_LIST, params)
    },

    createModelRule (params: any) {
        return http.post(dataModelReq.MODEL_RULE_CREATE, params)
    },
    getModelRules (params: any) {
        return http.post(dataModelReq.MODEL_RULE_LIST, params)
    },
    getColumnType (params?: any) {
        return http.post(dataModelReq.COLUMN_TYPE_LIST, params)
    },
    getType (params?: any) {
        return http.post(dataModelReq.TYPE_LIST, params)
    },

    // ============ 模型设计 ============
    getTableList (params: any) {
        return http.post(dataModelReq.TABLE_LIST, params)
    },
    createTable (params: any) {
        return http.post(dataModelReq.TABLE_CREATE, params)
    },
    alterTable (params: any) {
        return http.post(dataModelReq.TABLE_ALTER, params)
    },
    deleteTable (params: any) {
        return http.post(dataModelReq.TABLE_DELETE, params)
    },
    getTableNameRules (params?: any) {
        return http.post(dataModelReq.TABLE_NAME_RULE, params)
    },
    getTableListByType (params: any) {
        return http.post(dataModelReq.TABLE_LIST_BY_TYPE, params)
    },
    getTablePartitions (params?: any) {
        return http.post(dataModelReq.TABLE_PARITIONS, params)
    },
    createTableByDDL (params: any) {
        return http.post(dataModelReq.TABLE_CREATE_BY_DDL, params)
    },

    // ============ 检测中心 ============
    getCheckList (params: any) {
        return http.post(dataModelReq.CHECK_LIST, params)
    },
    ignoreCheck (params: any) {
        return http.post(dataModelReq.CHECK_IGNORE, params)
    },

    // ============ 总览 ============
    statisticTotal (params?: any) {
        return http.post(dataModelReq.STATISTICS_USAGE, params)
    },
    statisticTableRate (params?: any) {
        return http.post(dataModelReq.STATISTICS_TABLE_RATE, params)
    },
    statisticColumnRate (params?: any) {
        return http.post(dataModelReq.STATISTICS_COLUMN_RATE, params)
    },
    statisticTableTrend (params?: any) {
        return http.post(dataModelReq.STATISTICS_TABLE_TREND, params)
    },
    statisticColumnTrend (params?: any) {
        return http.post(dataModelReq.STATISTICS_COLUMN_TREND, params)
    }
}
