
import http from './http'
import req from '../consts/reqUrls'

export default {

    // 注册标签
    queryRegisteredTag(params: any) {
        return http.post(req.QUERY_REGISTERED_TAG, params);
    },
    addRegisterTag(params: any) {
        return http.post(req.ADD_REGISTER_TAG, params);
    },

    // 规则标签
    queryRuleTag(params: any) {
        return http.post(req.QUERY_RULE_TAG, params);
    },
    addRuleTag(params: any) {
        return http.post(req.ADD_RULE_TAG, params);
    },
    updateTagSqlInfo(params: any) {
        return http.post(req.UPDATE_TAG_SQL_INFO, params);
    },
    getTagCondition(params: any) {
        return http.post(req.GET_TAG_CONDITION, params);
    },
    editTagCondition(params: any) {
        return http.post(req.EDIT_TAG_CONDITION, params);
    },
    deleteTagCondition(params: any) {
        return http.post(req.DELETE_TAG_CONDITION, params);
    },
    getComputeSource(params: any) {
        return http.post(req.GET_COMPUTE_SOURCE, params);
    },
    queryTagLogInfo(params: any) {
        return http.post(req.QUERY_TAG_LOG, params);
    },

    // 共用
    updateTagBaseInfo(params: any) {
        return http.post(req.UPDATE_TAG_BASE_INFO, params);
    },
    deleteTag(params: any) {
        return http.post(req.DELETE_TAG, params);
    },
    checkPublish(params: any) {
        return http.post(req.PUBLISH_CHECK, params);
    },
    publishTag(params: any) {
        return http.post(req.PUBLISH_TAG, params);
    },
    getRuleTagDetail(params: any) {
        return http.post(req.GET_RULE_TAG_RETAIL, params);
    },

    // 识别列
    getAllIdentifyColumn(params: any) {
        return http.post(req.GET_ALL_IDENTIFY_COLUMN, params);
    },
    queryIdentifyColumn(params: any) {
        return http.post(req.QUERY_IDENTIFY_COLUMN, params);
    },
    addIdentifyColumn(params: any) {
        return http.post(req.ADD_IDENTIFY_COLUMN, params);
    },
    updateIdentifyColumn(params: any) {
        return http.post(req.UPDATE_IDENTIFY_COLUMN, params);
    },
    deleteIdentifyColumn(params: any) {
        return http.post(req.DELETE_IDENTIFY_COLUMN, params);
    }

}
