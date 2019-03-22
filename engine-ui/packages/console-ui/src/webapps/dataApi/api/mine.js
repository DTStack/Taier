import http from './http'
import req from '../consts/reqUrls'

export default {
    getApplyList: function (params) {
        return http.post(req.GET_APPLY_LIST, params);
    },
    updateApplyStatusForNormal: function (params) {
        return http.post(req.UPDATE_APPLY_STATUS, params);
    },
    updateApplyStatusForManager: function (params) {
        return http.post(req.UPDATE_APPLY_STATUS_ADMIN, params);
    },
    getApiCallInfoForNormal (params) {
        return http.post(req.GET_API_CALL_INFO, params);
    },
    getApiCallInfoForManager (params) {
        return http.post(req.GET_USER_API_CALL_INFO_ADMIN, params);
    },
    getApiCallErrorInfoForNormal: function (params) {
        return http.post(req.GET_API_CALL_ERROR_INFO, params);
    },
    getApiCallErrorInfoForManager: function (params) {
        return http.post(req.GET_API_CALL_ERROR_INFO_ADMIN, params);
    },
    getApiCallUrl: function (params) {
        return http.post(req.GET_API_CALL_URL, params);
    },
    queryApiCallLogForNormal: function (params) {
        return http.post(req.GET_API_CALL_ERROR_LOG, params);
    },
    queryApiCallLogForManager: function (params) {
        return http.post(req.GET_API_CALL_ERROR_LOG_ADMIN, params);
    },
    getApiCreatorInfo: function (params) {
        return http.post(req.GET_API_CREATOR_INFO, params);
    },
    getApiTimeInfo (params) {
        return http.post(req.GET_API_TIME_INFO, params)
    },
    getUserKey (params) {
        return http.post(req.GET_USER_SK_INFO, params)
    },
    resetUserKey (params) {
        return http.post(req.RESET_APP_SECRET, params)
    },
    generateSkInfo (params) {
        return http.post(req.GENERATE_SECRET, params)
    }
}
