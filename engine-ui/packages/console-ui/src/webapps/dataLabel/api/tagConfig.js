import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    queryRegisteredTag(params) {
        return http.post(req.QUERY_REGISTERED_TAG, params);
    },
    addRegisterTag(params) {
        return http.post(req.ADD_REGISTER_TAG, params);
    },
    updateTag(params) {
        return http.post(req.UPDATE_TAG, params);
    },
    deleteTag(params) {
        return http.post(req.DELETE_TAG, params);
    },

    queryRuleTag(params) {
        return http.post(req.QUERY_RULE_TAG, params);
    },
    addRuleTag(params) {
        return http.post(req.ADD_RULE_TAG, params);
    },
    updateTag(params) {
        return http.post(req.UPDATE_TAG, params);
    },
    updateTagBaseInfo(params) {
        return http.post(req.UPDATE_TAG_BASE_INFO, params);
    },
    updateTagSqlInfo(params) {
        return http.post(req.UPDATE_TAG_SQL_INFO, params);
    },
    deleteTag(params) {
        return http.post(req.DELETE_TAG, params);
    },
    getRuleTagDetail(params) {
        return http.post(req.GET_RULE_TAG_RETAIL, params);
    },
    getTagCondition(params) {
    	return http.post(req.GET_TAG_CONDITION, params);
    },
    editTagCondition(params) {
    	return http.post(req.EDIT_TAG_CONDITION, params);
    },
    deleteTagCondition(params) {
    	return http.post(req.DELETE_TAG_CONDITION, params);
    },
    getComputeSource(params) {
    	return http.post(req.GET_COMPUTE_SOURCE, params);
    },
    getPeriodType(params) {
    	return http.post(req.GET_PERIOD_TYPE, params);
    },
    getNotifyType(params) {
    	return http.post(req.GET_NOTIFY_TYPE, params);
    },

    getAllIdentifyColumn(params) {
        return http.post(req.GET_ALL_IDENTIFY_COLUMN, params);
    },
    queryIdentifyColumn(params) {
        return http.post(req.QUERY_IDENTIFY_COLUMN, params);
    },
    addIdentifyColumn(params) {
        return http.post(req.ADD_IDENTIFY_COLUMN, params);
    },
    updateIdentifyColumn(params) {
        return http.post(req.UPDATE_IDENTIFY_COLUMN, params);
    },
    deleteIdentifyColumn(params) {
        return http.post(req.DELETE_IDENTIFY_COLUMN, params);
    },

}