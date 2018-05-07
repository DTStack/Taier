import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getRegisteredTag(params) {
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

    getRuleTag(params) {
        return http.post(req.QUERY_RULE_TAG, params);
    },
    addRuleTag(params) {
        return http.post(req.ADD_RULE_TAG, params);
    },
    updateTag(params) {
        return http.post(req.UPDATE_TAG, params);
    },
    deleteTag(params) {
        return http.post(req.DELETE_TAG, params);
    },
    getRuleTagDetail(params) {
        return http.post(req.GET_RULE_TAG_RETAIL, params);
    },
    deleteTagCondition(params) {
    	return http.post(req.EDIT_TAG_CONDITION, params);
    },
    deleteTagCondition(params) {
    	return http.post(req.DELETE_TAG_CONDITION, params);
    }

}