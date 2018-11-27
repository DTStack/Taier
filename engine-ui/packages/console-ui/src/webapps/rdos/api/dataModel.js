import localDb from 'utils/localDb'
import utils from 'utils'

import http from './http'
import dataModelReq from './reqDataModel';

export default {

    // ============ 配置中心 ============
    addModel (params) {
        return http.post(dataModelReq.MODEL_ADD, params)
    },
    deleteModel (params) {
        return http.post(dataModelReq.MODEL_DELETE, params)
    },
    updateModel (params) {
        return http.post(dataModelReq.MODEL_UPDATE, params)
    },
    getModels (params) {
        return http.post(dataModelReq.MODEL_LIST, params)
    },

    // ============ 指标 ============
    addModelIndex (params) {
        return http.post(dataModelReq.MODEL_INDEX_ADD, params)
    },
    deleteModelIndex (params) {
        return http.post(dataModelReq.MODEL_INDEX_DELETE, params)
    },
    updateModelIndex (params) {
        return http.post(dataModelReq.MODEL_INDEX_UPDATE, params)
    },
    getModelIndexs (params) {
        return http.post(dataModelReq.MODEL_INDEX_LIST, params)
    },

    createModelRule (params) {
        return http.post(dataModelReq.MODEL_RULE_CREATE, params)
    },
    getModelRules (params) {
        return http.post(dataModelReq.MODEL_RULE_LIST, params)
    },
    getColumnType (params) {
        return http.post(dataModelReq.COLUMN_TYPE_LIST, params)
    },
    getType (params) {
        return http.post(dataModelReq.TYPE_LIST, params)
    },

    // ============ 模型设计 ============
    getTableList (params) {
        return http.post(dataModelReq.TABLE_LIST, params)
    },
    createTable (params) {
        return http.post(dataModelReq.TABLE_CREATE, params)
    },
    alterTable (params) {
        return http.post(dataModelReq.TABLE_ALTER, params)
    },
    deleteTable (params) {
        return http.post(dataModelReq.TABLE_DELETE, params)
    },
    getTableNameRules (params) {
        return http.post(dataModelReq.TABLE_NAME_RULE, params)
    },
    getTableListByType (params) {
        return http.post(dataModelReq.TABLE_LIST_BY_TYPE, params)
    },
    getTablePartitions (params) {
        return http.post(dataModelReq.TABLE_PARITIONS, params)
    },
    createTableByDDL (params) {
        return http.post(dataModelReq.TABLE_CREATE_BY_DDL, params)
    },

    // ============ 检测中心 ============
    getCheckList (params) {
        return http.post(dataModelReq.CHECK_LIST, params)
    },
    getCheckPartitions (params) {
        return http.post(dataModelReq.CHECK_PARTITIONS_LIST, params)
    },
    ignoreCheck (params) {
        return http.post(dataModelReq.CHECK_IGNORE, params)
    },

    // ============ 总览 ============
    statisticTotal (params) {
        return http.post(dataModelReq.STATISTICS_USAGE, params)
    },
    statisticTableRate (params) {
        return http.post(dataModelReq.STATISTICS_TABLE_RATE, params)
    },
    statisticColumnRate (params) {
        return http.post(dataModelReq.STATISTICS_COLUMN_RATE, params)
    },
    statisticTableTrend (params) {
        return http.post(dataModelReq.STATISTICS_TABLE_TREND, params)
    },
    statisticColumnTrend (params) {
        return http.post(dataModelReq.STATISTICS_COLUMN_TREND, params)
    }
}
