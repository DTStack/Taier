import localDb from 'utils/localDb'
import utils from 'utils'

import http from './http'
import dataModelReq from './reqDataModel';

export default {

    // 模型-层级/主题域/频率/增量
    addModel(params) {
        return http.post(dataModelReq.MODEL_ADD, params)
    },
    deleteModel(params) {
        return http.post(dataModelReq.MODEL_DELETE, params)
    },
    updateModel(params) {
        return http.post(dataModelReq.MODEL_UPDATE, params)
    },
    getModels(params) {
        return http.post(dataModelReq.MODEL_LIST, params)
    },

    // 指标
    addModelIndex(params) {
        return http.post(dataModelReq.MODEL_INDEX_ADD, params)
    },
    deleteModelIndex(params) {
        return http.post(dataModelReq.MODEL_INDEX_DELETE, params)
    },
    updateModelIndex(params) {
        return http.post(dataModelReq.MODEL_INDEX_UPDATE, params)
    },
    getModelIndexs(params) {
        return http.post(dataModelReq.MODEL_INDEX_LIST, params)
    },

    createModelRule(params) {
        return http.post(dataModelReq.MODEL_RULE_CREATE, params)
    },
    getModelRules(params) {
        return http.post(dataModelReq.MODEL_RULE_LIST, params)
    },

    getTableList(params) {
        return http.post(dataModelReq.TABLE_LIST, params)
    },
    createTable(params) {
        return http.post(dataModelReq.TABLE_CREATE, params)
    },
    getTableNameRules(params) {
        return http.post(dataModelReq.TABLE_NAME_RULE, params)
    },
}
